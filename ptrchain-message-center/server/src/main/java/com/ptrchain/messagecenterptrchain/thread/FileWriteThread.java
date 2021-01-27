package com.ptrchain.messagecenterptrchain.thread;

import com.ptrchain.messagecenterptrchain.util.JsonSerilizable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FileWriteThread {
    /**
     * 异步写文件
     * @param filePath 文件路径
     */
    @Async
    public void writeFile(Object object,String filePath)
    {
        try {
            JsonSerilizable.serilizableForMap(object, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
