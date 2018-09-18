package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import entity.Result;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class RegisterController {
    @Reference
    private SellerService sellerService;
    @RequestMapping("/register")
    public Result register(@RequestBody TbSeller seller){
       // System.out.println(seller);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(seller.getPassword());
        seller.setPassword(password);
        try {
            sellerService.register(seller);
            return  new Result(true,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"注册失败");
        }
    }

}
