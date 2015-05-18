# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: reviewAllAccounts.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
#
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usdevops40552 z12 ReviewAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam04611 z11 ReviewAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam03756 ReviewAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam82569 ReviewAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam09373 ReviewAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam85039 ReviewAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam08813 ReviewAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam47561 ReviewAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam32349 ReviewAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam15239 ReviewAccount

