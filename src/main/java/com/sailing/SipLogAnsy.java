//package com.sailing;
//
//import gov.nist.javax.sip.message.SIPMessage;
//import gov.nist.javax.sip.message.SIPRequest;
//import gov.nist.javax.sip.parser.StringMsgParser;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @program: VSCG
// * @description:
// * @author: wangsw
// * @create: 2021-01-22 10:08
// */
//public class SipLogAnsy {
//
//    public static void st() throws IOException {
//        File file = new File("C:\\Users\\edz\\Downloads\\VSIP_TASK-20200909183137-UP_20210122.2\\VSIP_TASK-20200909183137-UP_20210122.2.log");
//        if (file.exists()){
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            List<String> list = new ArrayList<>();
//            String s = "";
//            StringBuffer sb = new StringBuffer();
//            while ((s = reader.readLine()) != null){
//
//                if (s.startsWith("[ INFO ]")){
//                    if (sb.length()>0)
//                        list.add(sb.toString());
//                    sb = new StringBuffer();
//                    if (s.contains("MESSAGE") ){
//                        s = s.substring(s.indexOf("MESSAGE"));
//                        sb.append(s).append(System.lineSeparator());
//                    }else if(s.contains("SIP/2.0")){
//                        s = s.substring(s.indexOf("SIP/2.0"));
//                        sb.append(s).append(System.lineSeparator());
//                    }else {
//                        if (!s.contains("keepaliveOk") && !s.contains("Catalog消息"))
//                             System.out.println(s);
//                    }
//                }else {
//                    if (!s.startsWith("]   ["))
//                    sb.append(s).append(System.lineSeparator());
//                }
//            }
//            list.add(sb.toString());
//            for (String s1 : list) {
//                //对于需要发送出去的数据进行审计
//            }
//        }
//
//    }
//    private static SIPMessage processIncomingDataPacket(String s)  {
//        try {
//            return new StringMsgParser().parseSIPMessage(s.getBytes());
//        } catch (Exception parseException) {
//            parseException.printStackTrace();
//            return null;
//        }
//    }
//
//
//}