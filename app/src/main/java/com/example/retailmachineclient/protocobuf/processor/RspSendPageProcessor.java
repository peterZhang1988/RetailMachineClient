package com.example.retailmachineclient.protocobuf.processor;

import android.content.Context;

import com.example.retailmachineclient.util.Logger;
import com.google.protobuf.GeneratedMessageLite;


import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
public class RspSendPageProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspTest rspTest= (DDRAIServiceCmd.rspTest) msg;
        Logger.e("接口 发送页面信息 回复code ="+rspTest.getStrTestMsg());
    }
}