app.controller("registerController",function ($scope,registerService) {

    $scope.entity={status:0};//初始化注册信息

    $scope.register=function () {
        registerService.register($scope.entity).success(function (data) {
            if (data.status){
                location.href="shoplogin.html";
            }else {
                alert(data.msg);
                location.href="register.html";
            }
        })
    }
})