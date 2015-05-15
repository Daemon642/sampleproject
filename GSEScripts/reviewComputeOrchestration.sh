#!/bin/bash
#
if [ $# -lt 2 ]
then
        echo "Usage: reviewComputeOrchestration.sh <zone> <passwd>" 
        exit 1
fi
#
ZONE=$1
PASSWORD=$2
#
echo ${PASSWORD} > /u01/OPCWorkshop/lab/GSEScripts/passwd2.txt
chmod 600 /u01/OPCWorkshop/lab/GSEScripts/passwd2.txt
nimbula-api -a https://api-${ZONE}.compute.us2.oraclecloud.com -p /u01/OPCWorkshop/lab/GSEScripts/passwd2.txt -u /Compute-usoracle16033/pat.davies@oracle.com list orchestration /Comp
ute-usoracle16033 -f json

rm /u01/OPCWorkshop/lab/GSEScripts/passwd2.txt
