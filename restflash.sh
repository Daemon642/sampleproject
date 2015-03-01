#!/bin/bash

sqlplus /nolog << EOF
connect / as sysdba
shutdown immediate
startup force mount
flashback database to restore point goldflash;
alter database open resetlogs;
drop restore point goldflash;
create restore point goldflash guarantee flashback database;
exit;
EOF
