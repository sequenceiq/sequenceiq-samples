package com.aws.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.aws.core.credentials.SimpleAWSCredentials;

@Component
public class AwsCredentialsFactory {

	public AWSCredentials createSimpleAWSCredentials(String accessKey, String secretAccessKey) {
		String aKey = accessKey;
		String sAccessKey = secretAccessKey;
		try {
			aKey = URLDecoder.decode(aKey, ("UTF-8"));
			sAccessKey = URLDecoder.decode(sAccessKey, ("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.out.println(aKey + ":" + sAccessKey + e.getMessage());
		}
		return new SimpleAWSCredentials(aKey, sAccessKey);
	}

}
