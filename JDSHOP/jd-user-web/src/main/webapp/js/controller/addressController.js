 //控制层 
app.controller('addressController' ,function($scope ,indexService,addressService){	
	
	//$controller('baseController',{$scope:$scope});//继承
	//显示用户登录名
	$scope.showName = function(){
		indexService.showName().success(
			function(response){
				$scope.username = response.username;
			}
		);
	}
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		addressService.findAll().success(
			function(response){
				$scope.list=response;
				$scope.showName();
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		addressService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		addressService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	
	
	 
	//批量删除 
	$scope.dele=function(id){		
		//获取选中的复选框			
		addressService.dele(id).success(
			function(response){
				if(response.success){
					alert("删除成功!");
					location.href="home-setting-address.html";
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		addressService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//保存 
	$scope.saveAddress=function(){		
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=addressService.update( $scope.entity ); //修改  
		}else{
			serviceObject=addressService.add( $scope.entity  );//增加 
		}	
		serviceObject.success(
			function(response){
				alert("操作成功!");
				location.href="../home-setting-address.html";
			}		
		);				
	}
	
    
});	
