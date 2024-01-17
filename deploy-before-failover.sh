#!/bin/bash

sudo su tomcat /data/was/tomcat8/bin/shutdown.sh

if [ ! -d /data/was-app/omnimp ]; then
    sudo rm -rf /data/was-app/omnimp/
fi

if [ ! -d /data/was-app/auth ]; then
    sudo rm -rf /data/was-app/failover/
fi

sudo mkdir -vp /data/was-app/omnimp/
#sudo mkdir -vp /data/was-app/omnimp/omnimp-admin
#sudo mkdir -vp /data/was-app/omnimp/omnimp-api
#sudo mkdir -vp /data/was-app/omnimp/omnimp-failover

sudo chown -R tomcat:was /data/was-app/omnimp