package com.ptrchain.messageclientptrchain.service;

import com.ptrchain.messagecenter.common.dto.Result;
import com.ptrchain.messageclientptrchain.util.CustomErrorHandler;
import org.springframework.web.client.RestTemplate;

import static com.ptrchain.messageclientptrchain.util.MyFile.xie;

public class MyTestThread extends Thread {
    String qname;
    RestTemplate restTemplate;

    public MyTestThread(String qname, RestTemplate restTemplate) {
        this.qname = qname;
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        while (true) {
            String url = "http://192.168.0.245:9981/message?qName=" + qname;
            Result result = new Result();
            result.setSuccess(false);
            //restTemplate.setErrorHandler(new CustomErrorHandler());
            try {
                result = this.restTemplate.getForObject(url, Result.class);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            //System.out.println(result);
            if (result.isSuccess()) {
                xie("D:\\xie\\" + String.valueOf(System.currentTimeMillis() + "" + (Math.random() * 10)), result.toString());
                //xie("/opt/xie/" + String.valueOf(System.currentTimeMillis() + "" + (Math.random() * 10)), "result.toString()");
            }
        }
    }
}
