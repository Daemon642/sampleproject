#!/bin/sh
#
if [ $# -lt 1 ]
then
        echo "Usage: getport.sh <Weblogic Domain Name>"
        exit 1
fi
echo $*
for i in `ls /u01/Oracle/Middleware/FMW_12.1.2/mwaas_domains|grep $1`; do echo $i;awk '/listen-port/{i++}i==2' /u01/Oracle/Middleware/FMW_12.1.2/mwaas_domains/$i/config/config.xml|grep listen-port|cut -c 18-21;done
