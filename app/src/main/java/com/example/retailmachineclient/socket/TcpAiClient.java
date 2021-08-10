package com.example.retailmachineclient.socket;

import android.app.Activity;
import android.content.Context;

import com.easysocket.EasySocket;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.connection.heartbeat.HeartManager;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.config.IMessageProtocol;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.easysocket.utils.LogUtil;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.protocobuf.CmdSchedule;
import com.example.retailmachineclient.protocobuf.MessageRoute;
import com.example.retailmachineclient.protocobuf.dispatcher.BaseMessageDispatcher;
import com.example.retailmachineclient.util.ActivityStackManager;
import com.example.retailmachineclient.util.LogcatHelper;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.TimeIntervalUtils;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hjq.xtoast.XToast;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteOrder;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;

/**
 * 基于EasySocket库的TCP客户端
 * create 2019/10/16
 */
public class TcpAiClient extends BaseSocketConnection {
    public Context context;
    public static TcpAiClient tcpClient;
    private ConnectionInfo info;
    public IConnectionManager manager;
    private boolean isConnected; //是否连接

    private boolean isLand; //是否登录
    private static SocketCallBack socketCallBack;
    private byte[] heads = new byte[4];  //存储头部长度信息的字节数组
    private byte[] bodyLenths = new byte[4];        //存储body体的信息长度
    private XToast xToast;

    /**
     * 获取客户端
     *
     * @param context
     * @param baseMessageDispatcher
     * @return
     */
    public static TcpAiClient getInstance(Context context, BaseMessageDispatcher baseMessageDispatcher) {
        if (tcpClient == null) {
            synchronized (TcpClient.class) {
                if (tcpClient == null) {
                    Logger.e("接口 TcpAiClient:初始化 tcpClient ==null 22");
                    tcpClient = new TcpAiClient(context, baseMessageDispatcher);
                }
            }
        }
        return tcpClient;
    }

    private TcpAiClient(Context context, BaseMessageDispatcher baseMessageDispatcher) {
        this.context = context.getApplicationContext();         //使用Application的context 避免造成内存泄漏
        m_MessageRoute = new MessageRoute(context, this, baseMessageDispatcher);
    }

    /**
     * 创建连接通道
     *
     * @param ip
     * @param port
     */
    public synchronized void createConnect(Context mContext, String ip, int port) {
        Logger.e("接口 连接tcp:" + ip + ";" + port);
        EasySocketOptions options = new EasySocketOptions.Builder()
                // 主机地址，请填写自己的IP地址，以getString的方式是为了隐藏作者自己的IP地址
                .setSocketAddress(new SocketAddress(ip, port))
                //-1 就不会统计到一定数量之后就断开连接
                .setMaxHeartbeatLoseTimes(-1)
                // 定义消息协议，方便解决 socket黏包、分包的问题
                .setReaderProtocol(new MyMessageProtocol())

                .build();
        // 初始化
        EasySocket.getInstance().createConnection(options, mContext);
        if (socketCallBack == null) {
            socketCallBack = new SocketCallBack();
            EasySocket.getInstance().subscribeSocketAction(socketCallBack);
        }
    }

