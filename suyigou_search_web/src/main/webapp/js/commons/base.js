// 定义模块，没有分页模块
var app = angular.module("suyigou", []);

app.filter("trustHtml",['$sce',function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}])