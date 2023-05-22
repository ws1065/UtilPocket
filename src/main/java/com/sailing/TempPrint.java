package com.sailing;

import com.sailing.dscg.common.DateTool;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @program:
 * @description:
 * @author: wangsw
 * @create: 2021-09-30 13:36
 */
@Slf4j
public class TempPrint {

    public static ArrayBlockingQueue<String> responseCatalog = new ArrayBlockingQueue<String>(2000);

    public static ArrayBlockingQueue<String> request = new ArrayBlockingQueue<String>(2000);

    static String flahg = null;
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            request.put("A");
            responseCatalog.put("B");
        }
        while (true) {
            try {
                if (null != (flahg = responseCatalog.poll())) {
                    process(flahg);
                    continue;
                }
                flahg = request.take();
                process(flahg);
            } catch (Exception e) {
                log.error("获取Response队列中的数据欲遇到中断，msg:{}", e.getMessage());
            }
        }
    }

    private static void process(String flahg) {
        System.out.println(flahg + DateTool.dateToString(new Date()));
    }

    public static Random random = new Random();
    private static int randomPort() {
        int max = 12;
        int min = 10;
        return random.nextInt(max - min) + min + 1;
    }
    public static void start() throws InterruptedException {
        while (true){
            log.info(DateTool.dateToString(new Date()));
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        }
    }

}
class InnerClassess {
    int a ;
}