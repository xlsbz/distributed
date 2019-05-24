app.controller('dynItemController',function($scope,$http,$location){ 
//*****************非静态化操作***********************//
    $scope.loadItem = function(){
    	//得到传来的ID
    	$scope.itemId = $location.search()['id'];
    	$http.get('../pages/findByIdPage.do?id='+$scope.itemId).success(
    		function(response){
    			$scope.item = response.goods;
    			$scope.goodsDesc = response.goodsDesc;
    			
    			//商品分类
    			$scope.category01 =response.category01;
    			$scope.category02 =response.category02;
    			$scope.category03 =response.category03;
    			//<!--处理复杂类型数据-->
    			//<!--处理图片-->
    			$scope.imageList = JSON.parse(response.goodsDesc.itemImages);
    			//<!--处理扩展属性-->
    			//<#assign customAttributes=goodsDesc.customAttributeItems?eval>
    			$scope.customerAttributes= JSON.parse(response.goodsDesc.customAttributeItems);
    			//<!--处理规格列表-->
    			//<#assign specificationItems=goodsDesc.specificationItems?eval>
    			$scope.specificationItems = JSON.parse(response.goodsDesc.specificationItems);
    			
    			//加载默认sku
    			$scope.itemList = response.itemList;
    			$scope.sku = $scope.itemList[0];
    		    $scope.specList = JSON.parse($scope.sku.spec);
    		}
    	);
    }
    
    //购物车数量增减
    $scope.num=1;
    $scope.addNum = function(num){
        $scope.num = $scope.num+num;
        if($scope.num<1){
            $scope.num=1;
        }
    }
    
    //记录用户已经选择的规格
    $scope.specList = {};
    $scope.selectSpecList = function(name,value){
        //判断是否已经选中
        if($scope.isSelectSpec(name,value)){
            if($scope.specList!=null){
               delete $scope.specList[name];
            }
           // $scope.specList={};
        }else{
            $scope.specList[name] = value;
        }
        //用户选择规格后触发匹配方法
        search();
    }

    //判断是否选中
    $scope.isSelectSpec = function(name,value){
        if($scope.specList[name]==value){
            return true;
        }else{
            return false;
        }
    }
    

    $scope.loadSku = function(){
       
    }
    //匹配
    search = function () {
        for(var i=0;i<$scope.itemList.length;i++){
            if(match(JSON.parse($scope.itemList[i].spec),$scope.specList)){
                $scope.sku = $scope.itemList[i];
                return;
            }

        }
        $scope.sku={id:0,title:'-------',price:0};//如果没有匹配的
    }

    match = function(map1,map2){
        for(var k in map1){
            if(map1[k]!=map2[k]) {
                return false;
            }
        }

        for(var j in map2){
            if(map2[j]!=map1[j]){
                return false;
            }
        }
        return true;
    }

    //加入购物车
    $scope.addCart = function () {
    	$http.get('http://localhost:9107/cart/add2Cart.do?itemId='
				+ $scope.sku.id +'&num='+$scope.num,{'withCredentials':'true'}).success(
			function(response){
				if(response.success){
					location.href='http://localhost:9107/cart.html';//跳转到购物车页面
				}else{
					alert("加入购物车失败!");
				}
			});

    }
    
    
});