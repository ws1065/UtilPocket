package com.sailing.http;

import com.sailing.db.Db.HandleDb;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2022-04-06 19:10
 */
public class HttpDemo {
    private static Logger log = LoggerFactory.getLogger(HandleDb.class);

    public static void main1(String[] args) throws InterruptedException {
//        //md5加密
//
//        //
//        String s = "http://172.20.52.25:10000/api/v1/login?username=admin&password="+MD5Util.getMD5("admin");
//        String html  = getYeMian(s);
//        System.out.println(html);
//
//        //http://172.20.52.25:10000/api/v1/device/remove?serial=31011500001320000002
//
//        //http://172.20.52.26:10000/api/v1/stream/start?serial=34020000002000001275&code=31011500001320000001&_=1683281943608
//        s = "http://172.20.52.25:10000/api/v1/stream/start?serial=34020000002000001275&code=31011500001320000002";
//        html  = getYeMian(s);


    }

    public static void main(String[] args) throws InterruptedException {


        if (args.length != 7){
            System.out.println("参数不对");
            System.out.println("请跟参数 上级平台地址 下级平台ID 设备开始ID  设备结束ID token  并发线程的数目  持续时间（秒）");
            System.out.println("如: java -jar SendPacket.jar 172.20.52.26 34020000002000001275 31011500001320000001 31011500001320000099 pzUX48yVR  60 3");
            System.exit(0);
        }
        String paltformHost = args[0];
        String paltformId = args[1];
        String  deviceIDstart = args[2];
        String  deviceIDend = args[3];
        String token = args[4];

        String head = deviceIDstart.substring(0, 17);
        int start = Integer.parseInt(deviceIDstart.substring(17, 20));
        int end = Integer.parseInt(deviceIDend.substring(17, 20));

        //31011500001320000
        //31011500001320000001

        int  theardNum = Integer.parseInt(args[5]);
        int  duration = Integer.parseInt(args[6]);

//        int reousrceNum = 90 ;
//        int theardNum = 60;
//        int duration = 3;

        int queueNum = theardNum;


        List<Thread> list = new ArrayList<>();


        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue(queueNum);
        for (int i = 0; i < theardNum; i++) {

            list.add(new Thread(()->{
                while (true) {
                    try {
                        Integer take = queue.take();
                        String inta = String.format("%03d", take);
                        String s = "http://"+paltformHost+":10000/api/v1/stream/start?serial="+paltformId+"&code="+head + inta;
                        String html = getYeMian(s,token);


                        Thread.sleep(TimeUnit.SECONDS.toMillis(duration));


                        s = "http://"+paltformHost+":10000/api/v1/stream/stop?serial="+paltformId+"&code="+head + inta;
                        html = getYeMian(s,token);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        }

        for (Thread thread : list) {
            thread.start();
        }

        while (true) {
            for (int i = start; i <= end; i++) {
                queue.put(i);
            }
        }

    }

    private static String getYeMian(String uri,String token) {
        //HttpClient 超时配置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setConnectionRequestTimeout(6000).setConnectTimeout(6000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        //创建一个GET请求

        HttpGet httpGet1 = new HttpGet(uri);
        httpGet1.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
        httpGet1.addHeader("Cookie", "token="+token);
        httpGet1.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet1.addHeader("Accept-Encoding", "gzip, deflate");
        try {
            //发送请求，并执行
            CloseableHttpResponse response = httpClient.execute(httpGet1);
            InputStream in = response.getEntity().getContent();
            String html = convertStreamToString(in);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String convertStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }

}