resource "aws_alb" "clojure_saas_alb_load_balancer" {
  name            = "clojure-saas-alb-load-balancer"
  security_groups = [aws_security_group.clojure_saas_public_sg.id]
  subnets         = [aws_subnet.clojure_saas_public_sn_01.id, aws_subnet.clojure_saas_public_sn_02.id]

  tags = {
    Name = "clojure-saas-alb-load-balancer"
  }
}

resource "aws_alb_target_group" "clojure_saas_app_target_group" {
  name                 = "clojure-saas-app-target-group"
  port                 = 3000
  protocol             = "HTTP"
  vpc_id               = aws_vpc.clojure_saas_vpc.id
  deregistration_delay = "10"

  health_check {
    healthy_threshold   = "2"
    unhealthy_threshold = "6"
    interval            = "30"
    matcher             = "200,301,302"
    path                = "/"
    protocol            = "HTTP"
    timeout             = "5"
  }

  stickiness {
    type = "lb_cookie"
  }

  tags = {
    Name = "clojure-saas-app-target-group"
  }
}

resource "aws_alb_listener" "alb-listener" {
  load_balancer_arn = aws_alb.clojure_saas_alb_load_balancer.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    target_group_arn = aws_alb_target_group.clojure_saas_app_target_group.arn
    type             = "forward"
  }
}

resource "aws_autoscaling_attachment" "asg_attachment_clojure_saas_app" {
  autoscaling_group_name = "clojure-saas-autoscaling-group"
  alb_target_group_arn   = aws_alb_target_group.clojure_saas_app_target_group.arn
  depends_on             = ["aws_autoscaling_group.clojure-saas-autoscaling-group"]
}
