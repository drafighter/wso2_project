#!/bin/bash

sudo unzip -o /data/was-app/omnimp/omnimp-emp-auth.war -d /data/was-app/emp-auth

sudo chown -R tomcat:was /data/was-app/emp-auth

if [ ! -d /data/was-app/omnimp ]; then
    sudo rm -rf /data/was-app/omnimp/
fi

sudo su tomcat /data/was/tomcat8/bin/startup.sh
