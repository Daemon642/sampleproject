# !/bin/sh
#
PASSWORD='Oracle123!'
SLEEP_TIME=15m
#
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usdevops40552 > Account1.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usdevops40552..."
echo "*   check output in Account1.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam04611 > Account2.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usoracleam04611..."
echo "*   check output in Account2.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam03756 > Account3.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usoracleam03756..."
echo "*   check output in Account3.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam82569 > Account4.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usoracleam82569..."
echo "*   check output in Account4.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam09373 > Account5.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usoracleam09373..."
echo "*   check output in Account5.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam85039 > Account6.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usoracleam85039..."
echo "*   check output in Account6.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam08813 > Account7.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usoracleam08813..."
echo "*   check output in Account7.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam47561 > Account8.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usoracleam47561..."
echo "*   check output in Account8.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam32349 > Account9.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usoracleam32349..."
echo "*   check output in Account9.log"
echo "**********************************************************************"
sleep ${SLEEP_TIME}
nohup ./runOPCWorkshopJCSCreate.sh gse_support-admin@oracleads.com ${PASSWORD} usoracleam15239 > Account10.log &
echo "**********************************************************************"
echo "* Create of Alpha01JCS started for Identity Domain usoracleam15239..."
echo "*   check output in Account10.log"
echo "**********************************************************************"
