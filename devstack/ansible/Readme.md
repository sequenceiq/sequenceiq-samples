#Devstack

##Configuration
Change config in devstack.yml according to your environment:
```
devstack_host_ip: 192.168.1.116
```
Change IP_OF_MACHINE and KEY_TO_MACHINE in hosts according to your environment:
```
[local-kvm]
IP_OF_MACHINE ansible_ssh_user=ubuntu ansible_ssh_private_key_file=~/.ssh/KEY_TO_MACHINE.pem

```

##Starting provision
```
ansible-playbook --verbose devstack.yml -i hosts --limit local-kvm
```
