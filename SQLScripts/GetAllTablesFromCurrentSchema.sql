-- Written for DerbyDB 10.12.1.1
select t.tablename  
     from sys.systables t, sys.sysschemas s  
     where t.schemaid = s.schemaid 
          and t.tabletype = 'T'
          and s.schemaname = CURRENT SCHEMA
     order by s.schemaname, t.tablename;