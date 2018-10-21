package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private SeckillOrderService orderService;
    @Reference
    private WeixinPayService weixinPayService;
    @RequestMapping("/createNative")
    public Map createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder seckillOrder = orderService.searchOrderFromRedisByUserId(name);
        if (seckillOrder!=null){
            long fen=  (long)(seckillOrder.getMoney().doubleValue()*100);//金额（分）

            return weixinPayService.createNative(seckillOrder.getId()+"", "1");
        }else {
            return new HashMap();
        }
    }

   @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
       String name = SecurityContextHolder.getContext().getAuthentication().getName();
       int x = 0;
       Result result = new Result();
       while (true) {
           Map<String, String> payResult = weixinPayService.queryPayStatus(out_trade_no);

           if (payResult != null) {
               if (payResult.get("return_code").equals("FAIL")) {
                   result = new Result(false, "订单错误，请重新支付");
                   break;
               } else {
                   if (payResult.get("result_code").equals("SUCCESS")) {
                       result = new Result(true, "支付成功");
                       orderService.saveOrderFromRedisToDb(name, Long.valueOf(out_trade_no), payResult.get("transaction_id"));
                       break;
                   }
               }
           }
           try {
               Thread.sleep(3000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

           x++;
           if (x > 100) {
               result = new Result(false, "订单超时,请重新支付");
               Map<String, String> payresult = weixinPayService.closePay(out_trade_no);
               if (!"SUCCESS".equals(payresult.get("result_code"))) {//如果返回结果是正常关闭
                   if ("ORDERPAID".equals(payresult.get("err_code"))) {
                       result = new Result(true, "支付成功");
                       orderService.saveOrderFromRedisToDb(name, Long.valueOf(out_trade_no), payResult.get("transaction_id"));
                   }
               }
               if (result.isStatus() == false) {
                   System.out.println("超时，取消订单");
                   //2.调用删除
                   orderService.deleteOrderFromRedis(name, Long.valueOf(out_trade_no));
               }
           }

       }
       return result;
   }
}
