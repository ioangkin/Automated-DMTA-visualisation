'use strict';

angular.module('myApp.who', ['ngResource'])

	.controller('WhoCtrl', function($scope, peopleService) {
		$scope.people = peopleService.getMembersOf('RDISCSDP');
	})

	.service('peopleService', function($resource) {
		var People = $resource('res/:group.json');
		this.getMembersOf = function(group) {
			return People.query(
				{group: group},
				function(data) {
					for(var i = 0, c = data.length; i < c; ++i) {
						var item = data[i];
						for(var k in item) {
							if(item[k])
								item[k] = item[k].toString();
						}
					}
				}
			);
		};
	})

	.directive('contact', function() {
		return {
			// http://stackoverflow.com/questions/16502559/angular-js-using-a-directive-inside-an-ng-repeat-and-a-mysterious-power-of-sco
			restrict: 'E',
			scope: { address: '@' },
			template: '<a href="mailto://{{address}}"><i class="glyphicon glyphicon-envelope"></i></a>'
		}
	})
;
