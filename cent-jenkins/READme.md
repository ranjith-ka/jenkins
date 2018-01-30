Run playbook command to install jenkins in centos. 

Add the hostname details in inventory

`ansible-playbook cent-jenkins.yml -i inventory --private-key=~/key/centos.pem`
