 //控制层 
app.controller('itemCatController' ,function($scope,$http,$controller,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){	
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  
		//赋予上级ID
		$scope.entity.parentId =$scope.parentId;
		if($scope.entity.id!=null){//如果有ID
			serviceObject=$http.post('../itemCat/update.do',$scope.entity ); //修改  
		}else{
			serviceObject=$http.post('../itemCat/add.do',$scope.entity ); 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findNextCat($scope.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	//删除单个
	$scope.deleOne=function(id){			
		//获取选中的复选框			
		itemCatService.deleOne(id).success(
				function(response){
					if(response.success){
						$scope.findNextCat($scope.parentId);//刷新列表
					}						
				}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	//查询下级
	$scope.findNextCat = function(id){
		$scope.parentId=id;//记住上级ID
		itemCatService.findNextCat(id).success(
				function(response){
					$scope.list = response;
				}
		);
	}
	
	//查询所有模板
	$scope.typeIds = {data:[]};
	$scope.findAllTemplate = function(){
		itemCatService.findTemplate().success(
				function(response){
					$scope.typeIds = {data:response};
				}
		);
	}
	//根据id查询模板 
	$scope.findTemplate = function(typeId){
		typeTemplateService.findOne(typeId).success(
				function(response){
					//赋值给默认选项
					$("#select2").val(response.id).trigger('change');
				}	
		);
	}
	
	//面包导航
	//定义级别
	$scope.grade = 1;
	$scope.setGrade = function(value){
		$scope.grade = value;
	}
	
	//定义点击查询下级事件
	$scope.findCat = function(c_entity){

		//现在是顶级位置
		if($scope.grade==1){
			$scope.category_1=null;
			$scope.category_2=null;
		}
		//现在是二级分类
		if($scope.grade==2){
			$scope.category_1=c_entity;
			$scope.category_2=null;
		}
		//现在是三级分类
		if($scope.grade==3){
			$scope.category_2=c_entity;
		}
		
		//查询数据
		$scope.findNextCat(c_entity.id);
	}
});	
