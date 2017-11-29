'use strict';

angular.module('myApp', ['myApp.who', 'myApp.people', 'myApp.services'])

/*, 'ui.bootstrap' */

	.config(function($routeProvider, $locationProvider) {
		$locationProvider.html5Mode(true);
		$routeProvider.when('/home', {templateUrl: 'parts/home_compound.html'});
		$routeProvider.when('/who', {templateUrl: 'parts/who.html'});
		$routeProvider.when('/people', {templateUrl: 'parts/people.html'});
		$routeProvider.otherwise({redirectTo: '/home'});
	})

	.controller('IndexCtrl', function($rootScope, $scope, $location, alertService) {
		$scope.title = 'Automated DMTA visulaisation (vADMTA) Dashboard';
		$scope.version = '0.1';

		$scope.isRoute = function(route) {
			return $location.path() == route;
		};
		
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