# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: runOPCWorkshopCleanupAll.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
#
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle21884 CleanupAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle03294 CleanupAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle45722 CleanupAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle48514 CleanupAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle88327 CleanupAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle54892 CleanupAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle16340 CleanupAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle99246 CleanupAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle32870 CleanupAccount
