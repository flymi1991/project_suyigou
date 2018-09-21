//控制层
app.controller('contentController', function ($scope, $controller, contentService) {

    $controller('baseController', {$scope: $scope});//继承
    //定义实体数据结构

    //ContentCatgoryList
    $scope.contentList = [];

    //查询广告分类列表
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
            }
        )
    }
})