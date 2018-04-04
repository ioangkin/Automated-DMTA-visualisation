'use strict';

angular.module('myApp', ['myApp.who', 'myApp.people', 'myApp.services','myApp.compound'])

/*, 'ui.bootstrap' */

	.config(function($routeProvider, $locationProvider) {
		$locationProvider.html5Mode(true);
		$routeProvider.when('/home', {templateUrl: 'parts/home_compound.html'});
		$routeProvider.when('/who', {templateUrl: 'parts/who.html'});
		$routeProvider.when('/people', {templateUrl: 'parts/people.html'});
		$routeProvider.otherwise({redirectTo: '/home'});
	})

	.controller('IndexCtrl', function($rootScope, $scope, $location, alertService, $http) {
		$scope.title = 'ADMTA Dashboard';
		$scope.version = '0.1';

		$scope.isRoute = function(route) {
			return $location.path() == route;
		};
		
		$scope.showDesignDetails = function() {
			alert('inside showdesign');
			
			$http.get('http://localhost:8080/AutomatedDMTA-web/design/').then(function(response) {
				alert('inside response');
	    });
		};
		
	/*	$scope.showDesignDetails() {
		alert('inside showdesign');
		
		$http.get('http://localhost:8080/AutomatedDMTA-web/design/').
	    	then(function(response) {
	
	    });
		
	}*/
		
//		showSynthesisDetails($scope, $http) {
//		
//		$http.get('http://localhost:8080/AutomatedDMTA-web/synthesis/').
//	    	then(function(response) {
//	
//	    });
//		
//	}
		
//		showPurificationDetails($scope, $http) {
//		
//		$http.get('http://localhost:8080/AutomatedDMTA-web/purification/').
//	    	then(function(response) {
//	
//	    });
//		
//	}
		
//		showTestingDetails($scope, $http) {
//		
//		$http.get('http://localhost:8080/AutomatedDMTA-web/testing/').
//	    	then(function(response) {
//	
//	    });
//		
//	}
		
		
		// root binding for alertService
		$rootScope.closeAlert = alertService.closeAlert; 
	})
	
	;
	
// When the responsive nav menu is operating on a small screen, it doesn't collapse when something is clicked - this fixes that problem
$(document).on('click','.navbar-collapse.in',function(e) {
    if( $(e.target).is('a') ) {
        $(this).removeClass('in').addClass('collapse');
    }
});