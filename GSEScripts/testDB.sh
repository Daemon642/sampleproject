#!/bin/bash
##
if [ $# -lt 1 ]
then
    echo "Usage: runOPCWorkshopDatabaseSetup.sh <AlphaDBCS IP>"
    exit 1
fi
DB_IP=$1
#
echo "************************************************"
echo "* Copy over needed files to AlphaDBCS image ...."
echo "************************************************"
scp -o "StrictHostKeyChecking no" -i ../labkey checkAlphaUser.* oracle@${DB_IP}:/home/oracle/.
#
echo "********************************************"
echo "* Check that Product Table exists ...."
echo "********************************************"
ssh -o "StrictHostKeyChecking no" -i ../labkey oracle@${DB_IP} "/home/oracle/checkAlphaUser.sh"
#
echo "**********************************************"
echo "* OPC Workshop Database check is Complete ...."
echo "**********************************************"
