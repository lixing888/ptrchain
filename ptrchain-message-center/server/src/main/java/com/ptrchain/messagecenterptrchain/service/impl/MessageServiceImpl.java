package com.ptrchain.messagecenterptrchain.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ptrchain.common.utils.date.DateUtils;
import com.ptrchain.messagecenterptrchain.config.MessageConfig;
import com.ptrchain.messagecenterptrchain.exception.NoQnameException;
import com.ptrchain.messagecenterptrchain.service.MessageService;
import com.ptrchain.messagecenterptrchain.thread.FileWriteThread;
import com.ptrchain.messagecenterptrchain.util.JsonSerilizable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Configuration
public class MessageServiceImpl implements MessageService {
    //队列最大消息数量
    @Value("${ptrchain.queue.maxnums}")
    private int queueMaxNums;
    //记录不同队列
    static private ConcurrentHashMap<String, LinkedBlockingDeque> queueMap = new ConcurrentHashMap();
    //队列数据存放路径
    static private String messagePath;
    @Autowired
    FileWriteThread fileWriteThread;

    public MessageServiceImpl(MessageConfig messageConfig) {
        messagePath = messageConfig.getMessagePath();
        //反序列化
        if (messageConfig.isLoadNotSendMessage()) {
            String pathName = messageConfig.getMessagePath();
            try {
                queueMap = JsonSerilizable.deserilizableForMapFromFile(pathName);
                log.info("载入消息队情况：");
                for (String qname : queueMap.keySet()
                ) {
                    log.info("队列名字：{},对列数量：{}", qname, queueMap.get(qname).size());
                }


            } catch (IOException e) {
               log.info("无需要载入的队列文件。");
            }
        }
    }

    @Override
    public boolean createQueue(String qName) throws NoQnameException {
        //生成存放消息队列对象
        if (StringUtils.isEmpty(qName)) {
            throw new NoQnameException("队列名字不能是空！");
        }
        LinkedBlockingDeque<Object> messageQueue = new LinkedBlockingDeque<>();
        queueMap.put(qName, messageQueue);
        return true;
    }

    @Override
    public boolean putMessage(String qName, Object message) throws NoQnameException {
        boolean isPutSuccess = false;
        if (StringUtils.isEmpty(qName)) {
            throw new NoQnameException("队列名字不能是空！");
        }
        LinkedBlockingDeque<Object> messageQueue = queueMap.get(qName);
        if (messageQueue == null) {
            //生成存放消息队列对象
            messageQueue = new LinkedBlockingDeque<>(queueMaxNums);
            queueMap.put(qName, messageQueue);
        }
        //放入消息
        isPutSuccess = queueMap.get(qName).offer(message);

        //测试序列化
        // HashMap<String, Entity> Map = new HashMap<Integer, Entity>();
        String pathName = "./queue.txt";
        fileWriteThread.writeFile(queueMap, pathName);
        //打印消息放入情况
        log.info("放消息队列:{} 放入是否成功:{},放入后消息数量:{}", qName, isPutSuccess, queueMap.get(qName).size());

        return isPutSuccess;

    }

    @Override
    public Object getMessage(String qName) {
        //取得消息
        Object message = null;


        LinkedBlockingDeque<Object> messageQueue = queueMap.get(qName);

        if (messageQueue != null) {
            message = messageQueue.poll();
            //打印消息取出情况
            if (message != null) {
                //测试序列化
                // HashMap<String, Entity> Map = new HashMap<Integer, Entity>();
                String pathName = messagePath;
                fileWriteThread.writeFile(queueMap, pathName);
                log.info("取消息队列:{} 取出后消息数量:{}", qName, messageQueue.size());
            }
        }
        return message;

    }

    @Override
    public Object getMessage(String qName, long waitTime) {
        //取得消息
        LinkedBlockingDeque<Object> messageQueue = queueMap.get(qName);
        Object message;

        if (messageQueue != null) {
            try {
                //取队列最大等待时间
                message = queueMap.get(qName).poll(waitTime, TimeUnit.MILLISECONDS);
                if (message != null) {
                    //测试序列化

                    // HashMap<String, Entity> Map = new HashMap<Integer, Entity>();

                    fileWriteThread.writeFile(queueMap, messagePath);
                    log.info("同步取消息队列:{} 取出后消息数量:{}", qName, queueMap.get(qName).size());
                }
                return message;
            } catch (InterruptedException e) {
                return null;
            }
        } else
            return null;
    }


}
