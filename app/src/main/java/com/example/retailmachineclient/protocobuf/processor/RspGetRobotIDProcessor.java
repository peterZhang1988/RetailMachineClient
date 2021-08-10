package com.example.retailmachineclient.protocobuf.processor;

import android.content.Context;

import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.util.Logger;
import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;


public class RspGetRobotIDProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd.rspGetRobotID rspLogin= (DDRAIServiceCmd.rspGetRobotID) msg;
        Logger.e("接口 RspGetRobotIDProcessor回复"+rspLogin.getRobotID());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GET_GetRobotID,rspLogin.getRobotID()));
//        switch (rspLogin.getYourRoleValue()){
//            case 2:
//            case 0:
////                EventBus.getDefault().post();
//                break;
//            case 196608:
////                EventBus.getDefault().post();
//                break;
//        }

//        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.LoginSuccessResult,rspLogin.getRetcodeValue()));

    }
}