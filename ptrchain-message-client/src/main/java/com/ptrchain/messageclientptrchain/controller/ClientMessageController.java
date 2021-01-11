package com.ptrchain.messageclientptrchain.controller;

import com.ptrchain.messagecenter.common.dto.Result;
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
    public Result getMessage(@RequestParam String qName) {
      return   consumerService.getMessageByProvide(qName);
    }

    /**
     * 调用增加队列
     * @return
     */
    @GetMapping("/add")
    public Result addMessage() {
        return   consumerService.postMessageByProvide();
    }
}
