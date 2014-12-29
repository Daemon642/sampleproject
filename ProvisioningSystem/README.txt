#
# Install Subversion
yum install subversion
#
# Setup Proxy if needed
export http_proxy=http://www-proxy.us.oracle.com
#
# add Ingore
global-ignores = *.class classes *.jar *.ear *.war
#
# Setup source code location
mkdir SVNSource
#
# checkout projects
svn checkout https://gcdpsource.oracle.com/svn/esg-engSystemDemos/trunk/CatalogProvisioning --username 
svn checkout https://gcdpsource.oracle.com/svn/esg-engSystemDemos/trunk/BusinessServiceCatalog --username
#
# Get list of modified files
svn status
#
# Add a new file
svn add <filename>
#
# Commit changes into repository
svn commit
