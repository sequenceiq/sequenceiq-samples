'use strict';

/* Controllers */

var awsControllers = angular.module('awsControllers', []);

awsControllers.controller('AwsConsoleController', ['$scope', '$http', 'Instances', 'KeyPairs', 'Groups', 'SpotPrices', 'LaunchConfigurations', 'AutoScalingGroups', 'AutoScalingInstances',
    function ($scope, $http, Instances, KeyPairs, Groups, SpotPrices, LaunchConfigurations, AutoScalingGroups, AutoScalingInstances) {

        $scope.signIn = function () {
            localStorage.signedIn = true;
            localStorage.accessKey = encodeURIComponent($scope.accessKey);
            localStorage.secretKey = encodeURIComponent($scope.secretKey);

            $scope.accessKey = localStorage.accessKey;
            $scope.secretKey = localStorage.secretKey;
            $scope.signedIn = true;

            $scope.doQueries();
        };

        $scope.runInstance = function () {
            var amiId = amiIdInput.value;
            var keyName = sshKeyInput.value;
            var securityGroup = securityGroupInput.value;
            var gist = gistInput.value;
            $http({
                url: 'run?accessKey=' + $scope.accessKey + '&secretKey=' + $scope.secretKey,
                method: "POST",
                data: {
                    amiId: amiId,
                    keyName: keyName,
                    securityGroup: securityGroup,
                    gist: gist
                },
                headers: {
                    'Content-Type': 'application/json'
                }
            }).success(function (data, status, headers, config) {
                    console.log("success");
                }).error(function (data, status, headers, config) {
                    console.log("unsuccess");
                });
        }

        $scope.stopInstance = function (instanceId) {
            $http({
                url: 'stop?accessKey=' + $scope.accessKey + '&secretKey=' + $scope.secretKey,
                method: "POST",
                data: {
                    instanceId: instanceId
                },
                headers: {
                    'Content-Type': 'application/json'
                }
            }).success(function (data, status, headers, config) {
                    console.log("success");
                }).error(function (data, status, headers, config) {
                    console.log("unsuccess");
                });
        }

        $scope.terminateInstance = function (instanceId) {
            $http({
                url: 'terminate?accessKey=' + $scope.accessKey + '&secretKey=' + $scope.secretKey,
                method: "POST",
                data: {
                    instanceId: instanceId
                },
                headers: {
                    'Content-Type': 'application/json'
                }
            }).success(function (data, status, headers, config) {
                    console.log("success");
                }).error(function (data, status, headers, config) {
                    console.log("unsuccess");
                });
        }

        $scope.startInstance = function (instanceId) {
            $http({
                url: 'start?accessKey=' + $scope.accessKey + '&secretKey=' + $scope.secretKey,
                method: "POST",
                data: {
                    instanceId: instanceId
                },
                headers: {
                    'Content-Type': 'application/json'
                }
            }).success(function (data, status, headers, config) {
                    console.log("success");
                }).error(function (data, status, headers, config) {
                    console.log("unsuccess");
                });
        }

        $scope.createAutoScalingGroups = function () {
            $http({
                url: 'autoscalinggroup?accessKey=' + $scope.accessKey + '&secretKey=' + $scope.secretKey,
                method: "POST",
                data: {
                    autoScalingGroupName: nameInput.value,
                    maxSize: maxSizeInput.value,
                    minSize: minSizeInput.value,
                    defaultCooldown: coolDownInput.value,
                    launchConfigurationName: launchConfigNameInput.value
                },
                headers: {
                    'Content-Type': 'application/json'
                }
            }).success(function (data, status, headers, config) {
                console.log("success");
            }).error(function (data, status, headers, config) {
                console.log("unsuccess");
            });
        }

        $scope.updateAutoScalingGroups = function (groupName) {
            AutoScalingGroups.update({
                accessKey: $scope.accessKey,
                secretKey: $scope.secretKey
            }, {
                autoScalingGroupName: groupName,
                maxSize: maxSizeInput.value,
                minSize: minSizeInput.value,
                cooldown: coolDownInput.value
            });
        }

        $scope.signOut = function () {
            $scope.signedIn = false;
            localStorage.signedIn = false;
            localStorage.removeItem('accessKey');
            localStorage.removeItem('secretKey');
        };

        $scope.doQueries = function () {
            $scope.instances = Instances.query({
                accessKey: $scope.accessKey,
                secretKey: $scope.secretKey
            });
            $scope.keypairs = KeyPairs.query({
                accessKey: $scope.accessKey,
                secretKey: $scope.secretKey
            });
            $scope.groups = Groups.query({
                accessKey: $scope.accessKey,
                secretKey: $scope.secretKey
            });
            $scope.launchConfigs = LaunchConfigurations.query({
                accessKey: $scope.accessKey,
                secretKey: $scope.secretKey
            });
            $scope.asGroups = AutoScalingGroups.query({
                accessKey: $scope.accessKey,
                secretKey: $scope.secretKey
            });
            $scope.asInstances = AutoScalingInstances.query({
                accessKey: $scope.accessKey,
                secretKey: $scope.secretKey
            });
            $scope.prices = SpotPrices.query({
                accessKey: $scope.accessKey,
                secretKey: $scope.secretKey
            });
        };

        if (typeof (Storage) !== "undefined") {
            if (localStorage.signedIn === 'true' && localStorage.accessKey && localStorage.secretKey) {
                $scope.signedIn = true;
                $scope.accessKey = localStorage.accessKey;
                $scope.secretKey = localStorage.secretKey;
                $scope.doQueries();
            }
        } else {
            console.log("No localstorage support!");
        }
    }
]);

