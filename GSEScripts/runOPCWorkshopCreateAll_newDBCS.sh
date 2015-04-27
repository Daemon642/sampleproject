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
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle21884 SetupDBCSWorkshopAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle03294 SetupDBCSWorkshopAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle45722 SetupDBCSWorkshopAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle48514 SetupDBCSWorkshopAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle88327 SetupDBCSWorkshopAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle54892 SetupDBCSWorkshopAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle16340 SetupDBCSWorkshopAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle99246 SetupDBCSWorkshopAccount
./runManageOPC_DBCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle32870 SetupDBCSWorkshopAccount
