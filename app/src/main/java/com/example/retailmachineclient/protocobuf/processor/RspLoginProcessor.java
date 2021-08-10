package com.example.retailmachineclient.protocobuf.processor;

import android.content.Context;

import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.protocobuf.dispatcher.ClientMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.util.Logger;
import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;

public class RspLoginProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspLogin rspLogin= (BaseCmd.rspLogin) msg;
        Logger.e("接口 登陆回复code ="+rspLogin.getRetcode() +",role="+rspLogin.getYourRoleValue());
        if(rspLogin.getRetcode() == BaseCmd.rspLogin.eLoginRetCode.success){
            Logger.e("接口 登陆回复code1");
            TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance()).setLand(true);
            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_AI_SUCCESS));
        }else{
            TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance()).setLand(false);
            Logger.e("接口 登陆回复code2");
            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_AI_FAIL));
        }

        switch (rspLogin.getYourRoleValue()){

            case 2:
            case 0:
//                EventBus.getDefault().post();
                break;
            case 196608:
//                EventBus.getDefault().post();
                break;
        }

//        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.LoginSuccessResult,rspLogin.getRetcodeValue()));

    }
}
