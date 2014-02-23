package com.aws.core.service.simple;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.aws.core.service.SecurityGroupService;

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
