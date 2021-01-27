package com.ptrchain.common.exception;

import com.netflix.hystrix.exception.HystrixBadRequestException;

/**
 * FeignException 包装处理
 * 暂时不使用
 * @author lengxiangjun
 * @date 2019-02-17 23:30
 */
public class MyFeignException extends HystrixBadRequestException {

    private static final long serialVersionUID = 1L;

    private String jsonMsg;

    public MyFeignException(String jsonMsg) {
        super(jsonMsg);
        this.jsonMsg = jsonMsg;
    }

    public String getJsonMsg() {
        return jsonMsg;
    }
}