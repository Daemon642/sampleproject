#!/bin/bash

OPC_PATH=/u01/OPCWorkshop/lab
LOCAL_CLONE_PATH=/u01/app/oracle/oradata/orcl/AlphaClone
CLOUD_CLONE_PATH=/u02/app/oracle/oradata/ORCL/AlphaClone

# Make sure the clone has been created and the pluggable
# database definition exists.

if [ ! -d $LOCAL_CLONE_PATH ]; then
  zenity --error \
         --text="AlphaClone diretory not found - exiting."

  exit
fi

if [ ! -f $LOCAL_CLONE_PATH/AlphaClone.xml ]; then
  zenity --error \
         --text="AlphaClone does not appear to be unplugged - exiting."

  exit
fi

errorMsg=""
gotValidIP=0

while [ $gotValidIP -eq 0 ]; do
  IPADDR=$(zenity --entry \
                  --title="ALPHADBCS01 IP Address" \
                  --text="${errorMsg}Enter the IP address found on the Service Console for the databse." \
                  --entry-text="Cloud DBCS IP Address")

  # Check if the user canceled.
  [ $? -ne 0 ] && exit

  # Check to see if we can SSH to the cloud using the IP and private key.
  (ssh -o "StrictHostKeyChecking no" -i $OPC_PATH/labkey oracle@${IPADDR} echo "success") | \
     zenity --progress \
            --percentage=50 \
            --pulsate \
            --title="Validating IP" \
            --text="Checking connectivity..." \
            --auto-close

  # Get the status of the SSH command from the command status list.
  RETVAL=${PIPESTATUS[0]}

  # If we got 0 for the SSH then we can get to the image.
  [ $RETVAL -eq 0 ] && gotValidIP=1

  errorMsg="** Invalid or incorrect IP address.\n\nPlease try again.\n\n"
done

# This function packages the clone while echoing output to
# standard out.  The output updates the progress meter.

function packageAndSend() {
  cd $LOCAL_CLONE_PATH
  tar -czf /tmp/clone.tar.gz *

  echo "# Creating directory on server..."
  echo "25" ; sleep 1
  echo "mkdir -p $CLOUD_CLONE_PATH" | ssh -i $OPC_PATH/labkey oracle@${IPADDR} bash

  echo "50"
  echo "# Copying clone to cloud..."
  scp -i $OPC_PATH/labkey /tmp/clone.tar.gz oracle@${IPADDR}:$CLOUD_CLONE_PATH

  # Clean up the zip
  rm -Rf /tmp/clone.tar.gz

  echo "75" ; sleep 1
  echo "# Unzipping pluggable database..." ; sleep 3

  # Send a series of commands to unzip the cloned database
  (echo cd $CLOUD_CLONE_PATH; \
   echo tar -xzf clone.tar.gz; \
   echo rm clone.tar.gz) | \
       ssh -i $OPC_PATH/labkey oracle@${IPADDR} bash

  # Final echo to 100%
  echo "100" ; sleep 3
}

packageAndSend |
  zenity --progress \
         --title="Uploading ALPHACLONE" \
         --text="Zipping the pluggable DB..." \
         --percentage=5 \
         --auto-close

zenity --info \
       --text="ALPHACLONE successfully transferred to the cloud."
