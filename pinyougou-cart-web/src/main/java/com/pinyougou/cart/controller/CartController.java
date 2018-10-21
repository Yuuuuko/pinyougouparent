package com.pinyougou.cart.controller;


import Utils.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins="http://localhost:8087",allowCredentials="true")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;


    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登录用户名为:" + name);
        //anonymousUser为默认的匿名用户名


        String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartList == null || cartList.equals("")) {
            cartList = "[]";
        }
        List<Cart> cookie_cart = com.alibaba.fastjson.JSON.parseArray(cartList, Cart.class);
        if (name.equals("anonymousUser")) {
            return cookie_cart;
        } else {
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
            //合并本地与redies购物车
            if (cookie_cart.size()>0){
                cartListFromRedis= cartService.mergeCartList(cartListFromRedis, cookie_cart);
                //将合并后的购物车存入缓存
                cartService.saveCartListToRedis(name,cartListFromRedis);
                //清空本地cookie
                CookieUtil.deleteCookie(request,response,"cartList");
            }
            return cartListFromRedis;

        }


    }
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登录用户名为:"+name);
        try {

            List<Cart> cartList = findCartList();

            cartList=cartService.addGoodsToCartList(cartList,itemId,num);
            if (name.equals("anonymousUser")){
                CookieUtil.setCookie(request,response,"cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
            }else {
                cartService.saveCartListToRedis(name,cartList);
            }
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
    @RequestMapping("/removeCookie")
    public String removeCookie(){
        CookieUtil.deleteCookie(request,response,"cartList");
        return "清除成功";
    }

}
