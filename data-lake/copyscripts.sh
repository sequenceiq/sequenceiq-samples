#!/bin/bash -e

main() {
  mv /tmp/*.p12 /mnt/fs1
  chmod 777 /mnt/fs1/siq-haas.p12
  containername=$(docker ps -q)
  docker-enter $containername curl -o /usr/lib/hadoop/lib/gcs-connector-hadoop2.jar https://storage.googleapis.com/hadoop-lib/gcs/gcs-connector-latest-hadoop2.jar
}

main
