package com.ptrchain.messagecenter.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="消息对象")
public class MessageObj {
   @ApiModelProperty(value = "队列名称")
   private String qname;
   @ApiModelProperty(value = "消息对象")
   private Object message;


}
