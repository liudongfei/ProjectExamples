spark-submit \
--class com.liu.model.graphf.bfsdemo \
--master yarn \
--deploy-mode client \
--executor-memory 1G \
--executor-cores 2 \
--num-executors 3 \
--conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
--conf spark.memory.fraction=0.4 \
--conf spark.default.parallelism=20 \
--packages graphframes:graphframes:0.5.0-spark2.1-s_2.11 \
mavendemo-1.0-SNAPSHOT.jar
