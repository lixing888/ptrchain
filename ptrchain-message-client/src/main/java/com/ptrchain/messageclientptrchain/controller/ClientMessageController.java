package com.ptrchain.messageclientptrchain.controller;

import com.ptrchain.common.result.Result;
import com.ptrchain.messagecenter.common.dto.MessageObj;

import com.ptrchain.messageclientptrchain.service.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientmessage")
@Slf4j
public class ClientMessageController {
    @Autowired
    ConsumerService consumerService;

    /**
     * 调用读取队列
     * @param qName
     * @return
     */
    @GetMapping("/get")
    public Result<MessageObj> getMessage(@RequestParam String qName) {
      return  consumerService.getMessageByProvide(qName);
    }

    /**
     * 调用读取队列 同步方式
     * @param qName
     * @return
     */
    @GetMapping("/get/sync")
    public Result<MessageObj> getSyncMessage(@RequestParam String qName) {
        //最大等5秒
        return   consumerService.getMessageByProvide(qName,5000);
    }



    /**
     * 调用增加队列
     * @return
     */
    @GetMapping("/add")
    public Result<MessageObj> addMessage() {
        return   consumerService.postMessageByProvide();
    }
}
