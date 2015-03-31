# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: cleanupAll.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
#
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usdevops40552
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam04611
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam03756
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam82569
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam09373
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam85039
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam08813
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam47561
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam32349
./runOPCWorkshopCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam15239

