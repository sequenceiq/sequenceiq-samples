'use strict';

/* Services */

var accesKey="";
var secretKey="";

var awsServices = angular.module('awsServices', ['ngResource']);

awsServices.factory('Instances', ['$resource',
  	function($resource){
      	return $resource('instance?accessKey=:accessKey&secretKey=:secretKey');
  	}]);

awsServices.factory('KeyPairs', ['$resource',
    function($resource){
        return $resource('keypairs?accessKey=:accessKey&secretKey=:secretKey');
    }]);

awsServices.factory('Groups', ['$resource',
    function($resource){
        return $resource('groups/list?accessKey=:accessKey&secretKey=:secretKey');
    }]);

awsServices.factory('LaunchConfigurations', ['$resource',
    function($resource){
        return $resource('launchconfig?accessKey=:accessKey&secretKey=:secretKey');
    }]);

awsServices.factory('AutoScalingGroups', ['$resource',
    function($resource){
        return $resource('autoscalinggroup?accessKey=:accessKey&secretKey=:secretKey',{},{
            update: { method: 'PUT' }
        });
    }]);

awsServices.factory('AutoScalingInstances', ['$resource',
    function($resource){
        return $resource('autoscalinginstance?accessKey=:accessKey&secretKey=:secretKey');
    }]);


awsServices.factory('SpotPrices', ['$resource',
    function($resource){
        return $resource('spotprice/list?accessKey=:accessKey&secretKey=:secretKey');
    }]);