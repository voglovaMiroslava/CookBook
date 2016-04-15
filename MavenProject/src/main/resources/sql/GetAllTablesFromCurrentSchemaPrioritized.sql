select tablename from (select * from (select t.TABLENAME tablename, 1 priority
     from sys.systables t, sys.sysschemas s, sys.SYSCONSTRAINTS c  
     where t.schemaid = s.schemaid
          and t.TABLEID = c.tableid
          and t.tabletype = 'T'
          and s.schemaname = CURRENT SCHEMA
          and c."TYPE" = 'F'
     group by t.TABLENAME
     order by count(c."TYPE")) q1
UNION 
(select * from (
    select t.tablename tablename, 2 priority  
     from sys.systables t, sys.sysschemas s  
     where t.schemaid = s.schemaid 
          and t.tabletype = 'T'
          and s.schemaname = CURRENT SCHEMA
     order by t.tablename
) q2) ORDER BY priority) q3 GROUP BY TABLENAME ORDER BY sum(priority) desc;