package com.aws.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.ec2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aws.core.service.InstanceService;
import com.aws.model.AWSReservation;
import com.aws.web.transformers.ReservationTransformer;

@Controller
public class InstanceController {

	@Autowired
	private AwsCredentialsFactory awsCredentialsFactory;

	@Autowired
	private InstanceService awsec2Service;

	@Autowired
	private ReservationTransformer transformer;

	@RequestMapping(method = RequestMethod.GET, value = {"/instance"})
	@ResponseBody
	public List<AWSReservation> listInstances(ModelMap model, @RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey) {
		List<Reservation> reservations = awsec2Service.describeInstances(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey));
		List<AWSReservation> awsInstances = new ArrayList<>();
		for (Reservation reservation : reservations) {
			awsInstances.add(transformer.transform(reservation));
		}
		return awsInstances;
	}

	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	@ResponseBody
	public StopInstancesResult stopInstance(@RequestBody Map<String, String> body, @RequestParam("accessKey") String accessKey,
	        @RequestParam("secretKey") String secretKey) {
		StopInstancesResult stopInstancesResult = awsec2Service.stopInstances(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey),
		        body.get("instanceId"));
		return stopInstancesResult;
	}

	@RequestMapping(value = "/terminate", method = RequestMethod.POST)
	@ResponseBody
	public TerminateInstancesResult terminateInstance(@RequestBody Map<String, String> body, @RequestParam("accessKey") String accessKey,
	        @RequestParam("secretKey") String secretKey) {
		TerminateInstancesResult terminateInstancesResult = awsec2Service.terminateInstances(
		        awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey), body.get("instanceId"));
		return terminateInstancesResult;
	}

	@RequestMapping(value = "/run", method = RequestMethod.POST)
	@ResponseBody
	public AWSReservation runInstance(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey,
	        @RequestBody Map<String, String> body) {
		RunInstancesResult result = awsec2Service.runInstances(awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey), body.get("amiId"),
		        body.get("keyName"), body.get("securityGroup"), body.get("gist"));
		return transformer.transform(result.getReservation());
	}

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseBody
    public StartInstancesResult startInstance(@RequestParam("accessKey") String accessKey, @RequestParam("secretKey") String secretKey,
                                      @RequestBody Map<String, String> body) {
        StartInstancesResult startInstancesResult = awsec2Service.startInstances(
                awsCredentialsFactory.createSimpleAWSCredentials(accessKey, secretKey), body.get("instanceId"));
        return startInstancesResult;
    }

}