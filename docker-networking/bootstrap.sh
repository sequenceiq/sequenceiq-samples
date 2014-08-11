#!/bin/bash

if [ ! -f "/var/ssh_setup" ]; then
  echo "Setup ssh"
  #setup pk for root
  mkdir -p /root/.ssh
  cp /vagrant/insecure_rsa  /root/.ssh/id_rsa
  cp /vagrant/ssh_config /root/.ssh/config
  cat /vagrant/insecure_rsa.pub >> /root/.ssh/authorized_keys
  chmod 700 /root/.ssh
  chmod 600 /root/.ssh/*

  echo -e "\nPermitTunnel yes" >> /etc/ssh/sshd_config
  service ssh restart

  touch /var/ssh_setup
fi

apt-get update
apt-get install -y bridge-utils

if [ ! -f "/var/docker_setup" ]; then
  echo "Install docker"
  apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9
  sh -c "echo deb https://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list"
  apt-get update
  apt-get install -y lxc-docker

  sudo docker pull ubuntu
  touch /var/docker_setup
fi
