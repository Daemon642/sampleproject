# !/bin/sh
#
if [ $# -lt 3 ]
then
	echo "Usage: runPaasDemoCleanupStorage.sh <username> <password> <identity domain>" 
	exit 1
fi
/u01/jdeveloper/oracle_common/jdk/bin/java -server -classpath /u01/OPCWorkshop/lab/OPCSupport/.adf:/u01/OPCWorkshop/lab/OPCSupport/ManageStorage/classes:/u01/OPCWorkshop/lab/OracleCloudStorageAPI/lib/jersey-client-1.13.jar:/u01/OPCWorkshop/lab/OracleCloudStorageAPI/lib/jersey-core-1.13.jar:/u01/OPCWorkshop/lab/OracleCloudStorageAPI/lib/jersey-json-1.13.jar:/u01/OPCWorkshop/lab/OracleCloudStorageAPI/lib/jersey-multipart-1.13.jar:/u01/OPCWorkshop/lab/OracleCloudStorageAPI/lib/jettison-1.1.jar:/u01/OPCWorkshop/lab/OracleCloudStorageAPI/lib/mimepull-1.9.3.jar:/u01/OPCWorkshop/lab/OracleCloudStorageAPI/lib/oracle.cloud.storage.api-13.0.0.jar -Djavax.net.ssl.trustStore=/u01/OPCWorkshop/lab/trustStore.jks nassoleng.oracle.DeleteStorageContainer $1 $2 $3

