app.controller('itemController',function($scope,$http){

    //购物车数量增减
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

    //加载默认的sku

    $scope.loadSku = function(){
        $scope.sku = skuItemList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    }

    //匹配
    search = function () {
        for(var i=0;i<skuItemList.length;i++){
            if(match(skuItemList[i].spec,$scope.specList)){
                $scope.sku = skuItemList[i];
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