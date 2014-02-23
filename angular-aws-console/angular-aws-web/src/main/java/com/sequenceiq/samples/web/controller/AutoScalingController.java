package com.sequenceiq.samples.web.controller;

import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.AutoScalingInstanceDetails;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.autoscaling.model.Tag;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.sequenceiq.core.service.AutoScalingService;
import com.sequenceiq.samples.model.AwsLaunchConfiguration;
import com.sequenceiq.samples.model.UpdateAutoScalingGroupRequest;
import com.sequenceiq.web.transformers.LaunchConfigurationTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: doktoric
 */
@Controller
public class AutoScalingController {

	@Autowired
	private AwsCredentialsFactory awsCredentialsFactory;

	@Autowired
	private AutoScalingService awsec2Service;

	@Autowired
	private LaunchConfigurationTransformer transformer;

	@RequestMapping(value = "/autoscalinggroup", method = RequestMethod.GET)
	@ResponseBody
	public List<AutoScalingGroup> listAutoScalingGroups(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey) {
		return awsec2Service.describeAmazonAutoScalingGroups(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey));
	}

	@RequestMapping(value = "/autoscalinggroup", method = RequestMethod.POST)
	@ResponseBody
	public void createAutoScalingGroup(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey,
	        @RequestBody Map<String, String> body) {
		awsec2Service.createAmazonAutoScalingGroup(
                awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey),
		        body.get("autoScalingGroupName"),
                Integer.valueOf(body.get("maxSize")),
                Integer.valueOf(body.get("minSize")),
                Integer.valueOf(body.get("defaultCooldown")),
		        body.get("launchConfigurationName"),
                Collections.<String> emptyList(),
                Collections.<Tag> emptyList());
	}

	@RequestMapping(value = "/autoscalinggroup", method = RequestMethod.PUT)
	@ResponseBody
	public void updateAutoScalingGroup(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey,
	        @RequestBody UpdateAutoScalingGroupRequest updateAutoScalingGroupRequest) {
		if (updateAutoScalingGroupRequest.getCooldown() != null) {
			awsec2Service.updateAmazonAutoScalingGroupCoolDown(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey),
			        updateAutoScalingGroupRequest.getAutoScalingGroupName(), updateAutoScalingGroupRequest.getCooldown());
		}
		if (updateAutoScalingGroupRequest.getMinSize() != null) {
			awsec2Service.updateAmazonAutoScalingGroupMinSize(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey),
			        updateAutoScalingGroupRequest.getAutoScalingGroupName(), updateAutoScalingGroupRequest.getMinSize());
		}
		if (updateAutoScalingGroupRequest.getMaxSize() != null) {
			awsec2Service.updateAmazonAutoScalingGroupMaxSize(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey),
			        updateAutoScalingGroupRequest.getAutoScalingGroupName(), updateAutoScalingGroupRequest.getMaxSize());
		}
	}

	@RequestMapping(value = "/autoscalinginstance", method = RequestMethod.GET)
	@ResponseBody
	public List<AutoScalingInstanceDetails> listAutoScalingInstances(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey) {
		return awsec2Service.describeAmazonAutoScalingInstances(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey));
	}

	@RequestMapping(value = "/spotprice/list", method = RequestMethod.GET)
	@ResponseBody
	public List<SpotPrice> listSpotPriceHistory(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey) {
		return awsec2Service.describeSpotPriceHistory(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey));
	}

	@RequestMapping(value = "/launchconfig", method = RequestMethod.GET)
	@ResponseBody
	public List<AwsLaunchConfiguration> listLaunchConfigurations(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey) {
		List<AwsLaunchConfiguration> list = new ArrayList<>();
		for (LaunchConfiguration item : awsec2Service
		        .describeAmazonLaunchConfigurations(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey))) {
			list.add(transformer.transform(item));
		}
		return list;
	}

}
