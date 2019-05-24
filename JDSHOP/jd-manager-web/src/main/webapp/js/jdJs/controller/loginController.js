//登陆控制器
app.controller('loginController',function($scope,$http){
	//$controller("baseController",{$scope:$scope});
	
	$scope.showName = function(){
		$http.get('../login/loginName').success(
			function(response){
				$scope.loginName = response.loginName;
			}
		);
	}
});