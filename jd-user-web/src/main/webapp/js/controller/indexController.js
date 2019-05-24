app.controller('indexController',function($scope,indexService){
	
	//显示用户登录名
	$scope.showName = function(){
		indexService.showName().success(
			function(response){
				$scope.username = response.username;
			}
		);
	}
	
});