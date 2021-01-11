package com.ptrchain.messagecenterptrchain.service.impl;

import com.ptrchain.messagecenterptrchain.exception.NoQnameException;
import com.ptrchain.messagecenterptrchain.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class MessageServiceImpl implements MessageService {
    //记录不同队列
    static private ConcurrentHashMap<String,LinkedBlockingDeque> queueMap = new ConcurrentHashMap();

    @Override
    public boolean createQueue(String qName) throws NoQnameException {
        //生成存放消息队列对象
        if(StringUtils.isEmpty(qName))
        {
            throw new NoQnameException("队列名字不能是空！");
        }
        LinkedBlockingDeque<Object> messageQueue = new LinkedBlockingDeque<>();
        queueMap.put(qName, messageQueue);
        return true;
    }

    @Override
    public boolean putMessage(String qName, Object message) throws NoQnameException {

        if(StringUtils.isEmpty(qName))
        {
            throw new NoQnameException("队列名字不能是空！");
        }
        LinkedBlockingDeque<Object> messageQueue = queueMap.get(qName);
        if (messageQueue == null) {
            //生成存放消息队列对象
            messageQueue = new LinkedBlockingDeque<>();
            queueMap.put(qName, messageQueue);
        }
        //放入消息
        return  queueMap.get(qName).offer(message);

    }

    @Override
    public Object getMessage(String qName) {
        //取得消息
        LinkedBlockingDeque<Object> messageQueue = queueMap.get(qName);
        if(messageQueue!=null)
        {
            return queueMap.get(qName).poll();
        }
        else
            return null;

    }
}
