#!/bin/bash
##
if [ $# -lt 3 ]
then
    echo "Usage: runEMHybridSetup.sh <DBCS IP> <JCS IP> <OTD IP>"
    exit 1
fi
DB_IP=$1
JCS_IP=$2
OTD_IP=$3
#
echo "************************************************"
echo "* Setup ssh for oracle user on JCS VM ...."
echo "************************************************"
echo "copy over labkey.pub..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../labkey.pub opc@${JCS_IP}:/home/opc/.
echo "copy over oracleSudo.txt..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../EMHybrid/oracleSudo.txt opc@${JCS_IP}:/home/opc/.
echo "copy over setupJCS.sh..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../EMHybrid/setupJCS.sh opc@${JCS_IP}:/home/opc/.
echo "call setupJCS.sh..."
ssh -t -o "StrictHostKeyChecking no" -i ../labkey opc@${JCS_IP} "sudo /home/opc/setupJCS.sh"
#
echo "************************************************"
echo "* Setup ssh for oracle user on OTD VM ...."
echo "************************************************"
echo "copy over labkey.pub..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../labkey.pub opc@${OTD_IP}:/home/opc/.
echo "copy over oracleSudo.txt..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../EMHybrid/oracleSudo.txt opc@${OTD_IP}:/home/opc/.
echo "copy over setupOTD.sh..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../EMHybrid/setupOTD.sh opc@${OTD_IP}:/home/opc/.
echo "call setupOTD.sh..."
ssh -t -o "StrictHostKeyChecking no" -i ../labkey opc@${OTD_IP} "sudo /home/opc/setupOTD.sh"
#
echo "************************************************"
echo "* Setup DBCS VM ...."
echo "************************************************"
echo "copy over setupDBCS1.sh..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../EMHybrid/setupDBCS1.sh oracle@${DB_IP}:~/.
echo "copy over setupDBCS2.sh..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../EMHybrid/setupDBCS2.sh opc@${DB_IP}:/home/opc/.
echo "copy over oracleSudo.txt..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../EMHybrid/oracleSudo.txt opc@${DB_IP}:/home/opc/.
echo "copy over unlockDBSNP.sql..."
scp -o "StrictHostKeyChecking no" -i ../labkey ../EMHybrid/unlockDBSNMP.sql oracle@${DB_IP}:~/.
echo "call setupDBCS1.sh..."
ssh -t -o "StrictHostKeyChecking no" -i ../labkey oracle@${DB_IP} "~/setupDBCS1.sh"
echo "call setupDBCS2.sh..."
ssh -t -o "StrictHostKeyChecking no" -i ../labkey opc@${DB_IP} "sudo /home/opc/setupDBCS2.sh"
#
echo "**********************************************"
echo "* EM Hybrid setup is Complete ...."
echo "**********************************************"
