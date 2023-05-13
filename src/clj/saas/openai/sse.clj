(ns saas.openai.sse
  (:require
   [clojure.pprint :as pprint]
   [hato.client :as http]
   [hato.middleware :as hm]
   [clojure.core.async :as a]
   [clojure.string :as string]
   [cheshire.core :as json])
  (:import (java.io InputStream)))

(def event-mask (re-pattern (str "(?s).+?\n\n")))

(defn deliver-events
  [events {:keys [on-next]}]
  (when on-next
    (a/go
      (loop []
        (let [event (a/<! events)]
          (when (not= :done event)
            (on-next event)
            (recur)))))))

(defn- parse-event [raw-event]
  (let [data-idx (string/index-of raw-event "{")
        done-idx (string/index-of raw-event "[DONE]")]
    (if done-idx
      :done
      (-> (subs raw-event data-idx)
          (json/parse-string true)))))

(defn calc-buffer-size
  "Buffer size should be at least equal to max_tokens
  or 16 (the default in openai as of 2023-02-19)
  plus the [DONE] terminator"
  [{:keys [max_tokens]
    :or {max_tokens 16}}]
  (inc max_tokens))

(defn sse-events
  "Returns a core.async channel with events as clojure data structures.
  Inspiration from https://gist.github.com/oliyh/2b9b9107e7e7e12d4a60e79a19d056ee"
  [{:keys [request params]}]
  (let [event-stream ^InputStream (:body (http/request (merge request
                                                              params
                                                              {:as :stream})))
        buffer-size (calc-buffer-size params)
        events (a/chan (a/sliding-buffer buffer-size) (map parse-event))]
    (a/thread
     (loop [data nil]
       (let [byte-array (byte-array (max 1 (.available event-stream)))
             bytes-read (.read event-stream byte-array)]

         (if (neg? bytes-read)

           ;; Input stream closed, exiting read-loop
           (.close event-stream)

           (let [data (str data (slurp byte-array))]
             (if-let [es (not-empty (re-seq event-mask data))]
               (if (every? true? (map #(a/>!! events %) es))
                 (recur (string/replace data event-mask ""))

                 ;; Output stream closed, exiting read-loop
                 (.close event-stream))

               (recur data)))))))
    events))

(defn sse-request
  "Process streamed results.
  If on-next callback provided, then read from channel and call the callback.
  Returns a response with the core.async channel as the body"
  [{:keys [params] :as ctx}]
  (let [events (sse-events ctx)]
    (deliver-events events params)
    {:status 200
     :body events}))

; Define a new middleware
(defn log-and-return
  [resp]
  (pprint/pprint resp)
  resp)

(defn wrap-log
  [client]
  (fn
    ([req]
     (let [resp (client req)]
       (log-and-return resp)))
    ([req respond raise]
     (client req
             #(respond (log-and-return %))
             raise))))

; Create your own middleware stack.
; Note that ordering is important here:
; - After wrap-request-timing so :request-time is available on the response
; - Before wrap-exceptions so that exceptional responses have not yet caused an exception to be thrown
(def my-middleware (concat [(first hm/default-middleware) wrap-log] (drop 1 hm/default-middleware)))

(def perform-sse-capable-request
  {:name ::perform-sse-capable-request
   :leave (fn [{:keys [request params] :as ctx}]
            (assoc ctx :response (if (:stream params)
                                   (sse-request ctx)
                                   (http/request (assoc request :middleware my-middleware)))))})
