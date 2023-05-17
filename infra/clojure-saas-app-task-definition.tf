data "aws_ecs_task_definition" "clojure_saas_app" {
  task_definition = aws_ecs_task_definition.clojure_saas_app.family
  depends_on      = ["aws_ecs_task_definition.clojure_saas_app"]
}

resource "aws_ecs_task_definition" "clojure_saas_app" {
  family                = "clojure_saas_app"
  container_definitions = <<DEFINITION
[
  {
    "name": "clojure_saas_app",
    "image": "${var.clojure_saas_app_image}",
    "essential": true,
    "portMappings": [
      {
        "containerPort": 3000,
        "hostPort": 3000
      }
    ],
    "environment": [
      {
        "name": "DB_HOST",
        "value": "${aws_lb.clojure_saas_nw_load_balancer.dns_name}"
      },
      {
        "name": "DB_PASSWORD",
        "value": "${var.db_password}"
      }
    ],
    "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "clojure_saas_app",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "ecs"
        }
    },
    "memory": 1024,
    "cpu": 256
  }
]
DEFINITION
}
