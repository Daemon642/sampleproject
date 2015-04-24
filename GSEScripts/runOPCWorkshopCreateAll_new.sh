# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: runOPCWorkshopCreateAll.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
SLEEP_TIME=1m
#
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle21884 SetupJCSWorkshopAccount
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle03294 SetupJCSWorkshopAccount
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle45722 SetupJCSWorkshopAccount
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle48514 SetupJCSWorkshopAccount
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle88327 SetupJCSWorkshopAccount
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle54892 SetupJCSWorkshopAccount
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle16340 SetupJCSWorkshopAccount
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle99246 SetupJCSWorkshopAccount
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle32870 SetupJCSWorkshopAccount
