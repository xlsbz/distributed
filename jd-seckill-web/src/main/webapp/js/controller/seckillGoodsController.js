 //控制层 
app.controller('seckillGoodsController' ,function($scope,$location,$interval,seckillGoodsService){	
	
	
	//查出当前秒杀的商品
	$scope.findList = function(){
		seckillGoodsService.findList().success(
			function(response){
				$scope.seckillList = response;
			}
		);
	}
	
	//立即抢购
	$scope.goShop = function(id){
		location.href="http://localhost:9109/seckill-item.html#?id="+id;
	}
	
	//从缓存查商品详情
	$scope.findOneRedis = function(){
		seckillGoodsService.findOneReids($location.search()['id']).success(
				function(response){
					$scope.seckill = response;
					//倒计时
					seconds = Math.floor((new Date($scope.seckill.endTime).getTime()-new Date().getTime())/1000);
					time = $interval(function(){
						if(seconds>0){
							seconds -= 1;
							$scope.timeString = conertTimeString(seconds);
						}else{
							$interval.cancel(time);
							alert("秒杀已结束");
						}
					},1000);
				}
			);
	}
	
	//转换时间
	conertTimeString = function(seconds){
		var day = Math.floor(seconds/(60*60*24));
		var hour = Math.floor((seconds-day*60*60*24)/(60*60));
		var minute = Math.floor((seconds-day*24*60*60-hour*60*60)/60);
		var second = Math.floor(seconds-day*60*60*24-hour*60*60-minute*60);
		
		var timeString = "";
		if(day>0){
			timeString = timeString+""+day+"天";
		}
		return timeString = timeString+hour+"小时"+minute+"分钟"+second+"秒";
	}
	
	
	//立即抢购
	$scope.submitOrder = function(){
		seckillGoodsService.submitOrder($scope.seckill.id).success(
			function(response){
				if(response.success){
					alert("抢购成功！请在一分钟内完成付款!");
					location.href="pay.html";
				}else{
					alert(response.message);
				}
			}
		);
	}
	
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		seckillGoodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	
	
	
	
	//分页
	$scope.findPage=function(page,rows){			
		seckillGoodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		seckillGoodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=seckillGoodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=seckillGoodsService.add( $scope.entity  );//增加 
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
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		seckillGoodsService.dele( $scope.selectIds ).success(
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
		seckillGoodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
