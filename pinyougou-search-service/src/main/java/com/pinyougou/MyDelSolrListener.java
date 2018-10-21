package com.pinyougou;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

public class MyDelSolrListener implements MessageListener {

    @Autowired
    private ItemSearchService searchService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage= (ObjectMessage) message;

        try {
            Long[] object = (Long[]) objectMessage.getObject();
            searchService.deleteByGoodsIds(Arrays.asList(object));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
