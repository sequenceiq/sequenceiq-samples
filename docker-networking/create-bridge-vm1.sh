#!/bin/bash

# execute on vm1
brctl addbr bridge0
ifconfig bridge0 172.17.51.1 netmask 255.255.255.0
echo DOCKER_OPTS="-b=bridge0" >> /etc/default/docker
service docker restart
