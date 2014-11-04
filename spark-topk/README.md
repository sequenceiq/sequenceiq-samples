spark-topk
==========
Set YARN_CONF_DIR and SPARK_JAR (uber jar at hdfs)

Running on Spark: 
```
./bin/spark-submit --class com.sequenceiq.spark.TopKMain --master yarn-cluster --driver-memory 1g --executor-memory 1g --executor-cores 1 spark-topk-1.0.jar /data 10 cache
```

Running on Tez (with Spark):
```
./bin/spark-submit --class com.sequenceiq.spark.TopKMain --master execution-context:org.apache.spark.tez.TezJobExecutionContext --conf update-classpath=true spark-topk-1.0.jar /data 10
```
