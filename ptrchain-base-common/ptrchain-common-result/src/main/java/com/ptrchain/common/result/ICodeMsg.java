package com.ptrchain.common.result;

/**
 * 错误信息基础接口
 * @author lengxiangjun
 */
public interface ICodeMsg {

    /**
     * 错误码
     * @return
     */
    int getCode();

    /**
     * 错误信息
     * @return
     */
    String getMsg();
    
    ICodeMsg setMsg(String msg);
    
    ICodeMsg fillArgs(Object... args);
}
