resource "aws_cloudwatch_log_group" "clojure_saas_app" {
  name = "clojure_saas_app"
}

resource "aws_cloudwatch_log_group" "clojure_saas_db" {
  name = "clojure_saas_db"
}
