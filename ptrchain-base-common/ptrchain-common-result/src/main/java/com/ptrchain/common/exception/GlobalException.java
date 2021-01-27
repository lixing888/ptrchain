package com.ptrchain.common.exception;

import com.ptrchain.common.result.ICodeMsg;
import com.ptrchain.common.result.Result;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.Getter;

/**
 * 跨模块异常
 * 自定义异常统一传输信息采用 CodeMsg来配置
 *
 * @author lengxiangjun
 * @date 2019-02-17 23:30
 */
@Getter
public class GlobalException extends HystrixBadRequestException {

    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    private String trace;

    /**
     * 抛出方
     * 模块抛时使用
     */
    public GlobalException(ICodeMsg codeMsg) {
        super(codeMsg.toString());
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    /**
     * 接收方
     * 接收模块还原时使用
     *
     * @param result
     */
    public GlobalException(Result result) {
        super(result.getMsg());
        this.code = result.getCode();
        this.msg = result.getMsg();
        this.trace = result.getTrace();
    }

}
