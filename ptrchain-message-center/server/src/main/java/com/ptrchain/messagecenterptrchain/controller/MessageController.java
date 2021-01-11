package com.ptrchain.messagecenterptrchain.controller;

import com.ptrchain.messagecenter.common.dto.MessageObj;
import com.ptrchain.messagecenter.common.dto.Result;
import com.ptrchain.messagecenterptrchain.exception.NoQnameException;
import com.ptrchain.messagecenterptrchain.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {
    @Autowired
    MessageService messageService;

    /**
     * 向队列中添加消息
     * @param messageObj 消息对象
     * @return
     */
    @PostMapping
    public Result putMessage(@RequestBody MessageObj messageObj)  {
        Result result = new Result();
        result.setData(null);
        try {
            messageService.putMessage(messageObj.getQname(), messageObj.getMessage());
            result.setSuccess(true);
            result.setQName(messageObj.getQname());
        } catch (NoQnameException e) {
            result.setTrace(e.getMessage());
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * 从队列读取消息
     * @param qName 队列名字
     * @return
     */
    @GetMapping
    public Result getMessage(@RequestParam String qName) {
      Object message= messageService.getMessage(qName);

        Result result = new Result();
        result.setData(message);
        if(message!=null) {
            result.setSuccess(true);
            result.setQName(qName);
        }
        else
        {
            result.setSuccess(false);
        }
        return result;
    }
}
