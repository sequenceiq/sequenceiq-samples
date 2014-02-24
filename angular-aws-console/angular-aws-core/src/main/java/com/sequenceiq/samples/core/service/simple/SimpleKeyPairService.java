package com.sequenceiq.samples.core.service.simple;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.sequenceiq.samples.core.service.KeyPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimpleKeyPairService implements KeyPairService {

	@Autowired
	private AmazonEC2ClientFactory amazonEC2ClientFactory;

	public CreateKeyPairResult createKeyPair(AmazonEC2Client client, String keyName) {
		CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest().withKeyName(keyName);
		CreateKeyPairResult createKeyPairResult = client.createKeyPair(createKeyPairRequest);
		return createKeyPairResult;
	}

	@Override
	public CreateKeyPairResult createKeyPair(AWSCredentials credentials, String keyName) {
		return createKeyPair(amazonEC2ClientFactory.createAmazonEC2Client(credentials), keyName);
	}

	private List<KeyPairInfo> listKeyPairs(AmazonEC2Client client) {
		DescribeKeyPairsResult describeKeyPairsResult = client.describeKeyPairs();
		return describeKeyPairsResult.getKeyPairs();
	}

	@Override
	public List<KeyPairInfo> listKeyPairs(AWSCredentials credentials) {
		return listKeyPairs(amazonEC2ClientFactory.createAmazonEC2Client(credentials));
	}

	private String describeKeyPairFingerPrint(AmazonEC2Client client, String keyName) {
		DescribeKeyPairsResult describeKeyPairsResult = client.describeKeyPairs();
		for (KeyPairInfo keyPairInfo : describeKeyPairsResult.getKeyPairs()) {
			if (keyPairInfo.getKeyName().equals(keyName)) {
				return keyPairInfo.getKeyFingerprint();
			}
		}
		return "";
	}

	@Override
	public String describeKeyPairFingerPrint(AWSCredentials credentials, String keyName) {
		return describeKeyPairFingerPrint(amazonEC2ClientFactory.createAmazonEC2Client(credentials), keyName);
	}

}
