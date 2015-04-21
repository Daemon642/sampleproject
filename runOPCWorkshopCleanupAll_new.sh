# !/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: runOPCWorkshopCleanupAll.sh <password>" 
        exit 1
fi
#
PASSWORD=$1
SLEEP_TIME=1m
#
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle21884 CleanupAccount > Account11Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracle21884..."
echo "*   check output in Account1Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle03294 CleanupAccount > Account12Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracle03294..."
echo "*   check output in Account2Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle45722 CleanupAccount > Account13Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracle45722..."
echo "*   check output in Account3Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle48514 CleanupAccount > Account14Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracle48514..."
echo "*   check output in Account4Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle88327 CleanupAccount > Account15Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracle88327..."
echo "*   check output in Account5Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle54892 CleanupAccount > Account16Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracle54892..."
echo "*   check output in Account6Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle16340 CleanupAccount > Account17Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracle16340..."
echo "*   check output in Account7Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle99246 CleanupAccount > Account18Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracle99246..."
echo "*   check output in Account8Cleanup.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
./runManageOPC.sh gse_support-admin@oracleads.com ${PASSWORD} usoracle32870 CleanupAccount > Account19Cleanup.log 2>&1 &
echo "**********************************************************************"
echo "* Cleanup of Account started for Identity Domain usoracle32870..."
echo "*   check output in Account9Cleanup.log"
echo "**********************************************************************"
