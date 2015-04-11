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
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usdevops40552 SetupJCSWorkshopAccount > Account1Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usdevops40552..."
echo "*   check output in Account1Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam04611 SetupJCSWorkshopAccount > Account2Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracleam04611..."
echo "*   check output in Account2Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam03756 SetupJCSWorkshopAccount > Account3Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracleam03756..."
echo "*   check output in Account3Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam82569 SetupJCSWorkshopAccount > Account4Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracleam82569..."
echo "*   check output in Account4Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam09373 SetupJCSWorkshopAccount > Account5Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracleam09373..."
echo "*   check output in Account5Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam85039 SetupJCSWorkshopAccount > Account6Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracleam85039..."
echo "*   check output in Account6Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam08813 SetupJCSWorkshopAccount > Account7Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracleam08813..."
echo "*   check output in Account7Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
#./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam47561 SetupJCSWorkshopAccount > Account8Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracleam47561..."
echo "*   check output in Account8Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
#./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam32349 SetupJCSWorkshopAccount > Account9Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracleam32349..."
echo "*   check output in Account9Create.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam15239 SetupJCSWorkshopAccount > Account10Create.log 2>&1 &
echo "**********************************************************************"
echo "* Create of Account started for Identity Domain usoracleam15239..."
echo "*   check output in Account10Create.log"
echo "**********************************************************************"

