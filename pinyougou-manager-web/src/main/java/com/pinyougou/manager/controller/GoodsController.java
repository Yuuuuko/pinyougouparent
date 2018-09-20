package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {
	@Reference
	private GoodsService goodsService;
	@RequestMapping("/add")
	public Result addGoods(@RequestBody Goods goods) {
		/*System.out.println(goods);
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getGoods().setSellerId(name);*/
        /*Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            goods.getGoods().setSellerId(username);
        }*/

		try {
			goodsService.saveGoods(goods);
			return new Result(true, "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}

	}

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}


	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}

	/* *//**
	 * 增加
	 * @param goods
	 * @return
	 *//*
    @RequestMapping("/add")
    public Result add(@RequestBody TbGoods goods){
        try {
            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }*/

	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		/*goods.getGoods().setAuditStatus("0");
		String sellerId = goods.getGoods().getSellerId();
		Long id = goods.getGoods().getId();
		TbGoods tbGoods = goodsService.findOne(id);
		String sellerId1 = tbGoods.getSellerId();
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!sellerId.equals(name)||!name.equals(sellerId1)){
			return new Result(false, "非法操作");
		}*/

		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbGoods findOne(Long id){

		return goodsService.findOne(id);
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		System.out.println(Arrays.toString(ids));
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 分页查询
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		/*System.out.println(goods);
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(name);
*/
		return goodsService.findPage(goods, page, rows);
	}
	@RequestMapping("/findOneById")
	public Goods findOneById(@RequestBody Long id){
		System.out.println("id="+id);
		return goodsService.findOneById(id);
	}

	@RequestMapping("/commitGoods")
	public Result commitGoods(@RequestBody Long[] ids){

		System.out.println(Arrays.toString(ids));
		try {
			goodsService.commitGoods(ids);
			return new Result(true,"提交成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"提交失败");
		}

	}
}
