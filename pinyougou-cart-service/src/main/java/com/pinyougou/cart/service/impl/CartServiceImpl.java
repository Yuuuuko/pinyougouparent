package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //通过商品ID查询商品对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //获取商家id
        String sellerId = item.getSellerId();
        //通过商家id判断现有购物车列表里面是否有该商家的商品
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //判断返回的购物车是否存在
        if (cart!=null){  //如果存在，则在该购物车中增加商品，否则为该商家新增一个购物车对象
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            //判断该购物车内是否有该商品，如果没有则在商品列表中新增该商品
            if (orderItem!=null){  //如果存在该商品，则更新该商品购买数量及总价
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                if (cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }else {
                TbOrderItem orderItem1 = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem1);

            }
        }else {
            Cart newCart = new Cart();

            newCart.setSellerId(sellerId);
            newCart.setSellerName(item.getSeller());
            List orderItemList=new ArrayList();
            TbOrderItem newOrderItem= createOrderItem(item, num);
            orderItemList.add(newOrderItem);
            newCart.setOrderItemList(orderItemList);
            cartList.add(newCart);
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从缓存中读取数据");
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        System.out.println("读取列表:"+cartList);
        if (cartList==null){
            cartList=new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向缓存中存储数据");
        System.out.println("存储列表:"+cartList);
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList1;
    }

    public Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    public TbOrderItem createOrderItem(TbItem item, Integer num){

        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setItemId(item.getId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        orderItem.setPicPath(item.getImage());
        return orderItem;
    }
}
