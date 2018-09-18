//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //面包屑功能
    $scope.grade = 1;//面包屑等级
    $scope.setGrade = function (value) {
        $scope.grade = value;
    }

    //p_entity为父类entitty对象
    $scope.setList = function (p_entity) {
        if ($scope.grade == 1) {
            $scope.gradeList_2 = null;
            $scope.gradeList_3 = null;
        } else if ($scope.grade == 2) {
            $scope.gradeList_2 = p_entity;
            $scope.gradeList_3 = null;
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
        } else if ($scope.grade == 3) {
            $scope.gradeList_3 = p_entity;
            $scope.entity_2 = p_entity;
        }
        $scope.findByParentId(p_entity.id);
    }

    //记住父类id
    $scope.parentId = 0;
    //根据父类id查询
    $scope.findByParentId = function (parentId) {
        itemCatService.findByParentId(parentId).success(
            function (response) {
                $scope.parentId = parentId;
                $scope.list = response;
            }
        );
    }
    //查找父类
    $scope.findParent = function (parentId) {
        alert(123);
    }
    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                alert($scope.entity)
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            $scope.entity.parentId = $scope.parentId;
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    // $scope.reloadList();
                    $scope.findByParentId($scope.parentId);//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        alert($scope.selectIds);
        //获取选中的复选框
        itemCatService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.findByParentId($scope.parentId);//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
});	
