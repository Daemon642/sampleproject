# !/bin/sh
#
f [ $# -lt 1 ]
then
        echo "Usage: runOPCWorkshopCleanupAllStorage.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
#
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usdevops40552
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam04611
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam03756
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam82569
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam09373
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam85039
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam08813
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam47561
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam32349
./runOPCWorkshopStorageCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam15239

