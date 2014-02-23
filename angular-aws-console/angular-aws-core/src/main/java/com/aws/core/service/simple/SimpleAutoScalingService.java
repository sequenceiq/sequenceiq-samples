package com.aws.core.service.simple;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.*;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.*;
import com.aws.core.service.AutoScalingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class SimpleAutoScalingService implements AutoScalingService {

    @Autowired
    private AmazonAutoScalingClientFactory amazonAutoScalingClientFactory;

    @Autowired
    private AmazonElasticLoadBalancingClientFactory amazonElasticLoadBalancingClientFactory;

    @Autowired
    private AmazonEC2ClientFactory amazonEC2ClientFactory;

    private CreateLoadBalancerResult createLoadBalancer(AmazonElasticLoadBalancingClient client, String name, String... zones) {
        CreateLoadBalancerRequest lbRequest = new CreateLoadBalancerRequest();
        lbRequest.setLoadBalancerName(name);
        lbRequest.withAvailabilityZones(zones);
        lbRequest.setListeners(Arrays.asList(new Listener("HTTP", 80, 80)));
        return client.createLoadBalancer(lbRequest);
    }

    @Override
    public CreateLoadBalancerResult createLoadBalancer(AWSCredentials credentials, String name, String... zones) {
        return createLoadBalancer(amazonElasticLoadBalancingClientFactory.createAmazonElasticLoadBalancingClient(credentials), name, zones);
    }

    private RegisterInstancesWithLoadBalancerResult registerInstancesToLoadBalancer(AmazonElasticLoadBalancingClient client, AmazonEC2Client simpleClient,
                                                                                    String lbName) {
        List<Instance> instances = new ArrayList<>();
        for (Reservation reservation : simpleClient.describeInstances().getReservations()) {
            instances.addAll(reservation.getInstances());
        }
        List<com.amazonaws.services.elasticloadbalancing.model.Instance> instanceId = new ArrayList<>();
        List<String> instanceIdString = new ArrayList<>();
        for (Instance instance : instances) {
            instanceId.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(instance.getInstanceId()));
            instanceIdString.add(instance.getInstanceId());
        }
        RegisterInstancesWithLoadBalancerRequest register = new RegisterInstancesWithLoadBalancerRequest();
        register.setLoadBalancerName(lbName);
        register.setInstances(instanceId);
        return client.registerInstancesWithLoadBalancer(register);
    }

    @Override
    public RegisterInstancesWithLoadBalancerResult registerInstancesToLoadBalancer(AWSCredentials credentials, String lbName) {
        return registerInstancesToLoadBalancer(amazonElasticLoadBalancingClientFactory.createAmazonElasticLoadBalancingClient(credentials),
                amazonEC2ClientFactory.createAmazonEC2Client(credentials), lbName);
    }

    public CreateKeyPairResult createKeyPair(AmazonEC2Client client, String keyName) {
        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest().withKeyName(keyName);
        CreateKeyPairResult createKeyPairResult = client.createKeyPair(createKeyPairRequest);
        return createKeyPairResult;
    }

    @Override
    public List<LaunchConfiguration> describeAmazonLaunchConfigurations(AWSCredentials credentials) {
        AmazonAutoScalingClient amazonAutoScalingClient = amazonAutoScalingClientFactory.createAmazonAutoScalingClient(credentials);
        DescribeLaunchConfigurationsResult describeLaunchConfigurationsResult = amazonAutoScalingClient.describeLaunchConfigurations();
        return describeLaunchConfigurationsResult.getLaunchConfigurations();
    }

    @Override
    public List<AutoScalingGroup> describeAmazonAutoScalingGroups(AWSCredentials credentials) {
        AmazonAutoScalingClient amazonAutoScalingClient = amazonAutoScalingClientFactory.createAmazonAutoScalingClient(credentials);
        DescribeAutoScalingGroupsResult describeAutoScalingGroupsResult = amazonAutoScalingClient.describeAutoScalingGroups();
        return describeAutoScalingGroupsResult.getAutoScalingGroups();
    }

    @Override
    public List<AutoScalingInstanceDetails> describeAmazonAutoScalingInstances(AWSCredentials credentials) {
        AmazonAutoScalingClient amazonAutoScalingClient = amazonAutoScalingClientFactory.createAmazonAutoScalingClient(credentials);
        DescribeAutoScalingInstancesResult describeAutoScalingInstancesResult = amazonAutoScalingClient.describeAutoScalingInstances();
        return describeAutoScalingInstancesResult.getAutoScalingInstances();
    }

    @Override
    public List<SpotPrice> describeSpotPriceHistory(AWSCredentials credentials) {
        AmazonEC2Client amazonEC2Client = amazonEC2ClientFactory.createAmazonEC2Client(credentials);
        return amazonEC2Client.describeSpotPriceHistory().getSpotPriceHistory();
    }

    @Override
    public void createAmazonAutoScalingGroup(AWSCredentials credentials, String name, int maxsize, int minsize, int coolDown, String configName,
                                             Collection<String> loadBalancerNames, Collection<com.amazonaws.services.autoscaling.model.Tag> tags) {
        AmazonAutoScalingClient amazonAutoScalingClient = amazonAutoScalingClientFactory.createAmazonAutoScalingClient(credentials);
        CreateAutoScalingGroupRequest createAutoScalingGroupRequest = new CreateAutoScalingGroupRequest().withAutoScalingGroupName(name)
                .withDefaultCooldown(coolDown).withLaunchConfigurationName(configName).withMaxSize(maxsize).withMinSize(minsize).withTags(tags)
                .withLoadBalancerNames(loadBalancerNames).withAvailabilityZones(Arrays.asList("us-west-2b"));
        amazonAutoScalingClient.createAutoScalingGroup(createAutoScalingGroupRequest);
    }

    @Override
    public void updateAmazonAutoScalingGroupCoolDown(AWSCredentials credentials, String name, int coolDown) {
        AmazonAutoScalingClient amazonAutoScalingClient = amazonAutoScalingClientFactory.createAmazonAutoScalingClient(credentials);
        UpdateAutoScalingGroupRequest updateAutoScalingGroupRequest = new UpdateAutoScalingGroupRequest()
                .withAutoScalingGroupName(name)
                .withDefaultCooldown(coolDown);
        amazonAutoScalingClient.updateAutoScalingGroup(updateAutoScalingGroupRequest);
    }

    @Override
    public void updateAmazonAutoScalingGroupMaxSize(AWSCredentials credentials, String name, int maxSize) {
        AmazonAutoScalingClient amazonAutoScalingClient = amazonAutoScalingClientFactory.createAmazonAutoScalingClient(credentials);
        UpdateAutoScalingGroupRequest updateAutoScalingGroupRequest = new UpdateAutoScalingGroupRequest()
                .withAutoScalingGroupName(name)
                .withMaxSize(maxSize);
        amazonAutoScalingClient.updateAutoScalingGroup(updateAutoScalingGroupRequest);
    }

    @Override
    public void updateAmazonAutoScalingGroupMinSize(AWSCredentials credentials, String name, int minSize) {
        AmazonAutoScalingClient amazonAutoScalingClient = amazonAutoScalingClientFactory.createAmazonAutoScalingClient(credentials);
        UpdateAutoScalingGroupRequest updateAutoScalingGroupRequest = new UpdateAutoScalingGroupRequest()
                .withAutoScalingGroupName(name)
                .withMinSize(minSize);
        amazonAutoScalingClient.updateAutoScalingGroup(updateAutoScalingGroupRequest);
    }
}
