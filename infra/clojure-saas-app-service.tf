resource "aws_ecs_service" "clojure_saas_app_service" {
  name                               = "clojure_saas_app_service"
  iam_role                           = aws_iam_role.ecs-service-role.name
  cluster                            = aws_ecs_cluster.clojure_saas_ecs_cluster.id
  task_definition                    = "${aws_ecs_task_definition.clojure_saas_app.family}:${max("${aws_ecs_task_definition.clojure_saas_app.revision}", "${data.aws_ecs_task_definition.clojure_saas_app.revision}")}"
  depends_on                         = ["aws_ecs_service.clojure_saas_db_service"]
  desired_count                      = var.desired_capacity
  deployment_minimum_healthy_percent = "50"
  deployment_maximum_percent         = "100"
  lifecycle {
    ignore_changes = ["task_definition"]
  }

  load_balancer {
    target_group_arn = aws_alb_target_group.clojure_saas_app_target_group.arn
    container_port   = 3000
    container_name   = "clojure_saas_app"
  }
}
