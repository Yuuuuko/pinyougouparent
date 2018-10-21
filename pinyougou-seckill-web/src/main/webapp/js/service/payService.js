app.service("payService",function ($http) {

    this.createNative=function () {
       return $http.post("pay/createNative.do");
    }

    //检查订单支付状态
    this.queryPayStatus=function (out_trade_no) {
        return $http.post("pay/queryPayStatus.do",out_trade_no);
    }
})