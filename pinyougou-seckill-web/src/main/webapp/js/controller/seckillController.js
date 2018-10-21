app.controller("seckillController",function ($scope,seckillService,$location,$interval) {

    //查询秒杀商品列表
    $scope.findSecKillGoodsList=function () {
        seckillService.findSecKillGoodsList().success(function (data) {
            $scope.seckillgoodsList=data;
        })
    }

   // $scope.seckillId=$location.search()['id'];

    $scope.findOneById=function () {
        //console.log($location.search()['id']);
        seckillService.findOneById($location.search()['id']).success(function (data) {
            $scope.seckillGood=data;

            totalSec=Math.floor((new Date(data.endTime).getTime()-new Date().getTime())/1000);
            //console.log(totalSec);
            timer=$interval(function () {
                totalSec=totalSec-1;
                if (totalSec>0){
                    timeStr(totalSec);
                }else {
                    $interval.cancel(timer);
                }
            },1000)
        })
    }
    //格式化秒数为正常日期格式字符串
    timeStr=function (sec) {
        var days=Math.floor(sec/(60*60*24));
        var hour=Math.floor((sec-days*60*60*24)/(60*60));
        var min=Math.floor((sec-days*60*60*24-hour*60*60)/60);
        var secend=Math.floor(sec-days*60*60*24-hour*60*60-min*60);
        var daystr=days+"";
        var hourstr=hour+"";
        var minstr=min+"";
        var secendstr=secend+"";
        if (daystr.length===1) daystr="0"+daystr;
        if (hourstr.length===1) hourstr="0"+hourstr;
        if (minstr.length===1) minstr="0"+minstr;
        if (secendstr.length===1) secendstr="0"+secendstr;
        $scope.timeSting=daystr+":"+hourstr+":"+minstr+":"+secendstr
    }

    $scope.submitOrder=function () {
        seckillService.submitOrder($location.search()['id']).success(function (data) {

            if (data.status){
                location.href="pay.html";
            }else {
                alert("当前商品秒杀已经结束，请去参与其他商品的秒杀活动");
            }

        })
    }




})