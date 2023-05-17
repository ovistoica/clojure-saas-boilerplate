resource "aws_appautoscaling_target" "ecs_clojure_saas_target" {
  max_capacity       = 2
  min_capacity       = 1
  resource_id        = "service/clojure_saas_cluster/clojure_saas_app_service"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
  depends_on         = ["aws_ecs_service.clojure_saas_app_service"]
}