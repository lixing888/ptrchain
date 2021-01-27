package com.ptrchain.messageclientptrchain.thread;

import com.ptrchain.messageclientptrchain.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;

/**
 * spring boot 启动后自动执行线程任务
 */
@Component
@Order(value = 1)
public class ThreadBegin implements CommandLineRunner {
    @Autowired
    MessageThread messageThread;
    @Override
    public void run(String... args) throws Exception {
       // 执行 6个线程，创建3个队列
        /*messageThread.addMessageListObj("leng");
        messageThread.getMessage("leng");
*/

        messageThread.addMessage("guo");
        messageThread.getMessage("guo");


        //messageThread.addMessage("li");
       // messageThread.getMessage("li");

        //测试同步获取队列
       // messageThread.getMessage("leng",4000);


    }
}
