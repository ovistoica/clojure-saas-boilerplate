(ns saas.openai.api
  (:require [saas.openai.core :as core]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Models
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn list-models
  "Lists the currently available models, and provides basic information about each one such as the owner and availability.

  Example:
  ```
  (list-models)
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/models/list)
  "
  ([m]
   (list-models m nil))
  ([m options]
   (core/response-for m :list-models {} options)))

(defn retrieve-model
  "Retrieves a model instance, providing basic information about the model such as the owner and permissioning.

  Example:
  ```
  (retrieve-model m {:model \"text-davinci-003\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/models/retrieve)
  "
  ([m params]
   (retrieve-model m params nil))
  ([m params options]
   (core/response-for m :retrieve-model params options)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Completion
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-completion
  "Creates a completion for the provided prompt and parameters

  Example:
  ```
  (create-completion {:model \"text-davinci-003\"
                      :prompt \"Say this is a test\"
                      :max_tokens 7
                      :temperature 0})
  ```

  For Azure OpenAI pass `{:impl :azure}` for the `options` argument

  Streaming of token events is supported via the `:stream` param, see [Streaming Tokens](/doc/03-streaming.md)

  Also see the [OpenAI](https://platform.openai.com/docs/api-reference/completions/create) / [Azure OpenAI](https://learn.microsoft.com/en-us/azure/cognitive-services/openai/reference#completions) documentation
  "
  ([m params]
   (create-completion m params nil))
  ([m params options]
   (core/response-for m :create-completion params options)))

(defn create-chat-completion
  "Creates a completion for the chat message

  Example:
  ```
  (create-chat-completion {:model \"gpt-3.5-turbo\"
                           :messages [{:role \"system\" :content \"You are a helpful assistant.\"}
                                      {:role \"user\" :content \"Who won the world series in 2020?\"}
                                      {:role \"assistant\" :content \"The Los Angeles Dodgers won the World Series in 2020.\"}
                                      {:role \"user\" :content \"Where was it played?\"}]})
  ```

  For Azure OpenAI pass `{:impl :azure}` for the `options` argument

  Streaming of token events is supported via the `:stream` param, see [Streaming Tokens](/doc/03-streaming.md)

  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/chat/create) / [Azure OpenAI](https://learn.microsoft.com/en-us/azure/cognitive-services/openai/reference#chat-completions) documentation
  "
  ([m params]
   (create-chat-completion m params nil))
  ([m params options]
   (core/response-for m :create-chat-completion params options)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Edit
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-edit
  "Creates a new edit for the provided input, instruction, and parameters

  Example:
  ```
  (create-edit {:model \"text-davinci-edit-001\"
                :input \"What day of the wek is it?\"
                :instruction \"Fix the spelling mistakes\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/edits/create)
  "
  ([m params]
   (create-edit m params nil))
  ([m params options]
   (core/response-for m :create-edit params options)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Images
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-image
  "Creates an image given a prompt.

  Example:
  ```
  (create-image {:prompt \"A cute baby sea otter\"
                 :n 2
                 :size \"1024x1024\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/images/create)
  "
  ([m params]
   (create-image m params nil))
  ([m params options]
   (core/response-for m :create-image params options)))


(defn create-image-edit
  "Creates an edited or extended image given an original image and a prompt.

  Example:
  ```
  (create-image-edit {:image (clojure.java.io/file \"path/to/otter.png\")
                      :mask (clojure.java.io/file \"path/to/mask.png\")
                      :prompt \"A cute baby sea otter wearing a beret\"
                      :n 2
                      :size \"1024x1024\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/images/create-edit)
  "
  ([m params]
   (create-image-edit m params nil))
  ([m params options]
   (core/response-for m :create-image-edit params options)))

(defn create-image-variation
  "Creates a variation of a given image.

  Example:
  ```
  (create-image-variation {:image (clojure.java.io/file \"path/to/otter.png\")
                           :n 2
                           :size \"1024x1024\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/images/create-variation)
  "
  ([m params]
   (create-image-variation m params nil))
  ([m params options]
   (core/response-for m :create-image-variation params options)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Embedding
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-embedding
  "Creates an embedding vector representing the input text.

  Example:
  ```
  (create-embedding {:model \"text-embedding-ada-002\"
                     :input \"The food was delicious and the waiter...\"})
  ```

  For Azure OpenAI pass `{:impl :azure}` for the `options` argument

  Also see the [OpenAI](https://platform.openai.com/docs/api-reference/embeddings/create) / [Azure OpenAI](https://learn.microsoft.com/en-us/azure/cognitive-services/openai/reference#embeddings) documentation
  "
  ([m params]
   (create-embedding m params nil))
  ([m params options]
   (let [opt (if (keyword? options)
               {:impl options} ;; backwards compatibility for when 2nd arg was impl
               options)]
     (core/response-for m :create-embedding params opt))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Audio
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-transcription
  "Transcribes audio into the input language.

  Example:
  ```
  (create-transcription {:file (clojure.java.io/file \"path/to/audio.mp3\")
                         :model \"whisper-1\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/audio/create)
  "
  ([m params]
   (create-transcription m params nil))
  ([m params options]
   (core/response-for m :create-transcription params options)))

(defn create-translation
  "Translates audio into English.

  Example:
  ```
  (create-translation {:file (clojure.java.io/file \"path/to/file/german.m4a\")
                       :model \"whisper-1\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/audio/create)
  "
  ([m params]
   (create-translation m params nil))
  ([m params options]
   (core/response-for m :create-translation params options)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn list-files
  "Returns a list of files that belong to the user's organization.

  Example:
  ```
  (list-files)
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/files/list)
  "
  ([m]
   (list-files m nil))
  ([m options]
   (core/response-for m :list-files {} options)))

(defn create-file
  "Upload a file that contains document(s) to be used across various endpoints/features. Currently, the size of all the files uploaded by one organization can be up to 1 GB.

  Example:
  ```
  (create-file {:purpose \"fine-tune\"
                :file (clojure.java.io/file \"path/to/fine-tune.jsonl\")})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/files/upload)
  "
  ([m params]
   (create-file m params nil))
  ([m params options]
   (core/response-for m :create-file params options)))

(defn delete-file
  "Delete a file.

  Example:
  ```
  (delete-file {:file-id \"file-wefuhweof\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/files/delete)
  "
  ([m params]
   (delete-file m params nil))
  ([m params options]
   (core/response-for m :delete-file params options)))

(defn retrieve-file
  "Returns information about a specific file.

  Example:
  ```
  (retrieve-file {:file-id \"file-wefuhweof\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/files/retrieve)
  "
  ([m params]
   (retrieve-file m params nil))
  ([m params options]
   (core/response-for m :retrieve-file params options)))

(defn download-file
  "Returns the contents of the specified file

  Example:
  ```
  (download-file {:file-id \"file-wefuhweof\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/files/retrieve-content)
  "
  ([m params]
   (download-file m params nil))
  ([m params options]
   (core/response-for m :download-file params options)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fine tune
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-fine-tune
  "Creates a job that fine-tunes a specified model from a given dataset.\n\nResponse includes details of the enqueued job including job status and the name of the fine-tuned models once complete.

  Example:
  ```
  (create-fine-tune {:training_file \"file-xuhfiwuefb\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/fine-tunes/create)
  "
  ([m params]
   (create-fine-tune m params nil))
  ([m params options]
   (core/response-for m :create-fine-tune params options)))

(defn list-fine-tunes
  "List your organization's fine-tuning jobs

  Example:
  ```
  (list-fine-tunes)
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/fine-tunes/list)
  "
  ([m]
   (list-fine-tunes m nil))
  ([m options]
   (core/response-for m :list-fine-tunes {} options)))

(defn retrieve-fine-tune
  "Gets info about the fine-tune job.

  Example:
  ```
  (retrieve-fine-tune {:fine_tune_id \"ft-1wefweub\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/fine-tunes/retrieve)
  "
  ([m params]
   (retrieve-fine-tune m params nil))
  ([m params options]
   (core/response-for m :retrieve-fine-tune params options)))

(defn cancel-fine-tune
  "Immediately cancel a fine-tune job.

  Example:
  ```
  (cancel-fine-tune {:fine_tune_id \"ft-1wefweub\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/fine-tunes/cancel)
  "
  ([m params]
   (cancel-fine-tune m params nil))
  ([m params options]
   (core/response-for m :cancel-fine-tune params options)))


(defn list-fine-tune-events
  "Get fine-grained status updates for a fine-tune job.

  Example:
  ```
  (list-fine-tune-events {:fine_tune_id \"ft-1wefweub\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/fine-tunes/events)
  "
  ([m params]
   (list-fine-tune-events m params nil))
  ([m params options]
   (core/response-for m :list-fine-tune-events params options)))

(defn delete-model
  "Delete a fine-tuned model. You must have the Owner role in your organization.

  Example:
  ```
  (delete-model {:model \"fine-tune\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/fine-tunes/delete-model)
  "
  ([m params]
   (delete-model m params nil))
  ([m params options]
   (core/response-for m :delete-model params options)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Moderation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-moderation
  "Classifies if text violates OpenAI's Content Policy

  Example:
  ```
  (create-moderation {:input \"I want to kill them\"})
  ```
  Also see the [OpenAI documentation](https://platform.openai.com/docs/api-reference/moderations/create)
  "
  ([m params]
   (create-moderation m params nil))
  ([m params options]
   (core/response-for m :create-moderation params options)))
