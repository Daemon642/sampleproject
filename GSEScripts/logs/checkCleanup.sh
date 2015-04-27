# !/bin/sh
#
if [ $# -lt 1 ]
then
   echo "Usage: checkCleanup.sh <JCS|DBCS>" 
   exit 1
fi
if [ $1 == "JCS" ]; then
    grep "Storage Container Names" jcs/*_CleanupAccount.log
    grep "DBCS Instance Name" jcs/*_CleanupAccount.log
    grep "JCS Instance Name" jcs/*_CleanupAccount.log
elif [ $1 == "DBCS" ]; then
    grep "Storage Container Names" dbcs/*_CleanupAccount.log
    grep "DBCS Instance Name" dbcs/*_CleanupAccount.log
    grep "JCS Instance Name" dbcs/*_CleanupAccount.log
else
   echo "Usage: checkCleanup.sh <JCS|DBCS>" 
   exit 1
fi
