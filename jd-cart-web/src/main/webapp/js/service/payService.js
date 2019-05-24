//服务层
app.service('payService',function($http){
	    	
	this.createNative = function(){
		return $http.get('../pay/createNative.do');
	}
	this.paySuccess= function(out_trade_no){
		return $http.get('../pay/paySuccess.do?out_trade_no='+out_trade_no);
	}
});
