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
scp -o "StrictHostKeyChecking no" -i ../labkey expdat.dmp oracle@${DB_IP}:/home/oracle/.
scp -o "StrictHostKeyChecking no" -i ../labkey createAlphaUser.* oracle@${DB_IP}:/home/oracle/.
scp -o "StrictHostKeyChecking no" -i ../labkey parfile.txt oracle@${DB_IP}:/home/oracle/.
#
echo "********************************************"
echo "* Create alpha user (password = oracle) ...."
echo "********************************************"
ssh -o "StrictHostKeyChecking no" -i ../labkey oracle@${DB_IP} "/home/oracle/createAlphaUser.sh"
#
echo "******************************************"
echo "* Import default data into alpha user ...."
echo "******************************************"
ssh -o "StrictHostKeyChecking no" -i ../labkey oracle@${DB_IP} "imp alpha/oracle@PDB1 PARFILE=/home/oracle/parfile.txt"
#
echo "**********************************************"
echo "* OPC Workshop Database setup is Complete ...."
echo "**********************************************"
