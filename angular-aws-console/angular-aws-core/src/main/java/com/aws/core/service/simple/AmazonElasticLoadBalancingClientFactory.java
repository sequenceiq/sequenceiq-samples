package com.aws.core.service.simple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.aws.core.credentials.SimpleAWSCredentialsProvider;

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
