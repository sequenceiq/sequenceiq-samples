package com.sequenceiq.samples.model;

import com.amazonaws.services.autoscaling.model.BlockDeviceMapping;

import java.util.Date;
import java.util.List;

/**
 * User: doktoric
 */
public class AwsLaunchConfiguration {
	private String launchConfigurationName;
	private String launchConfigurationARN;
	private String imageId;
	private String keyName;
	private List<String> securityGroups;
	private String userData;
	private String instanceType;
	private String kernelId;
	private String ramdiskId;
	private List<BlockDeviceMapping> blockDeviceMappings;
	private Boolean instanceMonitoring;
	private String spotPrice;
	private String iamInstanceProfile;
	private Date createdTime;
	private Boolean ebsOptimized;
	private Boolean associatePublicIpAddress;

	public AwsLaunchConfiguration() {
	}

	public String getLaunchConfigurationName() {
		return launchConfigurationName;
	}

	public void setLaunchConfigurationName(String launchConfigurationName) {
		this.launchConfigurationName = launchConfigurationName;
	}

	public String getLaunchConfigurationARN() {
		return launchConfigurationARN;
	}

	public void setLaunchConfigurationARN(String launchConfigurationARN) {
		this.launchConfigurationARN = launchConfigurationARN;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public List<String> getSecurityGroups() {
		return securityGroups;
	}

	public void setSecurityGroups(List<String> securityGroups) {
		this.securityGroups = securityGroups;
	}

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	public String getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(String instanceType) {
		this.instanceType = instanceType;
	}

	public String getKernelId() {
		return kernelId;
	}

	public void setKernelId(String kernelId) {
		this.kernelId = kernelId;
	}

	public String getRamdiskId() {
		return ramdiskId;
	}

	public void setRamdiskId(String ramdiskId) {
		this.ramdiskId = ramdiskId;
	}

	public List<BlockDeviceMapping> getBlockDeviceMappings() {
		return blockDeviceMappings;
	}

	public void setBlockDeviceMappings(List<BlockDeviceMapping> blockDeviceMappings) {
		this.blockDeviceMappings = blockDeviceMappings;
	}

	public Boolean getInstanceMonitoring() {
		return instanceMonitoring;
	}

	public void setInstanceMonitoring(Boolean instanceMonitoring) {
		this.instanceMonitoring = instanceMonitoring;
	}

	public String getSpotPrice() {
		return spotPrice;
	}

	public void setSpotPrice(String spotPrice) {
		this.spotPrice = spotPrice;
	}

	public String getIamInstanceProfile() {
		return iamInstanceProfile;
	}

	public void setIamInstanceProfile(String iamInstanceProfile) {
		this.iamInstanceProfile = iamInstanceProfile;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Boolean getEbsOptimized() {
		return ebsOptimized;
	}

	public void setEbsOptimized(Boolean ebsOptimized) {
		this.ebsOptimized = ebsOptimized;
	}

	public Boolean getAssociatePublicIpAddress() {
		return associatePublicIpAddress;
	}

	public void setAssociatePublicIpAddress(Boolean associatePublicIpAddress) {
		this.associatePublicIpAddress = associatePublicIpAddress;
	}
}
