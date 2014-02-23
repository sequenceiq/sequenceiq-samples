package com.sequenceiq.samples.core.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.KeyPairInfo;

import java.util.List;

/**
 * This service communicates with the AWS backend and can be used to describe
 * and create KeyPairs.
 */
public interface KeyPairService {

	CreateKeyPairResult createKeyPair(AWSCredentials credentials, String keyName);

	String describeKeyPairFingerPrint(AWSCredentials credentials, String keyName);

	List<KeyPairInfo> listKeyPairs(AWSCredentials credentials);

}
