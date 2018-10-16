//控制层
app.controller('payController', function ($scope, $controller, $location,payService) {
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.money = (response.total_fee / 100).toFixed(2); //金额
                $scope.out_trade_no = response.out_trade_no;//订单号
                //二维码
                debugger;
                var qrious = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    level: 'H',
                    value: response.code_url
                });
                queryPayStatus(response.out_trade_no);//查询支付状态
            }
        )
    }

    //查询支付状态
    queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(
            function (response) {
                if (response.success) {
                    location.href = "paysuccess.html#?money="+$scope.money;
                } else {
                    debugger;
                    if (response.msg == "支付超时") {
                        alert(response.msg);
                        $scope.createNative();//重新生成二维码
                    } else {
                        location.href = "payfail.html";
                    }
                }
            }
        );
    }

    //获取金额
    $scope.getMoney=function(){
        debugger;
        return $location.search()['money'];
    }
})