//服务层
app.service('payService',function($http){
	    	
	this.createNative = function(){
		return $http.get('../paySeckill/createNative.do');
	}
	this.paySuccess= function(out_trade_no){
		return $http.get('../paySeckill/paySuccess.do?id='+out_trade_no);
	}
});
