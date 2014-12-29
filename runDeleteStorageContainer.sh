# !/bin/sh
#
if [ $# -lt 3 ]
then
	echo "Usage: runDeleteStorageContainer.sh <username> <password> <identity domain>" 
	exit 1
fi
echo $*
opcUsername=$1
opcPasssword=$2
opcDomain=$3

echo "Username = ${opcUsername}"
echo "Password = ${opcPassword}"
echo "Domain = ${opcDomain}"
/u01/jdeveloper/oracle_common/jdk/bin/java -server -classpath /u01/OPCWorkshop/OPCSupport/.adf:/u01/OPCWorkshop/OPCSupport/ManageStorage/classes:/u01/OPCWorkshop/OracleCloudStorageAPI/lib/jersey-client-1.13.jar:/u01/OPCWorkshop/OracleCloudStorageAPI/lib/jersey-core-1.13.jar:/u01/OPCWorkshop/OracleCloudStorageAPI/lib/jersey-json-1.13.jar:/u01/OPCWorkshop/OracleCloudStorageAPI/lib/jersey-multipart-1.13.jar:/u01/OPCWorkshop/OracleCloudStorageAPI/lib/jettison-1.1.jar:/u01/OPCWorkshop/OracleCloudStorageAPI/lib/mimepull-1.9.3.jar:/u01/OPCWorkshop/OracleCloudStorageAPI/lib/oracle.cloud.storage.api-13.0.0.jar -Djavax.net.ssl.trustStore=/tmp/trustStore4584576661800203914.jks nassoleng.oracle.DeleteStorageContainer $1 $2 $3

