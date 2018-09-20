package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbItemMapper itemMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbGoods goods) {
		goodsMapper.insert(goods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){

		goodsMapper.updateByPrimaryKey(goods.getGoods());

		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

		Long goodsId = goods.getGoods().getId();
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(goodsId);
		itemMapper.deleteByExample(tbItemExample);

		saveItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//修改商品表IsDelete属性为1
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
			//修改items表status字段属性为删除（3）
			TbItemExample example = new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			List<TbItem> tbItems = itemMapper.selectByExample(example);
			for (TbItem tbItem : tbItems) {
				tbItem.setStatus("3");
				itemMapper.updateByExample(tbItem,example);
			}
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void saveGoods(Goods goods) {
		TbGoods tbGoods = goods.getGoods();
		goodsMapper.insert(tbGoods);
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goodsDesc);

		saveItemList(goods);

	}

	private void saveItemList(Goods goods){
		if ("1".equals(goods.getGoods().getIsEnableSpec())){
			List<TbItem> itemList = goods.getItemList();
			for (TbItem item : itemList) {
				String goodsName = goods.getGoods().getGoodsName();
				Map<String,Object> specMap = JSON.parseObject(item.getSpec());
				String title=goodsName;
				for (String str:specMap.keySet()){
					title+=" "+specMap.get(str);
				}
				item.setTitle(title);
				setItemAttr(goods,item);
				itemMapper.insert(item);
			}
		}else {
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品 KPU+规格描述串作为

			item.setPrice( goods.getGoods().getPrice() );//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			setItemAttr(goods,item);
			itemMapper.insert(item);
		}
	}

	/**
	 * 根据商品id查询组合商品信息，返回组合实体类goods
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOneById(Long id) {
		Goods goods = new Goods();

		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);

		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);

		TbItemExample itemExample = new TbItemExample();
		TbItemExample.Criteria criteria = itemExample.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> items = itemMapper.selectByExample(itemExample);
		goods.setItemList(items);
		return goods;
	}

	/**
	 * 审核提交商品，处理方案为修改goods表内的商品状态为已审核（1）
	 * @param ids
	 */
	@Override

	public void commitGoods(Long[] ids) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus("1");
			goodsMapper.updateByPrimaryKey(tbGoods);


		}

	}

	@Override
	public void goodsMarketAble(Long[] ids) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsMarketable("1");
			goodsMapper.updateByPrimaryKey(tbGoods);


		}

	}

	private void setItemAttr(Goods goods,TbItem item){
		TbGoods tbGoods = goods.getGoods();
		item.setGoodsId(tbGoods.getId());
		item.setSellerId(tbGoods.getSellerId());

		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());

		item.setCategoryid(tbGoods.getCategory3Id());

		TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
		item.setBrand(tbBrand.getName());

		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
		item.setCategory(itemCat.getName());

		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		String itemImages = goodsDesc.getItemImages();
		List<Map> imgList = JSON.parseArray(itemImages, Map.class);
		String url = (String) imgList.get(0).get("url");

		item.setImage(url);

	}

}
