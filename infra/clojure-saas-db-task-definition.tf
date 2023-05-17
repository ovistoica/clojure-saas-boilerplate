data "aws_ecs_task_definition" "clojure_saas_db" {
  task_definition = aws_ecs_task_definition.clojure_saas_db.family
  depends_on      = ["aws_ecs_task_definition.clojure_saas_db"]
}

resource "aws_ecs_task_definition" "clojure_saas_db" {
  family = "clojure_saas_db"
  volume {
    name      = "cljsaasdbvolume"
    host_path = "/mnt/efs/postgres"
  }
  network_mode          = "awsvpc"
  container_definitions = <<DEFINITION
[
  {
    "name": "clojure_saas_db",
    "image": "postgres:alpine",
    "essential": true,
    "portMappings": [
      {
        "containerPort": 5432
      }
    ],
    "environment": [
      {
        "name": "POSTGRES_DB",
        "value": "cljsaasdb"
      },
      {
        "name": "POSTGRES_USER",
        "value": "filmuser"
      },
      {
        "name": "POSTGRES_PASSWORD",
        "value": "${var.db_password}"
      }
    ],
    "mountPoints": [
        {
          "readOnly": null,
          "containerPath": "/var/lib/postgresql/data",
          "sourceVolume": "cljsaasdbvolume"
        }
    ],
    "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "clojure_saas_db",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "ecs"
        }
    },
    "memory": 512,
    "cpu": 256
  }
]
DEFINITION
}
