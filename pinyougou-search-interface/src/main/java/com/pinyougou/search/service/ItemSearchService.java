package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    Map<String,Object> search(Map searchMap);

    public void importList(List list);

    public void deleteByGoodsIds(List goodsIdList);

}
