package com.example.retailmachineclient.util;

import com.example.retailmachineclient.socket.TcpAiClient;

public class TaskUtils {

    //TaskUtils.sendPage(tcpAiClient,DDRAIServiceCmd.AndroidPage.eAndroidPage.enPaymentMethodPage.getNumber());
//    enPurchasePage = 0;				// 选购      --- 看距离点击时间(20秒)，没有人点击恢复行走
//    enPaymentMethodPage = 1;	   	// 支付		 all pause
//    enFaceRecognitionPage = 2;	   	// 人脸支付
//    enShippingPage = 3;				// 出货页面
//    enConfigurationPage = 4;		// 配置页面
//    enShippingSuccessPage = 5;		// 出货成功页面
//    enShippingFailPage = 6;			// 出货失败页面
    //页面切换
    public static void sendPage(TcpAiClient tcpAiClient,int pageIndex){
        if(TimeIntervalUtils.lastTime == 0){
            TimeIntervalUtils.lastTime = System.currentTimeMillis();//重置上一次的发送时间
        }else{
            TimeIntervalUtils.lastTime = System.currentTimeMillis();//将上一次点击时间改成最新
            TimeIntervalUtils.interval = (int)(System.currentTimeMillis() -TimeIntervalUtils.lastTime); //计算间隔
        }
        Logger.e("-------向ai发送页面信息 pageValue = " +pageIndex+",interval="+ TimeIntervalUtils.interval);
        tcpAiClient.sendPageMsg(pageIndex,TimeIntervalUtils.interval);
    }

    //点击发送
    public static void sendPagePoint(TcpAiClient tcpAiClient,int pageIndex){
        if(TimeIntervalUtils.lastTime == 0){
            TimeIntervalUtils.lastTime = System.currentTimeMillis();//重置上一次的发送时间
        }else{
            TimeIntervalUtils.interval = (int)(System.currentTimeMillis() -TimeIntervalUtils.lastTime); //计算间隔
            TimeIntervalUtils.lastTime = System.currentTimeMillis();//重置上一次的发送时间
        }
        Logger.e("-------向ai发送页面信息 pageValue = " +pageIndex+",interval="+ TimeIntervalUtils.interval);
        tcpAiClient.sendPageMsg(pageIndex,TimeIntervalUtils.interval);
    }



}
