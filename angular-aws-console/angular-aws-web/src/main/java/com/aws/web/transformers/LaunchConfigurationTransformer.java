package com.aws.web.transformers;

import org.springframework.stereotype.Component;

import com.amazonaws.services.autoscaling.model.LaunchConfiguration;
import com.aws.model.AwsLaunchConfiguration;

/**
 * User: doktoric
 */

@Component
public class LaunchConfigurationTransformer {

	public AwsLaunchConfiguration transform(LaunchConfiguration configuration) {
		AwsLaunchConfiguration awsLaunchConfiguration = new AwsLaunchConfiguration();
		awsLaunchConfiguration.setKeyName(configuration.getKeyName());
		awsLaunchConfiguration.setImageId(configuration.getImageId());
		awsLaunchConfiguration.setInstanceType(configuration.getInstanceType());
		awsLaunchConfiguration.setAssociatePublicIpAddress(configuration.getAssociatePublicIpAddress());
		awsLaunchConfiguration.setBlockDeviceMappings(configuration.getBlockDeviceMappings());
		awsLaunchConfiguration.setCreatedTime(configuration.getCreatedTime());
		awsLaunchConfiguration.setEbsOptimized(configuration.getEbsOptimized());
		awsLaunchConfiguration.setIamInstanceProfile(configuration.getIamInstanceProfile());
		awsLaunchConfiguration.setKernelId(configuration.getKernelId());
		awsLaunchConfiguration.setLaunchConfigurationName(configuration.getLaunchConfigurationName());
		awsLaunchConfiguration.setLaunchConfigurationARN(configuration.getLaunchConfigurationARN());
		awsLaunchConfiguration.setSecurityGroups(configuration.getSecurityGroups());
		awsLaunchConfiguration.setUserData(configuration.getUserData());
		awsLaunchConfiguration.setRamdiskId(configuration.getRamdiskId());
		awsLaunchConfiguration.setInstanceMonitoring(configuration.getInstanceMonitoring().getEnabled());
		awsLaunchConfiguration.setSpotPrice(configuration.getSpotPrice());
		awsLaunchConfiguration.setAssociatePublicIpAddress(configuration.getAssociatePublicIpAddress());

		return awsLaunchConfiguration;
	}
}
