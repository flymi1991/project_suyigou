app.controller("baseController", function ($scope) {
    //定义分页参数对象
    $scope.paginationConf = {
        curPage: 1,
        itemsPerPage: 10,
        totalItem: 10,
        perPageOptions: [10, 20, 30],
        onChange: function () {
            $scope.reloadPage();
        }
    };
    $scope.searchEntity = {};

    //重新加载页面
    $scope.reloadPage = function () {
        // $scope.findByPage($scope.pagipaginationConfnationConf.curPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.curPage, $scope.paginationConf.itemsPerPage, $scope.searchEntity);
    };

    //删除复选框
    //定义删除索引对象
    $scope.delList = [];
    //复选框改变调用函数
    $scope.changeDelList = function (event, id) {
        if (event.target.checked) {//若选中
            $scope.delList.push(id);
        } else {//若是取消勾选
            var index = $scope.delList.indexOf(id);
            $scope.delList.splice(index, 1);//参数1：移除的位置 参数2:移除的个数
        }
    };
})