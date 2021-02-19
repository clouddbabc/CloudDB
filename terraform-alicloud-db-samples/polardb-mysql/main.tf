provider "alicloud" {
#   access_key = "${var.access_key}"
#   secret_key = "${var.secret_key}"
  region     = "ap-southeast-1"
}

variable "polardb_mysql_name" {
  default = "polardb_mysql"
}

variable "creation" {
  default = "PolarDB"
}

data "alicloud_zones" "default" {
  available_resource_creation = var.creation
}

resource "alicloud_vpc" "default" {
  name       = "vpc-test"
  cidr_block = "172.16.0.0/16"
}

resource "alicloud_vswitch" "default" {
  vpc_id            = alicloud_vpc.default.id
  cidr_block        = "172.16.0.0/24"
  availability_zone = data.alicloud_zones.default.zones[0].id
  name              = "vsw-test"
}

resource "alicloud_polardb_cluster" "cluster" {
  db_type               = "MySQL"
  db_version            = "5.7"
  db_node_class         = "polar.mysql.x4.medium"
  pay_type              = "PostPaid"
  vswitch_id            = alicloud_vswitch.default.id
  description           = var.polardb_mysql_name
}

resource "alicloud_polardb_account" "account" {
  db_cluster_id         = alicloud_polardb_cluster.cluster.id
  account_name          = "test_polardb"
  account_password      = "N1cetest"
  account_description   = var.polardb_mysql_name
}

resource "alicloud_polardb_database" "default" {
  db_cluster_id = alicloud_polardb_cluster.cluster.id
  db_name       = "test_database"
}
