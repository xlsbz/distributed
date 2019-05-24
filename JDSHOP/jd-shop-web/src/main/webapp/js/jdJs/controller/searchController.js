app.controller('searchController',function($scope,$location,searchService){
	
	//定义搜索条件
	$scope.searchMap = {'keywords':'','category':'','brand':'','price':'','pageNumber':1,'pageSize':30,'sort':'','sortField':'','spec':{}};
	//执行添加搜索项
	$scope.addSearchMap = function(key,value){
		if(key=='brand'||key=='category'||key=='price'){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		//执行搜索
		$scope.search();
	}
	//移除属性
	$scope.removeSearchMap = function(key){
		if(key=='brand'||key=='category'||key=='price'){
			$scope.searchMap[key]="";
		}else{
			delete $scope.searchMap.spec[key];
		}
		//执行搜索
		$scope.search();
	}
	
	
	//构建分页工具栏
	$scope.bulidPage = function(){
		$scope.pageLable = [];
		//总页数
		var totalPages = $scope.resultMap.totalPages;
		var firstPage = 1;
		var lastPage = totalPages;
		
		//搜索圆点
		$scope.firstPonit = false;
		$scope.endPonit = false;
		//判断是否足够五页
		if($scope.resultMap.totalPages>5){
			//如果当前页<=3显示前五页
			if($scope.searchMap.pageNumber<=3){
				lastPage = 5;
				$scope.endPonit = true;
			}
			//如果当前页-总页数<-2 显示后五页
			else if($scope.searchMap.pageNumber-$scope.resultMap.totalPages>-2){
				firstPage = $scope.resultMap.totalPages-4;
				$scope.firstPonit = true;
			}
			else{
				//显示当前页的前两页 后两页
				firstPage = $scope.searchMap.pageNumber-2;
				lastPage = $scope.searchMap.pageNumber+2;
				$scope.firstPonit = true;
				$scope.endPonit = true;
			}
		}else{
			//不足五页显示全部
			lastPage =  $scope.resultMap.totalPages;
		}
		//遍历填充页码
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLable.push(i);
		}
		
	}
	//分页查询
	$scope.toPage = function(pageNo){
		$scope.searchMap.pageNumber = parseInt(pageNo);
		if(pageNo<1){
			$scope.searchMap.pageNumber = 1;
			return ;
		}
		if($scope.searchMap.pageNumber>$scope.resultMap.totalPages){
			$scope.searchMap.pageNumber = $scope.resultMap.totalPages;
			return ;
		}
		
		$scope.search();
	}
	
	
	//处理排序
	$scope.sortFieldSearch = function(sort,sortField){
		$scope.searchMap.sort=sort;
		$scope.searchMap.sortField = sortField;
		$scope.search();
	}
	
	//判断关键字是否包含品牌
	$scope.keywordsIsBrand = function(){
		for(var i=0;i<$scope.resultMap.brandList.length;i++){
			//遍历品牌
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)!=-1){
				return true;
			}
		}
		return false;
		
	}
	
	//接受首页的搜索请求和数据
	$scope.loadkeywords = function(){
		$scope.searchMap.keywords = $location.search()['keywords'];
		$scope.search();
	}
	
	//搜索商品
	$scope.search = function(){
		searchService.search($scope.searchMap).success(
			function(response){
				$scope.resultMap = response;
				//构建分页
				//$scope.bulidPage();
			}
		);
		
	}
	
	
	
	
});