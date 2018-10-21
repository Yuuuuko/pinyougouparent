package com.pinyougou.pay.service.impl;

import Utils.HttpClient;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        Map param=new HashMap();
        param.put("appid",appid);  //公众账号ID
        param.put("mch_id",partner);//商户id
        param.put("nonce_str", WXPayUtil.generateNonceStr());//生产随机字符串
        param.put("body","品优购-商品购买");
        param.put("out_trade_no",out_trade_no);//商户订单号
        param.put("total_fee",total_fee);//标价金额
        param.put("spbill_create_ip","127.0.0.1");//ip
        param.put("notify_url","http://test.itcast.cn");//回调地址，随便写
        param.put("trade_type","NATIVE");//支付方式


        try {
            String signedXml = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post(); //向微信接口post相关订单信息

            String result  = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result); //微信返回结果

            Map map=new HashMap();
            map.put("code_url",resultMap.get("code_url"));
            map.put("total_fee", total_fee);
            map.put("out_trade_no",out_trade_no);
            return  map;
        } catch (Exception e) {
            e.printStackTrace();

            return new HashMap();
        }

    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map queryMap=new HashMap();
        queryMap.put("appid",appid);
        queryMap.put("mch_id",partner);
        queryMap.put("out_trade_no",out_trade_no);
        queryMap.put("nonce_str",WXPayUtil.generateNonceStr());

        try {
            String queryXML = WXPayUtil.generateSignedXml(queryMap, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(queryXML);
            httpClient.post();

            //获取订单查询结果  xml
            String queryResult = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(queryResult);

            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid", appid);//公众账号 ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);

            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
