app.service("seckillService",function ($http) {

    this.findSecKillGoodsList=function () {
        return $http.post("seckillGoods/findList.do");
    }

    this.findOneById=function (id) {
        return $http.get("seckillGoods/findOne.do?id="+id);
    }


    this.submitOrder=function (id) {
        return $http.get("seckillGoods/submitOrder.do?seckillId="+id);
    }




})