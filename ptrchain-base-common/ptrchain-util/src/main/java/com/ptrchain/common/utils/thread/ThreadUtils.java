package com.ptrchain.common.utils.thread;

/**
 * ThreadUtils
 * @author haijun.sun
 * @email haijunsun2@creditease.cn
 * @date 2019-02-17 23:30
 */
public class ThreadUtils {

	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void sleep(long millis, int nanos){
		try {
			Thread.sleep(millis, nanos);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
