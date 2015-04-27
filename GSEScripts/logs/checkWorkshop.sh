# !/bin/sh
#
if [ $# -lt 1 ]
then
   echo "Usage: checkWorkshop.sh <JCS|DBCS>" 
   exit 1
fi
if [ $1 == "JCS" ]; then
    grep "Storage Container Names" jcs/*WorkshopAccount.log
    grep "DBCS Instance Name" jcs/*WorkshopAccount.log
    grep "JCS Instance Name" jcs/*WorkshopAccount.log
    grep "Start Time" jcs/*WorkshopAccount.log
    grep "End Time" jcs/*WorkshopAccount.log
elif [ $1 == "DBCS" ]; then
    grep "Storage Container Names" dbcs/*WorkshopAccount.log
    grep "DBCS Instance Name" dbcs/*WorkshopAccount.log
    grep "JCS Instance Name" dbcs/*WorkshopAccount.log
    grep "Start Time" dbcs/*WorkshopAccount.log
    grep "End Time" dbcs/*WorkshopAccount.log
else
   echo "Usage: checkWorkshop.sh <JCS|DBCS>" 
   exit 1
fi
