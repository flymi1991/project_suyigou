//自定义服务
app.service("brandService", function ($http) {
    //查询所有
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    }
    //分页查询
    this.findByPage = function (curPage, size) {
        return $http.get('../brand/findByPage.do?curPage=' + curPage + '&size=' + size);
    }

    //按ID查询一个
    this.findById = function (id) {
        return $http.get('../brand/findById.do?id=' + id);
    }
    //删除
    this.delete = function (ids) {
        return $http.post('../brand/delete.do', ids);
    }
    //搜索
    this.search = function (curPage, size, entity) {
        return $http.post("../brand/search.do?curPage=" + curPage + "&size=" + size, entity);
    }
    //添加
    this.add = function (entity) {
        return $http.post('../brand/save.do', entity);
    }
    //更新
    this.update = function (entity) {
        return $http.post('../brand/update.do', entity);
    }

    //下拉选择数据源
    this.selectOptionList = function () {
        return $http.get("../brand/selectOptionList.do");
    }
})