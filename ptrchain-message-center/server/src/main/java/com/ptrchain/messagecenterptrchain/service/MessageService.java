package com.ptrchain.messagecenterptrchain.service;


import com.ptrchain.messagecenterptrchain.exception.NoQnameException;

public interface MessageService {
    /**
     * 生成队列
     * @param qName 队列名称
     * @return
     */
    public boolean createQueue(String qName) throws NoQnameException;

    /**
     * 消息放入队列
     * @param qName 队列名字
     * @param message 消息
     * @return
     */
    public boolean putMessage(String qName,Object message) throws NoQnameException;

    /**
     * 从队列中获取消息
     * @param qName 队列名字
     * @return
     */
    public Object getMessage(String qName);

}
