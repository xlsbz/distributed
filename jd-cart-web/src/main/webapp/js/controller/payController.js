 //控制层 
app.controller('payController' ,function($scope,$location,payService){	
	
	//支付
	$scope.createNative = function(){
		payService.createNative().success(
				function(response){
					//返回支付订单号和价格
					$scope.out_trade_no = response.out_trade_no;
					$scope.money = (response.total_fee/100).toFixed(2);
					//二维码
			    	var qr = new QRious({
			 		   element:document.getElementById('qrious'),
			 		   size:250,
			 		   level:'H',
			 		   value:response.code_url
			 		});				
				}
		);
	}
	
	//模拟订单支付成功失败
	$scope.payOK = function(status){
		if(status=='1'){
			payService.paySuccess($scope.out_trade_no).success(
				function(response){
					location.href="paysuccess.html#?money="+$scope.money;
				}
			);
			
		}else{
			location.href="payfail.html";
		}
	}
	
	//订单支付成功
	$scope.getMoney=function(){
		return $location.search()['money'];
	}
    
});	
