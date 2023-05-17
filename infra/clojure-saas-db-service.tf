resource "aws_ecs_service" "clojure_saas_db_service" {
  name            = "clojure_saas_db_service"
  cluster         = aws_ecs_cluster.clojure_saas_ecs_cluster.id
  task_definition = "${aws_ecs_task_definition.clojure_saas_db.family}:${max("${aws_ecs_task_definition.clojure_saas_db.revision}", "${data.aws_ecs_task_definition.clojure_saas_db.revision}")}"
  desired_count   = 1
  depends_on      = ["aws_lb.clojure_saas_nw_load_balancer"]

  load_balancer {
    target_group_arn = aws_lb_target_group.clojure_saas_db_target_group.arn
    container_port   = 5432
    container_name   = "clojure_saas_db"
  }

  network_configuration {
    subnets         = [aws_subnet.clojure_saas_public_sn_01.id, aws_subnet.clojure_saas_public_sn_02.id]
    security_groups = [aws_security_group.clojure_saas_public_sg.id]
  }

}
