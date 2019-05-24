app.controller("contentController",function($scope,contentService){
	
	$scope.contentList=[];
	$scope.findAllContent = function(categoryId){
		contentService.findCategoryById(categoryId).success(
			function(response){
				$scope.contentList[categoryId] = response;
			}
		);
	}
	
	//搜索
	$scope.keywords = "";
	$scope.search = function(){
		var key = $scope.keywords;
		if(key==""){
			$scope.keywords = "请输入您要的商品";
		}
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
});