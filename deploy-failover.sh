#!/bin/bash

#sudo unzip -o /data/was-app/omnimp/omnimp-admin.war -d /data/was-app/admin
#sudo unzip -o /data/was-app/omnimp/omnimp-api.war -d /data/was-app/api
sudo unzip -o /data/was-app/omnimp/omnimp-failover.war -d /data/was-app/failover

#sudo chown -R tomcat:was /data/was-app/admin
#sudo chown -R tomcat:was /data/was-app/api
sudo chown -R tomcat:was /data/was-app/failover

#sudo chown -R tomcat:was /data/was-app/omnimp

if [ ! -d /data/was-app/omnimp ]; then
    sudo rm -rf /data/was-app/omnimp/
fi

sudo su tomcat /data/was/tomcat8/bin/startup.sh
