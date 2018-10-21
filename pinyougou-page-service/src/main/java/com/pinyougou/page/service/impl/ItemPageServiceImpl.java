package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean getItemHtml(long goodsId) {

        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");

            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);

            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);


            FileWriter out = new FileWriter(pagedir + goodsId + ".html");

            TbItemCat cat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
            TbItemCat cat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
            TbItemCat cat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());

            TbItemExample itemExample = new TbItemExample();
            TbItemExample.Criteria itemExampleCriteria = itemExample.createCriteria();
            itemExampleCriteria.andGoodsIdEqualTo(goodsId);
            itemExampleCriteria.andStatusEqualTo("1");
            itemExample.setOrderByClause("is_default desc");
            List<TbItem> tbItems = itemMapper.selectByExample(itemExample);
            System.out.println(tbItems);

            Map map = new HashMap();
            map.put("itemList",tbItems);
            map.put("category1Id",cat1);
            map.put("category2Id",cat2);
            map.put("category3Id",cat3);

            map.put("goods",goods);
            map.put("goodsDesc",goodsDesc);

            template.process(map,out);
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }
}
