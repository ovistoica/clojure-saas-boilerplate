resource "aws_lb" "clojure_saas_nw_load_balancer" {
  name               = "clojure-saas-nw-load-balancer"
  internal           = true
  load_balancer_type = "network"
  subnets            = [aws_subnet.clojure_saas_public_sn_01.id, aws_subnet.clojure_saas_public_sn_02.id]

  tags = {
    Name = "clojure-saas-nw-load-balancer"
  }

}

resource "aws_lb_target_group" "clojure_saas_db_target_group" {
  name        = "clojure-saas-db-target-group"
  port        = "5432"
  protocol    = "TCP"
  vpc_id      = aws_vpc.clojure_saas_vpc.id
  target_type = "ip"

  health_check {
    healthy_threshold   = "3"
    unhealthy_threshold = "3"
    interval            = "10"
    port                = "traffic-port"
    protocol            = "TCP"
  }

  tags = {
    Name = "clojure-saas-db-target-group"
  }
}

resource "aws_lb_listener" "clojure_saas_nw_listener" {
  load_balancer_arn = aws_lb.clojure_saas_nw_load_balancer.arn
  port              = "5432"
  protocol          = "TCP"

  default_action {
    target_group_arn = aws_lb_target_group.clojure_saas_db_target_group.arn
    type             = "forward"
  }
}
