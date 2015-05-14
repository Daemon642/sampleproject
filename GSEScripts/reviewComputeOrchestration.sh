#!/bin/bash
#
if [ $# -lt 1 ]
then
        echo "Usage: reviewComputeOrchestration.sh <zone>" 
        exit 1
fi
#
ZONE=$1
#

nimbula-api -a https://api-${ZONE}.compute.us2.oraclecloud.com -p /u01/OPCWorkshop/lab/GSEScripts/passwd.txt -u /Compute-usoracle16033/pat.davies@oracle.com list orchestration /Compute-usoracle16033
# nimbula-api -a https://api-${ZONE}.compute.us2.oraclecloud.com -p /u01/OPCWorkshop/lab/GSEScripts/passwd.txt -u /Compute-usoracle16033/pat.davies@oracle.com list orchestration /Compute-usoracle16033 -f json
