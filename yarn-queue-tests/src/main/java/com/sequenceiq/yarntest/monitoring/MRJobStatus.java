package com.sequenceiq.yarntest.monitoring;

import java.io.IOException;

import org.apache.hadoop.mapred.YARNRunner;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.TaskReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MRJobStatus {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MRJobStatus.class);
	
	public JobStatus printJobStatus(YARNRunner yarnRunner, JobID jobID) throws IOException, InterruptedException {
		JobStatus jobStatus;
		jobStatus = yarnRunner.getJobStatus(jobID);
		
		// print overall job M/R progresses
		LOGGER.info("\nJob " + jobStatus.getJobName() + "in queue (" + jobStatus.getQueue() + ")" + " progress M/R: " + jobStatus.getMapProgress() + "/" + jobStatus.getReduceProgress());
		LOGGER.info("Tracking URL : " + jobStatus.getTrackingUrl());
		LOGGER.info("Reserved memory : " + jobStatus.getReservedMem() + ", used memory : "+ jobStatus.getUsedMem() + " and used slots : "+ jobStatus.getNumUsedSlots());
		
		// list map & reduce tasks statuses and progress		
		TaskReport[] reports = yarnRunner.getTaskReports(jobID, TaskType.MAP);
		for (int i = 0; i < reports.length; i++) {
			LOGGER.info("MAP: Status " + reports[i].getCurrentStatus() + " with task ID " + reports[i].getTaskID() + ", and progress " + reports[i].getProgress()); 
		}
		reports = yarnRunner.getTaskReports(jobID, TaskType.REDUCE);
		for (int i = 0; i < reports.length; i++) {
			LOGGER.info("REDUCE: " + reports[i].getCurrentStatus() + " with task ID " + reports[i].getTaskID() + ", and progress " + reports[i].getProgress()); 
		}
		return jobStatus;
	}	
}



