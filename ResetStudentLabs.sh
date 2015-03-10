# !!!!!!!!!!!!!!!!!!!!!!!!!!!!
# Reset the Student Files
# !!!!!!!!!!!!!!!!!!!!!!!!!!!!
# The proxy should already be configured, but if needed, here it is:
#   git config --global http.proxy http://www-proxy.us.oracle.com:80
# This command was used to clone the git repository. 
# If needed, you could start over by using this command and skipping step #1
#   git clone https://csbduser_us%40oracle.com@developer.us2.oraclecloud.com/developer81763-usoracletrial92622/s/developer81763-usoracletrial92622_alpha01mobileapplication/scm/developer81763-usoracletrial92622_alpha01mobileapplication.git Alpha01MobileApplication

# Execute the following commands
# This assumes your running this script from the ./lab directory

# 1. This assumes the repository still exists. 
#    Bring down the updates to any of the files in the remote repository
cd ./AlphaOffice
echo 'Pulling any changes from the git repository'
git pull

# 2. Copy over the baseline student files 
echo 'Copying student backup files to source directory'
cp ../StudentBackup/Student*.java ./ProductCatalog/AlphaOfficeAccess/src/oracle/alpha/

# 3. Update the date of any files copied over. If the files are different,
#    they will be uploaded. 
echo 'touching each file'
touch ./ProductCatalog/AlphaOfficeAccess/src/oracle/alpha/Student*.java

# 4. Push the changes to the cloud git repository
#    Note: Sometimes the proxy does not stick. Run the config command to be
#    sure it's configured. Also, only files that were modified will be pushed.
echo 'Commit files to git repository'
git commit -am "Restored Student*.java files to their original state"
echo 'Set proxy'
git config --global http.proxy http://www-proxy.us.oracle.com:80
echo 'Pushing changes'
git push

