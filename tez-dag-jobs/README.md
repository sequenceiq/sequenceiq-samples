Using Mahout with Tez
======================

In our introductory blog post about Tez we have submitted a Mahout job to YARN using both the classic MapReduce and the Tez Application Masters.
The Mahout job we used for this experiment was a classification algorithm, the training of a Partial Decision Forest.
There is a great example of using this classification algorithm on the [Apache Mahout page](https://mahout.apache.org/users/stuff/partial-implementation.html).
We needed a larger dataset than the one used in the example above. Because of simplicity we multiplied the number of rows in this dataset by 10 (this isn't useful when trying to build a real world classification model, but for now we were not interested in the outcome of the training just in the difference between the Tez and classic MR runtimes).
You can download this extended dataset [here](https://s3-eu-west-1.amazonaws.com/seq-tez/KDDTrain%2B_long.arff)

### Trying this example
To try this example you need to have a Tez runtime first. For your convenience we have put together a [Tez-Docker](https://github.com/sequenceiq/tez-docker) image where the Tez runtime is already configured.

When your Tez runtime is ready, put the downloaded dataset in HDFS:
```
hadoop fs -mkdir testdata
hadoop fs -put KDDTrain+_long.arff testdata
```

Mahout is also needed to run the commands below, we used the latest 0.8 release provided by Hortonworks.
To run the training on the dataset, a file descriptor must be generated first:
```
hadoop jar mahout-core-0.8.0.2.0.10.0-1-job.jar org.apache.mahout.classifier.df.tools.Describe -p testdata/KDDTrain+_long.arff -f testdata/KDDTrain+.info -d N 3 C 2 N C 4 N C 8 N 2 C 19 N L
```

When the descriptor is ready the classification training can be run on the dataset. This class contains a single MapReduce job that will be submitted to the Tez ApplicationMaster.
The true power of Tez shows up when more complicated DAGs (or at least MRR jobs) are created, but in this example we just wanted to show that even a single MapReduce job runs much faster on Tez without rewriting a single line of code.
```
hadoop jar mahout-examples-0.8.0.2.0.10.0-1-job.jar org.apache.mahout.classifier.df.mapreduce.BuildForest -Dmapred.max.split.size=1874231 -d testdata/KDDTrain+_long.arff -ds testdata/KDDTrain+.info -sl 5 -t 100 -o nsl-forest
```

If you would like to evaluate the model produced by the training, follow the instructions on the Apache Mahout page.
