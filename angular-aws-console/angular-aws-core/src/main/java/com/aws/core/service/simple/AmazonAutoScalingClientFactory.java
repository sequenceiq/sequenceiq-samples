package com.aws.core.service.simple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.aws.core.credentials.SimpleAWSCredentialsProvider;

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
