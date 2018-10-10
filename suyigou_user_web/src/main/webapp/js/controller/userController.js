//控制层
app.controller('userController', function ($scope, $controller, userService) {
    $controller('baseController', {$scope: $scope});
    $scope.entity = {};
    $scope.regist = function () {
        debugger;
        if ($scope.entity.password != $scope.password) {
            $scope.entity.password = "";
            $scope.password = "";
            return;
        } else {
            userService.add($scope.entity,$scope.smscode).success(
                function (response) {
                    alert(response.msg);
                }
            )
        }
    }
    $scope.sendCode = function () {
        debugger;
        if ($scope.entity.phone==null) {
            alert("手机号为空，请输入正确手机号");
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.msg);
            }
        );
    }

    $scope.showName = function () {
        debugger;
        userService.showName().success(
            function (response) {
                $scope.username = response.loginName;
            }
        )
    }
})