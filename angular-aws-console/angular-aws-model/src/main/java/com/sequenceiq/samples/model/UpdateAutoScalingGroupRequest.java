package com.sequenceiq.samples.model;

public class UpdateAutoScalingGroupRequest {

	private String autoScalingGroupName;
	private Integer maxSize;
	private Integer minSize;
	private Integer cooldown;

	public String getAutoScalingGroupName() {
		return autoScalingGroupName;
	}
	public void setAutoScalingGroupName(String autoScalingGroupName) {
		this.autoScalingGroupName = autoScalingGroupName;
	}
	public Integer getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}
	public Integer getMinSize() {
		return minSize;
	}
	public void setMinSize(Integer minSize) {
		this.minSize = minSize;
	}
	public Integer getCooldown() {
		return cooldown;
	}
	public void setCooldown(Integer cooldown) {
		this.cooldown = cooldown;
	}

}
