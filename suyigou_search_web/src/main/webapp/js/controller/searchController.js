app.controller('searchController', function ($scope, searchService) {
    //构建查询map实体
    // $scope.searchMap = {keywords: '', category: '', brand: '', spec: {}};
    $scope.initSearch = function () {
        $scope.searchMap = {keywords: '', category: '', brand: '', spec: {}};
    }

    //变更查询条件
    $scope.alterSearchMap = function (key, value) {
        if ('category' == key || 'brand' == key) {
            //如果改变的是brand或category
            $scope.searchMap[key] = value;
        } else {
            //否则就是spec
            $scope.searchMap.spec[key] = value;
        }
        debugger;
        //提交查询
        $scope.search();
    }

    $scope.removeSearchItem = function (key) {
        debugger;
        if ('category' == key || 'brand' == key) {
            //如果改变的是brand或category
            $scope.searchMap[key] = "";
        } else {
            //否则就是spec
            delete $scope.searchMap.spec[key];
        }
        debugger;
        //提交查询
        $scope.search();
    }

    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//搜索返回的结果
            }
        )
    }
})