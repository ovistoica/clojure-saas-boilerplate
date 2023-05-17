resource "aws_efs_file_system" "clj-saas-db-efs" {
  tags = {
    Name = "clj-saas-db-efs"
  }
}

locals {
  subnets = [aws_subnet.clojure_saas_public_sn_01.id, aws_subnet.clojure_saas_public_sn_02.id]
}

resource "aws_efs_mount_target" "clj-saas-db-efs-mnt" {
  count = "2"

  file_system_id = aws_efs_file_system.clj-saas-db-efs.id
  subnet_id      = element(local.subnets, count.index)

  security_groups = [aws_security_group.clojure_saas_public_sg.id]

}
