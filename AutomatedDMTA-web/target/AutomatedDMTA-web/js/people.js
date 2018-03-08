'use strict';

angular.module('myApp.people', ['myApp.services', 'ngResource'])

	// this $resource implements standard REST methods so the definition below only includes additional ones 
	// the url can be in a different domain as long as it supports CORS
	.factory('personFactory', function ($resource) {
		return $resource(
			'resources/people/persons/:id', 
//			'http://UKCEMNGV08.emea.astrazeneca.net\\:8080/resources/people/persons/:id', 
			{ id:'@id' }, 
            { 'update': { method: 'PUT'} }  
		);
	})
	
/*	.factory('personSearchFactory', function ($resource) {
		return $resource(
			'resources/people/search', 
			{ }, 
            { 'and': { method: 'GET', params: {op:'and', firstName:'@firstName', lastName:'@lastName'}, isArray:true } ,
			  'or': { method: 'GET', params: {op:'or', firstName:'@firstName', lastName:'@lastName'}, isArray:true }
			}  
		);*/
	
		.factory('personFactory', function ($resource) {
		return $resource(
			'resources/people/persons/:compoundId', 
//			'http://UKCEMNGV08.emea.astrazeneca.net\\:8080/resources/people/persons/:id', 
			{ compoundId:'@compoundId' }, 
            { 'update': { method: 'PUT'} }  
		);
		
	})
	
	.controller('PeopleCtrl', function($scope, personFactory, personSearchFactory, alertService, $location) {
		$scope.objects = [];
		$scope.addMode = false;
		$scope.personId = -1;
		$scope.found = {};
		$scope.saved = {};
		$scope.firstName;
		$scope.lastName;
		$scope.foundNames = [];
		
		$scope.toggleAddMode = function () {
            $scope.addMode = !$scope.addMode;
//            if ($scope.addMode) {
//            	$('#addFirstName')[0].focus();
//            }
        };
		
		$scope.toggleEditMode = function (object) {
			// save and restore values in case of user cancelling request
			if (object.editMode) {
				object.firstName = $scope.saved.firstName;
				object.lastName = $scope.saved.lastName;
				$scope.saved = {};
			} else {
				$scope.saved.firstName = object.firstName;
				$scope.saved.lastName = object.lastName;
			}
            object.editMode = !object.editMode;
        };

        var successCallback = function (e, cb) {
        	alertService.add("success", "Success!");
            $scope.getData(cb);
        };

        var successPostCallback = function (e) {
            successCallback(e, function () {
                $scope.toggleAddMode();
                $scope.object = {};
            });
        };

        var errorCallback = function (e) {
        	alertService.add("error", 'Error: Method ' + e.config.method+ ' failed on URL '+e.config.url+ ' with status '+e.status);
        };

        $scope.addObject = function () {
        	personFactory.save($scope.object, successPostCallback, errorCallback);
        };

        $scope.deleteObject = function (object) {
        	personFactory.remove({ id: object.id }, successCallback, errorCallback);
        	if ($scope.personId == object.id) {
        		$scope.clearFoundPerson();
        	}
        };

        $scope.updateObject = function (object) {
        	personFactory.update({ id: object.id }, object, successCallback, errorCallback);
        };

        $scope.getData = function (cb) {
        	return personFactory.query(function (data) {
                $scope.objects = data;
                if (cb) cb();
            });
        };

        $scope.findPersonById = function () {
        	if ($scope.personId != null) {
        		$scope.found = personFactory.get({ id: $scope.personId });
        	} else {
        		$scope.clearFoundPerson();
        	}
        };
        
        $scope.findPersonByName = function () {
        	if ($scope.firstName && $scope.lastName) {
        		$scope.foundNames = personSearchFactory.get({ compoundId: $scope.compoundId});
        	} else {
        		$scope.foundNames = personSearchFactory.or({ firstName: $scope.firstName});
        	}
        };
        
        $scope.clearFoundPerson = function() {
        	$scope.personId = -1;
        	$scope.found = {};
        };
        
        $scope.testError = function () {
        	alertService.add("danger", "Error when user clicked on text");
        };
        
        $scope.getData();

    })
;
