//���Ʋ�
app.controller('itemController', function ($scope, $controller, $http) {

    //��������
    $scope.num = 1;
    //��¼ѡ�еĹ��
    $scope.specificationItems = {};
    //ѡ�е�SKU
    $scope.sku = {};
    //$scope.specificationItems={"��������":["4g","8g"]};
    //��ѡ�еĹ������б�
    $scope.selectSpec = function (attribute, value) {
        if ($scope.isSelected(attribute, value)) {
            //�Ѿ�ѡ����Ҫ�Ƴ�
            delete $scope.specificationItems[attribute];
        } else {
            //δ��ѡ����Ҫ���
            $scope.specificationItems[attribute] = value;
            $scope.searchSku();
        }
    }
    //�ж�ĳ������Ƿ�ѡ��
    $scope.isSelected = function (attribute, value) {
        var flag = $scope.specificationItems[attribute] == value;
        //true���Ѿ�ѡ��false��δ��ѡ��
        return flag;
    }
    //��������
    $scope.addNum = function (x) {
        $scope.num = $scope.num + x;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }

    //����Ĭ�ϵ�SKU
    $scope.loadSku = function () {
        debugger;
        //����Ĭ��SKU
        $scope.sku = skuList[0];
        //����Ĭ�Ϲ��
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    }

    $scope.searchSku = function () {
        debugger;
        for (var i in skuList) {
            if (matchMap(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];
                return;
            }
        }
        $scope.sku = {"id": 0, "price": "---------", "title": "not exist"};
    }
    matchMap = function (map1, map2) {
        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }
        for (var k in map2) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }
        return true;
    }
    //加入购物车
    $scope.add2Cart = function () {
        console.log("add2Cart:" + $scope.sku.id);
        console.log("total:" + $scope.num);
        console.log("totalPrice:" + $scope.sku.price * $scope.num);
        alert($scope.sku.id);
        $http.get('http://localhost:9107/cart/add.do?itemId=' + $scope.sku.id + '&num=' + $scope.num,{'withCredentials':true}).success(
            function (response) {
                if (response.success) {
                    location.href = 'http://localhost:9107/cart.html';//跳转到购物车页面
                } else {
                    alert("添加错误");
                }
            }
        )
    }
});	
