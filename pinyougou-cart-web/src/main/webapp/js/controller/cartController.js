app.controller("cartController",function ($scope,cartService) {

    $scope.addressList={}; //存放地址列表
    $scope.selectAddress={};
    $scope.order={paymentType:'1'};
    //获取购物车列表
    $scope.findCartList=function () {
        cartService.findCartList().success(function (data) {
            $scope.cartList=data;
            $scope.totalValue=cartService.sum($scope.cartList);//求合计数

        })
    }
    //添加商品到购物车列表
    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(function (data) {
            if (data.status){
                $scope.findCartList();
            }else {
                alert("操作错误")
            }
        })
    }
    //获取收货地址列表
    $scope.findAddressByUserId=function () {
        cartService.findAddressByUserId().success(function (data) {
            $scope.addressList=data;
            for (var i=0;i<data.length;i++){
                if (data[i].isDefault==='1'){
                    $scope.selectAddress=data[i];
                }
            }
        })
    }
    //选择地址
    $scope.checkAddress=function (address) {
        $scope.selectAddress=address;
    }
    //选择支付类型
    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    }


    $scope.createOrder=function () {
        $scope.order.receiverAreaName=$scope.selectAddress.provinceId+$scope.selectAddress.cityId+$scope.selectAddress.address;
        $scope.order.receiverMobile=$scope.selectAddress.mobile;
        $scope.order.receiver=$scope.selectAddress.contact;

        cartService.createOrder($scope.order).success(function (data) {
            if (data.status){
                location.href="pay.html";
            }else {
                alert("提交订单失败");
            }
        })
    }

})