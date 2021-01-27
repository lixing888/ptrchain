package com.ptrchain.messageclientptrchain.thread;

import com.alibaba.fastjson.JSON;
import com.ptrchain.common.result.Result;
import com.ptrchain.messagecenter.common.dto.MessageObj;
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

        Result<MessageObj> result=null;
        while (true) {

            try {
                result=  consumerService.getMessageByProvide(qName);
                System.out.println(result.getData().getQname());
                System.out.println(result.getData().getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Thread.sleep(1000);
            System.out.println("get message result:"+JSON.toJSONString(result));
        }
    }


    public void   getMessage(String qName,long waitTime) throws InterruptedException {
        //循环读消息，每1秒读一次
        Result<MessageObj> result;
        while (true) {
            result=  consumerService.getMessageByProvide(qName,waitTime);
            Thread.sleep(1000);
            System.out.println("get message result:"+ JSON.toJSONString(result));
        }
    }

    /**
     *
     * 增加消息
     */
    @Async
    public void addMessage(String qName) throws InterruptedException {
        //增加100个消息到队列中
        Result<MessageObj> result;
        for (int i = 0; i < 2; i++) {
            result= consumerService.postMessageByProvide(i, qName);
            Thread.sleep(500);
            System.out.println("put message result:"+result);
        }

    }


    @Async
    public void addMessageListObj(String qName) throws InterruptedException {

           consumerService.postMessageListObj(qName);


    }

    @Async
    public void addMessage2(String qName) throws InterruptedException {
        //增加100个消息到队列中
        Result result;
        for (int i = 10; i < 20; i++) {
            result= consumerService.postMessageByProvide(i, qName);
            //Thread.sleep(500);
            // System.out.println("put message result:"+result);
        }

    }
}
