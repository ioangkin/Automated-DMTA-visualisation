'use strict';

angular.module('myApp.compound', ['ngResource'])

	
	
	.controller('CompoundCtrl', function($scope, alertService, $location,$http) {
	
//		$scope.filter('capitalize', function() {
//	        return function(input) {
//	            return (!!input) ? input.split(' ').map(function(wrd){return wrd.charAt(0).toUpperCase() + wrd.substr(1).toLowerCase();}).join(' ') : '';
//	        }
//	    });
		
        //Design Stage
		$scope.showDesignDetails = function () {
			 $http.get('http://localhost:8080/AutomatedDMTA-web/resources/compound/design')
		        .then(function(response) {
		        	$scope.data=response.data;
		        	 console.log(response.data);
		        });
		};
   
        $scope.createDesignDetails = function () {
        	 console.log("inside my createDesign");
			   var compound={
				    sampleNumber:'100001',
	        		stage:'DESIGN',
	        		smiles:'smiles1',
	        		results: ''
		        };
			   
			   $http.post("http://localhost:8080/AutomatedDMTA-web/resources/compound/saveCompounds", compound, {
				           headers: { 'Content-Type': 'application/json; charset=UTF-8'}
				       }).success(function(responseData) {
				         console.log(responseData);
				   $scope.showDesignDetails();
				       }).error(function(error){
			        	    console.log(error);
				   	});
        };
     
        //Synthesis Stage
		$scope.showSynthesisDetails = function () {
			 $http.get('http://localhost:8080/AutomatedDMTA-web/resources/compound/synthesis').
		       then(function(response) {
		       	$scope.data=response.data;
		       	 console.log(response.data);
		       });
		};

		$scope.createSynthesisDetails = function () {
			 console.log("inside my createSynthesis");
			   var compound={
					sampleNumber:'100002',
		       		stage:'SYNTHESIS',
		    		smiles:'smiles2',
		    		results: ''
		       };

			   $http.post("http://localhost:8080/AutomatedDMTA-web/resources/compound/saveCompounds", compound, {
				           headers: { 'Content-Type': 'application/json; charset=UTF-8'}
				       }).success(function(responseData) {
				         console.log(responseData);
				   $scope.showSynthesisDetails();
				       }).error(function(error){
			        	    console.log(error);
				   });
		};
		
		
        //Purification Stage
		$scope.showPurificationDetails = function () {			
			 $http.get('http://localhost:8080/AutomatedDMTA-web/resources/compound/purification').
		        then(function(response) {
		        	$scope.data=response.data;
		        	 console.log(response.data);
		        });
        };
        
        $scope.createPurificationDetails = function () {
        	 console.log("inside my createPurification");
			   var compound={
					    sampleNumber:'100003',
		        		stage:'PURIFICATION',
		        		smiles:'smiles3',
		        		results: ''
		        };

			   $http.post("http://localhost:8080/AutomatedDMTA-web/resources/compound/saveCompounds", compound, {
				           headers: { 'Content-Type': 'application/json; charset=UTF-8'}
				       }).success(function(responseData) {
				         console.log(responseData);
				   $scope.showPurificationDetails();
				       }).error(function(error){
			        	    console.log(error);
				   }); 
	
        };
        
        
        //Testing Stage
		$scope.showTestingDetails = function () {
			 $http.get('http://localhost:8080/AutomatedDMTA-web/resources/compound/testing').
		        then(function(response) {
		        	$scope.data=response.data;
		        	 console.log(response.data);
		        });
        };
   
        $scope.createTestingDetails = function () {
        	 console.log("inside my createTesting");
			   var compound={
					    sampleNumber:'100004',
		        		stage:'TESTING',
		        		smiles:'smiles4',
		        		results: '1.00'
		        };
			   
			   $http.post("http://localhost:8080/AutomatedDMTA-web/resources/compound/saveCompounds", compound, {
				           headers: { 'Content-Type': 'application/json; charset=UTF-8'}
				       }).success(function(responseData) {
				         console.log(responseData);
				   $scope.showTestingDetails();
				       }).error(function(error){
			        	    console.log(error);
		        	});
        };

    })
    ;
