package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));

        Map<String,Object> map=new HashMap<>();

        Map highLightMap = searchHighLightMap(searchMap);
        map.putAll(highLightMap);
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        String category = (String) searchMap.get("category");
        if (!"".equals(searchMap.get("category"))) {

            map.putAll(searchBrandAndSpecList(category));
        }else {
            if (categoryList.size() > 0) {

                map.putAll(searchBrandAndSpecList(categoryList.get(0)));

            }
        }

        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {


        Query query=new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private Map searchHighLightMap(Map searchMap){
        Map<String,Object> map=new HashMap<>();
       /* Query query=new SimpleQuery();
        //添加查询条件
        Criteria criteria=new
                Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);*/

        //高亮设置
        SimpleHighlightQuery highlightQuery = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        highlightQuery.setHighlightOptions(highlightOptions);
        //条件过滤

        //按分类过滤
        if (!"".equals(searchMap.get("category"))) {
            Criteria filtercriteria=new Criteria("item_category").is(searchMap.get("category"));

            FilterQuery query=new SimpleFilterQuery(filtercriteria);


            highlightQuery.addFilterQuery(query);
        }
        //按品牌过滤
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filtercriteria=new Criteria("item_brand").is(searchMap.get("brand"));

            FilterQuery query=new SimpleFilterQuery(filtercriteria);


            highlightQuery.addFilterQuery(query);
        }
        //按规格过滤
        if (searchMap.get("spec")!=null){
            Map<String,Object> spec = (Map) searchMap.get("spec");
            for (String key : spec.keySet()) {
                Criteria filtercriteria=new Criteria("item_spec_"+key).is(spec.get(key));
                FilterQuery query=new SimpleFilterQuery(filtercriteria);

                highlightQuery.addFilterQuery(query);
            }
        }
        //按价格区间过滤

        if (!"".equals(searchMap.get("price"))) {
            String priceStr = (String) searchMap.get("price");
            String[] price = priceStr.split("-");
            if (!price[0].equals("0")){
                Criteria filtercriteria=new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery query=new SimpleFilterQuery(filtercriteria);
                highlightQuery.addFilterQuery(query);
            }
            if (!price[1].equals("*")){
                Criteria filtercriteria=new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery query=new SimpleFilterQuery(filtercriteria);
                highlightQuery.addFilterQuery(query);
            }
        }
        //按条件排序
        String sortField = (String) searchMap.get("sortField");  //排序字段
        String sortway = (String) searchMap.get("sort");  //排序方式
        if (!"".equals(sortField)&&sortField!=null){
            if (sortway.equals("DESC")){

                Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
                highlightQuery.addSort(sort);
            }else if (sortway.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
                highlightQuery.addSort(sort);
            }
        }




        //设置分页
        Integer pageNo=(Integer)searchMap.get("pageNo");
        if (pageNo==null){
            pageNo=1;
        }
        Integer pageSize=(Integer)searchMap.get("pageSize");
        if (pageSize==null){
            pageSize=40;
        }
        highlightQuery.setOffset((pageNo-1)*pageSize);  //设置分页起始记录数
        highlightQuery.setRows(pageSize);               //设置每页条数


        //关键字条件设置
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(searchMap.get("keywords"));
        highlightQuery.addCriteria(criteria);
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(highlightQuery, TbItem.class);

        for (HighlightEntry<TbItem> entry : page.getHighlighted()) {
            TbItem item = entry.getEntity();
            if (entry.getHighlights().size()>0&&entry.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", page.getContent());
        map.put("totalPages",page.getTotalPages());  //插入总页码数
        map.put("total",page.getTotalElements());    //插入总查询记录条数
        return map;
    }


    private List<String> searchCategoryList(Map searchMap){
        ArrayList<String> list = new ArrayList<>();

        Query query=new SimpleQuery();
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");

        query.setGroupOptions(groupOptions);

        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        query.addCriteria(criteria);

        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);

        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");

        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> groupEntryList = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : groupEntryList) {

            list.add(entry.getGroupValue());
        }
        return list;
    }

    private Map searchBrandAndSpecList(String category){

        Map map=new HashMap();
        Long itemCat = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (itemCat!=null) {
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(itemCat);
            map.put("brandList",brandList);

            List specList = (List) redisTemplate.boundHashOps("specList").get(itemCat);

            map.put("specList",specList);
        }
        return map;
    }
}
