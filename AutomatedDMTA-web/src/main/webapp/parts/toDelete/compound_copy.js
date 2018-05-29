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
       
       $scope.zoomImage = function () {
    	   console.log("inside zoom image");
       
/*    	var imgId=    document.getElementById('struct_graph');
    	imgId.style.width = "300px";*/
    	
       };
       
           // Get the modal
       var modal = document.getElementById('myModal');

       // Get the image and insert it inside the modal - use its "alt" text as a caption
       var img = document.getElementById('struct_graph');
       var modalImg = document.getElementById("img01");
       var captionText = document.getElementById("caption");
       img.onclick = function(){
           modal.style.display = "block";
           modalImg.src = this.src;
           captionText.innerHTML = this.alt;
       }

       // Get the <span> element that closes the modal
       var span = document.getElementsByClassName("close")[0];

       // When the user clicks on <span> (x), close the modal
       span.onclick = function() { 
           modal.style.display = "none";
       };
       
       
    })
    ;
