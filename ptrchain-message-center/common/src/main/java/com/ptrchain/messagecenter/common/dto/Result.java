package com.ptrchain.messagecenter.common.dto;

import lombok.Data;

@Data
public class Result {
    boolean isSuccess;//是否成功
    String qName; //队列名称
    Object data; //消息对象
    String trace; //错误信息
}
