package com.sailing;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-08-19 17:50
 */

import com.alibaba.fastjson.util.Base64;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DownloadHtml {

    public static void getAjaxPage() throws Exception{
        String uri = "http://jandan.net/ooxx/page-1#comments";

        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
//        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setTimeout(Integer.MAX_VALUE);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        HtmlPage rootPage = webClient.getPage(uri);
        webClient.waitForBackgroundJavaScript(10* 1000);

        System.out.println();
        System.out.println(rootPage.asText());
        System.out.println(rootPage.asXml());
    }
    public static void a(String[] args) {
        try {
            String uri = "http://jandan.net/ooxx/page-1#comments";
            Document doc = Jsoup.connect(uri).get();


            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<String> oldImageUrl = new ArrayList<>();
    private static List<String> oldChildUrl = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String uri = "https://www.vmgirls.com/";


        getAll(uri);


    }

    private static void getAll(String uri) {
        String a = getYeMian(uri);
        System.out.println("new File:"+uri);
        List<String> childUrls = new ArrayList<String>();
        List<String> imagesUrl = new ArrayList<String>();

        Pattern compile = Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = compile.matcher(a);

        while (matcher.find()){
            String group = matcher.group();
            if (group.startsWith("https://www.vmgirls.com/") && !group.equalsIgnoreCase("https://www.vmgirls.com/")){
                if (!oldChildUrl.contains(group)) {
                    childUrls.add(group);
                }
            }else{
                System.out.println(group);
            }
        }


        Pattern compile1 = Pattern.compile("\\/\\/t\\.cdn\\.ink\\/image\\/\\d{4}\\/\\d{2}\\/\\d{16}\\.jpeg");
        Matcher matcher1= compile1.matcher(a);
        while (matcher1.find()){
            String group = matcher1.group();
            if (!oldImageUrl.contains(group)) {
                imagesUrl.add(group);
            }
        }
        if (!uri.equalsIgnoreCase("https://www.vmgirls.com/")) {
            uri = uri.substring(0, uri.lastIndexOf("/"));
        }

        if (imagesUrl.size() != 0){
            for (String imageUrl : imagesUrl) {
                String path = "D:\\" + uri.substring(uri.lastIndexOf("/") + 1) + "\\" + imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                System.out.println(path);
                downloadPicture("https:"+imageUrl, path);
            }
        }
        if (childUrls.size() != 0){
            for (String childUrl : childUrls) {
                getAll(childUrl);
            }
        }




    }

    private static void alterFileName() {
        try {

            File fileName = new File("C:\\Users\\sailing\\Desktop\\demo.txt");

            Map<String,Integer> fileN = new HashMap<>();
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String key = line.substring(0, 25);
                if (!fileN.containsKey(key)) {
                    fileN.put(key,1);
                }else {
                    fileN.put(key,fileN.get(key) + 1);
                }
            }
            LinkedHashMap<String, Integer> hashMap = fileN.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (olval, newval) -> olval,
                                    LinkedHashMap::new
                            )
                    );
            System.out.println();
//            for (File file : files.listFiles()) {
//                for (Map.Entry<String, String> entry : fileN.entrySet()) {
//                    if (entry.getKey().contains(file.getName())) {
//                        file.renameTo(new File(files.getAbsoluteFile()+"\\"+entry.getValue()+".mkv"));
//                        System.out.println();
//                        break;
//                    }
//                }
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void downloadFile() {
        String host = "http://mvxz.com";

        for (int i = 1; i < 100; i++) {
            if (i == 8)
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//i==8
            String uri = "http://mvxz.com/isearch.asp?page="+i+"&ktv=&type=1";
            String a = getYeMian(uri);
            //String a = "<!DOCTYPE HTML>/n<html>/n<head>/n<title> MV全集_第3页-MV下载王</title>/n<meta charset=\"utf-8\" />/n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />/n<meta NAME=\"Author\" CONTENT=\"MV下载王\">/n<meta name=\"description\" content=\"MV下载王：,最新最全的高清音乐mv下载，每月更新专业KTV歌库，并免费提供车载mv下载，找好看的mv视频就来MvXZ.com\"/>/n<meta name=\"Keywords\" content=\",MV下载王,MV下载,热门MV,MV排行榜,MV,MTV,高清MV\" />/n<link rel=\"stylesheet\" href=\"http://tu.mvmvdj.com/assets/css/main.css\" />/n<link rel=\"stylesheet\" href=\"https://cdn.staticfile.org/font-awesome/4.7.0/css/font-awesome.css\">/n<script>/nvar _hmt = _hmt || [];/n(function() {/n  var hm = document.createElement(\"script\");/n  hm.src = \"https://hm.baidu.com/hm.js?c901c3fa0db2dbca4954bac68c067b21\";/n  var s = document.getElementsByTagName(\"script\")[0]; /n  s.parentNode.insertBefore(hm, s);/n})();/n</script>/n</head>/n<body>/n<!-- Header -->/n<header id=\"header\">/n\t<h1><a href=\"http://mvxz.com/?home\"><img src=\"http://tu.mvmvdj.com/img/logo.gif\" width=\"100\" height=\"29\" alt=\"MV下载王\"> <span>by MvXZ.Com</span></a></h1>/n\t<a href=\"#menu\">菜单</a>/n</header>/n<!-- Nav -->/n<nav id=\"menu\">/n\t<ul class=\"links\">/n\t\t<li><a href=\"/\">首页</a></li>/n\t\t<li><a href=\"/ihot.htm\">热门</a></li>/n\t\t<li><a href=\"/inew.htm\">新歌</a></li>/n\t\t<li><a href=\"/inotdown.htm\">下架</a></li>/n\t\t<li><a href=\"/ihelp.htm\">帮助</a></li>/n\t\t<li><a href=\"/isoft.htm\">软件</a></li>/n\t\t<li><a href=\"/icopyright.htm\">版权</a></li>/n\t</ul>/n</nav>/n<!-- Main -->/n<div id=\"main\">/n<!-- search -->/n\t<section class=\"wrapper style1\">/n\t\t<div class=\"inner\">/n\t\t\t<div class=\"row 200%\">/n\t\t\t\t<div class=\"6u 12u$(medium)\">/n\t\t\t\t<!-- Image -->/n\t\t\t\t\t<span class=\"image fit\"><img src=\"http://tu.mvmvdj.com/images/pic-search.jpg\" alt=\"pic-search\" /></span>/n\t\t\t\t</div>/n\t\t\t\t<div class=\"6u$ 12u$(medium)\">/n\t\t\t\t<form method=\"post\" action=\"/isearch.asp\">/n\t\t\t\t\t<div class=\"row uniform\">/n\t\t\t\t\t\t<div class=\"9u 12u$(small)\">/n\t\t\t\t\t\t\t<input type=\"text\" name=\"ktv\" id=\"ktv\" value=\"\" placeholder=\"请输入您想搜索的音乐名或歌手姓名\" />/n\t\t\t\t\t\t</div>/n\t\t\t\t\t\t<div class=\"3u$ 12u$(small)\">/n\t\t\t\t\t\t\t<input type=\"submit\" value=\"Search\" class=\"button alt fit\" />/n\t\t\t\t\t\t</div>/n\t\t\t\t\t</div>/n\t\t\t\t</form>\t\t/n\t\t\t\t</div>\t/n\t\t\t</div>\t\t\t/n\t\t</div>/n\t</section>/n/n<!-- Table -->/n\t<section class=\"wrapper \">/n\t\t<div class=\"inner\">/n\t\t<span class=\"image fit\"><a href=\"https://www.ktvxg.com/single.aspx?Page=1&key=mvxz\"><img src=\"images/ktvxg.jpg\" alt=\"KTVXG\"></a></span>/n\t\t\t<header class=\"align-center\">/n\t\t\t\t<h2>MV 第3页</h2>/n\t\t\t</header>/n\t\t\t\t\t<div class=\"table-wrapper\">/n\t\t\t\t\t\t<table>/n\t\t\t\t\t\t\t<thead>/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<th width=\"6%\">编号</th>/n\t\t\t\t\t\t\t\t\t<th width=\"76%\">歌手姓名-歌曲名称_音乐类别</th>/n\t\t\t\t\t\t\t\t\t<th width=\"10%\">大小</th>/n\t\t\t\t\t\t\t\t\t<th width=\"8%\">热度</th>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t</thead>/n\t\t\t\t\t\t\t<tbody>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>1</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=48521\" class=\"link\"><span>张信哲-信仰_国语_流行_MD401907</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>42</td>/n\t\t\t\t\t\t\t\t\t<td>2882</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>2</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=21173\" class=\"link\"><span>罗文 甄妮-铁血丹心(MTV)_粤语_合唱歌曲_MA307232</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>26</td>/n\t\t\t\t\t\t\t\t\t<td>2880</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>3</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=83387\" class=\"link\"><span>范玮琪-大风吹_国语_流行_MB407150</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>49</td>/n\t\t\t\t\t\t\t\t\t<td>2880</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>4</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=167973\" class=\"link\"><span>魏新雨-百花香_国语_流行_ME501930</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>34</td>/n\t\t\t\t\t\t\t\t\t<td>2843</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>5</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=21391\" class=\"link\"><span>罗大佑-皇后大道东(MTV)_粤语_流行歌曲_MA307048</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>53</td>/n\t\t\t\t\t\t\t\t\t<td>2831</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>6</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=131472\" class=\"link\"><span>G.E.M.邓紫棋-泡沫(围炉音乐会)_国语_流行_MC700017</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>59</td>/n\t\t\t\t\t\t\t\t\t<td>2818</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>7</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=177836\" class=\"link\"><span>花僮-浪子闲话_国语_流行_MEF02552</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>66</td>/n\t\t\t\t\t\t\t\t\t<td>2795</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>8</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=40204\" class=\"link\"><span>胡夏_郁可唯-知否知否_国语_流行_MD304207</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>66</td>/n\t\t\t\t\t\t\t\t\t<td>2787</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>9</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=176247\" class=\"link\"><span>花僮-笑纳_国语_流行_MEE01316</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>72</td>/n\t\t\t\t\t\t\t\t\t<td>2756</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>10</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=60766\" class=\"link\"><span>降央卓玛-走天涯(MTV)_国语_流行歌曲_MA207264</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>45</td>/n\t\t\t\t\t\t\t\t\t<td>2721</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>11</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=8455\" class=\"link\"><span>周传雄-寂寞沙洲冷(MTV)_国语_流行歌曲_MA501184</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>34</td>/n\t\t\t\t\t\t\t\t\t<td>2636</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>12</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=8120\" class=\"link\"><span>周杰伦-晴天(MTV)_国语_流行歌曲_MA501645</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>65</td>/n\t\t\t\t\t\t\t\t\t<td>2629</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>13</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=98808\" class=\"link\"><span>邓紫棋-来自天堂的魔鬼_国语_流行歌曲_MB207456</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>47</td>/n\t\t\t\t\t\t\t\t\t<td>2613</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>14</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=37556\" class=\"link\"><span>BEYOND-光辉岁月〖演唱会〗[怀旧歌]_粤语_流行歌曲_MA100257</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>57</td>/n\t\t\t\t\t\t\t\t\t<td>2597</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>15</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=26358\" class=\"link\"><span>卢冠廷-一生所爱(MTV)_粤语_流行歌曲_MA302015</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>37</td>/n\t\t\t\t\t\t\t\t\t<td>2577</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>16</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=149247\" class=\"link\"><span>迪曲-超好听club劲爆电音嗨曲串烧_国语_串烧_NAO00238</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>140</td>/n\t\t\t\t\t\t\t\t\t<td>2571</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>17</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=107844\" class=\"link\"><span>李丽芬-爱江山更爱美人[R][怀旧歌]_国语_流行_MB103973</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>54</td>/n\t\t\t\t\t\t\t\t\t<td>2547</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>18</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=67449\" class=\"link\"><span>光良-童话(MTV)_国语_流行歌曲_MA200545</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>49</td>/n\t\t\t\t\t\t\t\t\t<td>2536</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>19</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=66735\" class=\"link\"><span>凤飞飞-追梦人(MTV)_国语_流行歌曲_MA201232</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>35</td>/n\t\t\t\t\t\t\t\t\t<td>2531</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>20</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=58056\" class=\"link\"><span>乌兰托娅-花桥流水_国语_流行_MD200400</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>51</td>/n\t\t\t\t\t\t\t\t\t<td>2522</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>21</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=39682\" class=\"link\"><span>费玉清-一剪梅_国语_流行_MD304697</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>55</td>/n\t\t\t\t\t\t\t\t\t<td>2520</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>22</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=54323\" class=\"link\"><span>秋裤大叔-一晃就老了(DJ版)_国语_舞曲_MD204268</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>56</td>/n\t\t\t\t\t\t\t\t\t<td>2518</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>23</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=2400\" class=\"link\"><span>郑源-一万个理由(MTV)_国语_流行歌曲_MA508795</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>40</td>/n\t\t\t\t\t\t\t\t\t<td>2497</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>24</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=28072\" class=\"link\"><span>任贤齐-流着泪的你的脸_国语_流行歌曲_MA300330</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>44</td>/n\t\t\t\t\t\t\t\t\t<td>2484</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>25</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=11734\" class=\"link\"><span>誓言-求佛(国语)_国语_流行歌曲_MA406936</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>48</td>/n\t\t\t\t\t\t\t\t\t<td>2478</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>26</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=42018\" class=\"link\"><span>本兮-小三你好贱_国语_流行_MD302429</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>33</td>/n\t\t\t\t\t\t\t\t\t<td>2476</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>27</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=149507\" class=\"link\"><span>苏三-八连杀_国语_流行_NAD11272</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>25</td>/n\t\t\t\t\t\t\t\t\t<td>2467</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>28</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=39940\" class=\"link\"><span>花姐-狂浪_国语_流行_MD304354</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>43</td>/n\t\t\t\t\t\t\t\t\t<td>2465</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>29</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=131667\" class=\"link\"><span>欧美女声-Stronger(冰河时代)(欣赏版)_英语_DJ嗨曲_MC600710</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>35</td>/n\t\t\t\t\t\t\t\t\t<td>2465</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>30</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=6391\" class=\"link\"><span>张学友-吻别(高清)(演唱会)_国语_流行歌曲_MA503320</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>38</td>/n\t\t\t\t\t\t\t\t\t<td>2455</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>31</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=31968\" class=\"link\"><span>邓丽君-甜蜜蜜(MTV)_国语_流行歌曲_MA105916</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>32</td>/n\t\t\t\t\t\t\t\t\t<td>2447</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>32</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=8393\" class=\"link\"><span>周华健-刀剑如梦(MTV)_国语_流行歌曲_MA501321</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>38</td>/n\t\t\t\t\t\t\t\t\t<td>2445</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>33</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=99325\" class=\"link\"><span>薛之谦-演员_国语_流行歌曲_MB206974</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>61</td>/n\t\t\t\t\t\t\t\t\t<td>2444</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>34</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=22161\" class=\"link\"><span>毛宁-涛声依旧(MTV)_国语_流行歌曲_MA306218</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>44</td>/n\t\t\t\t\t\t\t\t\t<td>2438</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>35</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=179111\" class=\"link\"><span>海来阿木-不过人间_国语_流行_MED00915</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>59</td>/n\t\t\t\t\t\t\t\t\t<td>2438</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>36</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=149634\" class=\"link\"><span>筷子兄弟-父亲_国语_流行_NCB13897</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>48</td>/n\t\t\t\t\t\t\t\t\t<td>2432</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>37</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=73024\" class=\"link\"><span>大壮-我们不一样(替换)_国语_流行_MD101497</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>61</td>/n\t\t\t\t\t\t\t\t\t<td>2403</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>38</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=27469\" class=\"link\"><span>刘德华-世界第一等(MTV)_台语_流行歌曲_MA300926</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>39</td>/n\t\t\t\t\t\t\t\t\t<td>2399</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>39</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=8761\" class=\"link\"><span>叶倩文-潇洒走一回(MTV)_国语_流行歌曲_MA500890</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>33</td>/n\t\t\t\t\t\t\t\t\t<td>2384</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>40</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=149607\" class=\"link\"><span>韩红-天路_国语_流行_NAF14439</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>58</td>/n\t\t\t\t\t\t\t\t\t<td>2364</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>41</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=6439\" class=\"link\"><span>张国荣-风继续吹(MTV)_粤语_流行歌曲_MA503170</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>24</td>/n\t\t\t\t\t\t\t\t\t<td>2363</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>42</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=65191\" class=\"link\"><span>李克勤-红日(MTV)_粤语_流行歌曲_MA202886</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>61</td>/n\t\t\t\t\t\t\t\t\t<td>2347</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>43</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=138467\" class=\"link\"><span>黄凯芹-雨中的恋人们(MTV)_粤语_流行歌曲_MC903996</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>40</td>/n\t\t\t\t\t\t\t\t\t<td>2337</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>44</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=149788\" class=\"link\"><span>邓紫棋-夜空中最亮的星（盖世英雄）_国语_流行_NBN08396</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>46</td>/n\t\t\t\t\t\t\t\t\t<td>2333</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>45</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=13653\" class=\"link\"><span>王杰-是否我真的一无所有_国语_流行歌曲_MA405055</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>36</td>/n\t\t\t\t\t\t\t\t\t<td>2330</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>46</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=8106\" class=\"link\"><span>周杰伦-搁浅(MTV)_国语_流行歌曲_MA501635</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>49</td>/n\t\t\t\t\t\t\t\t\t<td>2329</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>47</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=31951\" class=\"link\"><span>邓丽君-漫步人生路(MTV)_粤语_流行歌曲_MA105898</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>29</td>/n\t\t\t\t\t\t\t\t\t<td>2321</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>48</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=69693\" class=\"link\"><span>蔡依林_韩红-舞娘(我想和你唱第三季)_国语_流行_MD104768</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>54</td>/n\t\t\t\t\t\t\t\t\t<td>2321</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>49</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=46409\" class=\"link\"><span>王琪-红尘情痴_国语_流行_MD403993</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>67</td>/n\t\t\t\t\t\t\t\t\t<td>2296</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td>50</td>/n\t\t\t\t\t\t\t\t\t<td><a href=\"/imv.asp?id=8281\" class=\"link\"><span>周华健-花心(MTV)_国语_流行歌曲_MA501409</span></a></td>/n\t\t\t\t\t\t\t\t\t<td>34</td>/n\t\t\t\t\t\t\t\t\t<td>2277</td>/n\t\t\t\t\t\t\t\t</tr>/n\t/n\t\t\t\t\t\t\t</tbody>/n\t\t\t\t\t\t\t<tfoot>/n\t\t\t\t\t\t\t\t<tr>/n\t\t\t\t\t\t\t\t\t<td colspan=\"4\">/n共3670页 第3页 /n/n<a href=\"isearch.asp?page=2&ktv=&type=1\">上一页</a> /n/n<a href=\"isearch.asp?page=4&ktv=&type=1\">下一页</a> /n/n<a href=\"isearch.asp?page=1&ktv=&type=1\"> /n首页</a> /n/n<a href=\"isearch.asp?page=3670&ktv=&type=1\">末页</a> /n/n</td>/n\t\t\t\t\t\t\t\t</tr>/n\t\t\t\t\t\t\t</tfoot>/n\t\t\t\t\t\t</table>/n\t\t\t\t\t</div>/n\t\t</div>/n\t</section>/n</div>/n<!-- Footer -->/n<footer id=\"footer\">/n\t<div class=\"inner\">/n\t\t<div class=\"flex flex-3\">/n\t\t\t<div class=\"col\">/n\t\t\t\t<h3>分类</h3>/n\t\t\t\t<ul class=\"alt\">/n<li><a href=\"/hot.asp\">热门</a> <a href=\"/isearch.asp?ktv=国语\">国语</a> <a href=\"/isearch.asp?ktv=英语\">英语</a> <a href=\"/isearch.asp?ktv=粤语\">粤语</a> <a href=\"/isearch.asp?ktv=台语\">台语</a> <a href=\"/isearch.asp?ktv=法语\">法语</a></li>/n<li><a href=\"/isearch.asp?ktv=儿歌\">儿歌</a> <a href=\"/isearch.asp?ktv=迪曲\">迪曲</a> <a href=\"/isearch.asp?ktv=舞曲\">舞曲</a> <a href=\"/isearch.asp?ktv=民歌\">民歌</a> <a href=\"/isearch.asp?ktv=革命歌\">革命歌</a></li>/n\t\t\t\t</ul>/n\t\t\t</div>/n\t\t\t<div class=\"col\">/n\t\t\t\t<h3>男歌手</h3>/n\t\t\t\t<ul class=\"alt\">/n<li><a href=\"/isearch.asp?ktv=张学友\">张学友</a> <a href=\"/isearch.asp?ktv=周杰伦\">周杰伦</a> <a href=\"/isearch.asp?ktv=陈奕迅\">陈奕迅</a> <a href=\"/isearch.asp?ktv=谭咏麟\">谭咏麟</a> <a href=\"/isearch.asp?ktv=周华健\">周华健</a></li>/n<li><a href=\"/isearch.asp?ktv=伍佰\">伍佰</a> <a href=\"/isearch.asp?ktv=王杰\">王杰</a> <a href=\"/isearch.asp?ktv=张杰\">张杰</a> <a href=\"/isearch.asp?ktv=郑源\">郑源</a> <a href=\"/isearch.asp?ktv=林俊杰\">林俊杰</a></li>/n\t\t\t\t</ul>/n\t\t\t</div>/n\t\t\t<div class=\"col\">/n\t\t\t\t<h3>女歌手</h3>/n\t\t\t\t<ul class=\"alt\">/n<li><a href=\"/isearch.asp?ktv=邓丽君\">邓丽君</a> <a href=\"/isearch.asp?ktv=邓紫棋\">邓紫棋</a> <a href=\"/isearch.asp?ktv=卓依婷\">卓依婷</a> <a href=\"/isearch.asp?ktv=乌兰托娅\">乌兰托娅</a> <a href=\"/isearch.asp?ktv=刘若英\">刘若英</a></li>/n<li><a href=\"/isearch.asp?ktv=降央卓玛\">降央卓玛</a> <a href=\"/isearch.asp?ktv=孟庭苇\">孟庭苇</a> <a href=\"/isearch.asp?ktv=蔡依林\">蔡依林</a> <a href=\"/isearch.asp?ktv=韩红\">韩红</a> <a href=\"/isearch.asp?ktv=梁静茹\">梁静茹</a></li>\t\t\t/n\t\t\t\t</ul>/n\t\t\t</div>/n\t\t</div>/n\t</div>/n\t<div class=\"copyright\">/n\t\t<ul class=\"icons\">/n\t\t\t<li><a href=\"/\" class=\"icon fa-star\"><span class=\"label\">Star</span></a></li>/n\t\t\t<li><a href=\"/MP3/\" class=\"icon fa-music\"><span class=\"label\">Music</span></a></li>/n\t\t\t<li><a href=\"/ihot.asp\" class=\"icon fa-fire\"><span class=\"label\">Hot MV</span></a></li>/n\t\t\t<li><a href=\"/inew.asp\" class=\"icon fa-smile-o\"><span class=\"label\">New MV</span></a></li>/n\t\t\t<li><a href=\"/share/\" class=\"icon fa-film\"><span class=\"label\">Share</span></a></li>/n\t\t\t<li><a href=\"javascript:history.go(-1);\" class=\"icon fa-reply\"><span class=\"label\">reply</span></a></li>/n\t\t\t<li><a href=\"#\" class=\"icon fa-arrow-circle-up\"><span class=\"label\">Top</span></a></li>/n\t\t</ul>/n\t\t<ul class=\"icons\"><li><a href=\"http://mvxz.com/\">MvXZ.Com</a> <a href=\"/icopyright.htm\">Copyright Disclaimer</a></li> <li>MV来源于网络，仅供个人学习，禁止商用。</li>/n\t\t<li>MV comes from the network,</li><li>For personal study only,</li><li>Commercial use is prohibited.</li></ul>/n\t</div>/n</footer>/n<!-- Scripts -->/n<script src=\"http://tu.mvmvdj.com/assets/js/jquery.min.js\"></script>/n<script src=\"http://tu.mvmvdj.com/assets/js/jquery.scrolly.min.js\"></script>/n<script src=\"http://tu.mvmvdj.com/assets/js/skel.min.js\"></script>/n<script src=\"http://tu.mvmvdj.com/assets/js/util.js\"></script>/n<script src=\"http://tu.mvmvdj.com/assets/js/main.js\"></script>/n<!--MVXZ 2021/8/19 22:01:08-->/n<div style=\"display:none\">/n<script type=\"text/javascript\" src=\"https://js.users.51.la/663942.js\"></script>/n</div>/n</body>/n</html>/n\n";
            assert a != null;
            String[] lines = a.split("<tbody>")[1].split("</tbody>")[0].replaceAll("/n", "").split("<a href=\"");
            for (String line : lines) {
                if (line.startsWith("/imv.asp?")) {
                    String subUrl = host + line.split("\" class=")[0];
                    String yeMian = getYeMian(subUrl);
                    assert yeMian != null;
                    String mp4 = yeMian.split("正常<a href=\"")[1].split("\">下载MV</a>")[0];
                    String title = yeMian.split("<li>一起为【")[1].split("】这首好歌点赞！</li>")[0];
                    System.out.println(title+"         "+mp4);
                }
            }
        }
    }

    private static String getYeMian(String uri) {
        //HttpClient 超时配置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setConnectionRequestTimeout(6000).setConnectTimeout(6000).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();

        //创建一个GET请求

        HttpGet httpGet1 = new HttpGet(uri);
        httpGet1.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
        httpGet1.addHeader("Cookie", "__SDID=3f7b2c855b0d70f4; _ga=GA1.2.1073352931.1537106481; _gid=GA1.2.809290676.1539517317");
        httpGet1.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet1.addHeader("Accept-Encoding", "gzip, deflate");
        try {
            //发送请求，并执行
            CloseableHttpResponse response = httpClient.execute(httpGet1);
            InputStream in = response.getEntity().getContent();
            String html = convertStreamToString(in);
//            xiaZaiZhaoPian(html1);
            return html;

//            for (int i = 1; i < p.length; i++) {
//                String substring = p[i].substring(0, p[i].indexOf(".shtml"));
//                int i1 = Integer.parseInt(substring);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String huoDeXiaYiGeYeMian(String html1) {
        return  "http:" +html1.substring(html1.indexOf("Newer Comments\" href=\"")+22,html1.indexOf("\" class=\"next-comment-page\">上一页"));
    }

    private static void xiaZaiZhaoPian(String html1) {
        String[] meiYIGeTuPian = html1.split("<span class=\"img-hash\">");
        for (String s : meiYIGeTuPian) {
            if (s.startsWith("Ly93")) {
                String tuPianURL = s.substring(0, s.indexOf("</span>"));
                String tuPianURLStr = new String(Base64.decodeFast(tuPianURL));
                tuPianURLStr = "http:" + tuPianURLStr;
                String tuPianURLRaw = tuPianURLStr.replaceAll("mw600", "large");
                downloadPicture(tuPianURLRaw,"E:\\delete\\"+tuPianURLRaw.substring(tuPianURLRaw.lastIndexOf("/")+1));
            }
        }

    }
    //链接url下载图片
    private static void downloadPicture(String urlList,String path) {
        URL url = null;
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            System.err.println("下载图片出错！"+urlList);
            e.printStackTrace();
        }
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