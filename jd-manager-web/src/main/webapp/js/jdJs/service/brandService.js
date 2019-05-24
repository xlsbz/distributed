/*引入angular服务层*/
//创建品牌服务
app.service('brandService',function($http){
	//查询所有品牌
	this.search = function(pageSize,pageNumber,searchEntity){
		return $http.post('../brand/searchBrand?pageSize='+pageSize+'&pageNumber='+pageNumber,searchEntity);
	}
	
	//新增品牌
	this.saveBrand = function(methodName,entity){
		return $http.post('../brand/'+methodName,entity);
	}
	//修改
	this.updateBrand = function(methodName,entity){
		return $http.post('../brand/'+methodName,entity);
	}
	//数据回显
	this.findOne  = function(id){
		return $http.get('../brand/findBrandById/'+id);
	}
	//删除
	this.delBrand = function(ids){
		return $http.get('../brand/deleteBrand?ids='+ids);
	}
	//查询品牌
	this.findBrandList = function(){
		return $http.get('../brand/findBrandList')
	}
});