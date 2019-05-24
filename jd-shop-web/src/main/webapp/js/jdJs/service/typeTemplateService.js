//模板业务逻辑层
app.service('typeTemplateService',function($http){
	//查询所有
	this.findAll = function(){
		return $http.get('../typeTemplate/findAll.do');
	}
	
	//分页查询
	this.findPage = function(page,rows){
		return $http.get('../typeTemplate/findPage.do?page='+page+'&rows='+rows);
	}
	//搜索
	this.searchList=function(page,rows,searchEntity){
		return $http.post('../typeTemplate/search.do?page='+page+"&rows="+rows, searchEntity);
	}
	//保存
	this.add = function(entity){
		return $http.post('../typeTemplate/add.do',entity);
	}
	//查询单个
	this.findOne = function(id){
		return $http.get('../typeTemplate/findOne.do?id='+id);
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../typeTemplate/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		alert(ids)
		return $http.get('../typeTemplate/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../typeTemplate/search.do?page='+page+"&rows="+rows, searchEntity);
	}    
	//查询规格选项
	this.findSpecOption=function(id){
		return $http.get('../typeTemplate/findOptions.do?id='+id);
	}    
	
});