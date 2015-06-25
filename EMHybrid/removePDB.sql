COLUMN PDB_NAME FORMAT A15
SELECT PDB_ID, PDB_NAME, STATUS FROM DBA_PDBS ORDER BY PDB_ID;
alter pluggable database sales_dev2 close immediate;
drop pluggable database sales_dev2 including datafiles;
SELECT PDB_ID, PDB_NAME, STATUS FROM DBA_PDBS ORDER BY PDB_ID;
exit;
