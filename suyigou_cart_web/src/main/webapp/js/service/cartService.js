//服务层
app.service('cartService', function ($http) {

        //读取购物车列表
        this.findCartList = function () {
            debugger;
            return $http.get('cart/findCartList.do');
        }

        //添加商品到购物车
        this.addGoods2Cart = function (itemId, num) {
            return $http.get('cart/add.do?itemId=' + itemId + '&num=' + num);
        }

        //求合计
        this.sum = function (cartList) {
            debugger;
            var totalValue = {totalNum: 0, totalMoney: 0.00};
            for (var i = 0; i < cartList.length; i++) {
                var cart = cartList[i];
                for (var j = 0; j < cart.orderItemList.length; j++) {
                    var orderItem = cart.orderItemList[j];
                    totalValue.totalNum += orderItem.num;
                    totalValue.totalMoney += orderItem.totalFee;
                }
            }
            return totalValue;
        }
    }
)