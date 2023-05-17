resource "aws_autoscaling_group" "clojure-saas-autoscaling-group" {
  name                 = "clojure-saas-autoscaling-group"
  max_size             = var.max_instance_size
  min_size             = var.min_instance_size
  desired_capacity     = var.desired_capacity
  vpc_zone_identifier  = [aws_subnet.clojure_saas_public_sn_01.id, aws_subnet.clojure_saas_public_sn_02.id]
  launch_configuration = aws_launch_configuration.ecs-launch-configuration.name
  health_check_type    = "ELB"
}
