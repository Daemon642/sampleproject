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
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle21884 ReviewAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle03294 ReviewAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle45722 ReviewAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle48514 ReviewAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle88327 ReviewAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle54892 ReviewAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle16340 ReviewAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle99246 ReviewAccount
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle32870 ReviewAccount
