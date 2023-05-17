output "region" {
  value = var.region
}

output "clojure_saas_vpc_id" {
  value = aws_vpc.clojure_saas_vpc.id
}

output "clojure_saas_public_sn_01_id" {
  value = aws_subnet.clojure_saas_public_sn_01.id
}

output "clojure_saas_public_sn_02_id" {
  value = aws_subnet.clojure_saas_public_sn_02.id
}

output "clojure_saas_public_sg_id" {
  value = aws_security_group.clojure_saas_public_sg.id
}

output "ecs-service-role-arn" {
  value = aws_iam_role.ecs-service-role.arn
}

output "ecs-instance-role-name" {
  value = aws_iam_role.ecs-instance-role.name
}

output "app-alb-load-balancer-name" {
  value = aws_alb.clojure_saas_alb_load_balancer.name
}

output "app-alb-load-balancer-dns-name" {
  value = aws_alb.clojure_saas_alb_load_balancer.dns_name
}

output "nw-lb-load-balancer-dns-name" {
  value = aws_lb.clojure_saas_nw_load_balancer.dns_name
}

output "nw-lb-load-balancer-name" {
  value = aws_lb.clojure_saas_nw_load_balancer.name
}

output "clojure-saas-app-target-group-arn" {
  value = aws_alb_target_group.clojure_saas_app_target_group.arn
}

output "clojure-saas-db-target-group-arn" {
  value = aws_lb_target_group.clojure_saas_db_target_group.arn
}

output "mount-target-dns" {
  description = "Address of the mount target provisioned"
  value       = aws_efs_mount_target.clj-saas-db-efs-mnt[0].dns_name
}
