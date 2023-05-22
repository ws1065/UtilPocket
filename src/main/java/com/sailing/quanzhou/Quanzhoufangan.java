package com.sailing.quanzhou;

import com.alibaba.fastjson.JSONObject;
import com.sailing.SshExecuter;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2022-06-23 10:05
 */
public class Quanzhoufangan {

    private static void quanzhouFangan() {
        try {


            String s1 = SshExecuter.execSh("multi_scp -t 7 -p /root/ -m r -f /tmp/net226.log");
            System.out.println(s1);
            System.out.println("-------------------");
            String s2 = SshExecuter.execSh("multi_scp -t 6 -p /root/ -m r -f /tmp/net225.log");
            System.out.println(s2);
            System.out.println("-------------------");
            String s3 = SshExecuter.execSh("cat /root/net226.log");
            System.out.println(s3);
            System.out.println("-------------------");
            String s4 = SshExecuter.execSh("cat /root/net225.log");
            System.out.println(s4);
            System.out.println("-------------------");
//



            Pattern compile = Pattern.compile("(\\d+\\.\\d+).bit\\/s");
            Matcher matcher = compile.matcher(s3);

            List<Double> resultDouble = new ArrayList<>();
            Double mbit = null;
            while (matcher.find()) {
                String group = matcher.group();
                if (group.contains("Kbit/s")){
                    mbit = Double.parseDouble(group.split("Kbit")[0])/1024;
                }else {
                    mbit = Double.valueOf(group.split("Mbit")[0]);
                }

                resultDouble.add(mbit);
            }
            matcher = compile.matcher(s4);

            while (matcher.find()) {
                String group = matcher.group();
                if (group.contains("Kbit/s")){
                    mbit = Double.parseDouble(group.split("Kbit")[0])/1024;
                }else {
                    mbit = Double.valueOf(group.split("Mbit")[0]);
                }

                resultDouble.add(mbit);
            }


            //1652068163
            boolean fileAlter = false;
            String timeReg = "\\d{10}";
            Pattern compile1 = Pattern.compile(timeReg);
            Matcher matcher1 = compile1.matcher(s3);
            if (matcher1.find()){
                String group = matcher1.group();
                long time = new Date(Long.parseLong(group)*1000L).getTime();
                System.out.println(time);
                long time1 = new Date().getTime();
                System.out.println(time1);
                if ((time1-time)/1000D/60>20) {
                    fileAlter = true;
                }
            }
            matcher1 = compile1.matcher(s4);
            if (matcher1.find()){
                String group = matcher1.group();
                long time = new Date(Long.parseLong(group)*1000L).getTime();
                System.out.println(time);
                long time1 = new Date().getTime();
                System.out.println(time1);
                if ((time1-time)/1000D/60>20) {
                    fileAlter = true;
                }
            }



            if (fileAlter) {
                String url = "https://oapi.dingtalk.com/robot/send?access_token=b9ee7fff7599e764a14c924ac59636db653b99d713c353fc51d2a357efb5b760";
                String result1 = sendData(url, "泉州报告 某个机器获取流量信息异常告警：\r\n226:\r\n" + s3 + "\r\n\r\n" + "225:\r\n" + s4);
            }else {
                //如果参数不等于4  或者前两个值小于80Mbps或者后两个值小于80 或者文件修改时间超过20分钟。
                if (resultDouble.size() != 4 || (resultDouble.get(0) + resultDouble.get(1)) < 40 || (resultDouble.get(2) + resultDouble.get(3)) < 40) {
                    String url = "https://oapi.dingtalk.com/robot/send?access_token=b9ee7fff7599e764a14c924ac59636db653b99d713c353fc51d2a357efb5b760";
                    String result1 = sendData(url, "泉州报告 流量低告警：\r\n226:\r\n" + s3 + "\r\n\r\n" + "225:\r\n" + s4);
                }
            }


        }catch (Exception e){
            String url = "https://oapi.dingtalk.com/robot/send?access_token=b9ee7fff7599e764a14c924ac59636db653b99d713c353fc51d2a357efb5b760";
            String result1 = sendData(url, "泉州报告\r\n执行异常:"+e);

        }
    }

    private static String sendData(String uri,String body) {
        //HttpClient 超时配置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setConnectionRequestTimeout(6000).setConnectTimeout(6000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        //创建一个GET请求


        //设置post请求参数
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Map<String, String> paramMap1 = new HashMap<String, String>();
        paramMap1.put("content", body);

        paramMap.put("msgtype", "text");
        paramMap.put("text", paramMap1);

        HttpPost post = new HttpPost(uri);
        post.addHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(JSONObject.toJSONString(paramMap), ContentType.create("application/json", "utf-8")));
        try {
            //发送请求，并执行
            CloseableHttpResponse response = httpClient.execute(post);
            InputStream in = response.getEntity().getContent();
            return convertStreamToString(in);
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