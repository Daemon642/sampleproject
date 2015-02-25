# !/bin/sh
#
if [ $# -lt 4 ]
then
	echo "Usage: runManageJCS.sh <username> <password> <identity domain> <method>" 
        echo "Supported Methods:"
        echo "   GetJCSInstanceNames"
        echo "   PaaSDemoCleanup"
        echo "   PaaSDemoReview"
        echo "   DeleteAlpha01JCS"
        echo "   DeleteJCS <JCS Name>"
        echo "   CreateAlpha01JCS"
        echo "   CreateMyJCS2"
        echo "   DeleteMyJCS2"
        echo "   DeleteAllJCS"
	exit 1
fi
/u01/jdeveloper/oracle_common/jdk/bin/java -server -classpath ./OPCSupport/ManageJCS/deploy/ManageJCS.jar:./OracleCloudStorageAPI/lib/jersey-client-1.13.jar:./OracleCloudStorageAPI/lib/jersey-core-1.13.jar:./OracleCloudStorageAPI/lib/jersey-json-1.13.jar:./OracleCloudStorageAPI/lib/jersey-multipart-1.13.jar:./OracleCloudStorageAPI/lib/jettison-1.1.jar:./OracleCloudStorageAPI/lib/mimepull-1.9.3.jar:./OracleCloudStorageAPI/lib/oracle.cloud.storage.api-13.0.0.jar -Djavax.net.ssl.trustStore=./trustStore.jks nassoleng.oracle.ManageJCS $1 $2 $3 $4 $5
