var app= angular.module("agentApp",[]);

app.controller("agents",function($scope,$rootScope,$window,$http){
	
	
	
	var location = window.location.href;
	
	var hostport;
		
	var webSocket;
	
	$scope.consoleMessages = [];
	$scope.running = [];
	$scope.centers = [];
	$scope.types = [];
	
	$rootScope.hostport;
	
	$rootScope.receivers = [""]
	
	$scope.init = function() {
		console.log("entered into init");
		
		//location = window.location.href;
		console.log(location)
		
		var index = location.indexOf("//");
		hostport = location.substr(index+2);
		
		hostport = hostport.substr(0, hostport.indexOf("/"));
		
		$rootScope.hostport = hostport;
		$rootScope.$apply;
		
		console.log(hostport);
		
		setWebSocket();
		
		if (checkRest()) {
			getPerformative();
			getCenters();
			getClasses();
			getRunning();
		}
		

	}

	$scope.createAgent = function(agentName, agentType) {
		
		checkRest()
		console.log("Agent name: "+agentName)
		console.log("Agent type: "+agentType)
		
		if (agentName == "" || agentType == "") return;
		if (checkRest()) {
			console.log("rest way")
			
			$http({
			  method: 'PUT',
			  url: location+"rest/agents/running/"+agentType+"/"+agentName
			}).then(function successCallback(response) {
			    // this callback will be called asynchronously
			    // when the response is available
				console.log("success")
				getRunning()
			  }, function errorCallback(response) {
			    // called asynchronously if an error occurs
			    // or server returns response with an error status.
				  console.log("error")
			  });
		
		} else {
			console.log("websocket way")	
			var object = {      
			     method : "PUT",
			     url : location+"rest/agents/running/"+encodeURIComponent(agentType)+"/"+encodeURIComponent(agentName),
			     object : ""
			    }
			webSocket.send(JSON.stringify(object));
		}
		
	}
	
	$scope.sendACLMessage = function(perform, sender, replyTo, content, language, encoding, ontology, protocol, conversationId, replyWith, replyBy) {
		if (sender == undefined) console.log("sender is null")
		console.log("aclMessage: "+perform + sender+ replyTo+ content+ language+ encoding+ 
				ontology+ protocol+ conversationId+ replyWith+ replyBy)
	
		//$rootScope.receivers.push(receiver)
		for(var i = $rootScope.receivers.length - 1; i >= 0; i--) {
		    if($rootScope.receivers[i] === "") {
		    	$rootScope.receivers.splice(i, 1);
		    }
		}
		
		var receiversJSON = "["+$rootScope.receivers+"]";
		console.log("receivers" + JSON.parse("["+$rootScope.receivers+"]"))
		
		console.log("receivers" + $rootScope.receivers)
		
		if (perform == undefined) return;
		if (sender == undefined) sender = null;
		
		if (replyTo == undefined) replyTo = null;
			content = checkForUndefined(content)
			language = checkForUndefined(language)
			encoding = checkForUndefined(encoding)
			ontology = checkForUndefined(protocol)
			conversationId = checkForUndefined(conversationId)
			replyWith = checkForUndefined(replyWith)
			replyBy = checkForUndefined(replyBy)
			
	//		sender = getInternalAID(sender)
	//		receiver = getInternalAID(receiver)
					
			aclMessageJSON = {
					performative:perform,
					sender:JSON.parse(sender),
					receivers:JSON.parse(receiversJSON),
					replyTo:JSON.parse(replyTo),
					content:content,
					contentObject:JSON.parse(null),
					userArgs:JSON.parse(null),
					language:language,
					encoding:encoding,
					ontology:ontology,
					protocol:protocol,
					conversationId:conversationId,
					replyWith:replyWith,
					inReplyWith:"",
					inReplyTo:"",
					replyTo:replyTo
			}
		if (checkRest()) {
			$http({
				  method: 'POST',
				  url: location+'rest/messages',
				  data: JSON.stringify(aclMessageJSON),
				  contentType: "application/json",
				}).then(function successCallback(response) {
					console.log(response);
					document.getElementsByClassName(".newAgentName").value = "";
				  }, function errorCallback(response) {
					console.log("failed")
				  });
		} else {
			console.log("websocket way")	
			var object = {      
			     method : "POST",
			     url : location+'rest/messages',
			     object : JSON.stringify(aclMessageJSON)
			    }
			webSocket.send(JSON.stringify(object));
		}
		
		
	}
	
	$scope.stopAgent = function(aid) {
		console.log(aid)
		var aid = JSON.parse(angular.toJson(aid))
		if (checkRest()) {
			$http({
				  method: 'DELETE',
				  url: location+"rest/agents/running/"+JSON.stringify(aid)
				}).then(function successCallback(response) {
					console.log("success")
					getRunning()
				  }, function errorCallback(response) {
				    // called asynchronously if an error occurs
				    // or server returns response with an error status.
					  console.log("error")
				  });
			
		} else {
			console.log("websocket way")	
			var object = {      
			     method : "DELETE",
			     url : location+"rest/agents/running/"+encodeURIComponent(JSON.stringify(aid)),
			     object : ""
			    }
			webSocket.send(JSON.stringify(object));
		}
		
	}
	
	$scope.addReceiver = function() {
		$rootScope.receivers.push("");
		$rootScope.$apply;
	}
	
	$scope.removeReceiver = function(id) {
		console.log("removeReceiver")
		if (id > -1) {
			$rootScope.receivers.splice(id, 1);
		}
	}
	
	function checkRest() {
		if (document.getElementById("rest").checked == true) {
			console.log("it is rest")
			return true;
		} else if (document.getElementById("websocket").checked == true) {
			console.log("it is webscoket")
			return false;
		}
	}
		
	function getCenters() {
		if (checkRest()) {
		$http({
			  method: 'GET',
			  url: location+'rest/agents/centers'
			}).then(function successCallback(response) {
			    $scope.centers = response.data;
			    $scope.$apply;
			    //console.log($rootScope.centers)
			  }, function errorCallback(response) {
			    // called asynchronously if an error occurs
			    // or server returns response with an error status.
			  });
		} else {
			console.log("websocket way")	
			var object = {      
			     method : "GET",
			     url : location+'rest/agents/centers',
			     object : ""
			    }
			webSocket.send(JSON.stringify(object));
		}
	}
	
	function checkForUndefined(parameter) {
		if (parameter == undefined) {
			console.log(parameter)
			return "";
		} else {
			return parameter;
		}
	}
	
	function getRunning() {
		console.log("get running")
		if (checkRest()) {
			$http({
				  method: 'GET',
				  url: location+'rest/agents/running'
				}).then(function successCallback(response) {
				    $scope.running = response.data;
				    $scope.$apply;
				    //console.log($rootScope.running)
				  }, function errorCallback(response) {
				    // called asynchronously if an error occurs
				    // or server returns response with an error status.
				  });
		} else {
			console.log("websocket way")	
			var object = {      
			     method : "GET",
			     url : location+'rest/agents/running',
			     object : ""
			    }
			webSocket.send(JSON.stringify(object));
		}
	}
	
	function getClasses() {
		if (checkRest()) {
			$http({
				  method: 'GET',
				  url: location+'rest/agents/classes'
				}).then(function successCallback(response) {
				    $scope.types = response.data;
				    $scope.$apply;
				    //console.log($rootScope.types)
				  }, function errorCallback(response) {
				    // called asynchronously if an error occurs
				    // or server returns response with an error status.
				  });
		} else {
			console.log("websocket way")	
			var object = {      
			     method : "GET",
			     url : location+'rest/agents/running',
			     object : ""
			    }
			webSocket.send(JSON.stringify(object));
		}
		
	}
	
	function getPerformative() {
		if (checkRest()) {
			$http({
				  method: 'GET',
				  url: location+'rest/messages'
				}).then(function successCallback(response) {
				    $scope.performative = response.data;
				    $scope.$apply;
				    //console.log($rootScope.performative)
				  }, function errorCallback(response) {
				    // called asynchronously if an error occurs
				    // or server returns response with an error status.
				  });
		} else {
			console.log("websocket way")	
			var object = {      
			     method : "GET",
			     url : location+'rest/messages',
			     object : ""
			    }
			webSocket.send(JSON.stringify(object));
		}
	}
	
	function setWebSocket() {


		// Ensures only one connection is open at a time
        if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED){
        	console.log("already opened");
            return;
        }
        // Create a new instance of the websocket
        webSocket = new WebSocket("ws://"+hostport+"/AgentApp/agents");
         
        /**
         * Binds functions to the listeners for the websocket.
         */
        webSocket.onopen = function(event){
        	console.log("Connection opened");
            // For reasons I can't determine, onopen gets called twice
            // and the first time event.data is undefined.
            // Leave a comment if you know the answer.
            if(event.data === undefined)
                return;
            
            if (!checkRest()) {
            	getPerformative();
    			getCenters();
    			getClasses();
    			getRunning();
            }
        };
        
        webSocket.onmessage = function(event){
        	//console.log("Event: "+event.data)
        	if (event.data.startsWith("aid")) {
        		console.log("Event: "+event.data)
        		$scope.running = JSON.parse(event.data.substring(3))
        		setTimeout(function () {
        	        $scope.$apply(function () {
        	            $scope.message = "Timeout called!";
        	        });
        	    }, 2000);
        	} else if (event.data.startsWith("types")) {
        		$scope.types = JSON.parse(event.data.substring(5))
        		setTimeout(function () {
        	        $scope.$apply(function () {
        	            $scope.message = "Timeout called!";
        	        });
        	    }, 2000);
        	} else if (event.data.startsWith("center")) {
        		$scope.centers = JSON.parse(event.data.substring(6))
        		setTimeout(function () {
        	        $scope.$apply(function () {
        	            $scope.message = "Timeout called!";
        	        });
        	    }, 2000);
        	} else if (event.data.startsWith("perfomative")) {
        		$scope.performative = JSON.parse(event.data.substring(11))
        		setTimeout(function () {
        	        $scope.$apply(function () {
        	            $scope.message = "Timeout called!";
        	        });
        	    }, 2000);
        	} else {
        		$scope.consoleMessages = JSON.parse(event.data)
        		setTimeout(function () {
        	        $scope.$apply(function () {
        	            $scope.message = "Timeout called!";
        	        });
        	    }, 2000);
        	}
        	//$rootScope.$apply;
        	//$scope.$apply;
            
        };

        webSocket.onclose = function(event){
        	console.log("onClose")
        };
	}
	
	
});