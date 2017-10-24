'use strict';

/* services.js */

var appServices = angular.module('myApp.services', []);

appServices.factory('alertService', function($rootScope) {
	var alertService = {};

	$rootScope.alerts = [];

	alertService.add = function(type, msg) {
		var len = $rootScope.alerts.push({
			'type' : type,
			'msg' : msg
		});
		if (type == 'success') {
			setTimeout(function() {
				$rootScope.$apply(function() {
					alertService.closeAlert(len - 1);
				});
			}, 2000);
		}
	};

	alertService.closeAlert = function(index) {
		$rootScope.alerts.splice(index, 1);
	};

	return alertService;
});

appServices.directive('alert', function () {
	  return {
	    restrict:'EA',
	    templateUrl:'parts/alert.html',
	    transclude:true,
	    replace:true,
	    scope: {
	      type: '=',
	      close: '&'
	    },
	    link: function(scope, iElement, iAttrs, controller) {
	      scope.closeable = "close" in iAttrs;
	    }
	  };
	});