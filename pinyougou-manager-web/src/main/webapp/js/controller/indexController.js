app.controller("indexController",function ($scope,loginService) {
    //初始化登录用户
    $scope.loginUser={};

    $scope.login=function () {
        loginService.login().success(function (data) {
            $scope.loginUser=data;
            console.log($scope.loginUser);
        })
    }
})