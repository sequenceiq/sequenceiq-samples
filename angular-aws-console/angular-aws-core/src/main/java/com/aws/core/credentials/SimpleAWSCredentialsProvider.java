package com.aws.core.credentials;

import org.apache.commons.lang3.Validate;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;

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
