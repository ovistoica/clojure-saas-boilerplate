(ns saas.openai.prompts)

(defn calories-and-macros
  [food-text]
  (str "Give me total calories and total macros for: " food-text
       "\nGive me the response in edn format like {:calories 100 :protein 10 :carbs 20 :fat 5}"
       "\n Don't respond with nothing other than the edn format."))
