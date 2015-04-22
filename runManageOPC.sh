# !/bin/sh
#
if [ $# -lt 4 ]
then
	echo "Usage: runManageOPC.sh <username> <password> <identity domain> <method>" 
        echo "Supported Methods:"
        echo "   ReviewAccount"
        echo "   CleanupAccount"
        echo "   SetupJCSWorkshopAccount"
        echo "   SetupDBCSWorkshopAccount"
	exit 1
fi
LOGFILE=$3"_"$4".log"
java -server -classpath ./OPCSupport/ManageOPC/deploy/ManageOPC.jar:./OPCSupport/ManageStorage/deploy/ManageStorage.jar:./OPCSupport/ManageJCS/deploy/ManageJCS.jar:./OPCSupport/ManageDBCS/deploy/ManageDBCS.jar:./OracleCloudStorageAPI/lib/jersey-client-1.13.jar:./OracleCloudStorageAPI/lib/jersey-core-1.13.jar:./OracleCloudStorageAPI/lib/jersey-json-1.13.jar:./OracleCloudStorageAPI/lib/jersey-multipart-1.13.jar:./OracleCloudStorageAPI/lib/jettison-1.1.jar:./OracleCloudStorageAPI/lib/mimepull-1.9.3.jar:./OracleCloudStorageAPI/lib/oracle.cloud.storage.api-13.0.0.jar -Djavax.net.ssl.trustStore=./trustStore.jks nassoleng.oracle.ManageOPC $1 $2 $3 $4 $5 > ${LOGFILE} 2>&1 &
echo "**********************************************************************"
echo "* $4 started for Identity Domain $3..."
echo "*   check output in ${LOGFILE}"
echo "**********************************************************************"

