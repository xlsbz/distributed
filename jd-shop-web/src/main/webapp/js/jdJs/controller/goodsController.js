 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadSerivce,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
		//获取传来的商品ID
		var id = $location.search()['id'];
		if(id==null){
			return ;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//回显富文本编辑器
				editor.html($scope.entity.goodsDesc.introduction);
				//读取图片信息
				$scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
				//读取扩展属性
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//读取规格列表
				$scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
				//读取sku列表
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec= JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}
	
	//根据规格名称和选项名判断是否被勾选
	$scope.isChecked = function(name,value){
		//获取商品规格列表
		var itemList = $scope.entity.goodsDesc.specificationItems;
		//搜索有没有这个对象
		var object = $scope.searchObjectByKey(itemList,'attributeName',name);
		if(object==null){
			return false;
		}else{
			//判断这个对象规格的选项有哪些被勾选
			if(object.attributeValue.indexOf(value)!=-1){
				return true;
			}else{
				return false;
			}
		}
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			//获取富文本编辑器的内容
			$scope.entity.goodsDesc.introduction = editor.html();
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			//获取富文本编辑器的内容
			$scope.entity.goodsDesc.introduction = editor.html();
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
					alert("保存成功!");
					//清空数据
					editor.html("");
		        	$scope.entity = {};
		        	location.href="goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	
	//文件上传
	$scope.uploadFile = function(){
		uploadSerivce.uploadFile().success(
			function(response){
				if(response.success){
					//回显图片
					$scope.imageEntity.url = response.message;
				}else{
					alert("上传失败!");
				}
			}
		).error(
			function() {           
	        	alert("上传发生错误");
	     });    

	}
	
	//图片列表
	$scope.entity = {goodsDesc:{itemImages:[]}};
	$scope.addImageEntity = function(){
		$scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
	}
	//删除
	$scope.deleImage = function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	
	
	
	//读取一级分类的列表
	$scope.findFirstCate = function(){
		itemCatService.findNextCat(0).success(
			function(response){
				$scope.itemCatList1 = response;
			}
		);
	}
	
	
	//设置监听器，监听一级分类的变化
	$scope.$watch('entity.goods.category1Id',function(newValue,oleValue){
		itemCatService.findNextCat(newValue).success(
				function(response){
					$scope.itemCatList2 = response;
				}
		);
	});
	
	//监听二级分类的变化
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
		itemCatService.findNextCat(newValue).success(
				function(response){
					$scope.itemCatList3 = response;
				}
		);
	});
	
	//监听模板ID
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
		itemCatService.findOne(newValue).success(
			function(response){
				$scope.entity.goods.typeTemplateId = response.typeId;
			}
		);
		
	});
	
	//监听模板ID
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		typeTemplateService.findOne(newValue).success(
			function(response){
				 $scope.typeTemplate=response;//获取类型模板
      			 $scope.typeTemplate.brandIds= JSON.parse( $scope.typeTemplate.brandIds);//品牌列表
      			 var id = $location.search()['id'];
      			 if(id==null){
      				$scope.entity.goodsDesc.customAttributeItems= JSON.parse($scope.typeTemplate.customAttributeItems);
      			}
      			 
			}
		);
		
		//查询规格选项
		typeTemplateService.findSpecOption(newValue).success(
				function(response){
					$scope.specList = response;
				}
		);
	});
	

	//根据集合按照key查询对象
	$scope.searchObjectByKey = function(list,key,keyValue){
		for(var i=0;i<list.length;i++){
			if(list[i][key]==keyValue){
				return list[i];
			}
		}
		return null;
	}
	
	
	//定义一个对象
	//specificationItems = [{'attributeName':key,attibuteValues:[{},{}]}];
	$scope.entity = {goodsDesc:{itemImages:[],specificationItems:[]}};
	//更新复选框
	$scope.updateSpec = function($event,name,value){
		//搜索集合中是否存在这个规格对象
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		//如果不存在直接push
		if(object!=null){
			//判断是不是取消勾选
			if($event.target.checked){
				object.attributeValue.push(value);
			}else{
				//移除
				object.attributeValue.splice(object.attributeValue.indexOf(value,1));
				//如果都取消完了，就删除该子项
				if(object.attributeValue.length<=0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({'attributeName':name,'attributeValue':[value]});
		}
	}
	
	
	
	//创建sku列表
	$scope.createItemList = function(){
		//初始化
		$scope.entity.itemList = [{spec:{},price:'0',num:'9999',status:'0',isDefault:'0'}];
		var item = $scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<item.length;i++){
			$scope.entity.itemList = addRow($scope.entity.itemList,item[i].attributeName,item[i].attributeValue);
		}
	}
	
	
	//深克隆
	addRow = function(list,colName,colValues){
		//创建一个新对象
		var newList = [];
		for(var i=0;i<list.length;i++){
			var oldRow = list[i];
			for(var j=0;j<colValues.length;j++){
				//克隆
				var newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[colName] = colValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}
	//存储商品状态
	$scope.goodsStatus=['未审核','已审核','审核未通过','关闭'];
	
	//查询所有分类
	$scope.itemCatList = [];
	$scope.findAllCate = function(){
		itemCatService.findAll().success(
			function(response){
				for(var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id]=response[i].name;
				}
			}
		);
	}
	
	//提交审核

	$scope.updateStatus = function(status){
		goodsService.updateStatusById($scope.selectIds,status).success(
				function(response){
					if(response.success){
						alert("操作成功!");
						$scope.selectIds=[];
						location.href="goods.html";
					}else{
						alert("操作成功!");
					}
				}
			);
	}
	
	$scope.goodsMarketable = ['已下架','已上架'];
	//上下架商品
	$scope.updateMarketable = function(status){
		goodsService.updateMarketable($scope.selectIds,status).success(
				function(response){
					if(response.success){
						alert(response.message);
						$scope.selectIds=[];
						location.href="goods.html";
					}else{
						alert(response.message);
					}
				}
			);
	}
	
});	
