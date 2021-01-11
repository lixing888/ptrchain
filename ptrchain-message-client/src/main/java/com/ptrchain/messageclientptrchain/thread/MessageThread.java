package com.ptrchain.messageclientptrchain.thread;

import com.ptrchain.messagecenter.common.dto.Result;
import com.ptrchain.messageclientptrchain.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MessageThread {
    @Autowired
   private ConsumerService consumerService;
    /**
     * 读取消息队列
     */
    @Async
    public void   getMessage(String qName) throws InterruptedException {
        //循环读消息，每2秒读一次
        Result result;
        while (true) {
            result=  consumerService.getMessageByProvide(qName);
            Thread.sleep(2000);
            System.out.println("get message result:"+result);
        }
    }

    /**
     *
     * 增加消息
     */
    @Async
    public void addMessage(String qName) throws InterruptedException {
        //增加100个消息到队列中
        Result result;
        for (int i = 0; i < 100; i++) {
            result= consumerService.postMessageByProvide(i, qName);
            //Thread.sleep(500);
           // System.out.println("put message result:"+result);
        }

    }
}
