#!/bin/bash
#
mv /home/opc/labkey.pub ~oracle/.ssh/.
chown oracle:oracle ~oracle/.ssh/labkey.pub
cat ~oracle/.ssh/authorized_keys ~oracle/.ssh/labkey.pub > ~oracle/.ssh/authorized_keys_tmp
chown oracle:oracle ~oracle/.ssh/authorized_keys_tmp
mv ~oracle/.ssh/authorized_keys_tmp ~oracle/.ssh/authorized_keys
rm ~oracle/.ssh/labkey.pub 
mkdir /u01/app/oracle/agent
chown oracle:oracle /u01/app/oracle/agent
cat /etc/sudoers /home/opc/oracleSudo.txt > /tmp/oracleSudo
cp /tmp/oracleSudo /etc/sudoers
chown root:root /etc/sudoers
chmod 440 /etc/sudoers
