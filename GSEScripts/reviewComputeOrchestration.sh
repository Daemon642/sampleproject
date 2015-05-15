#!/bin/bash
#
if [ $# -lt 4 ]
then
        echo "Usage: reviewComputeOrchestration.sh <username> <password> <identity domain> <zone>" 
        exit 1
fi
#
USERNAME=$1
PASSWORD=$2
DOMAIN=$3
ZONE=$4
#
PASSWORDFILE="password"$3".txt"
echo ${PASSWORD} > ${PASSWORDFILE}
chmod 600 ${PASSWORDFILE}
nimbula-api -a https://api-${ZONE}.compute.us2.oraclecloud.com -p ${PASSWORDFILE} -u /Compute-${DOMAIN}/${USERNAME} list orchestration /Compute-${DOMAIN} -f json

rm ${PASSWORDFILE}
