app.controller('searchController', function ($scope, $location, searchService) {
        //构建查询map实体
        // $scope.searchMap = {keywords: '', category: '', brand: '', spec: {}};
        $scope.initSearch = function () {
            $scope.searchMap = {
                'keywords': '', 'category': '', 'brand': '', 'spec': {}, 'price': '',//筛选条件
                'pageNo': '1', 'pageSize': '20', //分页参数
                'sortField': '', 'searchMap': ''//排序参数
            };
        }

        //变更查询条件
        $scope.alterSearchMap = function (key, value) {
            if (['category', 'brand', 'price'].indexOf(key) != -1) {
                //如果改变的是brand或category或price
                $scope.searchMap[key] = value;
            } else {
                //否则就是spec
                $scope.searchMap.spec[key] = value;
            }
            //提交查询
            $scope.search();
        }
        //设置排序规则
        $scope.sortSearch = function (sortField, sortMethod) {
            $scope.searchMap.sortField = sortField;
            $scope.searchMap.sortMethod = sortMethod;
            $scope.search();
        }

        $scope.removeSearchItem = function (key) {
            if (['category', 'brand', 'price'].indexOf(key) != -1) {
                //如果改变的是brand或category
                $scope.searchMap[key] = "";
            } else {
                //否则就是spec
                delete $scope.searchMap.spec[key];
            }
            //提交查询
            $scope.search();
        }

        //搜索
        $scope.search = function () {
            searchService.search($scope.searchMap).success(
                function (response) {
                    $scope.resultMap = response;//搜索返回的结果
                    $scope.resultMap.pageNo = parseInt($scope.resultMap.pageNo);
                    buildPageLabel();
                }
            )
        }
        //构建分页标签
        buildPageLabel = function () {
            $scope.pageLabel = [];
            $scope.maxPageNo = $scope.resultMap.totalPages;//总页数
            //控制省略号
            $scope.firstDot = true;//前面有点
            $scope.lastDot = true;//后边有点

            $scope.lastPage = $scope.maxPageNo;
            $scope.startPage = 1;//起始页
            if ($scope.maxPageNo > 5) {
                if ($scope.searchMap.pageNo <= 3) {
                    //如果当前页小于等于3
                    $scope.lastPage = 5;
                    $scope.firstDot = false;//前面没点
                } else if ($scope.searchMap.pageNo >= $scope.maxPageNo - 2) {
                    //如果当前页为最后3页之一
                    $scope.startPage = $scope.maxPageNo - 4;
                    $scope.lastDot = false;//后面没点
                } else {//显示以当前页为中心的5页
                    $scope.startPage = $scope.resultMap.pageNo - 2;
                    $scope.lastPage = $scope.resultMap.pageNo + 2;
                }
            } else {
                $scope.firstDot = false;//前面没点
                $scope.lastDot = false;//后边没点
            }
            for (var i = $scope.startPage; i <= $scope.lastPage; i++) {
                $scope.pageLabel.push(i);
            }
        }

        $scope.searchPage = function (pageNo) {
            if (pageNo == $scope.searchMap.pageNo && (pageNo < 1 || pageNo > $scope.resultMap.totalPages)) {
                return;
            }
            $scope.searchMap.pageNo = pageNo + "";
            $scope.searchMap.pageSize = $scope.resultMap.pageSize;
            $scope.search();
        }

        //判断当前页为第一页
        $scope.isTopPage = function () {
            if ($scope.searchMap.pageNo == 1) {
                return true;
            } else {
                return false;
            }
        }

        //判断当前页是否未最后一页
        $scope.isEndPage = function () {
            if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
                return true;
            } else {
                return false;
            }
        }

        //判断是否是当前页
        $scope.isCurPage = function (pageNo) {
            if ($scope.searchMap.pageNo == pageNo) {
                return true;
            } else {
                return false;
            }
        }
        //判断关键字是不是品牌
        $scope.keywordsIsBrand = function () {
            debugger;
            if ($scope.resultMap) {
                var keywords = $scope.searchMap.keywords;
                //判断keywords在brandList中是否存在
                for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
                    var exits = $scope.resultMap.brandList[i]['text'] == keywords;
                    if (exits) {
                        return true;
                    }
                }
                return false;
            }
        }

        //加载地址栏参数，接受搜索的关键字
        $scope.locateKeywords = function () {
            $scope.searchMap.keywords = $location.search()['keywords'];
            $scope.search();
        }
    }
)