package com.pinyougou.sellergoods.service.impl;

import entity.PageResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.Result;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService{
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> tbBrands = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        PageResult PageResult=new PageResult();
        PageResult.setTotal(tbBrands.getTotal());
        PageResult.setRows(tbBrands.getResult());

        return PageResult;
    }

    @Override
    public Result insertBrand(TbBrand brand) {
        TbBrand tbBrand = tbBrandMapper.selectByName(brand.getName());
        if (tbBrand!=null){
            return new Result(false,"新增失败,品牌名称重复");
        }
        try {
            tbBrandMapper.insert(brand);
            return  new Result(true,"新增成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"新增失败");
        }

    }

    @Override
    public TbBrand findOne(long id) {

        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public Result updateBrand(TbBrand brand) {
        try {
            tbBrandMapper.updateByPrimaryKey(brand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @Override
    public void deleteBrand(long[] ids) {
        for (long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }

    }

    @Override
    public PageResult search(TbBrand tbBrand, int pageNum, int pageSize) {

        TbBrandExample example=new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
       if (tbBrand!=null){
           if (tbBrand.getName()!=null&&tbBrand.getName().length()>0){
               criteria.andNameLike("%"+tbBrand.getName()+"%");
           }
           if (tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
               criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
           }
       }
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> tbBrands = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        PageResult PageResult=new PageResult();
        PageResult.setTotal(tbBrands.getTotal());
        PageResult.setRows(tbBrands.getResult());
        return PageResult;
    }

    @Override
    public List<Map> selectOptionList() {
        return tbBrandMapper.selectOptionList();
    }
}
