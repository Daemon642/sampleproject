import sys

print 'Number of arguments:', len(sys.argv), 'arguments.'
print 'Argument List:', str(sys.argv)

domainName=sys.argv[1]
adminUrl="t3://esgpaas-mwaas.us.oracle.com:" + sys.argv[2]
adminUser="weblogic"
adminPassword="welcome1"

print '################################################################'
print ' '
connect(adminUser,adminPassword,adminUrl)
domainRuntime()
cd('ServerRuntimes/apps_server1/JDBCServiceRuntime/apps_server1/JDBCDataSourceRuntimeMBeans/AlphaOfficeAccessDS')
objArray=jarray.array([],java.lang.Object)
strArray=jarray.array([],java.lang.String)
invoke('shutdown',objArray,strArray)
exit ()
print ' '
