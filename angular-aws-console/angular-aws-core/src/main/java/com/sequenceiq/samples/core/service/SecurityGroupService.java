package com.sequenceiq.samples.core.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;

import java.util.List;

/**
 * This service communicates with the AWS backend and can be used to describe
 * and create SecurityGroups.
 */
public interface SecurityGroupService {

	CreateSecurityGroupResult createSecurityGroup(AWSCredentials credentials, String groupName, String description);

	DescribeSecurityGroupsResult describeSecurityGroups(AWSCredentials credentials);

	List<SecurityGroup> listSecurityGroups(AWSCredentials credentials);

}
