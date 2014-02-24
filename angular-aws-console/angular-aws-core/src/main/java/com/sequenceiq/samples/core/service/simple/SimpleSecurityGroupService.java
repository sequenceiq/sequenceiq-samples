package com.sequenceiq.samples.core.service.simple;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.sequenceiq.samples.core.service.SecurityGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimpleSecurityGroupService implements SecurityGroupService {

	@Autowired
	private AmazonEC2ClientFactory amazonEC2ClientFactory;

	private CreateSecurityGroupResult createSecurityGroup(AmazonEC2Client client, String groupName, String description) {
		CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest().withGroupName(groupName).withDescription(description);
		CreateSecurityGroupResult createSecurityGroupResult = client.createSecurityGroup(createSecurityGroupRequest);
		return createSecurityGroupResult;
	}

	@Override
	public CreateSecurityGroupResult createSecurityGroup(AWSCredentials credentials, String groupName, String description) {
		return createSecurityGroup(amazonEC2ClientFactory.createAmazonEC2Client(credentials), groupName, description);
	}

	private DescribeSecurityGroupsResult describeSecurityGroups(AmazonEC2Client client) {
		DescribeSecurityGroupsRequest describeSecurityGroupsRequest = new DescribeSecurityGroupsRequest();
		DescribeSecurityGroupsResult describeSecurityGroupsResult = client.describeSecurityGroups(describeSecurityGroupsRequest);
		return describeSecurityGroupsResult;
	}

	@Override
	public DescribeSecurityGroupsResult describeSecurityGroups(AWSCredentials credentials) {
		return describeSecurityGroups(amazonEC2ClientFactory.createAmazonEC2Client(credentials));
	}

	@Override
	public List<SecurityGroup> listSecurityGroups(AWSCredentials credentials) {
		return describeSecurityGroups(amazonEC2ClientFactory.createAmazonEC2Client(credentials)).getSecurityGroups();
	}
}
