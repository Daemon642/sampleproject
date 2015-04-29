# !/bin/sh
#
if [ $# -lt 1 ]
then
   echo "Usage: checkReview.sh <JCS|DBCS>" 
   exit 1
fi
if [ $1 == "JCS" ]; then
    grep "Storage Container Names" jcs/*_ReviewAccount.log
    grep "DBCS Instance Name" jcs/*_ReviewAccount.log
    grep "AlphaDBCS DB IP" jcs/*_ReviewAccount.log
    grep "JCS Instance Name" jcs/*_ReviewAccount.log
    grep "AlphaJCS WLS IP" jcs/*_ReviewAccount.log
    grep "AlphaJCS OTD IP" jcs/*_ReviewAccount.log
elif [ $1 == "DBCS" ]; then
    grep "Storage Container Names" dbcs/*_ReviewAccount.log
    grep "DBCS Instance Name" dbcs/*_ReviewAccount.log
    grep "AlphaDBCS DB IP" dbcs/*_ReviewAccount.log
    grep "JCS Instance Name" dbcs/*_ReviewAccount.log
else
   echo "Usage: checkReview.sh <JCS|DBCS>" 
   exit 1
fi
