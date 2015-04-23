# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: runOPCWorkshopCleanupAll.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
SLEEP_TIME=12m
#
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usdevops40552 CleanupAccount > Account1Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usdevops40552..."
echo "*   check output in Account1Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam04611 CleanupAccount > Account2Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracleam04611..."
echo "*   check output in Account2Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam03756 CleanupAccount > Account3Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracleam03756..."
echo "*   check output in Account3Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam82569 CleanupAccount > Account4Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracleam82569..."
echo "*   check output in Account4Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam09373 CleanupAccount > Account5Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracleam09373..."
echo "*   check output in Account5Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam85039 CleanupAccount > Account6Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracleam85039..."
echo "*   check output in Account6Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam08813 CleanupAccount > Account7Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracleam08813..."
echo "*   check output in Account7Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam47561 CleanupAccount > Account8Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracleam47561..."
echo "*   check output in Account8Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam32349 CleanupAccount > Account9Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracleam32349..."
echo "*   check output in Account9Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam15239 CleanupAccount > Account10Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracleam15239..."
echo "*   check output in Account10Cleanup.log"
echo "**********************************************************************"

