'use strict';

/* App Module */

var awsApp = angular.module('awsApp', ['ngRoute', 'awsControllers', 'awsServices' ]);

awsApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/instances', {
		templateUrl : 'partials/instances.html'
	}).when('/security', {
		templateUrl : 'partials/security.html'
	}).when('/create/instance', {
            templateUrl : 'partials/createinstance.html'
    }).when('/create/autscalinggroup', {
                templateUrl : 'partials/createautoscalinggroup.html'
    }).when('/list/spotprices', {
            templateUrl : 'partials/spotprices.html'
    }).otherwise({
		redirectTo : '/instances'
	});
} ]);
