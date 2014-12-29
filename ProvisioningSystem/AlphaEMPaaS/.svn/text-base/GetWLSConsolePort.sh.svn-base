#!/bin/bash
#
if [ $# -lt 1 ]
then
	echo "Usage: GetWLSConsolePort.sh <Weblogic Domain Name>"
	exit 1
fi
echo $*
WLS_DOMAIN=$1
ssh root@esgpaas-mwaas.us.oracle.com "/u01/Oracle/Middleware/FMW_12.1.2/getport.sh '${WLS_DOMAIN}'"
