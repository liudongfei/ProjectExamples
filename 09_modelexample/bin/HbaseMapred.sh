#使用MR从user表中读取数据插入到basic表中
HADOOP_CLASSPATH=`hbase mapredcp` yarn jar mavendemo-1.0-SNAPSHOT.jar com.liu.cdh.hbase.User2BasicMapReduce
#使用MR读取数据转换成HFile格式文件
HADOOP_CLASSPATH=`hbase mapredcp` yarn jar ~/cloudera/cdh5.10/hbase/lib/hbase-server-1.2.0-cdh5.10.0.jar importtsv -Dimporttsv.columns=HBASE_ROW_KEY,info:name,info:age,info:sex,info:address -Dimporttsv.bulk.output=/user/hbase/hfileoutput student /user/liudongfei/hbase/importtsv/student.tsv
#使用bulk load方式加载HFile文件到HBase表中
HADOOP_CLASSPATH=`hbase mapredcp` yarn jar ~/cloudera/cdh5.10/hbase/lib/hbase-server-1.2.0-cdh5.10.0.jar completebulkload /user/liudongfei/hbase/hfileoutput student