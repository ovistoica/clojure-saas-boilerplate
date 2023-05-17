resource "aws_launch_configuration" "ecs-launch-configuration" {
  name                 = "ecs-launch-configuration"
  image_id             = data.aws_ami.latest_ecs.id
  instance_type        = "t2.medium"
  iam_instance_profile = aws_iam_instance_profile.ecs-instance-profile.id

  root_block_device {
    volume_type           = "standard"
    volume_size           = 100
    delete_on_termination = true
  }

  lifecycle {
    create_before_destroy = true
  }

  security_groups             = [aws_security_group.clojure_saas_public_sg.id]
  associate_public_ip_address = "true"
  key_name                    = var.ecs_key_pair_name
  user_data                   = <<EOF
                                  #!/bin/bash
                                  echo ECS_CLUSTER=${var.ecs_cluster} >> /etc/ecs/ecs.config
                                  mkdir -p /mnt/efs/postgres
                                  cd /mnt
                                  sudo yum install -y amazon-efs-utils
                                  sudo mount -t efs ${aws_efs_mount_target.clj-saas-db-efs-mnt.0.dns_name}:/ efs
                                  EOF
}
