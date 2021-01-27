package com.ptrchain.messageclientptrchain.thread;

import com.ptrchain.messageclientptrchain.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

<<<<<<< HEAD
import javax.xml.namespace.QName;

=======
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972
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
<<<<<<< HEAD
        /*messageThread.addMessageListObj("leng");
        messageThread.getMessage("leng");
*/
=======
        messageThread.addMessage("leng");
        messageThread.getMessage("leng");
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972

        messageThread.addMessage("guo");
        messageThread.getMessage("guo");


<<<<<<< HEAD
        //messageThread.addMessage("li");
       // messageThread.getMessage("li");

        //测试同步获取队列
       // messageThread.getMessage("leng",4000);

=======
        messageThread.addMessage("li");
        messageThread.getMessage("li");
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972

    }
}
