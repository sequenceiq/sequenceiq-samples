package com.sequenceiq.yarntest.client;

import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.YARNRunner;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.yarntest.monitoring.MRJobStatus;
import com.sequenceiq.yarntest.monitoring.QueueInformation;
import com.sequenceiq.yarntest.mr.QuasiMonteCarlo;
import com.sequenceiq.yarntest.queue.QueueOrchestrator;



public class JobClient {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(JobClient.class);
	
	public static void main(String[] args) {
		try {
			JobClient jobClient = new JobClient();
			QueueOrchestrator qo = new QueueOrchestrator();
			HttpClient client = new HttpClient();
			ObjectMapper mapper = new ObjectMapper();
			String schedulerURL = "http://sandbox.hortonworks.com:8088/ws/v1/cluster/scheduler";
			
			LOGGER.info("Starting YARN Capacity Queue Test");
			LOGGER.info("yarn.scheduler.capacity.root.queues = default,highPriority,lowPriority");
			LOGGER.info("yarn.scheduler.capacity.root.highPriority.capacity = 70");
			LOGGER.info("yarn.scheduler.capacity.root.lowPriority.capacity = 20");
			LOGGER.info("yarn.scheduler.capacity.root.highPriority.default = 10");
			LOGGER.info("Scheduler URL: ", schedulerURL);
			MRJobStatus mrJobStatus = new MRJobStatus();
			QueueInformation queueInformation = new QueueInformation();
			
			//Create low priority setup - low priority root queue (capacity-scheduler.xml)
			Path tempDirLow = jobClient.createTempDir("lowPriority");
			//Create high priority setup - high priority root queue (capacity-scheduler.xml)
			Path tempDirHigh = jobClient.createTempDir("highPriority");
			
			String lowPriorityQueue = new String("lowPriority");
			String highPriorityQueue = new String("highPriority");

			// create YarnRunner to use for job status listing
			Configuration lowPriorityConf = qo.getConfiguration(lowPriorityQueue);
			// doesn't matter the configuration as we use YarnRunner only to retrieve job status info 
	        YARNRunner yarnRunner = new YARNRunner(lowPriorityConf);
			
	     	Configuration highPriorityConf = qo.getConfiguration(lowPriorityQueue);

			
			JobID lowPriorityJobID = qo.submitJobsIntoQueues(lowPriorityQueue, tempDirLow);
			JobID highPriorityJobID = qo.submitJobsIntoQueues(highPriorityQueue, tempDirHigh);
			
			
			// list low priority job status
			JobStatus lowPriorityJobStatus = mrJobStatus.printJobStatus(yarnRunner, lowPriorityJobID);
						
			// list high priority job status
			JobStatus highPriorityJobStatus = mrJobStatus.printJobStatus(yarnRunner, highPriorityJobID);
					
			// list job statuses & queue information until job(s) are completed
			for(;!lowPriorityJobStatus.isJobComplete();) {
				highPriorityJobStatus = mrJobStatus.printJobStatus(yarnRunner, highPriorityJobID);								
				lowPriorityJobStatus = mrJobStatus.printJobStatus(yarnRunner, lowPriorityJobID);				
				
				queueInformation.printQueueInfo(client, mapper, schedulerURL);
				Thread.sleep(1000);
			}
		
		} catch (Exception e) {
			LOGGER.error("Exception occured", e);
		}
	}

	
	private Path createTempDir(String priority) {
		long now = System.currentTimeMillis();
	    int rand = new Random().nextInt(Integer.MAX_VALUE);
	    Path tempDir = new Path(QuasiMonteCarlo.TMP_DIR_PREFIX + "_" + now + "_" + rand+ "_" + priority);
	    LOGGER.info("HDFS temp dir for" + priority.toUpperCase() + "is :", tempDir);
		return tempDir;
	}
}