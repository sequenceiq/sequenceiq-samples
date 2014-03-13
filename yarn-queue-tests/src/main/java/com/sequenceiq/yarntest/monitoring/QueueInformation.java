package com.sequenceiq.yarntest.monitoring;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueInformation {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QueueInformation.class);
	
	public void printQueueInfo(HttpClient client, ObjectMapper mapper, String schedulerResourceURL) {
		
		//http://sandbox.hortonworks.com:8088/ws/v1/cluster/scheduler in case of HDP
		GetMethod get = new GetMethod(schedulerResourceURL);
	    get.setRequestHeader("Accept", "application/json");
	    try {
	        int statusCode = client.executeMethod(get);

	        if (statusCode != HttpStatus.SC_OK) {
	        	LOGGER.error("Method failed: " + get.getStatusLine());
	        }
	        
			InputStream in = get.getResponseBodyAsStream();
			
			JsonNode jsonNode = mapper.readValue(in, JsonNode.class);
			ArrayNode queues = (ArrayNode) jsonNode.path("scheduler").path("schedulerInfo").path("queues").get("queue");
			for (int i = 0; i < queues.size(); i++) {
				JsonNode queueNode = queues.get(i);						
				LOGGER.info("queueName / usedCapacity / absoluteUsedCap / absoluteCapacity / absMaxCapacity: " + 
						queueNode.findValue("queueName") + " / " +
						queueNode.findValue("usedCapacity") + " / " + 
						queueNode.findValue("absoluteUsedCapacity") + " / " + 
						queueNode.findValue("absoluteCapacity") + " / " +
						queueNode.findValue("absoluteMaxCapacity"));
			}
		} catch (IOException e) {
			LOGGER.error("Exception occured", e);
		} finally {	        
			get.releaseConnection();
		}	      
	        
	}
	
}
