#!/bin/bash

##sudo su tomcat /data/was/tomcat9/bin/shutdown.sh

killproc() {
    local servicename=$1
    local user=$2
    local signal="TERM"

    if [ "$#" = 0 ] ; then
        echo $"Usage: killproc {servicename} {user} {signal}"
        return 1
    fi

    if [ "$#" = 3 ]; then
        signal=$3
    fi
    PIDS=`ps -eaf|grep ${servicename}|grep -v grep|grep ${user}|awk '{print $2}'`
    ## process still running..
    for p in ${PIDS}
    do
        if [ ! -z ${p} ] && [ ${p} -gt 0 ];then
            echo "sudo -u tomcat kill -${signal} ${p}"
            sudo -u tomcat kill -${signal} ${p};
            return $?;
        else
             return 0;
        fi
    done
}

## tomcat instance name. recommended tomcat running with custom property (aka -Dcom.example.servicename=myWebApp )
SERVICE_NAME=tomcat9

## tomcat home
TC_HOME=/data/was/tomcat9
if [ ! -d ${TC_HOME} ];then
        TC_HOME=`pwd`
fi

## tomcat process owner name
USER=tomcat

cd ${TC_HOME}
sudo -u tomcat ./bin/shutdown.sh >& /dev/null
sleep 1 # delay 1 sec for tomcat shutting down..

## 
for i in 1 2;do
    killproc ${SERVICE_NAME} ${USER}
    RET=$?
    if [ $RET = 0 ];then
        break;
    fi;
    sleep $i;
done

## if still running send KILL signal
killproc "${SERVICE_NAME}" "${USER}" "KILL"

if [ ! -d /data/was-app/omnimp ]; then
    sudo rm -rf /data/was-app/omnimp/
fi

if [ ! -d /data/was-app/admin ]; then
    sudo rm -rf /data/was-app/admin/
fi

if [ ! -d /data/was-app/api ]; then
    sudo rm -rf /data/was-app/api/
fi

if [ ! -d /data/was-app/auth ]; then
    sudo rm -rf /data/was-app/auth/
fi

sudo mkdir -vp /data/was-app/omnimp/
#sudo mkdir -vp /data/was-app/omnimp/omnimp-admin
#sudo mkdir -vp /data/was-app/omnimp/omnimp-api
#sudo mkdir -vp /data/was-app/omnimp/omnimp-auth

sudo chown -R tomcat:was /data/was-app/omnimp