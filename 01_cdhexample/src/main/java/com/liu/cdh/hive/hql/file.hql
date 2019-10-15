create database if not exists employees;
create table if not exists employees.employees(
emp_no int,
birth_date string,
first_name string,
last_name string,
gender string,
hire_date string
)
row format delimited fields terminated by ','
stored as textfile;

create table if not exists employees.dept_emp(
emp_no int,
dept_no int,
from_date string,
to_date string
)
row format delimited fields terminated by ','
stored as textfile;


create table if not exists employees.departments(
dept_no int,
dept_name string
)
row format delimited fields terminated by ','
stored as textfile;

create table if not exists employees.salaries(
emp_no int,
salary int,
frome_date string,
to_date string
)
row format delimited fields terminated by ','
stored as textfile;

select s.*, e.* from salaries s join employees e on s.emp_no=e.emp_no limit 10;
select s.*, e.* from salaries s left join employees e on s.emp_no=e.emp_no limit 10;
select s.*, e.* from salaries s right join employees e on s.emp_no=e.emp_no limit 10;
select s.*, e.* from salaries s full join employees e on s.emp_no=e.emp_no limit 10;

========================================================================================================================
create database if not exists db_hive;
show databases like 'db_*';
desc database db_hive;
drop table if exists db_hive.student;

create table if not exists db_hive.student(
id int comment 'student id',
name String comment 'student name',
age int comment 'student age'
)
comment 'student table info'
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
STORED AS TEXTFILE;
load data local inpath '/Users/liudongfei/Myworkspace/workspace/java_workspace/mavendemo/data/students.txt'
overwrite into table db_hive.student;

create table if not exists db_hive.student_sub1_asselect as select id, name from db_hive.student;

create table if not exists db_hive.student_sub2_like like db_hive.student;
insert overwrite into db_hive.student_sub2_like select * from db_hive.student;
truncate table db_hive.student_sub2_like;


create external table if not exists db_hive.student_ext like db_hive.student
location '/user/liudongfei/hive/input/student';
dfs -put /Users/liudongfei/Myworkspace/workspace/java_workspace/mavendemo/data/students.txt /user/liudongfei/hive/input/student;

drop table if exists db_hive.student_part;
create table if not exists db_hive.student_part(
id int comment 'student id',
name string comment 'student name',
age int comment 'student age'
)
partitioned by (cla string)
row format delimited fields terminated by ','
stored as textfile;
load data local inpath '/Users/liudongfei/Myworkspace/workspace/java_workspace/mavendemo/data/students.txt'
into table db_hive.student_part partition(cla='02');

dfs -mkdir -p /user/hive/warehouse/db_hive.db/student_part/cla=03;
dfs -put /Users/liudongfei/Myworkspace/workspace/java_workspace/mavendemo/data/students.txt /user/hive/warehouse/db_hive.db/student_part/cla=03;

msck repair table student_part;
alter table student_part add partition(cla='03')

show partitions student_part;

insert overwrite directory '/user/liudongfei/hive/output/student'
row format delimited fields terminated by '\t'
select * from db_hive.student;

========================================================================================================================
show functions;
desc function extended count;
select count(1) from student;
desc function extended avg;
select avg(age) from student;
desc function extended max;
select max(age) from student;

select cla, avg(age) avg_age from student_part group by cla;
select cla, max(age) max_age from student_part group by cla having max_age > 15;

export table db_hive.student to '/user/liudongfei/hive/output/student_export';
import table db_hive.student_sub3_imp from '/user/liudongfei/hive/output/student_export';
select * from student_sub3_imp;

select * from student order by id desc;
set mapreduce.job.reduces=3;
insert overwrite directory '/user/liudongfei/hive/output/student1'
select * from db_hive.student sort by id desc;

select * from db_hive.student_part distribute by cla sort by id desc;

select * from db_hive.student_part cluster by id desc;

drop database if exists db_hive cascade;

============================================================================

create table if not exists db_hive.student_orc(
id int comment 'student id',
name String comment 'student name',
age int comment 'student age'
)
row format delimited fields terminated by ','
stored as orc;
insert into db_hive.student_orc select * from db_hive.student;

create table if not exists db_hive.student_parquet(
id int comment 'student id',
name String comment 'student name',
age int comment 'student age'
)
row format delimited fields terminated by ','
stored as parquet;
insert into table db_hive.student_parquet select * from db_hive.student;

