//服务层
app.service('contentService',function($http){
	//根据categoryId查询
    this.findByCategoryId = function (categoryId) {
        return $http.get('content/findByCategoryId.do?categoryId='+categoryId);
    }
})
