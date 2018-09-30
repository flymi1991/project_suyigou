app.controller("baseController", function ($scope) {
    //定义分页参数对象
    $scope.paginationConf = {
        curPage: 1,
        itemsPerPage: 3,
        totalItem: 8,
        perPageOptions: [3, 6, 9],
        onChange: function () {
            $scope.reloadList();
        }
    };
    $scope.searchEntity = {};

    //重新加载页面
    $scope.reloadList = function () {
        // TODO: 2018/9/30 17:30 有个bug，当只改变时每页条数时，是否应该让当前页变为1
        $scope.search($scope.paginationConf.curPage, $scope.paginationConf.itemsPerPage);
    };

    //删除复选框
    //定义删除索引对象
    $scope.selectIds = [];
    //复选框改变调用函数
    $scope.changeDelList = function (event, id) {
        if (event.target.checked) {//若选中
            $scope.selectIds.push(id);
        } else {//若是取消勾选
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1);//参数1：移除的位置 参数2:移除的个数
        }
    };
    //提取 json 字符串数据中某个属性，返回拼接字符串 逗号分隔
    $scope.jsonToString = function (jsonString, key) {
        var value = "";
        var arr = JSON.parse(jsonString);
        for (var id = 0; id < arr.length; id++) {
            if (id != 0) {
                value += ",";
            }
            value += arr[id][key];
        }
        return value;
    }
})