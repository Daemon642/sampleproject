#!/bin/bash

# This script sets up all of the required tunnels for the labs. It asks for the IP
# addresses of the DBCS server, JCS WLS Admin server and Load Balancer and creates 
# a custome SSH configuration file "/u01/OPCWorkshop/lab/myshh" which gets used to
# create the tunnels. They all get run from one terminal. At that point the following
# are supported via localhost in the browser:
#
# DBCS
#   DBaas Monitor
#   Apex console
#   Glassfish console
#   EM Express - DB
#
# JCS
#   WLS Admin console
#   VNC Server support to the WLS Admin server (VNC has to be started on the server)
#  
# LB
#   Oracle Traffic Director console


OPC_PATH=/u01/OPCWorkshop/lab

errorMsg=""
gotValidIP=0

while [ $gotValidIP -eq 0 ]; do
  DBIPADDR=$(zenity --entry \
                  --title="DBCS IP Address" \
                  --text="${errorMsg}Enter the IP address found in the Database Cloud Service Console for your database." \
                  #--entry-text="DBCS IP Address"
            )

  # Check if the user canceled.
  [ $? -ne 0 ] && exit

  # Check to see if we can SSH to the cloud using the IP and private key.
  (ssh -o "StrictHostKeyChecking no" -o ConnectTimeout=45 -i $OPC_PATH/oldkey/labkey opc@${DBIPADDR} echo "success") | \
     zenity --progress \
            --percentage=50 \
            --pulsate \
            --title="Validating DBCS at: $DBIPADDR" \
            --width=500 \
            --text="Checking connectivity..." \
            --auto-close

  # Get the status of the SSH command from the command status list.
  RETVAL=${PIPESTATUS[0]}

  # If we got 0 for the SSH then we can get to the image.
  [ $RETVAL -eq 0 ] && gotValidIP=1

  errorMsg="** Invalid IP address: $DBIPADDR\n\nPlease try again.\n\n"
done

errorMsg=""
gotValidIP=0

while [ $gotValidIP -eq 0 ]; do
  JCSIPADDR=$(zenity --entry \
                  --title="JCS IP Address" \
                  --text="${errorMsg}Enter the IP address found in the Java Cloud Service Console for your WLS Admin Server ." \
                  #--entry-text="JCS IP Address"
             )

  # Check if the user canceled.
  [ $? -ne 0 ] && exit

  # Check to see if we can SSH to the cloud using the IP and private key.
  (ssh -o "StrictHostKeyChecking no" -o ConnectTimeout=45 -i $OPC_PATH/oldkey/labkey opc@${JCSIPADDR} echo "success") | \
     zenity --progress \
            --percentage=50 \
            --pulsate \
            --title="Validating JCS at: $JCSIPADDR" \
            --width=500 \
            --text="Checking connectivity..." \
            --auto-close

  # Get the status of the SSH command from the command status list.
  RETVAL=${PIPESTATUS[0]}

  # If we got 0 for the SSH then we can get to the image.
  [ $RETVAL -eq 0 ] && gotValidIP=1

  errorMsg="** Invalid IP address: $JCSIPADDR\n\nPlease try again.\n\n"
done

errorMsg=""
gotValidIP=0

while [ $gotValidIP -eq 0 ]; do
  LBIPADDR=$(zenity --entry \
                  --title="LB IP Address" \
                  --text="${errorMsg}Enter the IP address found on the Java Cloud Service Console for your Load Balancer" \
                  #--entry-text="Load Balancer IP Address"
            )

  # Check if the user canceled.
  [ $? -ne 0 ] && exit

  # Check to see if we can SSH to the cloud using the IP and private key.
  (ssh -o "StrictHostKeyChecking no" -o ConnectTimeout=45 -i $OPC_PATH/oldkey/labkey opc@${LBIPADDR} echo "success") | \
     zenity --progress \
            --percentage=50 \
            --pulsate \
            --title="Validating Load Balancer at: $LBIPADDR" \
            --width=500 \
            --text="Checking connectivity..." \
            --auto-close

  # Get the status of the SSH command from the command status list.
  RETVAL=${PIPESTATUS[0]}

  # If we got 0 for the SSH then we can get to the image.
  [ $RETVAL -eq 0 ] && gotValidIP=1

  errorMsg="** Invalid IP address: $LBIPADDR\n\nPlease try again.\n\n"
done

# If a redo delete the ssh config file
  #rm -f $OPC_PATH/myssh

# Now create the "myssh" file with the IPs substituted
  #DBCS
  sed s/REPLACE_WITH_DBCS_IP/$DBIPADDR/ < $OPC_PATH/config-cloud > $OPC_PATH/tmp1
  #JCS
  sed s/REPLACE_WITH_JCS_IP/$JCSIPADDR/ < $OPC_PATH/tmp1 > $OPC_PATH/tmp2
  #LB
  sed s/REPLACE_WITH_LB_IP/$LBIPADDR/ < $OPC_PATH/tmp2 > $OPC_PATH/myssh

# Clean up files
  rm -f $OPC_PATH/tmp1 $OPC_PATH/tmp2 $OPC_PATH/ssh.err $OPC_PATH/ssh.out

# Create Tunnels
  sudo ssh -t -t -F myssh AlphaDBCS >ssh.out 2>ssh.err < /dev/null &
  sudo ssh -t -t -F myssh AlphaJCS >>ssh.out 2>>ssh.err < /dev/null &
  sudo ssh -t -t -F myssh AlphaLB >>ssh.out 2>>ssh.err < /dev/null &

zenity --info \
       --text="Tunnels have been successfully created on...\n
               DBCS:  $DBIPADDR\n
               JCS:  $JCSIPADDR\n
               LB:  $LBIPADDR\n\n
   DO NOT CLOSE THE TERMINAL WINDOW"
