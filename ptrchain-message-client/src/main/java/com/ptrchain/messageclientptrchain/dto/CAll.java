package com.ptrchain.messageclientptrchain.dto;

import com.ptrchain.messagecenter.common.dto.MessageObj;
import lombok.Data;
import org.springframework.web.client.RestTemplate;

@Data
public class CAll<T> implements Runnable{
    T str ;
  //  IService instance;
    RestTemplate restTemplate ;
    String url;
    String Qname;
    public CAll(){

    }


     public  CAll(String Qname,RestTemplate restTemplate, String url) {
        this.Qname=Qname;
        this.restTemplate=restTemplate;
        this.url=url;
    }
//     public  void setCALL( T invoke, RestTemplate restTemplate, String url) {
//      //  this.instance =instance;
//        this.str.set(invoke);
//         System.out.println(this.str.get());
//        this.restTemplate=restTemplate;
//        this.url=url;
//    }
    @Override
    public void run() {
       //http://127.0.0.1:9981/message";
        MessageObj message = new MessageObj();
        message.setQname(Qname);
        message.setMessage(this.str);
        System.out.println(this.str);
       Object result=this.restTemplate.postForObject(url, message, Result.class);
       System.out.println(result.toString());
    }
}
