app.controller('cartController',function($scope,cartService,addressService){
	
	//显示用户登录名
	$scope.showName = function(){
		cartService.showName().success(
			function(response){
				
				$scope.username = response.username;
			}
		);
	}
	
	//查询购物车
	$scope.findAllCart = function(){
		cartService.findAll().success(
			function(response){
				$scope.cartList = response;
				//加载商品总计和数量
				$scope.totalNumAndValue = cartService.sumTotal($scope.cartList);
			}
		);
	}
	
	//添加购物车
	$scope.addCart = function(itemId,num){
		cartService.addCart(itemId,num).success(
			function(response){
				//刷新购物车
				$scope.findAllCart();
			}
		);
	}
	
	//根据用户ID查询地址
	$scope.findAllUserAddress = function(){
		cartService.findUserAddress().success(
			function(response){
				$scope.addressList = response;
				//设置默认选中地址
				for(var i=0;i<response.length;i++){
					if(response[i].isDefault=='1'){
						$scope.address = response[i];
					}
				}
			}
		);
	}
	
	//选中收件人
	$scope.selectAddress = function(address){
		$scope.address = address;
	}
	
	//判断是否选中
	$scope.isSelected = function(address){
		if(address==$scope.address){
			return true;
		}else{
			return false;
		}
	}
	
	//支付方式
	$scope.order={paymentType:'1'};
	$scope.selectPaymentType = function(type){
		$scope.order.paymentType = type;
	}
	
	//保存订单
	$scope.saveOrder = function(order){
		$scope.order.receiverAreaName=$scope.address.address;//地址
		$scope.order.receiverMobile=$scope.address.mobile;//手机
		$scope.order.receiver=$scope.address.contact;//联系人

		cartService.saveOrder($scope.order).success(
			function(response){
				if(response.success){
					//如果是微信支付去微信支付页面
					if($scope.order.paymentType=='1'){
						location.href="pay.html";
					}else{
						//货到付款
						location.href="paysuccess.html";
					}
				}else{
					alert(response.message);
				}
				
			}
		);
	}
	
	//保存 
	$scope.saveAddress=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=addressService.update( $scope.entity ); //修改  
		}else{
			serviceOb6ject=addressService.add( $scope.entity  );//增加 
		}	
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findAllUserAddress();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
}); 