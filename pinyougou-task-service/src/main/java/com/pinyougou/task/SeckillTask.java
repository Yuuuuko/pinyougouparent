package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Component
public class SeckillTask {
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Scheduled(cron = "1 * * * * ?")   //每分钟的第10秒执行该任务
    public void refreshSeckillGoods(){
        List ids=new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStartTimeLessThan(new Date());
        criteria.andEndTimeGreaterThan(new Date());
        criteria.andStockCountGreaterThan(0);
        criteria.andIdNotIn(ids);  //查找满足参与秒杀条件且还未存入缓存的商品信息

        List<TbSeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);

        for (TbSeckillGoods seckillGood : seckillGoods) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(),seckillGood);
            System.out.println("将商品"+seckillGood.getId()+"存入缓存");
        }


    }
    /*@Scheduled(cron = "1 * * * * ?")
    public void TestTask(){
        System.out.println("定时任务执行了");
    }
*/
    /*
    查询缓存中的秒杀商品结束时间，一旦商品秒杀时间结束，则删除缓存数据

     */
    @Scheduled(cron = "20 * * * * ?")
    public void removeSeckillGoodsFromRedies(){
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods seckillGood : seckillGoods) {
            if (seckillGood.getEndTime().getTime()<new Date().getTime()){
                System.out.println("商品秒杀活动已结束");
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());
                System.out.println("移除缓存数据");
            }

            if (seckillGood.getStockCount()<=0){
                System.out.println("秒杀商品已卖完");
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());
            }
        }
        System.out.println("商品已刷新完成");
    }

    @Scheduled(cron = "25 * * * * ?")
    public void remove(){
        redisTemplate.boundHashOps("seckillGoods").delete(2);
        System.out.println("删除2号商品");
    }

}
