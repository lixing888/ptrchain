package com.ptrchain.common.utils.exception;

/**
 * 默认异常信息格式化工具
 *
 */
public class DefaultExceptionMsgHandler implements ExceptionMessageFormat {

    private DefaultExceptionMsgHandler() {
    }

    private static class SingletonHolder{
        private static final DefaultExceptionMsgHandler instance = new DefaultExceptionMsgHandler();
    }

    public static DefaultExceptionMsgHandler getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * 格式化异常信息
     */
    @Override
    public String formate(Exception e) {
        return e.getMessage() + "\n";
    }

}