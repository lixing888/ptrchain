package com.ptrchain.messagecenter.common.dto;

<<<<<<< HEAD
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


=======
import lombok.Data;

@Data
public class MessageObj {
   private String qname;
   private Object message;
>>>>>>> e4b33fa0210e054af000922949131a1b4aef4972
}
