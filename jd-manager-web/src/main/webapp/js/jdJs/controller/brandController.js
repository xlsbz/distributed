/*引入angular控制器*/
app.controller('brandController',function($scope,$http,$controller,brandService){
				
	//继承公用的控制器
	$controller('baseController',{$scope:$scope});
	
	//查询品牌列表
	$scope.findAll=function(){
		$http.get('../brand/findAllBrand').success(
			function(response){
				$scope.list=response;
			}		
		);				
	}
	
	
	//查询所有带分页 
	/* $scope.findPage=function(pageSize,pageNumber){
		$http.get('../brand/findAllBrandByPage?pageSize='+pageSize+'&pageNumber='+pageNumber).success(
			function(response){
				$scope.list=response.list;//显示当前页数据 	
				$scope.paginationConf.totalItems=response.total;//更新总记录数 
			}		
		);				
	} */
	
	//带条件查询带分页
	$scope.searchEntity = {};
	$scope.search = function(pageNumber,pageSize){
		brandService.search(pageSize,pageNumber,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;//显示当前页数据 	
				$scope.paginationConf.totalItems=response.total;//更新总记录数 
			}
		);
		
	}
	
	
	//新增
	$scope.save=function(){
		var methodName='saveBrand';//方法名 
		//修改
		if($scope.entity.id!=null){
			methodName='updateBrand';
			brandService.updateBrand(methodName,$scope.entity).success(
					function(response){
						if(response.status==200){
							alert(response.msg)
							$scope.reloadList();//刷新
						}else{
							alert("修改失败!")
						}				
					}		
				);
		}
		else{
			//保存
			brandService.saveBrand(methodName,$scope.entity).success(
				function(response){
					if(response.status==200){
						alert(response.msg)
						$scope.reloadList();//刷新
					}else{
						alert("新增失败!")
					}				
				}		
			);
		}
	}
	
	//数据回显
	$scope.findOne = function(id){
		brandService.findOne(id).success(
			function(response){
				if(response.status==200){
					$scope.entity = response.object;
				}
			}		
		);
		
	}
	
	
	//删除
	$scope.delBrand = function(){
		brandService.delBrand($scope.selectIds).success(
			function(response){
				if(response.status==200){
					alert(response.msg)
					$scope.reloadList();//刷新
				}else{
					alert("删除失败!")
					}				
				}			
			);
		}
		
	
	
});
