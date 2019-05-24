//控制器
app.controller("typeTemplateController",function($scope,$controller,typeTemplateService,brandService,specificationService){
	//继承controller
	$controller('baseController',{$scope:$scope});
	
	//读取列表的数据
	$scope.findAll = function(){
		typeTemplateService.findAll().success(
				function(response){
					$scope.entity = response;
				}
		);
	}
	
	//分页查询所有
	$scope.findPage=function(page,rows){
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}	
		);
	}
	//搜索
	$scope.searchEntity={};//定义搜索对象 
	$scope.search=function(page,rows){			
		typeTemplateService.searchList(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询列表
	//定义一个下拉选择列表  {data:[{id:1,text:"三星"},{id:2,text:"小米"},{id:3,text:"苹果"}]}
	$scope.brandList = {data:[]};
	$scope.findBrandlist = function(){
		brandService.findBrandList().success(
			function(response){
				$scope.brandList = {data:response};
			}
		);
	}
	
	$scope.specificationList = {data:[]};
	$scope.findSpecList = function(){
		specificationService.findSpecList().success(
			function(response){
				$scope.specificationList = {data:response};
			}
		);
	}
	
	//新增扩展属性行
	$scope.newRowTable = function(){
		$scope.entity.customAttributeItems.push({});
	}
	//删除扩展属性行
	$scope.delRowTable = function(index){
		$scope.entity.customAttributeItems.splice(index,1);
	}
	
	//保存
	$scope.save = function(){
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);		
	}
	
	//查询单个
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//处理json字符串
				$scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
				$scope.entity.specIds = JSON.parse($scope.entity.specIds);
				$scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
				
			}
		);				
	}
	
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//处理json
	$scope.jsonToString = function(jsonString,key){
		//获得json字符串转为json对象
		var j = JSON.parse(jsonString);
		var value="";
		for(var i=0;i<j.length;i++){
			if(value.length>0){
				value+=",";
			}
			value += j[i][key];
			//value += j[i].text;
		}
		return value;
	}
});