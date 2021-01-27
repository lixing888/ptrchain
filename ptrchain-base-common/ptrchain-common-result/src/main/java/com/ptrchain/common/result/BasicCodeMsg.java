package com.ptrchain.common.result;

import com.alibaba.fastjson.JSONObject;

/**
 * 系统基础错误 BasicCodeMsg
 *
 * @author lengxiangjun
 */
public enum BasicCodeMsg implements ICodeMsg {
    
    /**
     * 成功
     */
    SUCCESS(20000, "success"),
    /**
     * 参数校验错误
     */
    PARAMETER_ERROR(40000, "参数校验失败"),
    /**
     * 需要登陆
     */
    NEED_LOGIN(40100, "未登陆"),
    /**
     * 权限不足
     */
    AUTHORIZATION_ERROR(40300, "权限不足"),
    /**
     * 资源不存在
     */
    NOT_FOUND(40400, "资源不存在"),
    /**
     * 服务器错误
     */
    SERVER_ERROR(50000, "服务端异常"),
    /**
     * 服务器暂不可用
     */
    SERVER_UNAVAILABLE(50300, "服务暂不可用"),

    /**
     * 服务网关超时
     */
    SERVER_GATEWAY_TIMEOUT(50400, "服务网关超时"),
    /**
     * 基础服务错误
     */
    RESOURCE_ERROR(50600, "基础设施异常");
    
    private int code;
    private String msg;
    
    BasicCodeMsg(int code,String msg){
        this.code = code;
        this.msg = msg;
    }
    
    private class CustomCodeMsg implements ICodeMsg{
        private String msg;
        private Integer code;
        
        CustomCodeMsg(Integer code,String msg){
            this.code = code;
            this.msg = msg;
        }
        
        @Override
        public int getCode() {
            return this.code;
        }
    
        @Override
        public String getMsg() {
            return this.msg;
        }
        
        @Override
        public CustomCodeMsg setMsg(String msg){
            this.msg = msg;
            return this;
        }
    
        @Override
        public CustomCodeMsg fillArgs(Object... args) {
            this.msg = String.format(getMsg(), args);
            return this;
        }
    
    }
    
    @Override
    public ICodeMsg setMsg(String msg){
        return new CustomCodeMsg(this.code,msg);
    }
    
    @Override
    public ICodeMsg fillArgs(Object... args) {
        return this;
    }
    
    public ICodeMsg setCustom(int code,String msg){
        return new CustomCodeMsg(this.code + code % 100, msg);
    }
    


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
    
    
    @Override
    public int getCode() {
        return this.code;
    }
    
    @Override
    public String getMsg() {
        return this.msg;
    }
    
    
    public static void main(String[] args) {
        System.out.println(BasicCodeMsg.NOT_FOUND.msg);
        System.out.println(BasicCodeMsg.NOT_FOUND.setMsg("123123").getMsg());
        System.out.println(BasicCodeMsg.NOT_FOUND.msg);
        System.out.println(BasicCodeMsg.NOT_FOUND.code);
        System.out.println(BasicCodeMsg.NOT_FOUND.setCustom(10,"123 %s").fillArgs("lalala").getMsg());
        System.out.println(BasicCodeMsg.NOT_FOUND.msg);
        System.out.println(BasicCodeMsg.NOT_FOUND.code);
        System.out.println(BasicCodeMsg.NOT_FOUND.setMsg("123222 %s").fillArgs("kakkaka").getMsg());

    
    }
}
