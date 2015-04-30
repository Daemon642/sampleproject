# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: runOPCWorkshopCreateAll.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
#
./runManageJCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle21884 CreateAlpha01JCS
./runManageJCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle03294 CreateAlpha01JCS
./runManageJCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle45722 CreateAlpha01JCS
./runManageJCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle48514 CreateAlpha01JCS
./runManageJCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle88327 CreateAlpha01JCS
./runManageJCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle54892 CreateAlpha01JCS
./runManageJCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle16340 CreateAlpha01JCS
./runManageJCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle99246 CreateAlpha01JCS
./runManageJCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle32870 CreateAlpha01JCS
