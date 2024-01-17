#!/bin/bash

## sleep deply.sh
sleep 5

#sudo unzip -o /data/was-app/omnimp/omnimp-admin.war -d /data/was-app/admin
sudo unzip -o /data/was-app/omnimp/omnimp-api.war -d /data/was-app/api
sudo unzip -o /data/was-app/omnimp/omnimp-auth.war -d /data/was-app/auth

#sudo chown -R tomcat:was /data/was-app/admin
sudo chown -R tomcat:was /data/was-app/api
sudo chown -R tomcat:was /data/was-app/auth

sudo chown -R tomcat:was /data/was-app/omnimp

sudo -u tomcat /data/was/tomcat9/bin/startup.sh