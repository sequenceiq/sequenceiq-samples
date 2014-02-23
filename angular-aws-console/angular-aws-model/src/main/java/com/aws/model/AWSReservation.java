package com.aws.model;

import java.util.List;

import com.amazonaws.services.ec2.model.GroupIdentifier;

public class AWSReservation {
	private String reservationId;
	private String ownerId;
	private String requesterId;
	private List<GroupIdentifier> groups;
	private List<String> groupNames;
	private List<AwsSimpleInstance> instances;

	public AWSReservation(String ownerId, String reservationId, String requesterId) {
		this.reservationId = reservationId;
		this.ownerId = ownerId;
		this.requesterId = requesterId;
	}

	public void setRequesterId(String requesterId) {
		this.requesterId = requesterId;
	}

	public String getRequesterId() {

		return requesterId;
	}

	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public void setGroups(List<GroupIdentifier> groups) {
		this.groups = groups;
	}

	public void setGroupNames(List<String> groupNames) {
		this.groupNames = groupNames;
	}

	public void setInstances(List<AwsSimpleInstance> instances) {
		this.instances = instances;
	}

	public List<GroupIdentifier> getGroups() {
		return groups;
	}

	public List<String> getGroupNames() {
		return groupNames;
	}

	public List<AwsSimpleInstance> getInstances() {
		return instances;
	}

}
