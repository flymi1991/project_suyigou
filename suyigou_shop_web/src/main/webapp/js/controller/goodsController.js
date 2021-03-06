//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //首先定义实体结构
    $scope.entity = {
        goods: {},
        goodsDesc: {
            itemImages: [],//图片
            specificationItems: [],//规格列表
            customAttributeItems: []//自定义属性
        }
    }

    //定义搜索对象
    $scope.searchEntity = {};

    //商品状态

    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];

    //定义品牌列表
    $scope.itemCatList = [];

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function () {
        if (goodsId) {
            goodsService.findOne(goodsId).success(
                function (response) {
                    $scope.entity = response;
                    //回写富文本
                    editor.html($scope.entity.goodsDesc.introduction);
                    //回写图片
                    $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                    //扩展属性
                    $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                    //规格
                    $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                    //SKU表
                    for (var i = 0; i < $scope.entity.itemList.length; i++) {
                        $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                    }
                }
            );
        }
    };

    //保存
    $scope.save = function () {
        //提取文本编辑器的值
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    editor.html('');//将富文本初始化
                    //首先定义实体结构
                    $scope.entity = {
                        goods: {},
                        goodsDesc: {
                            itemImages: [],//图片
                            specificationItems: [],//规格列表
                            customAttributeItems: []//自定义属性
                        }
                    };//将实体初始化
                    //重新查询
                    location.href = 'goods.html';
                } else {
                    alert(response.message);
                }
            }
        );
    };

    //添加
    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(
            function (response) {
                if (!response.success) {
                    $scope.entity = {};
                    editor.html('');
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        )
    }

    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.msg;
                } else {
                    alert(response.msg);
                }
            }
        ).error(function (response) {
            alert("上传发生错误");
        })
    }


    //搜索
    // TODO: 2018/9/20 0:19 分页有问题
    // DONE: paginationConf变量名错误，跟代码生成器中的不一致
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    var itemCat = response[i];
                    $scope.itemCatList[itemCat.id] = itemCat.name;
                }
            }
        )
    }


    $scope.addImageEntity = function () {
        if (!$scope.entity.goodsDesc.itemImages) {
            $scope.entity.goodsDesc.itemImages = [];
        }
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //删除
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    //类别选择下拉框第1级
    $scope.findItemCatList1 = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCatList_1 = response;
                $scope.itemCatList_2 = [];
                $scope.itemCatList_3 = [];
            }
        )
    }
    //监听第1级下拉框，改变第2级下拉框
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        if (!goodsId) {
            //如果地址栏id值为空，才重置
            $scope.entity.goods.category2Id = "";
        }
        if (newValue) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCatList_2 = response;
                    $scope.itemCatList_3 = [];
                }
            )
        }
    })

    //监听第2级下拉框，改变第3级下拉框
    var goodsId = $location.search()['id'];
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        if (!goodsId) {
            //如果地址栏id值为空，才重置
            $scope.entity.goods.category3Id = "";
        }
        if (newValue) {//
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    // TODO: 2018/9/20 14:47 如果是修改数据，则不应该将数据置为空
                    $scope.itemCatList_3 = response;
                    if (!goodsId) {
                        $scope.entity.goods.category3Id = "";
                    }
                }
            )
        }
    });

    //监听第3级下拉框，改变模板
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {

        if (!goodsId) {
            //如果地址栏id值为空，才重置
            $scope.entity.goods.typeTemplateId = "";
        }
        if (newValue) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.goods.typeTemplateId = response.typeId;
                }
            )
        }
    })

    //监听模板ID，改变品牌和扩展属性
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        if (!goodsId) {
            //如果地址栏id值为空，才重置
            $scope.entity.goodsDesc.specificationItems = [];
            $scope.entity.goodsDesc.customAttributeItems = [];
        }
        if (newValue) {
            typeTemplateService.findOne(newValue).success(
                function (response) {
                    $scope.typeTemplate = response;
                    $scope.typeTemplate.brandList = JSON.parse($scope.typeTemplate.brandIds);
                    if ($scope.entity.goods.typeTemplateId != newValue) {
                        $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                    }
                }
            )
            typeTemplateService.findSpecList(newValue).success(
                function (respongse) {
                    $scope.specList = respongse;
                    // $scope.entity.goodsDesc.customAttributeItems = [];
                }
            )
        }
    });

    /*
        需求：
        集合：[{"attributeName":"网络制式","attributeValue":["移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["5.5寸","4.5寸"]}]
        判断集合中的是否包含attributeName为"网络制式"的选项
    */
    $scope.changeSpecOption = function ($event, specName, specOption) {

        var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, "attributeName", specName)
        //判断是否已经存在
        if (object) {
            //如果已经存在
            if ($event.target.checked) {
                //操作为添加
                object.attributeValue.push(specOption);
            } else {
                //操作为删除
                object.attributeValue.splice(object.attributeValue.indexOf(specOption), 1);
                if (object.attributeValue.length == 0) {
                    //如果删完了
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object), 1);
                }
            }
        } else {
            //如果不存在，就添加
            $scope.entity.goodsDesc.specificationItems.push(
                {"attributeName": specName, "attributeValue": [specOption]}
            );
        }
    }

    /*
    需求：
        根据集合 [{"attributeName":"网络制式","attributeValue":["移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["5.5寸","4.5寸"]}]
        动态生成SKU表
        {"spec":{},"xxx":{}};
        {"spec":{"网络制式":4寸},"xxx":{}};
     */

    //动态生成SKU列表
    $scope.createItemList = function () {
        //初始化实体
        $scope.entity.itemList = [{spec: {}, price: 0, status: 0, num: 9999, isDefault: '0'}];
        var specificationItems = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < specificationItems.length; i++) {
            $scope.entity.itemList = addColumn(
                $scope.entity.itemList,
                specificationItems[i].attributeName,
                specificationItems[i].attributeValue
            );
        }
    }
    //添加行的局部方法
    addColumn = function (oldList, attributeName, attributeValues) {
        var newList = [];
        for (var i = 0; i < oldList.length; i++) {
            var oldRow = oldList[i];
            for (var j = 0; j < attributeValues.length; j++) {
                //{"网络制式":4寸}
                //attributeName:attributeValue
                //深克隆
                var newRow = JSON.parse(JSON.stringify(oldRow));
                newRow.spec[attributeName] = attributeValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

    //判断复选框是否选中
    $scope.checkAttributeValue = function (text, optionName) {

        var items = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectByKey(items, "attributeName", text);
        if ((obj != null) && (obj['attributeValue'].indexOf(optionName) >= 0)) {
            //存在text规格(比如内存大小)
            return true;
        }
    }
})
