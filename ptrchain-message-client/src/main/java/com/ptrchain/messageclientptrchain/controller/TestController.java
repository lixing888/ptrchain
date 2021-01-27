package com.ptrchain.messageclientptrchain.controller;

import com.ptrchain.messageclientptrchain.dto.CAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@Slf4j
public class TestController {
    // public static IService instance = ClientUtils.getInstance();
    //    ExecutorService newCachedThreadPool = new ThreadPoolExecutor(200, 200 + 100, 60L, TimeUnit.SECONDS,
    //           new SynchronousQueue<Runnable>());
    private static final ThreadPoolExecutor newCachedThreadPool;

    static {
        TimeUnit unit = TimeUnit.MILLISECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = new NameTreadFactory();
        RejectedExecutionHandler handler = new MyIgnorePolicy();
        newCachedThreadPool = new ThreadPoolExecutor(100, 1000, 0, unit,
                workQueue, threadFactory, handler);
        newCachedThreadPool.prestartAllCoreThreads();
    }


    public static class MyIgnorePolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            doLog(r, e);
        }

        private void doLog(Runnable r, ThreadPoolExecutor e) {
            // 可做日志记录等
            System.err.println(r.toString() + " rejected");
//         log.info("completedTaskCount: " + e.getCompletedTaskCount());
        }
    }

    static class NameTreadFactory implements ThreadFactory {

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "my-thread-" + mThreadNum.getAndIncrement());
            log.info(t.getName() + " has been created");
            return t;
        }
    }

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/test")
    @ResponseBody
    public String Test(String Qname, String url) {

           /* CAll call= new CAll();
            call.setCALL( sendEntity,restTemplate,url);*/
        //newCachedThreadPool.execute(new CAll(Qname, restTemplate, url));

        return "ok";
    }
}
