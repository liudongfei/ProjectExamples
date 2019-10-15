#查看mysql的数据库
sqoop list-databases \
--connect jdbc:mysql://localhost:3306 \
--username root \
--password 123456

#查看mysql指定数据的表
sqoop list-tables \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456


#从mysql导入数据到hdfs
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--target-dir /user/liudongfei/sqoop/import/dept_manager

#hdfs先删除后导入，设置并发数量
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--delete-target-dir \
--target-dir /user/liudongfei/sqoop/import/dept_manager \
--num-mappers 1

#hdfs条件查询导入
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--delete-target-dir \
--target-dir /user/liudongfei/sqoop/import/dept_manager_where \
--where 'dept_no="d009"' \
--num-mappers 1

#hdfs条件查询导入
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--delete-target-dir \
--target-dir /user/liudongfei/sqoop/import/dept_manager_where \
--num-mappers 1 \
--query 'select emp_no, dept_no from dept_manager where $CONDITIONS'

#hdfs自定义分隔符导入
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--delete-target-dir \
--target-dir /user/liudongfei/sqoop/import/dept_manager \
--num-mappers 1 \
--fields-terminated-by '\t'

#hdfs追加方式导入
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--target-dir /user/liudongfei/sqoop/import/dept_manager \
--num-mappers 1 \
--append \
--fields-terminated-by '\t'

#另一种增量导入方式
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--target-dir /user/liudongfei/sqoop/import/dept_manager \
--num-mappers 1 \
--check-column emp_no \
--incremental append \
--last-value 110228 \
--fields-terminated-by '\t'


#导入到hdfs，以parquet方式存储(会出错，缺少native)
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--delete-target-dir \
--target-dir /user/liudongfei/sqoop/import/dept_manager_parquet \
--as-parquetfile \
--num-mappers 1

#以压缩的方式导入hdfs，配置压缩格式(会报错，缺少压缩库支持)
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--delete-target-dir \
--target-dir /user/liudongfei/sqoop/import/dept_manager_parquet \
--as-parquetfile \
--compress \
--compression-code org.apache.hadoop.io.compress.SnappyCodec \
--num-mappers 1

#使用direct方式直接从mysql导入数据（有些错误需要排查）
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--delete-target-dir \
--target-dir /user/liudongfei/sqoop/import/dept_manager \
--direct \
--num-mappers 1

#导入hive表中，自动创建表
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--hive-import \
--create-hive-table \
--hive-database employees \
--hive-table dept_manager \
--num-mappers 1

#追加方式导入hive表
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--hive-import \
--hive-database employees \
--hive-table dept_manager \
--check-column emp_no \
--incremental append \
--last-value 110228 \
--num-mappers 1

#覆盖方式导入hive
sqoop import \
--connect jdbc:mysql://localhost:3306/employees \
--username root \
--password 123456 \
--table dept_manager \
--hive-import \
--hive-database employees \
--hive-table dept_manager \
--num-mappers 1 \
--hive-overwrite


#数据导出到mysql
hdfs dfs -cp /user/hive/warehouse/db_hive.db/student/students.txt /user/liudongfei/sqoop/export/student/

sqoop export \
--connect jdbc:mysql://localhost:3306/test \
--username root \
--password 123456 \
--table student \
--export-dir /user/liudongfei/sqoop/export/student/students.txt \
--fields-terminated-by ',' \
--num-mappers 1


sqoop --options-file ./hdfs_import_dept_manager.sh --num-mappers 1


