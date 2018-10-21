package com.pinyougou;


import com.alibaba.dubbo.common.json.ParseException;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

public class MySolrListener implements MessageListener {

    @Autowired
    private ItemSearchService searchService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;

        try {
            String text = textMessage.getText();
            System.out.println(text);
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);

            searchService.importList(itemList);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
