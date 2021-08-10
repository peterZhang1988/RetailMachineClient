package com.example.retailmachineclient.protocobuf.processor;

import android.content.Context;

import com.easysocket.EasySocket;
import com.example.retailmachineclient.socket.TcpClient;
import com.example.retailmachineclient.util.Logger;
import com.google.protobuf.GeneratedMessageLite;

import DDRCommProto.BaseCmd;

public class RspHeartBeatProcess extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        TcpClient tcpClient= TcpClient.tcpClient;
        Logger.e("接口--------接收返回心跳 RspHeartBeatProcess");
        if (tcpClient!=null){
            tcpClient.feedDog();
        }

    }
}
