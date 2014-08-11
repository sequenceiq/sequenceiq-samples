#!/bin/bash

# execute on vm2
sudo brctl addbr bridge0
sudo ifconfig bridge0 172.17.52.1 netmask 255.255.255.0
echo DOCKER_OPTS="-b=bridge0" >> /etc/default/docker
sudo service docker restart
