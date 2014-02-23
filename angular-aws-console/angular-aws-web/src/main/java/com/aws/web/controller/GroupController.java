package com.aws.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.aws.core.service.SecurityGroupService;

/**
 * User: doktoric
 */
@Controller
public class GroupController {

	@Autowired
	private AwsCredentialsFactory awsCredentialsFactory;

	@Autowired
	private SecurityGroupService awsec2Service;

	@RequestMapping(method = RequestMethod.GET, value = {"/group/create"})
	@ResponseBody
	public CreateSecurityGroupResult createSecurityGroup(@RequestParam("name") String name, @RequestParam("description") String description,
	        @RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey) {
		return awsec2Service.createSecurityGroup(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey), name, description);
	}

	@RequestMapping(method = RequestMethod.GET, value = {"/groups"})
	@ResponseBody
	public DescribeSecurityGroupsResult describeSecurityGroups(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey) {
		return awsec2Service.describeSecurityGroups(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey));
	}

	@RequestMapping(method = RequestMethod.GET, value = {"/groups/list"})
	@ResponseBody
	public List<SecurityGroup> describeSecurityGroupList(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey) {
		return awsec2Service.listSecurityGroups(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey));
	}
}
