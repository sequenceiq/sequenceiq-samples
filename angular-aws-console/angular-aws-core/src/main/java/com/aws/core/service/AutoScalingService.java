package com.aws.core.service;

import java.util.Collection;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.AutoScalingInstanceDetails;
import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult;

/**
 * This service communicates with the AWS backend and can be used to describe,
 * create and update LaunchConfigurations, AutoScalingGroups and
 * ElasticLoadBalancers.
 */
public interface AutoScalingService {

	CreateLoadBalancerResult createLoadBalancer(AWSCredentials credentials, String name, String... zones);

	RegisterInstancesWithLoadBalancerResult registerInstancesToLoadBalancer(AWSCredentials credentials, String lbName);

	List<LaunchConfiguration> describeAmazonLaunchConfigurations(AWSCredentials credentials);

	List<AutoScalingGroup> describeAmazonAutoScalingGroups(AWSCredentials credentials);

	List<AutoScalingInstanceDetails> describeAmazonAutoScalingInstances(AWSCredentials credentials);

	void createAmazonAutoScalingGroup(AWSCredentials credentials, String name, int maxsize, int minsize, int coolDown, String configName,
	        Collection<String> loadBalancerNames, Collection<com.amazonaws.services.autoscaling.model.Tag> tags);

	void updateAmazonAutoScalingGroupCoolDown(AWSCredentials credentials, String name, int coolDown);

	void updateAmazonAutoScalingGroupMaxSize(AWSCredentials credentials, String name, int maxSize);

	void updateAmazonAutoScalingGroupMinSize(AWSCredentials credentials, String name, int minSize);

	List<SpotPrice> describeSpotPriceHistory(AWSCredentials credentials);

}
