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
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle05758 CleanupAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle50038 CleanupAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle71552 CleanupAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle07819 CleanupAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle24288 CleanupAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle46695 CleanupAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle26336 CleanupAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle90960 CleanupAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle50539 CleanupAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle69275 CleanupAccount
