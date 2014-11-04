spark-topk
==========
Set YARN_CONF_DIR and SPARK_JAR (uber jar at hdfs)

Running the exsmple: 
```
./bin/spark-submit --class com.sequenceiq.spark.TopKMain --master yarn-cluster --driver-memory 1g --executor-memory 1g --executor-cores 1 spark-topk-1.0.jar /input /output 10 cache
```
