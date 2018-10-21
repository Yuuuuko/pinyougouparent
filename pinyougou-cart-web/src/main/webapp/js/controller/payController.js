app.controller("payController",function ($scope,payService,$location) {

    $scope.createNative=function () {
        payService.createNative().success(function (data) {
            $scope.money=(data.total_fee/100).toFixed(2);
            $scope.out_trade_no=data.out_trade_no;
            if (data.code_url){
                var qr = new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:'M',
                    value:data.code_url
                });
                $scope.queryPayStatus();

            }else {
                alert("创建订单失败，请重试")
            }


        })
    }


    $scope.queryPayStatus=function () {
        payService.queryPayStatus($scope.out_trade_no).success(function (data) {
            if (data.status){
                location.href="paysuccess.html#?money="+$scope.money;
            }else {
                if(response.message=='订单超时,请重新支付') {
                    $scope.createNative();//重新生成二维码
                }else {

                    location.href="payfail.html";
                }
            }
        })
    }

    $scope.getMoney=function(){
        return $location.search()['money'];
    }
})