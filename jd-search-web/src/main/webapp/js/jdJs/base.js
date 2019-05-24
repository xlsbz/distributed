/*不带分页的基类模块*/
var app = angular.module("jd",[]);

//过滤器
app.filter('trustHtml',['$sce',function($sce){
	
	return function(data){
		 return $sce.trustAsHtml(data);
	}
}]);