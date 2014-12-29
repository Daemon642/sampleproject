#!/bin/bash
#
if [ $# -lt 2 ]
then
	echo "Usage: ShutdownDS.sh <Weblogic Domain Name> <WLS Console Port>"
	exit 1
fi
echo $*
WLS_DOMAIN=$1
WLS_PORT=$2
ssh root@esgpaas-mwaas.us.oracle.com "/u01/Oracle/Middleware/FMW_12.1.2/runShutdownDS.sh '${WLS_DOMAIN}' '${WLS_PORT}'"
