package com.aws.model;

import com.amazonaws.services.ec2.model.*;

import java.util.Date;

/**
 * User: doktoric
 */
public class AwsSimpleInstance {
    private String instanceId;
    private String imageId;
    private InstanceState state;
    private String privateDnsName;
    private String publicDnsName;
    private String stateTransitionReason;
    private String keyName;
    private Integer amiLaunchIndex;
    private String instanceType;
    private Date launchTime;
    private Placement placement;
    private String kernelId;
    private String ramdiskId;
    private String platform;
    private Monitoring monitoring;
    private String subnetId;
    private String vpcId;
    private String privateIpAddress;
    private String publicIpAddress;
    private StateReason stateReason;
    private String architecture;
    private String rootDeviceType;
    private String rootDeviceName;
    private String virtualizationType;
    private String instanceLifecycle;
    private String spotInstanceRequestId;
    private InstanceLicense license;
    private String clientToken;

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setState(InstanceState state) {
        this.state = state;
    }

    public void setPrivateDnsName(String privateDnsName) {
        this.privateDnsName = privateDnsName;
    }

    public void setPublicDnsName(String publicDnsName) {
        this.publicDnsName = publicDnsName;
    }

    public void setStateTransitionReason(String stateTransitionReason) {
        this.stateTransitionReason = stateTransitionReason;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setAmiLaunchIndex(Integer amiLaunchIndex) {
        this.amiLaunchIndex = amiLaunchIndex;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public void setLaunchTime(Date launchTime) {
        this.launchTime = launchTime;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public void setKernelId(String kernelId) {
        this.kernelId = kernelId;
    }

    public void setRamdiskId(String ramdiskId) {
        this.ramdiskId = ramdiskId;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    public void setPublicIpAddress(String publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
    }

    public void setStateReason(StateReason stateReason) {
        this.stateReason = stateReason;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public void setRootDeviceType(String rootDeviceType) {
        this.rootDeviceType = rootDeviceType;
    }

    public void setRootDeviceName(String rootDeviceName) {
        this.rootDeviceName = rootDeviceName;
    }

    public void setVirtualizationType(String virtualizationType) {
        this.virtualizationType = virtualizationType;
    }

    public void setInstanceLifecycle(String instanceLifecycle) {
        this.instanceLifecycle = instanceLifecycle;
    }

    public void setSpotInstanceRequestId(String spotInstanceRequestId) {
        this.spotInstanceRequestId = spotInstanceRequestId;
    }

    public void setLicense(InstanceLicense license) {
        this.license = license;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getImageId() {
        return imageId;
    }

    public InstanceState getState() {
        return state;
    }

    public String getPrivateDnsName() {
        return privateDnsName;
    }

    public String getPublicDnsName() {
        return publicDnsName;
    }

    public String getStateTransitionReason() {
        return stateTransitionReason;
    }

    public String getKeyName() {
        return keyName;
    }

    public Integer getAmiLaunchIndex() {
        return amiLaunchIndex;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public Date getLaunchTime() {
        return launchTime;
    }

    public Placement getPlacement() {
        return placement;
    }

    public String getKernelId() {
        return kernelId;
    }

    public String getRamdiskId() {
        return ramdiskId;
    }

    public String getPlatform() {
        return platform;
    }

    public Monitoring getMonitoring() {
        return monitoring;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public String getVpcId() {
        return vpcId;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public String getPublicIpAddress() {
        return publicIpAddress;
    }

    public StateReason getStateReason() {
        return stateReason;
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getRootDeviceType() {
        return rootDeviceType;
    }

    public String getRootDeviceName() {
        return rootDeviceName;
    }

    public String getVirtualizationType() {
        return virtualizationType;
    }

    public String getInstanceLifecycle() {
        return instanceLifecycle;
    }

    public String getSpotInstanceRequestId() {
        return spotInstanceRequestId;
    }

    public InstanceLicense getLicense() {
        return license;
    }

    public String getClientToken() {
        return clientToken;
    }

    public AwsSimpleInstance(){

    }
}
