package com.sequenceiq.samples.core.service.simple;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.sequenceiq.core.service.InstanceService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.List;

@Component
public class SimpleInstanceService implements InstanceService {

	@Autowired
	private AmazonEC2ClientFactory amazonEC2ClientFactory;

	@Override
	public List<Reservation> describeInstances(AWSCredentials crendentials) {
		AmazonEC2Client client = amazonEC2ClientFactory.createAmazonEC2Client(crendentials);
		DescribeInstancesResult describeInstancesResult = client.describeInstances();
		return describeInstancesResult.getReservations();
	}

	private RunInstancesResult runInstances(AmazonEC2Client client, String amiId, String keyName, String securityGroup, String gist) {
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest(amiId, 1, 1);
		runInstancesRequest.withInstanceType("t1.micro");
		runInstancesRequest.withKeyName(keyName);
		runInstancesRequest.withUserData(new String(Base64.encodeBase64(readGist(gist).getBytes())));
		runInstancesRequest.withSecurityGroups(securityGroup);
		RunInstancesResult runInstancesResult = client.runInstances(runInstancesRequest);
		return runInstancesResult;
	}

	@Override
	public RunInstancesResult runInstances(AWSCredentials crendentials, String amiId, String keyName, String securityGroup, String gist) {
		return runInstances(amazonEC2ClientFactory.createAmazonEC2Client(crendentials), amiId, keyName, securityGroup, gist);
	}

	private StopInstancesResult stopInstances(AmazonEC2Client client, String... instanceIds) {
		StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(instanceIds);
		StopInstancesResult stopInstancesResult = client.stopInstances(stopInstancesRequest);
		return stopInstancesResult;
	}

	@Override
	public StopInstancesResult stopInstances(AWSCredentials crendentials, String... instanceIds) {
		return stopInstances(amazonEC2ClientFactory.createAmazonEC2Client(crendentials), instanceIds);
	}

	private TerminateInstancesResult terminateInstances(AmazonEC2Client client, String... instanceIds) {
		TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest().withInstanceIds(instanceIds);
		TerminateInstancesResult terminateInstancesResult = client.terminateInstances(terminateInstancesRequest);
		return terminateInstancesResult;
	}

	@Override
	public TerminateInstancesResult terminateInstances(AWSCredentials credentials, String... instanceIds) {
		return terminateInstances(amazonEC2ClientFactory.createAmazonEC2Client(credentials), instanceIds);
	}

    private StartInstancesResult startInstances(AmazonEC2Client client, String... instanceIds) {
        StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds(instanceIds);
        StartInstancesResult startResult = client.startInstances(startRequest);
        return startResult;
    }

    @Override
    public StartInstancesResult startInstances(AWSCredentials credentials, String... instanceId) {
        return startInstances(amazonEC2ClientFactory.createAmazonEC2Client(credentials), instanceId);
    }

    public String readGist(String gist) {
		try {
			URL url = new URL(gist);
			final InputStream in = new BufferedInputStream(url.openStream());
			InputStreamReader is = new InputStreamReader(in);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();
			while (read != null) {
				sb.append(read);
				sb.append("\n");
				read = br.readLine();
			}

			return sb.toString();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return "";
	}

}
