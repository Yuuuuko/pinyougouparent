package com.pinyougou.manager.controller;


import entity.PageResult;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    @RequestMapping("/findAll")
    public List<TbBrand> findAllBrand(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage.do")
    public PageResult findPage(int page, int size){
        PageResult PageResult = brandService.findPage(page, size);
        //System.out.println(resultPage);
        return PageResult;
    }

    @RequestMapping("/insertBrand")
    public Result insertBrand(@RequestBody TbBrand brand){
        Result result = brandService.insertBrand(brand);
        return result;
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(long id){
        TbBrand tbBrand = brandService.findOne(id);
        return tbBrand;
    }
    @RequestMapping("/updateBrand")
    public Result updateBrand(@RequestBody TbBrand brand){
        Result result = brandService.updateBrand(brand);
        return result;
    }
    @RequestMapping("/deleteBrands")
    public Result deleteBrands(@RequestBody long[] ids){
        try {
            brandService.deleteBrand(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, int page, int size){
        PageResult PageResult = brandService.search(brand, page, size);
        return PageResult;
    }
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
