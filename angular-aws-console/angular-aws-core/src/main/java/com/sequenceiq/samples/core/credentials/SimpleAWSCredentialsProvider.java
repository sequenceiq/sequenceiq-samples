package com.sequenceiq.samples.core.credentials;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import org.apache.commons.lang3.Validate;

public class SimpleAWSCredentialsProvider implements AWSCredentialsProvider {

	private final AWSCredentials credentials;

	public SimpleAWSCredentialsProvider(AWSCredentials credentials) {
		Validate.notNull(credentials, "credentials can not be null!");
		this.credentials = credentials;
	}

	@Override
	public AWSCredentials getCredentials() {
		return credentials;
	}

	@Override
	public void refresh() {
	}

}
