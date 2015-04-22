# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: runOPCWorkshopCreateAll.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
SLEEP_TIME=1m
#
#./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle21884 SetupJCSWorkshopAccount > Account11Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracle21884..."
echo "*   check output in Account11Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle03294 SetupJCSWorkshopAccount > Account12Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracle03294..."
echo "*   check output in Account12Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle45722 SetupJCSWorkshopAccount > Account13Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracle45722..."
echo "*   check output in Account13Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle48514 SetupJCSWorkshopAccount > Account14Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracle48514..."
echo "*   check output in Account14Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle88327 SetupJCSWorkshopAccount > Account15Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracle88327..."
echo "*   check output in Account15Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle54892 SetupJCSWorkshopAccount > Account16Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracle54892..."
echo "*   check output in Account16Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle16340 SetupJCSWorkshopAccount > Account17Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracle16340..."
echo "*   check output in Account17Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle99246 SetupJCSWorkshopAccount > Account18Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracle99246..."
echo "*   check output in Account18Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle32870 SetupJCSWorkshopAccount > Account19Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracle32870..."
echo "*   check output in Account19Create.log"
echo "**********************************************************************"
