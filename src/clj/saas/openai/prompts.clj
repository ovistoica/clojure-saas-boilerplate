(ns saas.openai.prompts)

(defn calories-and-macros
  [food-text]
  (str "Give me total calories and total macros for: " food-text
       "\nGive me the response in edn format like {:calories 100 :protein 10 :carbs 20 :fat 5}"
       "\n Don't respond with nothing other than the edn format."))


(defn input->ingredients-quantities
  [food-input]
  (str "Give me the primary food ingredients and quantities for food inputted in a JSON format like: \n\n{:food_name quantity}. This data will be used to query calories from a database."
       "If the input is ambiguous reply in the same language as the inputted food saying: 'Can you be more specific? Try using quantities',"
       "\n " food-input))