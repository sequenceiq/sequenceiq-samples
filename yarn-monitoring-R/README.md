---
layout: post
title: "Job profiling with R"
date: 2014-04-22 10:35:04 +0100
comments: true
categories: [YARN, R, Profiling, Hadoop, Ambari]
author: Janos Matyas
published: false
---

Management of a large Hadoop cluster is not an easy task - however thanks to projects like [Apache Ambari](http://ambari.apache.org/) these tasks are getting easier. Ambari provides an intuitive, easy-to-use Hadoop management web UI backed by its REST API to provision, manage and monitor a Hadoop cluster. While Ambari helps us a lot to monitor a cluster (leverages [Ganglia](http://ganglia.sourceforge.net/) and [Nagios](http://www.nagios.org/)), many times we have to profile our MapReduce jobs as well.

At SequenceIQ in order to profile MapReduce jobs, understand (job)internal statistics and create usefull graphs many times we rely on [R](http://www.r-project.org/). The metrics are collected from Ambari and the [YARN History Server](http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/HistoryServerRest.html).

In this blog post we would like to explain and guide you through a simple process of collecting MapReduce job metrics, calculate different statistics and generate easy to understand charts.

The MapReduce application is the following:
* The input set of data is 12*1 GB size files. Each file containes the same line of 16 bytes (012345678998765 plus the new line character)
* The number of mappers running is 48, because the block size on HDFS is 256 MB and there are 12 files.
* We use TextInputFormat (line num, line content) pairs. The output of the mapper function is the same as the input `IdentityMapper`
* The number of reducers is 20.
* For simplicity we use `IdentityReducer` as the reducer function.
* We use a special partitioner called `LinePartitoner`. The partitioning is based on line numbers (the key) and it makes sure that each reducer gets the same amount of data (line number *modulo* reducer number).

## How to get the job results with R

The job id that we are analysing with R is job_1395530889914_0005 (*replace this with your job is*)

First we load the R functions:

`source("JobHistory.r")`

Then we extract/read the job from the HistoryServer. It is actually using the Rest API of HistoryServer, parsing the JSON output.

`job<-getJob("job_1395530889914_0005","node02:19888")`

The structure of the job follows the structure that is returned from the HistoryServer except that for example the parameters of all the tasks are converted into vectors so that can be easily handled in R.

A job is a list of `things`:

`> names(job)`

`[1] "job"      "counters" "tasks"    "attempts"`

The job$job contains some basic data

`> names(job$job)`

` [1] "startTime"                "finishTime"               "id"                       "name"                     "queue"`

` [6] "user"                     "state"                    "mapsTotal"                "mapsCompleted"            "reducesTotal"`

`[11] "reducesCompleted"         "uberized"                 "diagnostics"              "avgMapTime"               "avgReduceTime"`

`[16] "avgShuffleTime"           "avgMergeTime"             "failedReduceAttempts"     "killedReduceAttempts"     "successfulReduceAttempts"`

`[21] "failedMapAttempts"        "killedMapAttempts"        "successfulMapAttempts"`

The items below job$tasks are all vectors (if there are numeric) or non-named lists:

`> names(job$tasks)`

`[1] "startTime"         "finishTime"        "elapsedTime"       "progress"          "id"          "state"             "type"`

`[8] "successfulAttempt"`

This way we can easily calculate the mean of the `running` times of all the tasks like this:

`mean(job$tasks$finishTime-job$tasks$startTime)`

`[1] 147307`

The `attempts` list also contains vectors or lists of parameters. Only the successful attempts are in the attempt list.

`> names(job$attempts)`

` [1] "startTime"           "finishTime"          "elapsedTime"         "progress"            "id"                  "rack"`

` [7] "state"               "nodeHttpAddress"     "diagnostics"         "type"                "assignedContainerId" "shuffleFinishTime"`

`[13] "mergeFinishTime"     "elapsedShuffleTime"  "elapsedMergeTime"    "elapsedReduceTime"`

This way we can easily calculate the average `merge` times:

`> mean(job$attempts$mergeFinishTime-job$attempts$shuffleFinishTime)`

`[1] 4875.15`

Which is the same as:

`> mean(job$attempts$elapsedMergeTime)`

`[1] 4875.15`

## The R generated graphs
The are two types of graphs for the beginning

`plotTasksTimes(job)`

![](https://raw.githubusercontent.com/sequenceiq/sequenceiq-samples/master/yarn-monitoring-R/images/48_mappers_20_reducers_mr_task_times.png)

This graph shows start and finish times for each tasks (mappers and reducers as well). The tasks are sorted by their start times, so the reducers are on the top. There are 48 mappers and 20 reducers. The times are relative to the startTime of the first mapper in milliseconds(could show absolute values as well).

`plotActiveMRTasksNum(job)`

![](https://raw.githubusercontent.com/sequenceiq/sequenceiq-samples/master/yarn-monitoring-R/images/48_mappers_20_reducers_mr.png)

The graph above contains the number of active tasks at each time. It shows the mappers with green and also show the reduce phases as well. The shuffle part is orange, the merge part is magenta and the reduce part (reducer function is running) is blue. The times are relative to the startTime of the first mapper in milliseconds (could show absolute values as well).

`plotActiveReduceTasksNumDetailed(job, FALSE)`

![](https://raw.githubusercontent.com/sequenceiq/sequenceiq-samples/master/yarn-monitoring-R/images/48_mappers_20_reducers_reduce_phases.png)


This graph shows only the reduce part with the three phases: shuffle, merge, reduce. The times are absolute times (could show absolute values as well).

`plotTimeBoxes<-function(data, nodeNum=21, slotsPerNode=4)`

![](https://raw.githubusercontent.com/sequenceiq/sequenceiq-samples/master/yarn-monitoring-R/images/48_mappers_20_reducers_mr_by_nodes.png)


As you can see monitoring a MapReduce job through the HistoryServer it is extremely easy, and R is very usefull to apply different statistics and plot graphs. Also as you start playing with different setups the results can quickly be retrived, the graphs regenerated to analyze how different configuratins are affecting the execution time/behaviour of the jobs.

![](https://raw.githubusercontent.com/sequenceiq/sequenceiq-samples/master/yarn-monitoring-R/images/96_mappers_20_reducers_mr_by_nodes.png)


As always, the example project is available at our [GitHub](https://github.com/sequenceiq/yarn-monitoring) page. We are working on a `heuristic` queue scheduler for a better utilization of our cluster, and also to provide QoS on Hadoop - profiling and understanding the running MapReduce jobs and the job queues are essential for that. Also based on the charts broken down by nodes we can quickly identify servers with potential issues (slow I/O, memory, etc).

Follow us on [LinkedIn](https://www.linkedin.com/company/sequenceiq/) to read about how we progress with the sceduler and get early access, or feel free to contribute to our YARN monitoring project.
