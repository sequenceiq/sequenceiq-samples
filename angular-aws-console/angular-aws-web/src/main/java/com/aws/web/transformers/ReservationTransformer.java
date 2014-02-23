package com.aws.web.transformers;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.aws.model.AWSReservation;
import com.aws.model.AwsSimpleInstance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: doktoric
 */

@Component
public class ReservationTransformer {

    public AWSReservation transform(Reservation reservation) {
        AWSReservation awsInstance = new AWSReservation(reservation.getOwnerId(), reservation.getReservationId(), reservation.getRequesterId());
        awsInstance.setGroupNames(reservation.getGroupNames());
        awsInstance.setGroups(reservation.getGroups());
        List<AwsSimpleInstance> awsSimpleInstanceList = new ArrayList<>();
        for (Instance item : reservation.getInstances()) {
            AwsSimpleInstance instance = new AwsSimpleInstance();
            instance.setAmiLaunchIndex(item.getAmiLaunchIndex());
            instance.setArchitecture(item.getArchitecture());
            instance.setClientToken(item.getClientToken());
            instance.setImageId(item.getImageId());
            instance.setInstanceId(item.getInstanceId());
            instance.setState(item.getState());
            instance.setPrivateDnsName(item.getPrivateDnsName());
            instance.setPublicDnsName(item.getPublicDnsName());
            instance.setStateTransitionReason(item.getStateTransitionReason());
            instance.setKeyName(item.getKeyName());
            instance.setAmiLaunchIndex(item.getAmiLaunchIndex());
            instance.setInstanceType(item.getInstanceType());
            instance.setLaunchTime(item.getLaunchTime());
            instance.setPlacement(item.getPlacement());
            instance.setKernelId(item.getKernelId());
            instance.setRamdiskId(item.getRamdiskId());
            instance.setPlatform(item.getPlatform());
            instance.setMonitoring(item.getMonitoring());
            instance.setSubnetId(item.getSubnetId());
            instance.setVpcId(item.getVpcId());
            instance.setPrivateIpAddress(item.getPrivateIpAddress());
            instance.setPublicIpAddress(item.getPublicIpAddress());
            instance.setStateReason(item.getStateReason());
            instance.setArchitecture(item.getArchitecture());
            instance.setRootDeviceType(item.getRootDeviceType());
            instance.setRootDeviceName(item.getRootDeviceName());
            instance.setVirtualizationType(item.getVirtualizationType());
            instance.setInstanceLifecycle(item.getInstanceLifecycle());
            instance.setSpotInstanceRequestId(item.getSpotInstanceRequestId());
            instance.setLicense(item.getLicense());
            instance.setClientToken(item.getClientToken());
            awsSimpleInstanceList.add(instance);
        }
        awsInstance.setInstances(awsSimpleInstanceList);
        return awsInstance;
    }
}
