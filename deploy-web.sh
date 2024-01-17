#!/bin/bash

sudo unzip -o /data/web-app/omnimp-auth.war -d /data/web-app/auth

sudo mv /data/web-app/auth/WEB-INF/classes/static/css /data/web-app/auth/
sudo mv /data/web-app/auth/WEB-INF/classes/static/fonts /data/web-app/auth/
sudo mv /data/web-app/auth/WEB-INF/classes/static/images /data/web-app/auth/
sudo mv /data/web-app/auth/WEB-INF/classes/static/js /data/web-app/auth/
sudo mv /data/web-app/auth/WEB-INF/classes/static/html /data/web-app/auth/

if [ ! -d /data/web-app/auth/org ]; then
    sudo rm -rf /data/web-app/auth/org
fi

if [ ! -d /data/web-app/auth/WEB-INF ]; then
    sudo rm -rf /data/web-app/auth/WEB-INF
fi

sudo chown -R apache:web /data/web-app/auth
#apache graceful issue
#sudo chown apache:web /data/web/apache/bin/httpd