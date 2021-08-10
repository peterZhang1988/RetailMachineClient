package com.example.retailmachineclient.protocobuf.dispatcher;


import com.example.retailmachineclient.protocobuf.processor.RspGetRobotIDProcessor;
import com.example.retailmachineclient.protocobuf.processor.RspHeartBeatProcess;
import com.example.retailmachineclient.protocobuf.processor.RspLoginProcessor;
import com.example.retailmachineclient.protocobuf.processor.RspSendPageProcessor;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;

public class ClientMessageDispatcher extends BaseMessageDispatcher {
    static ClientMessageDispatcher clientMessageDispatcher;

    public static ClientMessageDispatcher getInstance(){
        if (clientMessageDispatcher==null){
            clientMessageDispatcher=new ClientMessageDispatcher();
        }
        return clientMessageDispatcher;
    }

    private ClientMessageDispatcher(){
        BaseCmd.HeartBeat heartBeat= BaseCmd.HeartBeat.newBuilder().build();
        m_ProcessorMap.put(heartBeat.getClass().toString(),new RspHeartBeatProcess());

        BaseCmd.rspLogin rspLogin=BaseCmd.rspLogin.newBuilder().build();
        m_ProcessorMap.put(rspLogin.getClass().toString(),new RspLoginProcessor());

        DDRAIServiceCmd.rspGetRobotID rspGetRobotID=DDRAIServiceCmd.rspGetRobotID.newBuilder().build();
        m_ProcessorMap.put(rspGetRobotID.getClass().toString(),new RspGetRobotIDProcessor());

        DDRAIServiceCmd.rspTest rspTest=DDRAIServiceCmd.rspTest.newBuilder().build();
        m_ProcessorMap.put(rspTest.getClass().toString(),new RspSendPageProcessor());


    }
}
