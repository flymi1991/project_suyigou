//搜索服务层
app.service("searchService", function ($http) {
    this.search = function (searchMap) {
        debugger;
        return $http.post('itemsearch/search.do', searchMap);
    }
});