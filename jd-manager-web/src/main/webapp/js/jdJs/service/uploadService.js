//文件上传
app.service('uploadSerivce',function($http){
	
	this.uploadFile = function(){
		var forData = new FormData();
		forData.append('file',file.files[0]);
		return $http({
			method:'POST',
			url:"../upload.do",
			data:forData,
			headers:{'Content-type':undefined},
			transformRequest:angular.identity
		});
	}
});