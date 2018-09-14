package com.pinyougou.sellergoods.service;

import entity.PageResult;
import entity.Result;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

public interface BrandService {

    List<TbBrand> findAll();

    PageResult findPage(int pageNum, int pageSize);

    Result insertBrand(TbBrand brand);
    TbBrand findOne(long id);

    Result updateBrand(TbBrand brand);

    void deleteBrand(long[] ids);

    PageResult search(TbBrand tbBrand, int pageNum, int pageSize);

}