    /**
     * socket行为监听
     */
    public class SocketCallBack extends SocketActionListener {
        /**
         * socket连接成功
         *
         * @param socketAddress
         */
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            LogUtil.d("--->接口 连接成功");
            isConnected = true;
            Activity activity = ActivityStackManager.getInstance().getTopActivity();
            if (activity != null) {
                if (activity.getLocalClassName().contains("LoginActivity")) {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_TCP_CONNECTED));
                } else if (activity.getLocalClassName().contains("MainActivity")) {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_TCP_CONNECTED));
                } else {
                    if (xToast != null) {
                        xToast.cancel();
                    }
                }
            }
            if (isConnected) {
                sendData(null, CmdSchedule.localLogin("admin", "admin"));
                startHeartbeat();
                startSendMsg();
            }

        }

        /**
         * socket连接失败
         *
         * @param socketAddress
         * @param isNeedReconnect 是否需要重连
         */
        @Override
        public void onSocketConnFail(SocketAddress socketAddress, boolean isNeedReconnect) {
            LogUtil.d("--->接口 连接失败");
            isConnected = false;
        }

        /**
         * socket断开连接
         *
         * @param socketAddress
         * @param isNeedReconnect 是否需要重连
         */
        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, boolean isNeedReconnect) {
            LogUtil.d("---> 接口 socket断开连接，是否需要重连：" + isNeedReconnect);
            isConnected = false;
        }

        /**
         * socket接收的数据
         *
         * @param socketAddress
         * @param readData
         */
        @Override
        public void onSocketResponse(SocketAddress socketAddress, String readData) {
        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
            byte[] headBytes = originReadData.getHeaderData();
            System.arraycopy(headBytes, 8, heads, 0, 4);
            int headLength = bytesToIntLittle(heads, 0);
            try {
                m_MessageRoute.parseBody(originReadData.getBodyBytes(), headLength);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };

    public class MyMessageProtocol implements IMessageProtocol {
        @Override
        public int getHeaderLength() {
            return 12; // 包头长度，用来保存body的长度值
        }

        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {
            if (header == null || header.length < getHeaderLength()) {
                return 0;
            }
            System.arraycopy(header, 4, bodyLenths, 0, 4);
            int bodyLength = bytesToIntLittle(bodyLenths, 0) - 8;
            return bodyLength;
        }
    }

    /**
     * 以小端模式将byte[]转成int
     */
    public int bytesToIntLittle(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * 断开连接
     */
    public void disConnect(boolean isNeedConnect) {
        isConnected = false;
        isLand = false;
        EasySocket.getInstance().getDefconnection().unSubscribeSocketAction(socketCallBack);
        EasySocket.getInstance().disconnect(isNeedConnect);
        Logger.e("接口 手动断开tcp连接");
    }

    public void sendData(BaseCmd.CommonHeader commonHeader, GeneratedMessageLite message) {
        byte[] data = m_MessageRoute.serialize(commonHeader, message);
        EasySocket.getInstance().upMessage(data);
    }

    public void sendSpeed(final float lineSpeed, final float palstance) {
        BaseCmd.reqCmdMove reqCmdMove = BaseCmd.reqCmdMove.newBuilder()
                .setLineSpeed(lineSpeed)
                .setAngulauSpeed(palstance)
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eModuleServer)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        sendData(commonHeader, reqCmdMove);
    }

    public void sendPageMsg(final int pageIndex, final int time) {
        if (isConnected && isLand) {
            if (isLand) {
                DDRAIServiceCmd.AndroidPage reqAndroidPage = DDRAIServiceCmd.AndroidPage.newBuilder()
                        .setPageValue(pageIndex)
                        .setNInterval(time)
                        .build();
                BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                        .setFromCltType(BaseCmd.eCltType.eSellV2AndroidClient)
                        .setToCltType(BaseCmd.eCltType.eAIServer)// 95module, 96 ai
                        .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                        .build();
                sendData(commonHeader, reqAndroidPage);
            } else {
            }

        } else {
//            Logger.e("接口 发送页面信息 失败 isConnected ="+isConnected+",isLand="+isLand);
        }

    }

    public void sendGetRobotID(final String salesId) {
        if (isConnected && isLand) {
            DDRAIServiceCmd.reqGetRobotID reqGetRobotID = DDRAIServiceCmd.reqGetRobotID.newBuilder()
                    .setSalesID(salesId)
                    .build();
            BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                    .setFromCltType(BaseCmd.eCltType.eSellV2AndroidClient)
                    .setToCltType(BaseCmd.eCltType.eAIServer)// 95module, 96 ai
                    .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                    .build();
            sendData(commonHeader, reqGetRobotID);
        }

    }

    // 不停发送页面信息
    public void startSendMsg() {
        Logger.e("接口 不停发送页面信息 1");
        if (isSendPageTask) {
            Logger.e("接口 不停发送页面信息 已经有不停发送页面信息任务");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                isSendPageTask = true;
                while (isConnected) {
                    int time = 0;
                    long tt = (System.currentTimeMillis() - TimeIntervalUtils.lastTime);
                    if (TimeIntervalUtils.lastTime == 0) {
                        time = 3000;
                    } else {
                        time = (int) (tt);
                    }
                    sendPageMsg(TimeIntervalUtils.pageValue, time);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LogUtil.d("---> 接口 不停发送页面信息 结束");
                isSendPageTask = false;
            }
        }).start();

    }

    // 启动心跳检测功能
    boolean isSendHearBeatTask = false;
    boolean isSendPageTask = false;

    public void startHeartbeat() {
        Logger.e("接口 发送心跳信息startHeartbeat1");
        if (isSendHearBeatTask) {
            Logger.e("接口 发送心跳信息 已经有心跳任务");
            return;
        }
        final BaseCmd.HeartBeat hb = BaseCmd.HeartBeat.newBuilder()
                .setWhatever("hb")
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                isSendHearBeatTask = true;
                while (isConnected) {
                    byte[] data = m_MessageRoute.serialize(null, hb);
                    try {
                        EasySocket.getInstance().startHeartBeat(data,
                                new HeartManager.HeartbeatListener() {
                                    // 用于判断当前收到的信息是否为服务器心跳，根据自己的实际情况实现
                                    @Override
                                    public boolean isServerHeartbeat(OriginReadData orginReadData) {
                                        LogUtil.d("---> 接口 收到服务端心跳 isServerHeartbeat");
                                        return false;
                                    }
                                }
                        );
                    } catch (Exception e) {
                        LogUtil.d("---> 接口 发送服务端心跳 异常");
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isSendHearBeatTask = false;
            }
        }).start();
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isLand() {
        return isLand;
    }

    public void setLand(boolean land) {
        isLand = land;
    }

    public void destroyConnection() {
        isLand = false;
        isConnected = false;
        EasySocket.getInstance().destroyConnection();
    }

}

