# !/bin/sh
#
if [ $# -lt 5 ]
then
	echo "Usage: runManageOPC.sh <username> <password> <identity domain> <zone> <method>" 
        echo "Supported Methods:"
        echo "   ReviewAccount"
        echo "   CleanupAccount"
        echo "   SetupJCSWorkshopAccount"
        echo "   SetupJCSWorkshopOnsiteAccount"
	exit 1
fi
LOGFILE="logs/jcs/"$3"_"$5".log"
GSE_RUNPATH=
VBOX_RUNPATH=/u01/OPCWorkshop/lab/
RUNPATH=$VBOX_RUNPATH
#RUNPATH=$GSE_RUNPATH
HTTPPROXY=
#HTTPPROX=-Dhttp://www-proxy.us.oracle.com:80
java -server -classpath ${RUNPATH}OPCSupport/ManageOPC/deploy/ManageOPC.jar:${RUNPATH}OPCSupport/ManageCompute/deploy/ManageCompute.jar:${RUNPATH}OPCSupport/ManageStorage/deploy/ManageStorage.jar:${RUNPATH}OPCSupport/ManageJCS/deploy/ManageJCS.jar:${RUNPATH}OPCSupport/ManageDBCS/deploy/ManageDBCS.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/commons-codec-1.9.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/commons-logging-1.2.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/fluent-hc-4.4.1.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/httpclient-4.4.1.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/httpclient-cache-4.4.1.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/httpclient-win-4.4.1.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/httpcore-4.4.1.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/httpmime-4.4.1.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/jna-4.1.0.jar:${RUNPATH}httpcomponents-client-4.4.1/lib/jna-platform-4.1.0.jar:${RUNPATH}OracleCloudStorageAPI/lib/jersey-client-1.13.jar:${RUNPATH}OracleCloudStorageAPI/lib/jersey-core-1.13.jar:${RUNPATH}OracleCloudStorageAPI/lib/jersey-json-1.13.jar:${RUNPATH}OracleCloudStorageAPI/lib/jersey-multipart-1.13.jar:${RUNPATH}OracleCloudStorageAPI/lib/jettison-1.1.jar:${RUNPATH}OracleCloudStorageAPI/lib/mimepull-1.9.3.jar:${RUNPATH}OracleCloudStorageAPI/lib/oracle.cloud.storage.api-13.0.0.jar -Djavax.net.ssl.trustStore=${RUNPATH}trustStore.jks ${HTTPPROXY} nassoleng.oracle.ManageOPC $1 $2 $3 $4 $5 $6 > ${LOGFILE} 2>&1 &
echo "**********************************************************************"
echo "* $5 started for Identity Domain $3..."
echo "*   check output in ${LOGFILE}"
echo "**********************************************************************"
