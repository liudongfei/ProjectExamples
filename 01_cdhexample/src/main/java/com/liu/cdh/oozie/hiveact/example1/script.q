
insert overwrite directory '${OUTPUT}'
row format delimited fields terminated by '\t'
select * from db_hive.student;

