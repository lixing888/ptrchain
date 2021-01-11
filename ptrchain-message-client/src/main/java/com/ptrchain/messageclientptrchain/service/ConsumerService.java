package com.ptrchain.messageclientptrchain.service;

import com.ptrchain.messagecenter.common.dto.MessageObj;
import com.ptrchain.messagecenter.common.dto.Result;
import com.ptrchain.messageclientptrchain.dto.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsumerService {
    @Autowired
    private RestTemplate restTemplate ;

    public Result getMessageByProvide(String qname){
        String url = "http://127.0.0.1:9981/message?qName="+qname;
        return this.restTemplate.getForObject(url,Result.class);
    }

    public Result postMessageByProvide(){
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

    public Result postMessageByProvide(int i,String qName){
        //创建个对象放到app1队列中
        UserInfo userInfo = new UserInfo();
        userInfo.setName("lengxiangjun"+i);
        userInfo.setAge(40);
        userInfo.setAddress("回龙观"+i);

        String url = "http://127.0.0.1:9981/message";
        MessageObj message = new MessageObj();
        message.setQname(qName);
        message.setMessage(userInfo);
        return this.restTemplate.postForObject(url, message, Result.class);
    }
}