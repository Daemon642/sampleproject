# !/bin/sh
#
PASSWORD='Oracle123!'
SLEEP_TIME=1m
#
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usdevops40552 > Account1Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usdevops40552..."
echo "*   check output in Account1Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam04611 > Account2Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usoracleam04611..."
echo "*   check output in Account2Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam03756 > Account3Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usoracleam03756..."
echo "*   check output in Account3Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam82569 > Account4Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usoracleam82569..."
echo "*   check output in Account4Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam09373 > Account5Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usoracleam09373..."
echo "*   check output in Account5Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam85039 > Account6Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usoracleam85039..."
echo "*   check output in Account6Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam08813 > Account7Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usoracleam08813..."
echo "*   check output in Account7Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam47561 > Account8Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usoracleam47561..."
echo "*   check output in Account8Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam32349 > Account9Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usoracleam32349..."
echo "*   check output in Account9Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runOPCWorkshopJCSCleanup.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam15239 > Account10Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of JCS started for Identity Domain usoracleam15239..."
echo "*   check output in Account10Cleanup.log"
echo "**********************************************************************"

