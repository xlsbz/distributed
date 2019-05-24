app.service('cartService',function($http){
	
	//显示用户登录名
	this.showName = function(){
		return $http.get('../cart/showName.do');
	}
	
	//查询所有购物车商品
	this.findAll = function(){
		return $http.get('../cart/findAllCart.do');
	}
	//添加商品到购物车
	this.addCart = function(itemId,num){
		return $http.get('../cart/add2Cart.do?itemId='+itemId+'&num='+num);
	}
	
	//计算所有商品和总共价格
	this.sumTotal = function(cartList){
		var total = {numTotal:0,priceTotal:0.00};
		for(var i=0;i<cartList.length;i++){
			for(var j=0;j<cartList[i].orderItemList.length;j++){
				total.numTotal += cartList[i].orderItemList[j].num;
				total.priceTotal += cartList[i].orderItemList[j].totalFee;
			}
		}
		return total;
	}
	
	//查询所有用户地址
	this.findUserAddress = function(){
		return $http.get('../address/findUserAddress.do');
	}
	
	//保存订货
	this.saveOrder = function(order){
		return $http.post('../order/add.do',order);
	}
	
	
});