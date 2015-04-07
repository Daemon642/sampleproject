set termout off
set lines 500
set heading off
set pagesize 0

spool /tmp/enable.sql

select 'alter table ' || table_name || ' disable constraint ' || constraint_name || ';' from user_constraints;

spool off

@/tmp/enable.sql
