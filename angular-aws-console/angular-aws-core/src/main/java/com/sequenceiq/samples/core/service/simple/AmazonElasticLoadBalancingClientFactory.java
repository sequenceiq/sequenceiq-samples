package com.sequenceiq.samples.core.service.simple;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.sequenceiq.samples.core.credentials.SimpleAWSCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmazonElasticLoadBalancingClientFactory {

	@Autowired
	private Region region;

	public AmazonElasticLoadBalancingClient createAmazonElasticLoadBalancingClient(AWSCredentials crendentials) {
		AmazonElasticLoadBalancingClient amazonEC2Client = new AmazonElasticLoadBalancingClient(new SimpleAWSCredentialsProvider(crendentials));
		amazonEC2Client.setRegion(region);
		return amazonEC2Client;
	}

}
