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
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle05758 z11 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle50038 z11 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle71552 z11 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle07819 z11 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle24288 z11 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle46695 z11 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle26336 z11 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle90960 z11 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle50539 z11 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle69275 z11 ReviewAccount
