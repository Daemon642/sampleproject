#!/bin/sh
#
if [ $# -lt 2 ]
then
        echo "Usage: runAssignDS.sh <Weblogic Domain Name> <Weblogic console Port>"
        exit 1
fi
echo $*
WLS_DOMAIN=$1
WLS_PORT=$2
. /u01/Oracle/Middleware/FMW_12.1.2/wlserver/server/bin/setWLSEnv.sh

java weblogic.WLST /u01/Oracle/Middleware/FMW_12.1.2/AssignDSTarget.py ${WLS_DOMAIN} ${WLS_PORT}
