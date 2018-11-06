package go.timothy.util;

import java.util.concurrent.TimeUnit;

/**
 * @author TimothyZz
 * @description 线程工具
 * @program my-spider
 * @date 2018-10-12 17:27
 **/
public class ThreadUtil {
    public static void sleepMillisecond(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
