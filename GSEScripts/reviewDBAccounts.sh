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
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle05758 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle50038 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle71552 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle07819 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle24288 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle46695 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle26336 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle90960 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle50539 ReviewAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle69275 ReviewAccount
