app.service('indexService',function($http){
	
	//显示用户登录名
	this.showName = function(){
		return $http.get('../login/showName.do');
	}
});