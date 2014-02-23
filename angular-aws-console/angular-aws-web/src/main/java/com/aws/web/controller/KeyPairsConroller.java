package com.aws.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.aws.core.service.KeyPairService;

/**
 * User: doktoric
 */

@Controller
public class KeyPairsConroller {

	@Autowired
	private AwsCredentialsFactory awsCredentialsFactory;

	@Autowired
	private KeyPairService awsec2Service;

	@RequestMapping(method = RequestMethod.GET, value = {"/keypairs"})
	@ResponseBody
	public List<KeyPairInfo> listKeyPairs(ModelMap model, @RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey) {
		return awsec2Service.listKeyPairs(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey));
	}

	@RequestMapping(method = RequestMethod.GET, value = {"/keypairs/create"})
	@ResponseBody
	public CreateKeyPairResult createKeyPairs(@RequestParam("name") String name, @RequestParam("accessKey") String accessKey,
	        @RequestParam("secretKey") String secretKey) {
		return awsec2Service.createKeyPair(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey), name);
	}

	@RequestMapping(method = RequestMethod.GET, value = {"/keypairs/{secretKey}"})
	@ResponseBody
	public String getFingerPrintKeyPairs(@RequestParam("name") String name, @RequestParam("accessKey") String accessKey,
	        @PathVariable("secretKey") String secretKey) {
		return awsec2Service.describeKeyPairFingerPrint(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey), name);
	}
}
