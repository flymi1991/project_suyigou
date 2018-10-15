//控制层
app.controller('cartController', function ($scope, $controller, cartService) {
        $controller('baseController', {$scope: $scope});//继承

        //定义实体数据结构
        $scope.cartList = {};

        //查询收获地址
        $scope.findAddressesByUserId = function () {
            cartService.findAddressesByUserId().success(
                function (response) {
                    debugger;
                    $scope.addressList = response;
                    for (var i = 0; i < $scope.addressList.length; i++) {
                        var address = $scope.addressList[i];
                        if (address.isDefault == "1") {
                            $scope.address = address;
                            break;
                        }
                    }
                }
            )
        }

        //查询购物车列表
        $scope.findCartList = function () {
            cartService.findCartList().success(
                function (response) {
                    $scope.cartList = response;
                    $scope.totalValue = cartService.sum($scope.cartList);//求合计数
                    $scope.totalValue.totalMoney = $scope.totalValue.totalMoney.toFixed(2);
                }
            )
        }

        //添加商品到购物车
        $scope.addGoods2Cart = function (itemId, num) {
            cartService.addGoods2Cart(itemId, num).success(
                function (response) {
                    if (response.success) {
                        //如果添加成功,刷新列表
                        $scope.findCartList();
                    } else {
                        //如果添加失败，弹出错误信息
                        alert(reponse.msg);
                    }
                }
            )
        }

        //改变商品数量
        $scope.changeItemNum = function (itemId, num) {
            debugger;
            // cartSerivce.addGoods2Cart(itemId,)
        }

        //改变收货地址
        $scope.changeAddress = function (address) {
            $scope.address = address;
        }

        //判断当前地址是否选中
        $scope.isSelected = function (address) {
            return address == $scope.address;
        }

        //初始化支付方式为微信支付
        $scope.order = {paymentType: '1'};
        //改变支付方式
        $scope.changePayType = function (type) {
            $scope.order.paymentType = type;
        }

        //提交订单
        $scope.submitOrder = function () {
            debugger;
            $scope.order.receiverAreaName = $scope.address.address;//地址
            $scope.order.receiverMobile = $scope.address.mobile;//手机
            $scope.order.receiver = $scope.address.contact;//联系人
            cartService.submitOrder($scope.order).success(
                function (response) {
                    if (response.success) {
                        //页面跳转
                        if ($scope.order.paymentType == '1') {//如果是微信支付，跳转到支付页面
                            location.href = "pay.html";
                        } else {//如果货到付款，跳转到提示页面
                            location.href = "paysuccess.html";
                        }
                    } else {
                        alert(response.message); //也可以跳转到提示页面
                    }
                }
            );
        }
    }
)