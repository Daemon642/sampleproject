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
edit()
startEdit()
cd('/JDBCSystemResources/AlphaOfficeAccessDS')
set('Targets',jarray.array([ObjectName('com.bea:Name=apps_Cluster,Type=Cluster')], ObjectName))
save()
activate()
disconnect()
exit ()
print ' '
