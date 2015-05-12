# !/bin/sh
#
if [ $# -lt 5 ]
then
	echo "Usage: runManageOPC.sh <username> <password> <identity domain> <zone> <method>" 
        echo "Supported Methods:"
        echo "   ReviewAccount"
        echo "   CleanupAccount"
        echo "   SetupDBCSWorkshopAccount"
        echo "   SetupDBCSWorkshopOnsiteAccount"
	exit 1
fi
LOGFILE="logs/dbcs/"$3"_"$5".log"
java -server -classpath ../OPCSupport/ManageOPC/deploy/ManageOPC.jar:../OPCSupport/ManageCompute/deploy/ManageCompute.jar:../OPCSupport/ManageStorage/deploy/ManageStorage.jar:../OPCSupport/ManageJCS/deploy/ManageJCS.jar:../OPCSupport/ManageDBCS/deploy/ManageDBCS.jar:../OracleCloudStorageAPI/lib/jersey-client-1.13.jar:../OracleCloudStorageAPI/lib/jersey-core-1.13.jar:../OracleCloudStorageAPI/lib/jersey-json-1.13.jar:../OracleCloudStorageAPI/lib/jersey-multipart-1.13.jar:../OracleCloudStorageAPI/lib/jettison-1.1.jar:../OracleCloudStorageAPI/lib/mimepull-1.9.3.jar:../OracleCloudStorageAPI/lib/oracle.cloud.storage.api-13.0.0.jar -Djavax.net.ssl.trustStore=../trustStore.jks nassoleng.oracle.ManageOPC $1 $2 $3 $4 $5 $6 > ${LOGFILE} 2>&1 &
echo "**********************************************************************"
echo "* $5 started for Identity Domain $3..."
echo "*   check output in ${LOGFILE}"
echo "**********************************************************************"

