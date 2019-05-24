//文件上传服务
app.service("uploadSerivce",function($http){
	
	this.uploadFile = function(){
		var formData = new FormData();
		formData.append("file",file.files[0]);
		return $http({
			method:"POST",
			url:"../upload.do",
			data:formData,
			headers:{'Content-Type':undefined},//默认angular请求是json格式  必须转换为multiparty/form-data
			//将formData序列化
			transformRequest:angular.identity
			
		});
	}
	
	
	
});