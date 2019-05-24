app.service('contentService',function($http){
	
	this.findCategoryById = function(id){
		return $http.get("../content/findCategoryContent.do?id="+id);
	}
});