package com.sequenceiq.samples.core.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.*;

import java.util.List;

/**
 * This service communicates with the AWS backend and can be used to describe,
 * run, stop and terminate AWS EC2 instances.
 */
public interface InstanceService {

	List<Reservation> describeInstances(AWSCredentials credentials);

	RunInstancesResult runInstances(AWSCredentials credentials, String amiId, String keyName, String securityGroup, String gist);

	StopInstancesResult stopInstances(AWSCredentials credentials, String... instanceId);

	TerminateInstancesResult terminateInstances(AWSCredentials credentials, String... instanceId);

    StartInstancesResult startInstances(AWSCredentials credentials, String... instanceId);
}
