package com.sequenceiq.samples.core.service.simple;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.sequenceiq.samples.core.credentials.SimpleAWSCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmazonAutoScalingClientFactory {

	@Autowired
	protected Region region;

	public AmazonAutoScalingClient createAmazonAutoScalingClient(AWSCredentials crendentials) {
		AmazonAutoScalingClient amazonEC2Client = new AmazonAutoScalingClient(new SimpleAWSCredentialsProvider(crendentials));
		amazonEC2Client.setRegion(region);
		return amazonEC2Client;
	}

}