--没有编译cdh没有snappy的压缩支持，加载数据会报错
create table if not exists db_hive.student_orc_snappy(
id int comment 'student id',
name String comment 'student name',
age int comment 'student age'
)
row format delimited fields terminated by ','
stored as orc tblproperties("orc.compress"="snappy");
insert into table db_hive.student_orc_snappy select * from db_hive.student;

=============================================================================================
日志格式
    remote_addr
    客户端的ip地址（如果中间有代理服务器，这里显示的IP就是代理服务器的ip）
    remote_user
    用于记录远程客户端的用户名称（一般为"-"）
    time_local
    用于记录访问时间和时区
    request
    用于记录请求的url以及请求方法
    status
    响应状态码
    body_bytes_sent
    给客户端发送的文件主题内容大小
    request_body
    为post的数据
    http_referer
    可以记录用户从哪个连接访问过来的
    http_user_agent
    用户缩使用的代理（一般为浏览器）
    http_x_forwarded_for
    可以记录客户端ip，通过代理服务器来记录客户端的IP地址
    host
    服务器主机名称

create database if not exists log_analysis;
create table if not exists log_analysis.log_src(
remote_addr string,
remote_user string,
time_local string,
request string,
status string,
body_bytes_sent string,
request_body string,
http_referer string,
http_user_agent string,
http_x_forward_for string,
host string
)
row format serde 'org.apache.hadoop.hive.serde2.RegexSerDe'
with serdeproperties(
"input.regex" = "(\"[^ ]*\") (\"-|[^ ]*\") (\"[^\]]*\") (\"[^\"]*\") (\"[0-9]*\") (\"[0-9]*\") (-|[^ ]*) (\"[^ ]*\") (\"[^\"]*\") (-|[^ ]*) (\"[^ ]*\")"
)
stored as textfile;

load data local inpath '/Users/liudongfei/Myworkspace/workspace/java_workspace/mavendemo/data/moodle.ibeifeng.access.log'
overwrite into table log_analysis.log_src;

drop table if exists log_analysis.log_comm;
create table if not exists log_analysis.log_comm(
remote_addr string,
time_local string,
request string,
http_referer string
)
row format delimited fields terminated by '\t'
stored as orc;

insert overwrite table log_analysis.log_comm select remote_addr,time_local,request,http_referer from log_analysis.log_src;
--引号处理
add jar /Users/liudongfei/Myworkspace/workspace/java_workspace/mavendemo/target/mavendemo-1.0-SNAPSHOT.jar;
create temporary function my_rmquotes as "com.liu.cdh.hive.RemoveQuotesUDF";

insert overwrite table log_analysis.log_comm select my_rmquotes(remote_addr),my_rmquotes(time_local),my_rmquotes(request),my_rmquotes(http_referer) from log_analysis.log_src;
--时间格式处理
add jar /Users/liudongfei/Myworkspace/workspace/java_workspace/mavendemo/target/mavendemo-1.0-SNAPSHOT.jar;
create temporary function my_datetransform as "com.liu.cdh.hive.DateTransformUDF";

insert overwrite table log_analysis.log_comm select my_rmquotes(remote_addr),my_datetransform(my_rmquotes(time_local)),my_rmquotes(request),my_rmquotes(http_referer) from log_analysis.log_src;
------------------------------------------------------------------------
--登录时间段分析
select t.hour, count(1) cnt from
(select substring(time_local,9,2) hour from log_comm) t
group by t.hour order by cnt desc limit 10;
--ip地址归属分析
select t.prex_ip, count(1) cnt from
(select substring(remote_addr,1,7) prex_ip from log_comm) t
group by t.prex_ip order by cnt desc limit 10;

--python脚本分析
--字符串截取
add file /Users/liudongfei/Myworkspace/workspace/java_workspace/mavendemo/src/main/java/com/liu/hive/weekday_mapper.py;
select
    transform (remote_addr, time_local, request, http_referer)
    using 'python weekday_mapper.py'
    as (remote_addr, time_local, weekday, http_referer)
from log_comm limit 10;

--与Hbase集成

CREATE TABLE hbase_table_1(key int, value string)
STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'
WITH SERDEPROPERTIES ("hbase.columns.mapping" = ":key,cf1:val")
TBLPROPERTIES ("hbase.table.name" = "xyz", "hbase.mapred.output.outputtable" = "xyz");