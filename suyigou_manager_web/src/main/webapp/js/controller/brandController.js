app.controller("brandController", function ($scope, $controller, brandService) {

    $controller('baseController', {$scope: $scope});

    //查找所有
    $scope.findAll = function () {
        brandService.findAll().success(function (result) {
            $scope.list = result.rows;
            $scope.paginationConf.totalItem = result.total;
        })
    };

    //分页查询
    $scope.findByPage = function (curPage, size) {
        brandService.findByPage(curPage, size).success(function (result) {
            $scope.list = result.rows;
            $scope.paginationConf.totalItem = result.total;
        });
    };

    //查找一个
    $scope.findById = function (id) {
        brandService.findById(id).success(function (result) {
            $scope.brand2save = result;
        });
    };

    //搜索
    $scope.search = function (curPage, size, entity) {
        brandService.search(curPage, size, entity)
            .success(function (result) {
                $scope.list = result.rows;
                $scope.paginationConf.totalItem = result.total;
            });
    };
    //保存
    $scope.save = function () {
        var res = null;
        if ($scope.brand2save.id == null) {
            //如果为空，则为添加
            res = brandService.add($scope.brand2save);
        } else {
            res = brandService.update($scope.brand2save);
        }
        res.success(function (result) {
                if (!result.success) {
                    alert(result.msg);
                }
                $scope.reloadList();
            }
        )
    };

    //删除
    $scope.delete = function () {
        brandService.delete($scope.delList).success(
            function (result) {
                if (!result.success) {
                    alert(result.msg);
                }
                $scope.delList = [];
                $scope.reloadList();
            }
        )
    };

})