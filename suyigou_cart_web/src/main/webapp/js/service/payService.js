//服务层
app.service('payService', function ($http) {
    //读取购物车列表
    this.createNative = function () {
        return $http.get('order/createNative.do');
    }

    //查询订单支付状态
    this.queryPayStatus=function(out_trade_no){
        return $http.get('order/queryPayStatus.do?out_trade_no='+out_trade_no);
    }
})