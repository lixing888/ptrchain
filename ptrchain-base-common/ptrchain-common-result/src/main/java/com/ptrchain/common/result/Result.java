package com.ptrchain.common.result;


import com.alibaba.fastjson.JSON;
import com.ptrchain.common.exception.GlobalException;
import com.ptrchain.common.exception.MyFeignException;
import lombok.Data;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;

/**
 * 统一响应 Result
 * @author lengxiangjun
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private String trace;
    private T data;
    
    private Result(int code,String msg,String trace,T data){
        this.code = code;
        this.msg = msg;
        this.trace = trace;
        this.data = data;
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return BasicCodeMsg.SUCCESS.getCode() == this.code;
    }

    /**
     * 成功时候的调用
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     * 失败时候的调用，使用Icode
     */
    public static <T> Result<T> error(ICodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }
    
    /**
     * 失败时候的调用，使用Icode,自定义错误码(只能2位)
     */
    public static <T> Result<T> error(ICodeMsg codeMsg ,int customCode) {
        return new Result<T>(codeMsg.getCode()+customCode % 100,codeMsg.getMsg());
    }
    /**
     * 失败时候的调用，使用异常
     */
    public static <T> Result<T> error(ICodeMsg codeMsg,int customCode,Throwable e) {
        return new Result<>(codeMsg.getCode()+customCode % 100, e.getMessage(),ExceptionUtils.getStackTrace(e),null);
    }
    
    /**
     * 失败时候的调用，使用异常
     */
    public static <T> Result<T> error(ICodeMsg codeMsg,Throwable e) {
        return new Result<>(codeMsg.getCode(), codeMsg.getMsg(),ExceptionUtils.getStackTrace(e),null);
    }
    
    
    /**
     * 失败时候的调用,自定义msg
     */
    public static <T> Result<T> error(ICodeMsg codeMsg,int customCode,String msg) {
        return new Result<>(codeMsg.getCode()+customCode % 100, msg,null,null);
    }
    
    /**
     * 失败时候的调用,基于GlobalException
     */
    public static <T> Result<T> error(GlobalException e) {
        return error(e,false);
    }
    
    /**
     * 失败时候的调用,基于MyFeignException
     */
    public static Result error(MyFeignException e) {
        try {
            return JSON.parseObject(e.getJsonMsg(), Result.class);
//            if (result.getCode() == 0) {
//                return Result.error(BasicCodeMsg.SERVER_ERROR.setMsg("fegin调用异常"));
//            }
        } catch (Exception ex){
            return error(BasicCodeMsg.SERVER_ERROR, e);
        }
    }
    /**
     * 失败时候的调用,基于GlobalException,提供tracelog
     */
    public static <T> Result<T> error(GlobalException e,Boolean showTrace) {
        return new Result<>(e.getCode(), e.getMsg(),showTrace ? ExceptionUtils.getStackTrace(e) : null,null);
    }

    private Result(ICodeMsg codeMsg) {
        if (codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }

    private Result(T data) {
        this.code = BasicCodeMsg.SUCCESS.getCode();
        this.msg = BasicCodeMsg.SUCCESS.getMsg();
        this.data = data;
    }

    /**
     * 序列化需要无参构造方法
     * 不要自己用无参构造方法进行new
     */
    private Result() {

    }
    private Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
}
