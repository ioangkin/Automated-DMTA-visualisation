'use strict';

angular.module('myApp.compound', ['ngResource'])

	
	
	.controller('CompoundCtrl', function($scope, alertService, $location,$http) {
	
		//Show All Compounds
		$scope.showAllCompounds = function () {
			$scope.structureGraph=null;
			$scope.flag=false;
			$scope.showImg=false;
			 $http.get('/AutomatedDMTA-web/resources/compound/compounds')
			 /*$http.get('http://localhost:8080/AutomatedDMTA-web/resources/compound/compounds')*/

		        .then(function(response) {
		        	$scope.data=response.data;
		        	 console.log(response.data);
		        });
		}

        //Design Stage
		$scope.showDesignDetails = function () {
			$scope.structureGraph=null;
			$scope.flag=false;
			$scope.showImg=false;
			 $http.get('/AutomatedDMTA-web/resources/compound/design')
		        .then(function(response) {
		        	$scope.data=response.data;
		        	 console.log(response.data);
		        });
		};
 
        //Synthesis Stage
		$scope.showSynthesisDetails = function () {
			$scope.structureGraph=null;
			$scope.flag=false;
			$scope.showImg=false;
			 $http.get('/AutomatedDMTA-web/resources/compound/synthesis').
		       then(function(response) {
		       	$scope.data=response.data;
		       	 console.log(response.data);
		       });
		};

        //Purification Stage
		$scope.showPurificationDetails = function () {
			$scope.structureGraph=null;
			$scope.flag=false;
			$scope.showImg=false;
			 $http.get('/AutomatedDMTA-web/resources/compound/purification').
		        then(function(response) {
		        	$scope.data=response.data;
		        	 console.log(response.data);
		        });
        };
        
        //Testing Stage
		$scope.showTestingDetails = function () {
			$scope.structureGraph=null;
			$scope.flag=false;
			$scope.showImg=false;
			 $http.get('/AutomatedDMTA-web/resources/compound/testing').
		        then(function(response) {
		        	$scope.data=response.data;
		        	 console.log(response.data);
		        });
        };
        
        //Display Graphs
		$scope.showGraph = function (data) {
	
			$scope.showImg=true;
			if(data.stage=='TESTING'){
				$scope.flag=true;
			}else{
				$scope.flag=false;
			}
			
			$scope.structureGraph =data.structureGraph;
			$scope.lineGraph =data.lineGraph;
			
       };
      
       // Get the Structure-graph modal
       var structModal = document.getElementById('structureGraphModal');

       // Get Structure-graph and insert it inside the modal
       var structureImg = document.getElementById('struct_graph');
       var structModalImg = document.getElementById("struct_graph_img");
       structureImg.onclick = function(){
    	   structModal.style.display = "block";
    	   structModalImg.src = this.src;
       }

       // Get the <span> element that closes the modal
       var span = document.getElementsByClassName("closeStructureGraphModal")[0];

       // When the user clicks on <span> (x), close the modal
       span.onclick = function() { 
    	   structModal.style.display = "none";
       }
       
       // Get the Line-graph modal
       var lineModal = document.getElementById('lineGraphModal');

       // Get the Line-graph and insert it inside the modal
       var lineImg = document.getElementById('line_graph');
       var lineModalImg = document.getElementById("line_graph_img");
       lineImg.onclick = function(){
    	   lineModal.style.display = "block";
    	   lineModalImg.src = this.src;
       }

       // Get the <span> element that closes the modal
       var lineSpan = document.getElementsByClassName("closeLineGraphModal")[0];

       // When the user clicks on <span> (x), close the modal
       lineSpan.onclick = function() {
    	   lineModal.style.display = "none";
       }
       
    });
