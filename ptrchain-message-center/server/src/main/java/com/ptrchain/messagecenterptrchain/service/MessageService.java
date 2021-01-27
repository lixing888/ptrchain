package com.ptrchain.messagecenterptrchain.service;


import com.ptrchain.messagecenterptrchain.exception.NoQnameException;

public interface MessageService {
<<<<<<< HEAD


=======
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972
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

<<<<<<< HEAD
    /**
     * 同步方式取队列消息
     * @param qName
     * @param waitTime 最大等待时间 单位毫秒 超过时间返回null
     * @return
     */
    public Object getMessage(String qName, long waitTime);

=======
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972
}
