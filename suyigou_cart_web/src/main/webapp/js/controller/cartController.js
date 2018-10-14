//控制层
app.controller('cartController', function ($scope, $controller, cartService) {
        $controller('baseController', {$scope: $scope});//继承


        //定义实体数据结构
        $scope.cartList = {};

        //查询购物车列表
        $scope.findCartList = function () {
            cartService.findCartList().success(
                function (response) {
                    $scope.cartList = response;
                    debugger;
                    $scope.totalValue = cartService.sum($scope.cartList);//求合计数
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
    }
)