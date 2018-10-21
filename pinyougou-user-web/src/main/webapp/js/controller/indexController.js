app.controller('indexController' ,function($scope,loginService){
    $scope.loginName='';
    $scope.showLoginName=function () {
        loginService.showLoginName().success(function (data) {
            $scope.loginName=data.loginName;
        })
    }

})