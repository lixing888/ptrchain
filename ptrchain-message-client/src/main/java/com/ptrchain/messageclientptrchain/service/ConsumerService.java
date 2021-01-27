package com.ptrchain.messageclientptrchain.service;

<<<<<<< HEAD
import com.ptrchain.common.result.Result;
import com.ptrchain.messagecenter.common.dto.MessageObj;

=======
import com.ptrchain.messagecenter.common.dto.MessageObj;
import com.ptrchain.messagecenter.common.dto.Result;
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972
import com.ptrchain.messageclientptrchain.dto.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

<<<<<<< HEAD
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

=======
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972
@Service
public class ConsumerService {
    @Autowired
    private RestTemplate restTemplate ;

<<<<<<< HEAD
    public Result<MessageObj> getMessageByProvide(String qname){
=======
    public Result getMessageByProvide(String qname){
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972
        String url = "http://127.0.0.1:9981/message?qName="+qname;
        return this.restTemplate.getForObject(url,Result.class);
    }

<<<<<<< HEAD
    public Result<MessageObj> getMessageByProvide(String qname,long waitTime){
        String url = "http://127.0.0.1:9981/message/sync?qName="+qname+"&waitTime="+waitTime;
        return this.restTemplate.getForObject(url,Result.class);
    }

    public Result<MessageObj> postMessageByProvide(){
=======
    public Result postMessageByProvide(){
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972
        //创建个对象放到app1队列中
        UserInfo userInfo = new UserInfo();
        userInfo.setName("lengxiangjun");
        userInfo.setAge(40);
        userInfo.setAddress("回龙观");

        String url = "http://127.0.0.1:9981/message";
        MessageObj message = new MessageObj();
        message.setQname("leng");
        message.setMessage(userInfo);
        return this.restTemplate.postForObject(url, message, Result.class);
    }

<<<<<<< HEAD
    public Result<MessageObj> postMessageByProvide(int i,String qName){
        //创建个对象放到app1队列中
        UserInfo userInfo = new UserInfo();
        userInfo.setName("tax"+i);
        userInfo.setAge(40);
        userInfo.setAddress("北京海淀"+i);
=======
    public Result postMessageByProvide(int i,String qName){
        //创建个对象放到app1队列中
        UserInfo userInfo = new UserInfo();
        userInfo.setName("lengxiangjun"+i);
        userInfo.setAge(40);
        userInfo.setAddress("回龙观"+i);
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972

        String url = "http://127.0.0.1:9981/message";
        MessageObj message = new MessageObj();
        message.setQname(qName);
        message.setMessage(userInfo);
        return this.restTemplate.postForObject(url, message, Result.class);
    }
<<<<<<< HEAD

    public Result<MessageObj> postMessageListObj(String qName){
        //创建个对象放到app1队列中
        UserInfo userInfo = new UserInfo();
        userInfo.setName("lengxiangjun1");
        userInfo.setAge(41);
        userInfo.setAddress("回龙观1");

        UserInfo userInfo1 = new UserInfo();
        userInfo1.setName("lengxiangjun2");
        userInfo1.setAge(41);
        userInfo1.setAddress("回龙观2");

        List<Object> userList = new ArrayList<>();

        userList.add(userInfo);
        userList.add(userInfo1);

        String url = "http://127.0.0.1:9981/message";
        MessageObj message = new MessageObj();
        message.setQname(qName);
        message.setMessage(userList);
        return this.restTemplate.postForObject(url, message, Result.class);
    }
=======
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972
}