 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$scope.entity={};

	//提交注册
	$scope.register=function () {

		if ($scope.entity.password!==$scope.password){
            $scope.passwordMsg='\*输入的密码不一致';
			return;
		}
		userService.add($scope.entity,$scope.smscode).success(function (data) {
            $scope.passwordMsg='';
			if (data.status){
				alert(data.msg);
                $scope.entity={};
                $scope.password='';
                $scope.smscode='';
			}else {
				alert(data.msg);

                $scope.smscode='';
			}

        })

    }

    //发送验证码,前台传入手机号，后台生产验证码发送到对应手机并将验证码存入redies缓存
	$scope.sendCode=function () {
		if ($scope.entity.phone==null||$scope.entity.phone===''){
            $scope.phoneMsg='请输入正确的手机号码';
            return;
		}
        var reg = /^1[34578][0-9]{9}$/; //验证规则
        var flag = reg.test($scope.entity.phone);
        if(!flag){
        	$scope.phoneMsg='请输入正确的手机号码';
        	return;
		}

		userService.sendSmsCode($scope.entity.phone).success(function (data) {
            $scope.phoneMsg='';
			if (!data){
				$scope.phoneMsg=data.msg;
			}else {
                alert(data.msg);
			}

        })
    }


    
});	
