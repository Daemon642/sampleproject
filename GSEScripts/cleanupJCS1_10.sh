# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: cleanupJCS1_10.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
#
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usdevops40552 z12 CleanupAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam04611 z11 CleanupAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam03756 z13 CleanupAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam82569 z11 CleanupAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam09373 z13 CleanupAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam85039 z11 CleanupAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam08813 z13 CleanupAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam47561 z11 CleanupAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam32349 z13 CleanupAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam15239 z11 CleanupAccount

