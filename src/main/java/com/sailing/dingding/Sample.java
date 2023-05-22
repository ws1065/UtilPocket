//// This file is auto-generated, don't edit it. Thanks.
//package com.sailing.dingding;
//
//import com.alibaba.fastjson.JSON;
//import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTOHeaders;
//import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTORequest;
//import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTOResponse;
//import com.aliyun.tea.*;
//import com.aliyun.teautil.models.*;
//import com.aliyun.teaopenapi.models.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Sample {
//
//    /**
//     * 使用 Token 初始化账号Client
//     * @return Client
//     * @throws Exception
//     */
//    public static com.aliyun.dingtalkrobot_1_0.Client createClient() throws Exception {
//        Config config = new Config();
//        config.protocol = "https";
//        config.regionId = "central";
//        return new com.aliyun.dingtalkrobot_1_0.Client(config);
//    }
//
//    public static void main(String[] args_) throws Exception {
//        java.util.List<String> args = java.util.Arrays.asList(args_);
//        com.aliyun.dingtalkrobot_1_0.Client client = Sample.createClient();
//        BatchSendOTOHeaders batchSendOTOHeaders = new BatchSendOTOHeaders();
//        batchSendOTOHeaders.xAcsDingtalkAccessToken = "<your access token>";
//        // 发送文本
//        Map<String, String> param = new HashMap<>();
//        param.put("content","对于开发者而言，钉钉机器人是全局唯一的应用，即无论是用在单聊还是群聊，无论是用来推送微应用的通知还是用来对用户进行对话式服务，其对应的机器人ID都可以是唯一的，这意味开发者既可以选择仅创建一个机器人，而后将其放在各个场景下进行使用，也可以创建多个机器人，并分别部署在不同场景下。");
//        BatchSendOTORequest batchSendOTORequest = new BatchSendOTORequest()
//                .setRobotCode("dingxxxxxx")
//                .setUserIds(java.util.Arrays.asList(
//                        "manager1234"
//                ))
//                .setMsgKey("sampleText")
//                .setMsgParam(JSON.toJSONString(param));
//        try {
//            BatchSendOTOResponse response = client.batchSendOTOWithOptions(batchSendOTORequest, batchSendOTOHeaders, new RuntimeOptions());
//            System.out.println(JSON.toJSONString(response));
//        } catch (TeaException err) {
//            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
//                // err 中含有 code 和 message 属性，可帮助开发定位问题
//            }
//
//        } catch (Exception _err) {
//            TeaException err = new TeaException(_err.getMessage(), _err);
//            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
//                // err 中含有 code 和 message 属性，可帮助开发定位问题
//            }
//        }
//    }
//}