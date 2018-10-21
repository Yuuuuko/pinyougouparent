package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
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
    private WeixinPayService weixinPayService;
    @Reference
    private OrderService orderService;
    @RequestMapping("/createNative")
    public Map createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.searchPayLogFromRedis(name);
        if (payLog!=null) {
            return weixinPayService.createNative(payLog.getOutTradeNo(), "1");
        }else {
            return new HashMap();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        int x=0;
        Result result = new Result();
        while (true){
            Map<String,String> payResult = weixinPayService.queryPayStatus(out_trade_no);

            if (payResult!=null) {
                if (payResult.get("return_code").equals("FAIL")){
                    result= new Result(false,"订单错误，请重新支付");
                    break;
                }else {
                    if (payResult.get("result_code").equals("SUCCESS")){
                        result= new Result(true,"支付成功");
                        orderService.updateOrderStatus(out_trade_no,payResult.get("transaction_id"));
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
            if (x>100){
                result= new Result(false,"订单超时,请重新支付");
                break;
            }
        }
        return result;

    }

}
