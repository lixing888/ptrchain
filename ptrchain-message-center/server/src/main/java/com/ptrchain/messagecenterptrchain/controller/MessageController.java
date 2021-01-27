package com.ptrchain.messagecenterptrchain.controller;

import com.ptrchain.common.result.BasicCodeMsg;
import com.ptrchain.common.result.Result;
import com.ptrchain.messagecenter.common.dto.MessageObj;
import com.ptrchain.messagecenterptrchain.exception.NoQnameException;
import com.ptrchain.messagecenterptrchain.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("消息中心接口")
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
    @ApiOperation(value = "发送消息到队列", notes = "发送消息到队列", tags = "消息中心接口")
    @PostMapping
    public Result<String> putMessage(@RequestBody @ApiParam("消息对象") MessageObj messageObj)  {

        try {
            messageService.putMessage(messageObj.getQname(), messageObj.getMessage());
            return Result.success(messageObj.getQname());
        } catch (NoQnameException e) {
            return Result.error(BasicCodeMsg.SERVER_ERROR, e);
        }
    }

    /**
     * 从队列读取消息
     * @param qName 队列名字
     * @return
     */
    @ApiOperation(value = "读取消息到队列", notes = "读取消息到队列", tags = "消息中心接口")
    @GetMapping
    public Result<MessageObj> getMessage(@RequestParam @ApiParam("队列名称") String qName) {
      Object message= messageService.getMessage(qName);

        MessageObj messageObj = new MessageObj();
        messageObj.setMessage(message);
        messageObj.setQname(qName);
        return Result.success(messageObj);
    }

    /**
     * 创建消息队列
     * @param qName 队列名字
     * @return
     */
    @ApiOperation(value = "创建消息队列", notes = "创建消息队列", tags = "消息中心接口")
    @GetMapping("/queue")
    public Result<Boolean> createQueue(@RequestParam @ApiParam("队列名称") String qName) throws NoQnameException {
          messageService.createQueue(qName);
        return Result.success(Boolean.TRUE);
    }
    /**
     * 同步方式获取消息
     * @param qName 队列名称
     * @param waitTime 最大等待时间
     * @return
     */
    @ApiOperation(value = "同步读取消息到队列", notes = "同步读取消息到队列", tags = "消息中心接口")
    @GetMapping("sync")
    public Result<MessageObj> getMessage(@RequestParam @ApiParam("队列名称") String qName,@RequestParam @ApiParam("最大等待时间") long waitTime) {
        Object message= messageService.getMessage(qName,waitTime);
        MessageObj messageObj = new MessageObj();
        messageObj.setMessage(message);
        messageObj.setQname(qName);
        return Result.success(messageObj);
    }
}
