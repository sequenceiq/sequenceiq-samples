package com.sequenceiq.samples.core.service.simple;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.sequenceiq.samples.core.credentials.SimpleAWSCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmazonEC2ClientFactory {

	@Autowired
	protected Region region;

	public AmazonEC2Client createAmazonEC2Client(AWSCredentials crendentials) {
		AmazonEC2Client amazonEC2Client = new AmazonEC2Client(new SimpleAWSCredentialsProvider(crendentials));
		amazonEC2Client.setRegion(region);
		return amazonEC2Client;
	}
}
