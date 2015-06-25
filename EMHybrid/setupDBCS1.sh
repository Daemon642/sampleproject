#!/bin/bash
#
mkdir /u01/app/oracle/agent
sqlplus / as sysdba @/home/oracle/unlockDBSNMP.sql
