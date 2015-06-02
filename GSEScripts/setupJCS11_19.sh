# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: setupJCS11_19.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
#
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle21884 z11 SetupJCSWorkshopAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle03294 z11 SetupJCSWorkshopAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle45722 z11 SetupJCSWorkshopAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle48514 z11 SetupJCSWorkshopAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle88327 z11 SetupJCSWorkshopAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle54892 z11 SetupJCSWorkshopAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle16340 z11 SetupJCSWorkshopAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle99246 z11 SetupJCSWorkshopAccount
./runManageOPC_JCS.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle32870 z11 SetupJCSWorkshopAccount
