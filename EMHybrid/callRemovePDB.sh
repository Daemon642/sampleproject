#!/bin/bash
##
if [ $# -lt 1 ]
then
    echo "Usage: callRemovePDB.sh <AlphaDBCS IP>"
    exit 1
fi
DB_IP=$1
#
echo "************************************************"
echo "* Copy over needed files to AlphaDBCS image ...."
echo "************************************************"
scp -o "StrictHostKeyChecking no" -i /u01/OPCWorkshop/lab/labkey removePDB.* oracle@${DB_IP}:/home/oracle/.
#
echo "********************************************"
echo "* Drop PDB SALES_DEV2 ....                  "
echo "********************************************"
ssh -o "StrictHostKeyChecking no" -i /u01/OPCWorkshop/lab/labkey oracle@${DB_IP} "/home/oracle/removePDB.sh"
