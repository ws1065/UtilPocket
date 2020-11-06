1.VSCG引擎配置文件
    手动增加threadTime字段建议数值5,表示在流通到开启后5秒进行查询流通道信息
    手动增加debugRtp字段,在测试视频码流控制的时候设置为true 一般为false
2.发包程序含有两个程序:
a)发送厂商不支持的控制信令.我们准备的不支持的控制信令的body为json,国标的body为sdp或者为xml.故为不支持的的信令
b)发送预先未注册的rtp,然后vscg引擎通道查询网闸查看封装格式不一致则关闭通道,(注册引擎是在开启通道后的threadTime秒进行查询一次)
    发包程序启动例子    
     1. java -jar test.jar  RTPTransmit    媒体文件路径  单播地址 单播端口     
       例子:java -jar test.jar  RTPTransmit    file:/C:/media/test.mov  129.130.131.132 42050     
    2.  java -jar test.jar  SIPControl-allow    times     dstDeviceId dstHost dstPort localHost localDeviceId 
       times:循环发送次数  
3.测试中还有一项为允许控制信令,让发包程序发送包看能否经过交换系统,禁用控制信令让发包程序发包看能否经过交换系统
    中的发包程序直接使用下级代替,然后页面的控制信息中进行禁用