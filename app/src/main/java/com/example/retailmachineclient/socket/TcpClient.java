package com.example.retailmachineclient.socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.protocobuf.MessageRoute;
import com.example.retailmachineclient.protocobuf.dispatcher.BaseMessageDispatcher;
import com.example.retailmachineclient.util.ActivityStackManager;
import com.example.retailmachineclient.util.LogcatHelper;
import com.example.retailmachineclient.util.Logger;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hjq.toast.ToastUtils;
import com.hjq.xtoast.OnClickListener;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

//import DDRCommProto.BaseCmd;


import DDRCommProto.BaseCmd;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Timeout;

/**
 * 基于OkSocket库的TCP客户端
 * create 2019/10/16
 */
public class TcpClient extends BaseSocketConnection {
    public Context context;
    public static TcpClient tcpClient;
    private ConnectionInfo info;
    public IConnectionManager manager;
    private boolean isConnected; //是否连接
    private SocketCallBack socketCallBack;
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
    public static TcpClient getInstance(Context context, BaseMessageDispatcher baseMessageDispatcher) {
        if (tcpClient == null) {
            synchronized (TcpClient.class) {
                if (tcpClient == null) {
                    tcpClient = new TcpClient(context, baseMessageDispatcher);
                }
            }
        }
        return tcpClient;
    }

    private TcpClient(Context context, BaseMessageDispatcher baseMessageDispatcher) {
        this.context = context.getApplicationContext();         //使用Application的context 避免造成内存泄漏
        m_MessageRoute = new MessageRoute(context, this, baseMessageDispatcher);
    }

    /**
     * 创建连接通道
     *
     * @param ip
     * @param port
     */
    public synchronized void createConnect(String ip, int port) {
        Logger.e("连接tcp:" + ip + ";" + port);
        try {
            info = new ConnectionInfo(ip, port);
            manager = OkSocket.open(info);
            OkSocketOptions.Builder clientOptions = new OkSocketOptions.Builder();
//            clientOptions.setPulseFeedLoseTimes(1000);
//            clientOptions.setReaderProtocol(new ReaderProtocol());
            manager.option(clientOptions.build());
            socketCallBack = new SocketCallBack();
            manager.registerReceiver(socketCallBack);
            manager.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 连接的状态信息
     */
    public class SocketCallBack extends SocketActionAdapter {
//        private BaseDialog waitDialog;

        public SocketCallBack() {
            super();
        }

        /**
         * 当客户端连接成功会回调这个方法
         *
         * @param info
         * @param action
         */
        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            isConnected = true;
            Logger.e("--------连接成功---------");
            Activity activity = ActivityStackManager.getInstance().getTopActivity();
            if (activity != null) {
                if (activity.getLocalClassName().contains("LoginActivity")) {
//                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.tcpConnected));
                } else {
//                    if (xToast != null) {
//                        xToast.cancel();
//                    }
//                    sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), CmdSchedule.localLogin(GlobalParameter.getAccount(), GlobalParameter.getPassword()));
//                    LitePal.deleteAll(RobotCoordinatesWork.class);
//                    tcpClient.reqGetAutoActualPath(ByteString.copyFromUtf8("map"));
                }
            }
//            if (waitDialog != null) {
//                if (waitDialog.isShowing()) {
//                    waitDialog.dismiss();
//                }
//            }
//            sendHeartBeat();
        }

        /**
         * 当客户端连接失败会调用
         *
         * @param info
         * @param action
         * @param e
         */
        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            Logger.e("--------连接tcp 失败:" + e.toString());
            isConnected = false;
        }

        /**
         * 当连接法断开时会调用此方
         *
         * @param info
         * @param action
         * @param e
         */
        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            isConnected = false;
            Activity activity = ActivityStackManager.getInstance().getTopActivity();
            if (activity != null) {
                if (activity.getLocalClassName().contains("PayActivity")) {
                    Logger.e("当前activity" + ActivityStackManager.getInstance().getTopActivity().getLocalClassName());
//                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.TcpAndUdpFailed));
                    disConnect();
                } else {
                    if (xToast != null) {
                        xToast.cancel();
                    }
                    Logger.e("网络连接断开，当前处于" + activity.getLocalClassName());
//                    LogcatHelper.getInstance(context).stop();//出现异常断开日志保存
//                    showXToast(activity);
                }
            }
        }

        /**
         * 当接收tcp服务端数据时调用此方法
         *
         * @param info
         * @param action
         * @param data
         */
        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            byte[] headBytes = data.getHeadBytes();
            System.arraycopy(headBytes, 8, heads, 0, 4);
            int headLength = bytesToIntLittle(heads, 0);
            try {
                m_MessageRoute.parseBody(data.getBodyBytes(), headLength);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            Logger.d("---------" + action);
        }
    }

    /**
     * 自定义解析头
     */
    public class ReaderProtocol implements IReaderProtocol {

        /**
         * 返回固定的头部长度
         *
         * @return
         */
        @Override
        public int getHeaderLength() {
            return 12;
        }

        /**
         * 返回不固定长的body包长度
         *
         * @param header
         * @param byteOrder
         * @return
         */
        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {
            if (header == null || header.length < getHeaderLength()) {
                return 0;
            }
            System.arraycopy(header, 4, bodyLenths, 0, 4);
            return bytesToIntLittle(bodyLenths, 0) - 8;
        }
    }

    /**
     * 显示全局弹窗
     *
     * @param activity
     */
//    private void showXToast(Activity activity) {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                xToast = new XToast(activity.getApplication())
//                        .setView(R.layout.xtoast_layout)
//                        .setDraggable()
//                        .setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//                        .setAnimStyle(android.R.style.Animation_Dialog)
//                        .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER)
//                        .setOnClickListener(R.id.tvBackLogin, new OnClickListener<TextView>() {
//                            @Override
//                            public void onClick(XToast toast, TextView view) {
//                                toast.cancel();
//                                disConnect();
////                                Intent intent = new Intent(activity, LoginActivity.class);
////                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
////                                toast.startActivity(intent);
//                            }
//                        })
//                        .show();
//
////                Intent intent=new Intent(activity,LoginActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
////                xToast.startActivity(intent);
////                disConnect();
////                xToast.cancel();
//            }
//        });
//    }


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
     * 喂狗操作，否则当超过一定次数的心跳发送,未得到喂狗操作后,狗将会将此次连接断开重连.
     */
    public void feedDog() {
        if (manager != null) {
            manager.getPulseManager().feed();
            Logger.d("---喂狗");
        }
    }

    /**
     * 断开连接
     */
    public void disConnect() {
        if (manager != null) {
            manager.unRegisterReceiver(socketCallBack);
            manager.disconnect();
            isConnected = false;
            manager = null;
            Logger.e("断开tcp连接");
        }
    }


//    /**
//     * 发送消息
//     *
//     * @param commonHeader
//     * @param message
//     */
//    public void sendData(BaseCmd.CommonHeader commonHeader, GeneratedMessageLite message) {
//        if (manager != null) {
//            byte[] data = m_MessageRoute.serialize(commonHeader, message);
//            Logger.d("--------sendData");
//            manager.send(new SendData(data));
//        }
//    }


//    /**
//     * 持续发送心跳
//     */
//    public void sendHeartBeat() {
//        final BaseCmd.HeartBeat hb = BaseCmd.HeartBeat.newBuilder()
//                .setWhatever("hb")
//                .build();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (isConnected && manager != null) {
//                    try {
//                        manager.getPulseManager().setPulseSendable(new PulseData(m_MessageRoute.serialize(null, hb))).pulse();
//                        Logger.d("发送心跳包");
//                        Thread.sleep(3000);
//                    } catch (NullPointerException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }


    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isConnected() {
        return isConnected;
    }


//    /**
//     * 获得某个地图下的信息
//     */
//    public void getMapInfo(ByteString routeName){
//        DDRVLNMap.reqGetDDRVLNMapEx reqGetDDRVLNMapEx=DDRVLNMap.reqGetDDRVLNMapEx.newBuilder()
//                .setOnerouteName(routeName)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqGetDDRVLNMapEx);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqGetDDRVLNMapEx.class));
//            ByteString message=reqGetDDRVLNMapEx.toByteString();
//            Logger.e("解析前"+reqGetDDRVLNMapEx.toByteString());
//            postHttpMsg(typename,message);
//        }
//        Logger.e("请求地图信息");
//    }
//
//
//    /**
//     * 还没采集完时退出采集模式，再进入请求之前采集过的栅格图
//     */
//    public void getAllLidarMap(){
//        Logger.e("获取所有栅格地图");
//        BaseCmd.reqGetAllLidarCurSubMap reqGetAllLidarCurSubMap= BaseCmd.reqGetAllLidarCurSubMap.newBuilder()
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqGetAllLidarCurSubMap);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqGetAllLidarCurSubMap.class));
//            ByteString message=reqGetAllLidarCurSubMap.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//    /**
//     * 请求文件（txt、png) 刷新文件列表
//     */
//    public void requestFile() {
//        String all="all";
//        //final ByteString currentFile = ByteString.copyFromUtf8("OneRoute_*" + "/bkPic.png");
//        BaseCmd.reqClientGetMapInfo reqClientGetMapInfo=BaseCmd.reqClientGetMapInfo.newBuilder()
//                .setParam(ByteString.copyFromUtf8(all))
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqClientGetMapInfo);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqClientGetMapInfo.class));
//            ByteString message=reqClientGetMapInfo.toByteString();
//            postHttpMsg(typename,message);
//        }
//        Logger.e("请求文件中....");
//    }
//
//
////    /**
////     * 发送线速度，角速度
////     * @param lineSpeed
////     * @param palstance
////     */
//    public void sendSpeed(final float lineSpeed, final float palstance) {
//        BaseCmd.reqCmdMove reqCmdMove = BaseCmd.reqCmdMove.newBuilder()
//                .setLineSpeed(lineSpeed)
//                .setAngulauSpeed(palstance)
//                .build();
//        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
//                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
//                .setToCltType(BaseCmd.eCltType.eModuleServer)
//                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(commonHeader, reqCmdMove);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf( BaseCmd.reqCmdMove.class));
//            ByteString message=reqCmdMove.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//
//    /**
//     * 添加或删除临时任务
//     * @param routeName
//     * @param taskName
//     * @param num
//     * @param type
//     */
//    public void addOrDetTemporary(ByteString routeName, ByteString taskName,int num,int type){
//        DDRVLNMap.reqTaskOperational.OptItem optItem= DDRVLNMap.reqTaskOperational.OptItem.newBuilder()
//                .setOnerouteName(routeName)
//                .setTaskName(taskName)
//                .setRunCount(num)
//                .setTypeValue(type)
//                .build();
//        DDRVLNMap.reqTaskOperational reqTaskOperational=DDRVLNMap.reqTaskOperational.newBuilder()
//                .setOptSet(optItem)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqTaskOperational);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqTaskOperational.class));
//            ByteString message=reqTaskOperational.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//
//    /**
//     * 退出当前模式
//     */
//    public void exitModel() {
//        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
//                .setError("noError")
//                .build();
//        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
//                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
//                .setToCltType(BaseCmd.eCltType.eModuleServer)
//                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(commonHeader, reqCmdEndActionMode);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqCmdEndActionMode.class));
//            ByteString message=reqCmdEndActionMode.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 发送去噪后的图片到服务端
//     * @param mapName
//     * @param pictureName
//     * @param data
//     * @param isReset
//     */
//    public void sendEditMap(String mapName,String pictureName,byte[]data,boolean isReset){
//        DDRVLNMap.reqSetMapBkpicData reqSetMapBkpicData= DDRVLNMap.reqSetMapBkpicData.newBuilder()
//                .setOnerouteName(ByteString.copyFromUtf8(mapName))
//                .setBkpicDataname(ByteString.copyFromUtf8(pictureName))
//                .setBkpicData(ByteString.copyFrom(data))
//                .setBResumeOriginal(isReset)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqSetMapBkpicData);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqSetMapBkpicData.class));
//            ByteString message=reqSetMapBkpicData.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 原图去噪相关功能(在地图不旋转的情况下适用)
//     */
//    public void reqEditMap(List<Rectangle>rectangles,int type,boolean isReset,String mapName){
//        List<BaseCmd.reqEditorLidarMap.eraseRange> eraseRanges=new ArrayList<>();
//        for (Rectangle rectangle:rectangles){
//            BaseCmd.reqEditorLidarMap.eraseRange eraseRange=BaseCmd.reqEditorLidarMap.eraseRange.newBuilder()
//                    .setLeft(rectangle.getFirstPoint().getY())
//                    .setTop(rectangle.getFirstPoint().getX())
//                    .setBottom(rectangle.getSecondPoint().getX())
//                    .setRight(rectangle.getSecondPoint().getY())
//                    .build();
//            eraseRanges.add(eraseRange);
//        }
//        BaseCmd.reqEditorLidarMap reqEditorLidarMap=BaseCmd.reqEditorLidarMap.newBuilder()
//                .addAllRange(eraseRanges)
//                .setTypeValue(type)
//                .setBOriginal(isReset)
//                .setOneroutename(ByteString.copyFromUtf8(mapName))
//                .build();
//        sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqEditorLidarMap);
//    }
//
//    /**
//     * 编辑噪点（当地图旋转就必须要四个点才能确定矩形）
//     */
//    public void reqEditMapNoise(List<Rectangle>rectangles ,int type,boolean isReset,String mapName){
//        List<BaseCmd.reqEditorLidarMap.VirtualLineItem> noiseList=new ArrayList<>();
//        for (Rectangle rectangle:rectangles){
//            List<BaseCmd.reqEditorLidarMap.optPoint> optPoints=new ArrayList<>();
//            for (XyEntity xyEntity:rectangle.getRectanglePoints()){
//                BaseCmd.reqEditorLidarMap.optPoint optPoint=BaseCmd.reqEditorLidarMap.optPoint.newBuilder()
//                        .setPtX(xyEntity.getX())
//                        .setPtY(xyEntity.getY())
//                        .build();
//                optPoints.add(optPoint);
//            }
//            BaseCmd.reqEditorLidarMap.VirtualLineItem virtualLineItem= BaseCmd.reqEditorLidarMap.VirtualLineItem
//                    .newBuilder()
//                    .addAllLineSet(optPoints)
//                    .build();
//            noiseList.add(virtualLineItem);
//        }
//        BaseCmd.reqEditorLidarMap reqEditorLidarMap=BaseCmd.reqEditorLidarMap.newBuilder()
//                .addAllVlSet(noiseList)
//                .setTypeValue(type)
//                .setBOriginal(isReset)
//                .setOneroutename(ByteString.copyFromUtf8(mapName))
//                .build();
//        sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqEditorLidarMap);
//    }
//
//    /**
//     * 处理虚拟墙（该命令会修改bkPic_obs.png的地图）
//     * type=6 ->添加虚拟墙，由多个线段组成。就是 reqEditorLidarMap 中的 vlSet。
//     * type=7 -> 移除虚拟墙，由多个线段组成。就是 reqEditorLidarMap 中的 vlSet。
//     */
//    public void reqEditMapVirtual(int type,List<BaseCmd.reqEditorLidarMap.VirtualLineItem> virtualLineItems,String mapName){
//        BaseCmd.reqEditorLidarMap reqEditorLidarMap=BaseCmd.reqEditorLidarMap.newBuilder()
//                .setTypeValue(type)
//                .addAllVlSet(virtualLineItems)
//                .setOneroutename(ByteString.copyFromUtf8(mapName))
//                .build();
//        sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqEditorLidarMap);
//    }
//
//    /**
//     * 获取上位机版本信息
//     */
//    public void getHostComputerEdition() {
//        String bytes="get";
//        ByteString get=ByteString.copyFromUtf8(bytes);
//        BaseCmd.reqGetSysVersion reqGetSysVersion = BaseCmd.reqGetSysVersion.newBuilder()
//                .setParam(get)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqGetSysVersion);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqGetSysVersion.class));
//            ByteString message=reqGetSysVersion.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 只保存已修改的目标点到服务器
//     */
//    public void savePointData(DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx,List<TargetPoint> targetPoints){
//        List<DDRVLNMap.targetPtItem> targetPtItems=new ArrayList<>();
//        for (int i=0;i<targetPoints.size();i++){
//            TargetPoint targetPoint=targetPoints.get(i);
//            DDRVLNMap.space_pointEx space_pointEx=DDRVLNMap.space_pointEx.newBuilder()
//                    .setX(targetPoint.getX())
//                    .setY(targetPoint.getY())
//                    .setTheta(targetPoint.getTheta())
//                    .build();
//            DDRVLNMap.targetPtItem targetPtItem=DDRVLNMap.targetPtItem.newBuilder()
//                    .setPtName(ByteString.copyFromUtf8(targetPoint.getName()))
//                    .setTargetPtTypeValue(targetPoint.getPointType().getTypeValue())
//                    .setPtData(space_pointEx).build();
//            targetPtItems.add(targetPtItem);
//        }
//        DDRVLNMap.DDRMapTargetPointData targetPointData=DDRVLNMap.DDRMapTargetPointData.newBuilder()
//                .addAllTargetPt(targetPtItems)
//                .build();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
//                .setBasedata(reqDDRVLNMapEx.getBasedata())
//                .setSpacedata(reqDDRVLNMapEx.getSpacedata())
//                .setTargetPtdata(targetPointData)
//                .addAllTaskSet(reqDDRVLNMapEx.getTaskSetList())
//                .setPathSet(reqDDRVLNMapEx.getPathSet())
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDDRVLNMapEx1);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqDDRVLNMapEx.class));
//            ByteString message=reqDDRVLNMapEx1.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//
//    /**
//     * 保存已修改路径数据到服务
//     * @param reqDDRVLNMapEx
//     * @param pathLines
//     */
//    public void savePathData(DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx,List<PathLine> pathLines){
//        List<DDRVLNMap.path_line_itemEx> pathLineItemExes=new ArrayList<>();
//        for (int i=0;i<pathLines.size();i++){
//            PathLine pathLine=pathLines.get(i);
//            DDRVLNMap.path_line_config path_line_config=DDRVLNMap.path_line_config.newBuilder()
//                    .setConfig(ByteString.copyFromUtf8(pathLine.getConfig())).build();
//            List<DDRVLNMap.path_line_itemEx.path_lint_pt_Item> pathLintPtItems=new ArrayList<>();
//            List<PathLine.PathPoint> pathPoints=pathLine.getPathPoints();
//            for (int j=0;j<pathPoints.size();j++){
//                DDRVLNMap.space_pointEx spacePointEx=DDRVLNMap.space_pointEx.newBuilder()
//                        .setX(pathPoints.get(j).getX())
//                        .setY(pathPoints.get(j).getY())
//                        .build();
//                DDRVLNMap.path_line_itemEx.path_lint_pt_Item path_lint_pt_item=DDRVLNMap.path_line_itemEx.path_lint_pt_Item.newBuilder()
//                        .setPt(spacePointEx)
//                        .setRotationangle(pathPoints.get(j).getRotationAngle())
//                        .setTypeValue(pathPoints.get(j).getPointType())
//                        .setPtName(ByteString.copyFromUtf8(pathPoints.get(j).getName()))
//                        .build();
//                pathLintPtItems.add(path_lint_pt_item);
//            }
//            DDRVLNMap.path_line_itemEx path_line_itemEx=DDRVLNMap.path_line_itemEx.newBuilder()
//                    .setName(ByteString.copyFromUtf8(pathLine.getName()))
//                    .setModeValue(pathLine.getPathModel())
//                    .setTypeValue(pathLine.getPathType())
//                    .setVelocity(pathLine.getVelocity())
//                    .setConfig(path_line_config)
//                    .setVelocity(pathLine.getVelocity())
//                    .addAllPointSet(pathLintPtItems)
//                    .setBStartFromSeg0(pathLine.isbStartFromSeg0())
//                    .setBNoCornerSmoothing(pathLine.isbNoCornerSmoothing())
//                    .setNReserve(pathLine.getCleanType())
//                    .build();
//            pathLineItemExes.add(path_line_itemEx);
//        }
//        Logger.e("----------路径size:"+pathLineItemExes.size());
//        DDRVLNMap.DDRMapPathDataEx ddrMapPathDataEx=DDRVLNMap.DDRMapPathDataEx.newBuilder()
//                .addAllPathLineData(pathLineItemExes)
//                .build();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
//                .setBasedata(reqDDRVLNMapEx.getBasedata())
//                .setSpacedata(reqDDRVLNMapEx.getSpacedata())
//                .setTargetPtdata(reqDDRVLNMapEx.getTargetPtdata())
//                .addAllTaskSet(reqDDRVLNMapEx.getTaskSetList())
//                .setPathSet(ddrMapPathDataEx)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDDRVLNMapEx1);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqDDRVLNMapEx.class));
//            ByteString message=reqDDRVLNMapEx1.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//
//    /**
//     * 保存已修改任务列表到服务
//     * @param reqDDRVLNMapEx
//     * @param taskModes
//     */
//    public void saveTaskData(DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx,List<TaskMode> taskModes) throws NullPointerException{
//        List<DDRVLNMap.task_itemEx> taskItemExes=new ArrayList<>();
//        for (int i=0;i<taskModes.size();i++){
//            TaskMode taskMode=taskModes.get(i);
//            List<BaseMode> baseModes=taskMode.getBaseModes();
//            List<DDRVLNMap.path_elementEx> path_elementExes=new ArrayList<>();
//            List<Integer> integerList=new ArrayList<>();
//            for (int j=0;j<baseModes.size();j++){
//                DDRVLNMap.path_elementEx path_elementEx;
//                String name="";
//                int typeValue=0;
//                if (baseModes.get(j).getType()==1){
//                    PathLine pathLine= (PathLine) baseModes.get(j);
//                    name=pathLine.getName();
//                    typeValue=1;
//                }else if (baseModes.get(j).getType()==2){
//                    TargetPoint targetPoint= (TargetPoint) baseModes.get(j);
//                    name=targetPoint.getName();
//                    typeValue=2;
//                }
//                path_elementEx=DDRVLNMap.path_elementEx.newBuilder()
//                        .setName(ByteString.copyFromUtf8(name))
//                        .setTypeValue(typeValue)
//                        .build();
//                path_elementExes.add(path_elementEx);
//            }
//            Logger.e("数据大小"+taskMode.getWeekList().size());
//            for (int k=0;k<taskMode.getWeekList().size();k++){
//                int v=taskMode.getWeekList().get(k);
//                integerList.add(v);
//                Logger.e("星期"+taskMode.getWeekList().get(k));
//            }
//            DDRVLNMap.timeItem timeItem=DDRVLNMap.timeItem.newBuilder()
//                    .setStartHour(taskMode.getStartHour())
//                    .setStartMin(taskMode.getStartMin())
//                    .setEndHour(taskMode.getEndHour())
//                    .setEndMin(taskMode.getEndMin())
//                    .build();
//            DDRVLNMap.task_itemEx task_itemEx=DDRVLNMap.task_itemEx.newBuilder()
//                    .setName(ByteString.copyFromUtf8(taskMode.getName()))
//                    .setRunCount(taskMode.getRunCounts())
//                    .setStateValue(taskMode.getTaskState())
//                    .setTypeValue(taskMode.getType())
//                    .setTimeSet(timeItem)
//                    .setBIsRepeat(taskMode.isbIsRepeat())
//                    .addAllPathSet(path_elementExes)
//                    .addAllWeekSet(integerList)
//                    .build();
//            taskItemExes.add(task_itemEx);
//        }
//        Logger.e("保存到服务端的任务size:"+taskItemExes.size());
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
//                .setBasedata(reqDDRVLNMapEx.getBasedata())
//                .setSpacedata(reqDDRVLNMapEx.getSpacedata())
//                .setTargetPtdata(reqDDRVLNMapEx.getTargetPtdata())
//                .addAllTaskSet(taskItemExes)
//                .setPathSet(reqDDRVLNMapEx.getPathSet())
//                .build();
//        if (GlobalParameter.isLan){
//            Logger.e("局域网---");
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDDRVLNMapEx1);
//        }else {
//            Logger.e("广域网---");
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqDDRVLNMapEx.class));
//            ByteString message=reqDDRVLNMapEx1.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//
//
//    /**
//     * 所有数据保存到服务端
//     */
//    public void saveDataToServer(DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx,List<TargetPoint> targetPoints,List<PathLine> pathLines,List<TaskMode> taskModes){
//        /*****************************保存到服务的目标点数据********************************************/
//        List<DDRVLNMap.targetPtItem> targetPtItems=new ArrayList<>();
//        for (int i=0;i<targetPoints.size();i++){
//            TargetPoint targetPoint=targetPoints.get(i);
//            DDRVLNMap.space_pointEx space_pointEx=DDRVLNMap.space_pointEx.newBuilder()
//                    .setX(targetPoint.getX())
//                    .setY(targetPoint.getY())
//                    .setTheta(targetPoint.getTheta())
//                    .build();
//            Logger.e("保存的点名字："+targetPoint.getName());
//            DDRVLNMap.targetPtItem targetPtItem=DDRVLNMap.targetPtItem.newBuilder()
//                    .setPtName(ByteString.copyFromUtf8(targetPoint.getName()))
//                    .setTargetPtTypeValue(targetPoint.getPointType().getTypeValue())
//                    .setPtData(space_pointEx).build();
//            targetPtItems.add(targetPtItem);
//        }
//        DDRVLNMap.DDRMapTargetPointData targetPointData=DDRVLNMap.DDRMapTargetPointData.newBuilder()
//                .addAllTargetPt(targetPtItems)
//                .build();
//        Logger.e("保存到服务端的目标点size:"+targetPtItems.size());
//
//        /****************************保存到服务的路径数据**********************************************/
//        List<DDRVLNMap.path_line_itemEx> pathLineItemExes=new ArrayList<>();
//        for (int i=0;i<pathLines.size();i++){
//            PathLine pathLine=pathLines.get(i);
//            DDRVLNMap.path_line_config path_line_config=DDRVLNMap.path_line_config.newBuilder()
//                    .setConfig(ByteString.copyFromUtf8(pathLine.getConfig())).build();
//            List<DDRVLNMap.path_line_itemEx.path_lint_pt_Item> pathLintPtItems=new ArrayList<>();
//            List<PathLine.PathPoint> pathPoints=pathLine.getPathPoints();
//            for (int j=0;j<pathPoints.size();j++){
//                DDRVLNMap.space_pointEx spacePointEx=DDRVLNMap.space_pointEx.newBuilder()
//                        .setX(pathPoints.get(j).getX())
//                        .setY(pathPoints.get(j).getY())
//                        .build();
//                DDRVLNMap.path_line_itemEx.path_lint_pt_Item path_lint_pt_item=DDRVLNMap.path_line_itemEx.path_lint_pt_Item.newBuilder()
//                        .setPt(spacePointEx)
//                        .setRotationangle(pathPoints.get(j).getRotationAngle())
//                        .setTypeValue(pathPoints.get(j).getPointType())
//                        .setPtName(ByteString.copyFromUtf8(pathPoints.get(j).getName()))
//                        .build();
//                pathLintPtItems.add(path_lint_pt_item);
//            }
//            DDRVLNMap.path_line_itemEx path_line_itemEx=DDRVLNMap.path_line_itemEx.newBuilder()
//                    .setName(ByteString.copyFromUtf8(pathLine.getName()))
//                    .setModeValue(pathLine.getPathModel())
//                    .setTypeValue(pathLine.getPathType())
//                    .setVelocity(pathLine.getVelocity())
//                    .setConfig(path_line_config)
//                    .setVelocity(pathLine.getVelocity())
//                    .addAllPointSet(pathLintPtItems)
//                    .setBStartFromSeg0(pathLine.isbStartFromSeg0())
//                    .setBNoCornerSmoothing(pathLine.isbNoCornerSmoothing())
//                    .build();
//            pathLineItemExes.add(path_line_itemEx);
//        }
//        Logger.e("----------路径size:"+pathLineItemExes.size());
//        DDRVLNMap.DDRMapPathDataEx ddrMapPathDataEx=DDRVLNMap.DDRMapPathDataEx.newBuilder()
//                .addAllPathLineData(pathLineItemExes)
//                .build();
//        /**********************************************保存到服务的任务数据***************************************/
//        List<DDRVLNMap.task_itemEx> taskItemExes=new ArrayList<>();
//        for (int i=0;i<taskModes.size();i++){
//            TaskMode taskMode=taskModes.get(i);
//            List<BaseMode> baseModes=taskMode.getBaseModes();
//            List<DDRVLNMap.path_elementEx> path_elementExes=new ArrayList<>();
//            for (int j=0;j<baseModes.size();j++){
//                DDRVLNMap.path_elementEx path_elementEx;
//                String name="";
//                int typeValue=0;
//                if (baseModes.get(j).getType()==1){
//                    PathLine pathLine= (PathLine) baseModes.get(j);
//                    name=pathLine.getName();
//                    typeValue=1;
//                }else if (baseModes.get(j).getType()==2){
//                    TargetPoint targetPoint= (TargetPoint) baseModes.get(j);
//                    name=targetPoint.getName();
//                    typeValue=2;
//                }
//                path_elementEx=DDRVLNMap.path_elementEx.newBuilder()
//                        .setName(ByteString.copyFromUtf8(name))
//                        .setTypeValue(typeValue)
//                        .build();
//                path_elementExes.add(path_elementEx);
//            }
//            DDRVLNMap.timeItem timeItem=DDRVLNMap.timeItem.newBuilder()
//                    .setStartHour(taskMode.getStartHour())
//                    .setStartMin(taskMode.getStartMin())
//                    .setEndHour(taskMode.getEndHour())
//                    .setEndMin(taskMode.getEndMin())
//                    .build();
//            DDRVLNMap.task_itemEx task_itemEx=DDRVLNMap.task_itemEx.newBuilder()
//                    .setName(ByteString.copyFromUtf8(taskMode.getName()))
//                    .setRunCount(999)
//                    .setStateValue(taskMode.getTaskState())
//                    .setTypeValue(taskMode.getType())
//                    .setTimeSet(timeItem)
//                    .addAllPathSet(path_elementExes)
//                    .build();
//            taskItemExes.add(task_itemEx);
//        }
//        Logger.e("-------------保存到服务端的任务size:"+taskItemExes.size());
//        /***************************************保存到服务的空间信息******************************************/
//        List<DDRVLNMap.space_item> space_items=new ArrayList<>();           //接收到的空间数据
//        List<SpaceItem> spaceItems=MapFileStatus.getInstance().getSpaceItems();
//        Logger.e("-------空间信息:"+spaceItems.size());
//        for (int i=0;i<spaceItems.size();i++){
//            DDRVLNMap.line line=DDRVLNMap.line.newBuilder()
//                    .addAllPointset(spaceItems.get(i).getLines())
//                    .build();
//            SpaceItem.Circle circle1=spaceItems.get(i).getCircle();
//            DDRVLNMap.circle circle;
//            if (circle1!=null){
//                circle=DDRVLNMap.circle.newBuilder()
//                        .setCenter(DDRVLNMap.space_pointEx.newBuilder().setX(circle1.getX()).setY(circle1.getY()).build())
//                        .setRadius(circle1.getRadius())
//                        .build();
//            }else {
//                circle=DDRVLNMap.circle.newBuilder().build();
//            }
//            DDRVLNMap.polygon polygon=DDRVLNMap.polygon.newBuilder()
//                    .addAllPointset(spaceItems.get(i).getPolygons())
//                    .build();
//            DDRVLNMap.space_item space_item=DDRVLNMap.space_item.newBuilder()
//                    .setName(ByteString.copyFromUtf8(spaceItems.get(i).getName()))
//                    .setTypeValue(spaceItems.get(i).getType())
//                    .setLinedata(line)
//                    .setCircledata(circle)
//                    .setPolygondata(polygon)
//                    .build();
//            space_items.add(space_item);
//        }
//        DDRVLNMap.DDRMapSpaceData spaceData=DDRVLNMap.DDRMapSpaceData.newBuilder()
//                .addAllSpaceSet(space_items)
//                .build();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
//                .setBasedata(reqDDRVLNMapEx.getBasedata())
//                .setSpacedata(spaceData)
//                .setTargetPtdata(targetPointData)
//                .addAllTaskSet(taskItemExes)
//                .setPathSet(ddrMapPathDataEx)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDDRVLNMapEx1);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqDDRVLNMapEx.class));
//            ByteString message=reqDDRVLNMapEx1.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 修改当前地图的模式
//     * @param modeType
//     */
//    public void saveDataToServer(int modeType,int abmode){
//        MapFileStatus mapFileStatus=MapFileStatus.getInstance();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx=mapFileStatus.getReqDDRVLNMapEx();
//        DDRVLNMap.DDRMapBaseData baseData=DDRVLNMap.DDRMapBaseData.newBuilder()
//                .setAbNaviTypeValue(modeType)
//                .setName(reqDDRVLNMapEx.getBasedata().getName())
//                .setDescription(reqDDRVLNMapEx.getBasedata().getDescription())
//                .setAffinedata(reqDDRVLNMapEx.getBasedata().getAffinedata())
//                .setColPointData(reqDDRVLNMapEx.getBasedata().getColPointData())
//                .setRecTime(reqDDRVLNMapEx.getBasedata().getRecTime())
//                .setRecUserName(reqDDRVLNMapEx.getBasedata().getRecUserName())
//                .setWaittime(reqDDRVLNMapEx.getBasedata().getWaittime())
//                .setTargetPtName(reqDDRVLNMapEx.getBasedata().getTargetPtName())
//                .setAbPathModeValue(abmode)
//                .setAbPathSpeed(reqDDRVLNMapEx.getBasedata().getAbPathSpeed())
//                .build();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
//                .setBasedata(baseData)
//                .setSpacedata(reqDDRVLNMapEx.getSpacedata())
//                .setTargetPtdata(reqDDRVLNMapEx.getTargetPtdata())
//                .addAllTaskSet(reqDDRVLNMapEx.getTaskSetList())
//                .setPathSet(reqDDRVLNMapEx.getPathSet())
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDDRVLNMapEx1);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqDDRVLNMapEx.class));
//            ByteString message=reqDDRVLNMapEx1.toByteString();
//            postHttpMsg(typename,message);
//        }
//        Logger.e("----modeType:"+modeType+"----name:"+reqDDRVLNMapEx.getBasedata().getName().toStringUtf8());
//    }
//
//    public void saveDataToServer(float speed,String mapname){
//        MapFileStatus mapFileStatus=MapFileStatus.getInstance();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx=mapFileStatus.getReqDDRVLNMapEx();
//        DDRVLNMap.DDRMapBaseData baseData=DDRVLNMap.DDRMapBaseData.newBuilder()
//                .setAbNaviTypeValue(reqDDRVLNMapEx.getBasedata().getAbNaviTypeValue())
//                .setName(ByteString.copyFromUtf8(mapname))
//                .setDescription(reqDDRVLNMapEx.getBasedata().getDescription())
//                .setAffinedata(reqDDRVLNMapEx.getBasedata().getAffinedata())
//                .setColPointData(reqDDRVLNMapEx.getBasedata().getColPointData())
//                .setRecTime(reqDDRVLNMapEx.getBasedata().getRecTime())
//                .setRecUserName(reqDDRVLNMapEx.getBasedata().getRecUserName())
//                .setWaittime(reqDDRVLNMapEx.getBasedata().getWaittime())
//                .setTargetPtName(reqDDRVLNMapEx.getBasedata().getTargetPtName())
//                .setAbPathModeValue(reqDDRVLNMapEx.getBasedata().getAbPathModeValue())
//                .setAbPathSpeed(speed)
//                .build();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
//                .setBasedata(baseData)
//                .setSpacedata(reqDDRVLNMapEx.getSpacedata())
//                .setTargetPtdata(reqDDRVLNMapEx.getTargetPtdata())
//                .addAllTaskSet(reqDDRVLNMapEx.getTaskSetList())
//                .setPathSet(reqDDRVLNMapEx.getPathSet())
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDDRVLNMapEx1);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqDDRVLNMapEx.class));
//            ByteString message=reqDDRVLNMapEx1.toByteString();
//            postHttpMsg(typename,message);
//        }
//        Logger.e("----speed:"+speed+"----name:"+reqDDRVLNMapEx.getBasedata().getName().toStringUtf8());
//    }
//
//    /**
//     * 修改当前地图的模式
//     * @param modeType
//     * @param pointName  待机点设置
//     */
//    public void saveDataToServer(int modeType,String pointName,int abMode,float abSpeed){
//        MapFileStatus mapFileStatus=MapFileStatus.getInstance();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx=mapFileStatus.getReqDDRVLNMapEx();
//        DDRVLNMap.DDRMapBaseData baseData=DDRVLNMap.DDRMapBaseData.newBuilder()
//                .setAbNaviTypeValue(modeType)
//                .setName(reqDDRVLNMapEx.getBasedata().getName())
//                .setDescription(reqDDRVLNMapEx.getBasedata().getDescription())
//                .setAffinedata(reqDDRVLNMapEx.getBasedata().getAffinedata())
//                .setColPointData(reqDDRVLNMapEx.getBasedata().getColPointData())
//                .setRecTime(reqDDRVLNMapEx.getBasedata().getRecTime())
//                .setRecUserName(reqDDRVLNMapEx.getBasedata().getRecUserName())
//                .setWaittime(reqDDRVLNMapEx.getBasedata().getWaittime())
//                .setTargetPtName(ByteString.copyFromUtf8(pointName))
//                .setAbPathModeValue(abMode)
//                .setAbPathSpeed(abSpeed)
//                .build();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
//                .setBasedata(baseData)
//                .setSpacedata(reqDDRVLNMapEx.getSpacedata())
//                .setTargetPtdata(reqDDRVLNMapEx.getTargetPtdata())
//                .addAllTaskSet(reqDDRVLNMapEx.getTaskSetList())
//                .setPathSet(reqDDRVLNMapEx.getPathSet())
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDDRVLNMapEx1);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqDDRVLNMapEx.class));
//            ByteString message=reqDDRVLNMapEx1.toByteString();
//            postHttpMsg(typename,message);
//        }
//        Logger.e("----modeType:"+modeType+"----name:"+reqDDRVLNMapEx.getBasedata().getName().toStringUtf8()+"ab点速度："+abSpeed);
//    }
//
//    /**
//     * 保存空间信息到服务
//     * @param
//     */
//    public void saveSpaceToServer(){
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx=MapFileStatus.getInstance().getReqDDRVLNMapEx();
//        List<DDRVLNMap.space_item> space_items=new ArrayList<>();           //接收到的空间数据
//        List<SpaceItem> spaceItems=MapFileStatus.getInstance().getSpaceItems();
//        for (int i=0;i<spaceItems.size();i++){
//            DDRVLNMap.line line=DDRVLNMap.line.newBuilder()
//                    .addAllPointset(spaceItems.get(i).getLines())
//                    .build();
//            DDRVLNMap.circle circle=DDRVLNMap.circle.newBuilder()
//                    .setCenter(DDRVLNMap.space_pointEx.newBuilder().setX(spaceItems.get(i).getCircle().getX()).setY(spaceItems.get(i).getCircle().getY()).build())
//                    .setRadius(spaceItems.get(i).getCircle().getRadius())
//                    .build();
//            DDRVLNMap.polygon polygon=DDRVLNMap.polygon.newBuilder()
//                    .addAllPointset(spaceItems.get(i).getPolygons())
//                    .build();
//            DDRVLNMap.space_item space_item=DDRVLNMap.space_item.newBuilder()
//                    .setName(ByteString.copyFromUtf8(spaceItems.get(i).getName()))
//                    .setTypeValue(spaceItems.get(i).getType())
//                    .setLinedata(line)
//                    .setCircledata(circle)
//                    .setPolygondata(polygon)
//                    .build();
//            space_items.add(space_item);
//        }
//        DDRVLNMap.DDRMapSpaceData spaceData=DDRVLNMap.DDRMapSpaceData.newBuilder()
//                .addAllSpaceSet(space_items)
//                .build();
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
//                .setBasedata(reqDDRVLNMapEx.getBasedata())
//                .setSpacedata(spaceData)
//                .setTargetPtdata(reqDDRVLNMapEx.getTargetPtdata())
//                .addAllTaskSet(reqDDRVLNMapEx.getTaskSetList())
//                .setPathSet(reqDDRVLNMapEx.getPathSet())
//                .build();
//        sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDDRVLNMapEx1);
//    }
//
//
//    /**
//     * 对地图进行操作
//     */
//    public void reqMapOperational(List<DDRVLNMap.reqMapOperational.OptItem> optItems){
//        DDRVLNMap.reqMapOperational reqMapOperational=DDRVLNMap.reqMapOperational.newBuilder()
//                .addAllOptSet(optItems)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqMapOperational);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqMapOperational.class));
//            ByteString message=reqMapOperational.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 切换地图
//     * @param mapName
//     */
//    public void reqRunControlEx(String mapName){
//        DDRVLNMap.reqRunControlEx reqRunControlEx=DDRVLNMap.reqRunControlEx.newBuilder()
//                .setOnerouteName(ByteString.copyFromUtf8(mapName))
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqRunControlEx);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqRunControlEx.class));
//            ByteString message=reqRunControlEx.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//
//    /**
//     * 关机or重启
//     * @param eCmdIPCMode
//     */
//    public void reqCmdIpcMethod(BaseCmd.eCmdIPCMode eCmdIPCMode ){
//        BaseCmd.reqCmdIPC reqCmdIPC=BaseCmd.reqCmdIPC.newBuilder()
//                .setMode(eCmdIPCMode)
//                .build();
//        sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdIPC);
//    }
//    /**
//     * 机器人暂停/重新运动
//     *
//     * @param value
//     */
//    public void pauseOrResume(String value) {
//        BaseCmd.reqCmdPauseResume reqCmdPauseResume = BaseCmd.reqCmdPauseResume.newBuilder()
//                .setError(value)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqCmdPauseResume);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqCmdPauseResume.class));
//            ByteString message=reqCmdPauseResume.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//        Logger.e("机器人暂停/重新运动");
//    }
//
//    /**
//     * 去充电
//     */
//    public void goToCharge(){
//        BaseCmd.reqCmdStartActionMode reqCmdStartActionMode= BaseCmd.reqCmdStartActionMode.newBuilder()
//                .setMode(BaseCmd.eCmdActionMode.eReCharging)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdStartActionMode);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqCmdStartActionMode.class));
//            ByteString message=reqCmdStartActionMode.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 获取飞鼠的清扫记录
//     */
//    public void getFeishuDBData(int type,boolean isGetImage,int errorType){
//        Logger.e("发送请求清扫记录数据");
//        FeishuCmd.enFeishuDBOperator enFeishuDBOperator;
//        switch (type){
//            case 0:
//                enFeishuDBOperator=FeishuCmd.enFeishuDBOperator.enFeishuDB_Reserve;
//                break;
//            case 1:
//                enFeishuDBOperator=FeishuCmd.enFeishuDBOperator.enFeishuDB_Select;
//                break;
//            case 2:
//                enFeishuDBOperator=FeishuCmd.enFeishuDBOperator.enFeishuDB_Delete;
//                break;
//            case 3:
//                enFeishuDBOperator=FeishuCmd.enFeishuDBOperator.enFeishuDB_Update;
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + type);
//        }
//        FeishuCmd.reqFeishuDBOperator reqFeishuDBOperator=FeishuCmd.reqFeishuDBOperator.newBuilder()
//                .setType(enFeishuDBOperator)
//                .setBGetMapdata(isGetImage)
//                .setTableType(errorType)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqFeishuDBOperator);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf( FeishuCmd.reqFeishuDBOperator.class));
//            ByteString message=reqFeishuDBOperator.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 获取飞鼠的清扫记录
//     */
//    public void getFeishuDBData(int type,boolean isGetImage,int errorType,ByteString sql){
//        Logger.e("发送请求清扫记录数据");
//        FeishuCmd.enFeishuDBOperator enFeishuDBOperator;
//        switch (type){
//            case 0:
//                enFeishuDBOperator=FeishuCmd.enFeishuDBOperator.enFeishuDB_Reserve;
//                break;
//            case 1:
//                enFeishuDBOperator=FeishuCmd.enFeishuDBOperator.enFeishuDB_Select;
//                break;
//            case 2:
//                enFeishuDBOperator=FeishuCmd.enFeishuDBOperator.enFeishuDB_Delete;
//                break;
//            case 3:
//                enFeishuDBOperator=FeishuCmd.enFeishuDBOperator.enFeishuDB_Update;
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + type);
//        }
//        FeishuCmd.reqFeishuDBOperator reqFeishuDBOperator=FeishuCmd.reqFeishuDBOperator.newBuilder()
//                .setType(enFeishuDBOperator)
//                .setBGetMapdata(isGetImage)
//                .setTableType(errorType)
//                .setStrSQL(sql)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqFeishuDBOperator);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf( FeishuCmd.reqFeishuDBOperator.class));
//            ByteString message=reqFeishuDBOperator.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 发送请求示教
//     */
//    public void getFeishuTeach(int type){
//        FeishuCmd.reqFeishuTeaching reqFeishuTeaching=FeishuCmd.reqFeishuTeaching.newBuilder()
//                .setRet(type)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqFeishuTeaching);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuTeaching.class));
//            ByteString message=reqFeishuTeaching.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//
//    /**
//     * 请求跟更改或获取音量
//     * @param num
//     * @param isSuc
//     */
//    public void getFeishuVolum(int num,boolean isSuc){
//        FeishuCmd.reqFeishuVloumeSet reqFeishuVloumeSet=FeishuCmd.reqFeishuVloumeSet.newBuilder()
//                .setVolumevalue(num)
//                .setBsetvolme(isSuc)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqFeishuVloumeSet);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuVloumeSet.class));
//            ByteString message=reqFeishuVloumeSet.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 设置勿扰模式
//     * @param sHour
//     * @param sMin
//     * @param eHour
//     * @param eMin
//     * @param isSuc
//     * @param isGet
//     */
//    public void getFeishuWL(int sHour,int sMin,int eHour,int eMin,boolean isSuc,boolean isGet){
//        FeishuCmd.reqFeishuNotDisturb reqFeishuNotDisturb=FeishuCmd.reqFeishuNotDisturb.newBuilder()
//                .setStartHour(sHour)
//                .setStartMin(sMin)
//                .setEndHour(eHour)
//                .setEndMin(eMin)
//                .setBEnable(isSuc)
//                .setBGetData(isGet)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqFeishuNotDisturb);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuNotDisturb.class));
//            ByteString message=reqFeishuNotDisturb.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 退出采集模式
//     */
//
//    public void quitCollect() {
//        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
//                .setError("noError")
//                .setCancelRec(true)
//                .build();
//
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqCmdEndActionMode);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqCmdEndActionMode.class));
//            ByteString message=reqCmdEndActionMode.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//
//    /**
//     * 采集时添加点
//     */
//    public void addPoint(){
//        BaseCmd.reqAddPathPointWhileCollecting reqAddPathPointWhileCollecting = BaseCmd.reqAddPathPointWhileCollecting.newBuilder().build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqAddPathPointWhileCollecting);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqAddPathPointWhileCollecting.class));
//            ByteString message=reqAddPathPointWhileCollecting.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    public void reqCmdRelocation1() {
//        BaseCmd.reqCmdReloc reqCmdRelocation = BaseCmd.reqCmdReloc.newBuilder()
//                .setTypeValue(0)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqCmdRelocation);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqCmdReloc.class));
//            ByteString message=reqCmdRelocation.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 修改行走速度
//     */
//    public void getNowSpeed(int speed){
//        BaseCmd.reqCmdChangeSpeed reqCmdChangeSpeed=BaseCmd.reqCmdChangeSpeed.newBuilder()
//                .setAddSpeed(speed)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdChangeSpeed);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqCmdChangeSpeed.class));
//            ByteString message=reqCmdChangeSpeed.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 设置速度
//     * @param value
//     */
//
//    public void getNowSpeed(float value){
//        BaseCmd.reqCmdAutoAdjSp reqCmdAutoAdjSp =BaseCmd.reqCmdAutoAdjSp.newBuilder()
//                .setChgType(0)
//                .setSegMode(2)
//                .setFVal(value)
//                .build();
//        if (GlobalParameter.isLan){
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdAutoAdjSp);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqCmdAutoAdjSp.class));
//            ByteString message=reqCmdAutoAdjSp.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//
//
//    /**
//     *  获取传感器参数
//     */
//    public void getSensorParam(){
//        BaseCmd.eSensorConfigItemOptType eSensorConfigItemOptType;
//        eSensorConfigItemOptType=BaseCmd.eSensorConfigItemOptType.eSensorConfigOptTypeGetData;
//        BaseCmd.reqSensorConfigOperational reqSensorConfigOperational = BaseCmd.reqSensorConfigOperational.newBuilder()
//                .setType(eSensorConfigItemOptType)
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqSensorConfigOperational);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqSensorConfigOperational.class));
//            ByteString message=reqSensorConfigOperational.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    //获取导航参数
//    public void getNaparmeter(){
//        BaseCmd.eConfigItemOptType eConfigItemOptType;
//        eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeGetData;//获取数据
//        BaseCmd.reqConfigOperational reqConfigOperational = BaseCmd.reqConfigOperational.newBuilder()
//                .setType(eConfigItemOptType)
//                .build();
//        try {
//            if (GlobalParameter.isLan){
//                tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqConfigOperational);
//            }else {
//                String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqConfigOperational.class));
//                ByteString message=reqConfigOperational.toByteString();
//                postHttpMsg(typename,message);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 提交采集
//     * @param name
//     */
//    public void postCollect(String name){
//        BaseCmd.reqCmdStartActionMode reqCmdStartActionMode = BaseCmd.reqCmdStartActionMode.newBuilder()
//                .setMode(BaseCmd.eCmdActionMode.eRec)
//                .setRouteName(ByteString.copyFromUtf8(name))
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqCmdStartActionMode);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqCmdStartActionMode.class));
//            ByteString message=reqCmdStartActionMode.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//    /**
//     * 发送请求获取耗材信息
//     */
//    public void getConsumable(boolean isConsu){
//        FeishuCmd.reqFeishuSuppliesInformation reqFeishuSuppliesInformation=FeishuCmd.reqFeishuSuppliesInformation.newBuilder()
//                .setBGetData(isConsu)
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqFeishuSuppliesInformation);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuSuppliesInformation.class));
//            ByteString message=reqFeishuSuppliesInformation.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 发送请求修改耗材信息
//     */
//    public void updataConsumable(boolean isConsu, List<Consumables> consumables){
//        List<FeishuCmd.SuppliesInformationItem> suppliesInformationItemList=new ArrayList<>();
//        for (int i=0;i<consumables.size();i++){
//            FeishuCmd.SuppliesInformationItem suppliesInformationItem =FeishuCmd.SuppliesInformationItem.newBuilder()
//                    .setType(consumables.get(i).getType())
//                    .setUseLife(Long.parseLong(consumables.get(i).getLife())*3600)
//                    .setTotalLife(Long.parseLong(consumables.get(i).getTime())*3600)
//                    .build();
//            suppliesInformationItemList.add(suppliesInformationItem);
//        }
//        FeishuCmd.suppliesInformation suppliesInformation=FeishuCmd.suppliesInformation.newBuilder()
//                .addAllInfo(suppliesInformationItemList)
//                .build();
//        FeishuCmd.reqFeishuSuppliesInformation reqFeishuSuppliesInformation=FeishuCmd.reqFeishuSuppliesInformation.newBuilder()
//                .setBGetData(isConsu)
//                .setInfo(suppliesInformation)
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqFeishuSuppliesInformation);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuSuppliesInformation.class));
//            ByteString message=reqFeishuSuppliesInformation.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 发送重定位
//     * @param x
//     * @param y
//     * @param rotation
//     */
//    public void reqCmdReloc(float x,float y,float rotation){
//        BaseCmd.reqCmdReloc reqCmdReloc=BaseCmd.reqCmdReloc.newBuilder()
//                .setTypeValue(2)
//                .setPosX0(x)
//                .setPosY0(y)
//                .setPosTh0(rotation)
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdReloc);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(BaseCmd.reqCmdReloc.class));
//            ByteString message=reqCmdReloc.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 请求补扫
//     */
//    public void reqMendClean(int type){
//        FeishuCmd.reqUserReqMendClean reqUserReqMendClean=FeishuCmd.reqUserReqMendClean.newBuilder()
//                .setData(type)
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqUserReqMendClean);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqUserReqMendClean.class));
//            ByteString message=reqUserReqMendClean.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//    /**
//     * 请求获取上位机地图
//     */
//    public void reqGetAutoActualPath(ByteString nresver){
//        FeishuCmd.reqGetAutoActualPath reqGetAutoActualPath=FeishuCmd.reqGetAutoActualPath.newBuilder()
//                .setNresver(nresver)
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqGetAutoActualPath);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqGetAutoActualPath.class));
//            ByteString message=reqGetAutoActualPath.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 请求自动分区
//     * @param mapname
//     * @param type
//     */
//
//    public void reqFeishuAutomaticRoomSegmentation(ByteString mapname,int type){
//        FeishuCmd.reqFeishuAutomaticRoomSegmentation reqFeishuAutomaticRoomSegmentation=FeishuCmd.reqFeishuAutomaticRoomSegmentation.newBuilder()
//                .setStrMapName(mapname)
//                .setType(type)
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqFeishuAutomaticRoomSegmentation);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuAutomaticRoomSegmentation.class));
//            ByteString message=reqFeishuAutomaticRoomSegmentation.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 发送普通http请求
//     */
//    private String jsondata;
//    private void okhttpData(RequestBody requestBody,String url){
//        Logger.e("--ok-");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Logger.e("--开始-");
//                OkHttpClient client=new OkHttpClient();
//                //Form表单格式的参数传递
//                Request request = new Request
//                        .Builder()
//                        .post(requestBody)//Post请求的参数传递
//                        .url(url)
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Logger.e("--请求失败--"+e.getMessage()+"---b"+url);
////                        okhttpData(requestBody,url);
//                        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.postFailure));
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        //此方法运行在子线程中，不能在此方法中进行UI操作。
//
//                        jsondata = response.body().string();
//                        Logger.e("--返回信息基础-"+jsondata);
//                        try {
//                            JSONObject jsonObject = new JSONObject(jsondata);
////                            String msg="Cu+\\/vQgKC09uZVJvdXRlXzI1EiBBdXRvbWF0aWNhbGx5IGdlbmVyYXRlZCBtYXAgaGVyZRoUFSQ577+977+9He+\\/ve+\\/vTRDJTJm77+977+9NSVm77+9QyLvv70IChUIARDvv73vv73Yhy4yCENvcm5lcl8xOAIKHwgDEO+\\/ve+\\/vdiHLh3vv70RZDolH++\\/ve+\\/vTkyCEF1dG9QdF8xOAEKHwgDENOy77+92IcuHSJhGUAlfQfvv73vv70yCEF1dG9QdF8yOAEKJAgBEM+K77+92IcuHe+\\/ve+\\/vRtAJQbvv71gPi1Z3b4\\/MghDb3JuZXJfMjgCCh8IAxDpubTYhy4d77+9bixAJR7vv70xQDIIQXV0b1B0XzM4AQokCAEQ77+977+977+92IcuHT\\/vv73vv70\\/JcOeOkAtaO+\\/vT5AMghDb3JuZXJfMzgCCh8IAxDuobXYhy4dAe+\\/ve+\\/vT8l77+9LDxAMghBdXRvUHRfNDgBCiQIARDvv73vv73vv73Yhy4d77+9Pm4+Je+\\/vTBALe+\\/vUU777+9MghDb3JuZXJfNDgCCiQIARDvv73JtdiHLh3vv73DviXvv719KUAtYi0877+9MghDb3JuZXJfNTgCCh8IAxDvv73JtdiHLh1zQh\\/vv70lI0ksQDIIQXV0b1B0XzU4AQofCAMQ77+977+977+92IcuHSYBCEAlHxFFQDIIQXV0b1B0XzY4AQofCAMQ77+977+977+92IcuHQfvv71tQCUGCV1AMghBdXRvUHRfNzgBCiQIARDvv73Tt9iHLh3vv71w77+9QCXvv71sLEAt77+9YO+\\/ve+\\/vTIIQ29ybmVyXzY4AgofCAMQ77+937fYhy4dLlrvv71AJe+\\/ve+\\/vQNAMghBdXRvUHRfODgBCiQIARDvv73vv73vv73Yhy4d77+9Ge+\\/vUAl77+9ZhBALWHvv70HPzIIQ29ybmVyXzc4AgofCAMQ77+977+977+92IcuHTXcvkAlFO+\\/vVhAMghBdXRvUHRfOTgBCiQIARDvv73vv73vv73Yhy4d77+9E++\\/vUAl77+977+9TkAt77+9BFc\\/MghDb3JuZXJfODgCCiQIARDvv73vv73vv73Yhy4d77+9Iu+\\/vUAl77+977+9RUAtCAMH77+9MghDb3JuZXJfOTgCCiUIARDur7nYhy4dW++\\/ve+\\/vUAlCu+\\/vQRALVxaCe+\\/vTIJQ29ybmVyXzEwOAIKIAgDEO6vudiHLh3vv71i77+9QCVO77+977+9PzIJQXV0b1B0XzEwOAEKJQgBEO+\\/ve+\\/vdiHLh3vv73vv73vv71AJR9qIz8tCe+\\/vcO\\/MglDb3JuZXJfMTE4AgogCAMQ77+977+92IcuHUp877+9QCV6fu+\\/vT4yCUF1dG9QdF8xMTgBCiAIAxDvv73NutiHLh3vv73vv73vv71AJe+\\/vX0DQDIJQXV0b1B0XzEyOAEKJQgBEO+\\/ve+\\/ve+\\/vdiHLh1Zbu+\\/vUAlBg0HQC3vv73vv71CQDIJQ29ybmVyXzEyOAIKIAgDEO+\\/ve+\\/ve+\\/vdiHLh3vv73vv70rQCXvv70mG0AyCUF1dG9QdF8xMzgBCiUIARDvv73cu9iHLh3vv71pH0Al77+9egBALe+\\/vVXvv73vv70yCUNvcm5lcl8xMzgCCiAIAxDvv73vv73vv73Yhy4d77+977+977+9PyVrHRQ+MglBdXRvUHRfMTQ4AQolCAEQ77+92bzYhy4d77+9Bk0\\/JShJFTwtIe+\\/vUPvv70yCUNvcm5lcl8xNDgCCiAIAxDvv73vv73vv73Yhy4d77+9Sk3vv70l77+9CnI8MglBdXRvUHRfMTU4AQolCAEQ77+977+92IcuHe+\\/ve+\\/vUE7Je+\\/vdqu77+9LR3vv70kPTIJQ29ybmVyXzE1OAIo77+937\\/Yhy4yBWFkbWluSAISABodChsKDemNkua\\/hu6dkOmQkD8SCg3vv70RZDoVH++\\/ve+\\/vTkiSwoRRERSVGFza19hdXRvLnRhc2sSFggBEhLlr6Tlk4TmtZjnkrruiJrnt54SEQgCEg3pjZLmv4bunZDpkJA\\/GggIARABGBcgOyDvv70HIiIKD0REUlRhc2tfSGgudGFzaxIGCAESAkhoGgAg77+9BygBMAIq77+9Agrvv70CChLlr6Tlk4TmtZjnkrruiJrnt54SDgoKDe+\\/vRFkOhUf77+977+9ORAIEg4KCg0iYRlAFX0H77+977+9EAgSDgoKDe+\\/vW4sQBUe77+9MUAQCBIOCgoNAe+\\/ve+\\/vT8V77+9LDxAEAgSDgoKDXNCH++\\/vRUjSSxAEAgSDgoKDSYBCEAVHxFFQBAIEg4KCg0H77+9bUAVBgldQBAIEg4KCg0uWu+\\/vUAV77+977+9A0AQCBIOCgoNNdy+QBUU77+9WEAQCBIOCgoN77+9Yu+\\/vUAVTu+\\/ve+\\/vT8QCBIOCgoNSnzvv71AFXp+77+9PhAIEg4KCg3vv73vv73vv71AFe+\\/vX0DQBAIEg4KCg3vv73vv70rQBXvv70mG0AQCBIOCgoN77+977+977+9PxVrHRQ+EAgSEwoKDe+\\/vUpN77+9Fe+\\/vQpyPBAHHQAA77+9Px3vv73vv73vv70+IEAqADAB";
////                            byte[] bytes=Base64.decode(msg.getBytes(),Base64.NO_WRAP | Base64.DEFAULT);
////                            Logger.e("返回信息基础解析后"+bytes.toString()+"信息"+new String(bytes,"US-ASCII"));
////                            String type = jsonObject.getString("head");
////                            String msg = jsonObject.getString("msg");
////                            msg = new String(Base64.decode(msg.getBytes(),Base64.DEFAULT));
////                            m_MessageRoute.processReceive1(type,msg);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                        //解析
//                        response.body().close();
//                    }
//                });
//            }
//        }).start();
//    }
//
//    /**
//     * 发送http心跳请求并解析回复
//     */
//    private void okhttpDataHead(RequestBody requestBody,String url){
////        Logger.e("--ok-");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpClient client=new OkHttpClient();
//                //Form表单格式的参数传递
//                Request request = new Request
//                        .Builder()
//                        .post(requestBody)//Post请求的参数传递
//                        .url(url)
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Logger.e("返回信息基础--心跳请求失败--"+e.getMessage());
//                        if (timer!=null){
//                            Logger.e("返回信息基础关闭定时任务");
//                            timer.cancel();
//                            timer=null;
//                        }
//                        if (task!=null){
//                            task.cancel();
//                            Logger.e("返回信息基础关闭-----定时任务");
//                            task=null;
//                        }
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        //此方法运行在子线程中，不能在此方法中进行UI操作。
//                        String jsondata = response.body().string();
////                        Logger.e("--返回信息心跳-"+jsondata);
//                        try {
//                            if (jsondata.contains("html")){
//                                Logger.e("返回信息基础服务器返回出错");
//                            }else {
//                                JSONObject jsonObject = new JSONObject(jsondata);
//                                JSONArray userArray =jsonObject.getJSONArray("user_message");
//                                if (userArray.length()<=0){
////                                Toast.makeText(context,"请求无返回信息",Toast.LENGTH_SHORT);
//                                    Logger.e("返回信息基础心跳请求返回为空");
//                                }else {
//                                    for (int i=0;i<userArray.length();i++){
//                                        String msg = userArray.getJSONObject(i).getString("msg");
//                                        String type = (userArray.getJSONObject(i).getString("head"));
//                                        byte[] bytes=Base64.decode(msg.getBytes(),Base64.NO_WRAP);
//                                        //msg = new String();
//                                    Logger.e("type----"+type+"---msg"+bytes);
//                                        m_MessageRoute.processReceive1(type,bytes);
//                                    }
//                                }
//                            }
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        //解析
//                        response.body().close();
//                    }
//                });
//            }
//        }).start();
//    }
//
//
//    private boolean isWorking=true;
//    private boolean isReceive;
//    public Thread headThread;
//    public void initThread(){
//        Logger.e("请求机器ID"+GlobalParameter.getRobotID());
//        if (headThread==null){
//            headThread=new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (isWorking){
//                        try {
//                            RequestBody requestBody = new MultipartBody.Builder()
//                                    .setType(MultipartBody.FORM)
//                                    .addFormDataPart("user",GlobalParameter.getAccount())
//                                    .addFormDataPart("token",GlobalParameter.getToken())
//                                    .addFormDataPart("robot_id",GlobalParameter.getRobotID())
//                                    .build();
//                            String uri="http://47.115.19.32:8089/user/user_msgs/user_HB";
////                            String uri="http://192.168.1.52:8089/user/user_msgs/user_HB";
//                            Logger.e("返回心跳信息基础发送token"+GlobalParameter.getToken());
////                    okhttpDataHead(requestBody,uri);
//                            OkHttpClient client=new OkHttpClient.Builder()
//                                    .readTimeout(10, TimeUnit.SECONDS)
//                                    .build();
//
//                            //Form表单格式的参数传递
//                            Request request = new Request
//                                    .Builder()
//                                    .post(requestBody)//Post请求的参数传递
//                                    .url(uri)
//                                    .build();
//                            isReceive=false;
//                            Logger.e("--------请求------："+requestBody.contentLength());
//                            Call call=client.newCall(request);
//                            Response response=call.execute();
//                            //此方法运行在子线程中，不能在此方法中进行UI操作。
//                            String jsondata = response.body().string();
////                             Logger.e("--返回信息心跳-"+jsondata);
//                            try {
//                                if (jsondata.contains("html")){
//                                    Logger.e("返回信息基础服务器返回出错");
//                                }else {
//                                    JSONObject jsonObject = new JSONObject(jsondata);
//                                    JSONArray userArray =jsonObject.getJSONArray("user_message");
//                                    if (userArray.length()<=0){
////                                Toast.makeText(context,"请求无返回信息",Toast.LENGTH_SHORT);
//                                        Logger.e("返回信息基础心跳请求返回为空");
//                                    }else {
//                                        for (int i=0;i<userArray.length();i++){
//                                            String msg = userArray.getJSONObject(i).getString("msg");
//                                            String type = (userArray.getJSONObject(i).getString("head"));
//                                            byte[] bytes=Base64.decode(msg.getBytes(),Base64.NO_WRAP);
//                                            //msg = new String();
//                                            Logger.e("type----"+type+"---msg"+bytes);
//                                            m_MessageRoute.processReceive1(type,bytes);
//                                        }
//                                    }
//                                }
//                            }
//                            catch (Exception e){
//                                if (e.getClass().equals(SocketTimeoutException.class)){
//                                    Logger.e("连接超时");
//                                }else {
//                                    Logger.e("心跳请求失败");
//                                    Logger.e("异常"+e.getMessage());
//                                    e.printStackTrace();
//                                }
//                            }
//                            //解析
//                            response.body().close();
////                        client.newCall(request).enqueue(new Callback() {
////                            @Override
////                            public void onFailure(Call call, IOException e) {
////                                //isWorking=false;
////                                Logger.e("返回信息基础--心跳请求失败--"+isWorking+e.getMessage());
////                              return;
////                            }
////
////                            @Override
////                            public void onResponse(Call call, Response response) throws IOException {
////                                //此方法运行在子线程中，不能在此方法中进行UI操作。
////                                String jsondata = response.body().string();
//////                        Logger.e("--返回信息心跳-"+jsondata);
////                                try {
////                                    if (jsondata.contains("html")){
////                                        Logger.e("返回信息基础服务器返回出错");
////                                    }else {
////                                        JSONObject jsonObject = new JSONObject(jsondata);
////                                        JSONArray userArray =jsonObject.getJSONArray("user_message");
////                                        if (userArray.length()<=0){
//////                                Toast.makeText(context,"请求无返回信息",Toast.LENGTH_SHORT);
////                                            Logger.e("返回信息基础心跳请求返回为空");
////                                        }else {
////                                            for (int i=0;i<userArray.length();i++){
////                                                String msg = userArray.getJSONObject(i).getString("msg");
////                                                String type = (userArray.getJSONObject(i).getString("head"));
////                                                byte[] bytes=Base64.decode(msg.getBytes(),Base64.NO_WRAP);
////                                                //msg = new String();
////                                                Logger.e("type----"+type+"---msg"+bytes);
////                                                m_MessageRoute.processReceive1(type,bytes);
////                                            }
////                                        }
////                                    }
////                                }catch (Exception e){
////                                    e.printStackTrace();
////                                }
////                                //解析
////                                response.body().close();
////                            }
////                        });
//                        }
//                        catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        try {
//                            Thread.sleep(500);
//                            Logger.e("------休眠---");
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                }
//            });
//            headThread.start();
//        }
//    }
//    /**
//     * 定时器，每2000毫秒执行一次
//     */
//    Timer timer;
//    TimerTask task;
////    public void initTimer() {
////        timer = new Timer();
////        task = new TimerTask() {
////            @Override
////            public void run() {
////                try {
////                    RequestBody requestBody = new MultipartBody.Builder()
////                            .setType(MultipartBody.FORM)
////                            .addFormDataPart("user",GlobalParameter.getAccount())
////                            .addFormDataPart("token",GlobalParameter.getToken())
////                            .addFormDataPart("robot_id",GlobalParameter.getRobotID())
////                            .build();
////                    String uri="http://47.115.19.32:8089/user/user_msgs/user_HB";
//////                    okhttpDataHead(requestBody,uri);
////                    OkHttpClient client=new OkHttpClient();
////                    //Form表单格式的参数传递
////                    Request request = new Request
////                            .Builder()
////                            .post(requestBody)//Post请求的参数传递
////                            .url(uri)
////                            .build();
////                    Logger.e("--------请求------："+requestBody.contentLength());
////                    client.newCall(request).enqueue(new Callback() {
////                        @Override
////                        public void onFailure(Call call, IOException e) {
////                            Logger.e("返回信息基础--心跳请求失败--"+e.getMessage());
////                            if (timer!=null){
////                                Logger.e("返回信息基础关闭定时任务");
////                                timer.cancel();
////                                timer=null;
////                            }
////                            if (task!=null){
////                                task.cancel();
////                                Logger.e("返回信息基础关闭-----定时任务");
////                                task=null;
////                            }
////                        }
////
////                        @Override
////                        public void onResponse(Call call, Response response) throws IOException {
////                            //此方法运行在子线程中，不能在此方法中进行UI操作。
////                            String jsondata = response.body().string();
//////                        Logger.e("--返回信息心跳-"+jsondata);
////                            try {
////                                if (jsondata.contains("html")){
////                                    Logger.e("返回信息基础服务器返回出错");
////                                }else {
////                                    JSONObject jsonObject = new JSONObject(jsondata);
////                                    JSONArray userArray =jsonObject.getJSONArray("user_message");
////                                    if (userArray.length()<=0){
//////                                Toast.makeText(context,"请求无返回信息",Toast.LENGTH_SHORT);
////                                        Logger.e("返回信息基础心跳请求返回为空");
////                                    }else {
////                                        for (int i=0;i<userArray.length();i++){
////                                            String msg = userArray.getJSONObject(i).getString("msg");
////                                            String type = (userArray.getJSONObject(i).getString("head"));
////                                            byte[] bytes=Base64.decode(msg.getBytes(),Base64.NO_WRAP);
////                                            //msg = new String();
////                                            Logger.e("type----"+type+"---msg"+bytes);
////                                            m_MessageRoute.processReceive1(type,bytes);
////                                        }
////                                    }
////                                }
////                            }catch (Exception e){
////                                e.printStackTrace();
////                            }
////                            //解析
////                            response.body().close();
////                        }
////                    });
////                }catch (Exception e){
////                    e.printStackTrace();
////                }
////            }
////        };
////        timer.schedule(task, 0, 1000);
////    }
//
//    /**
//     * 发送飞鼠数据
//     */
//    public void getFeishuOpera(int type,boolean isopen){
//        FeishuCmd.enFeishuOperatorType enFeishuOperatorType;
//        switch (type) {
//            case 1:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorParking;//驻车
//                break;
//            case 2:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorSideBrush;//边刷
//                break;
//            case 3:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorWaterAbsorption;//吸水
//                break;
//            case 4:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorTest;//调试
//                break;
//            case 5:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorWashTheFloor;//洗地
//                break;
//            case 6:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorPushDust;//尘推
//                break;
//            case 7:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorWaterPurificationConversion;//净水转换
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + type);
//        }
//        FeishuCmd.reqFeishuOperator reqFeishuOperator = FeishuCmd.reqFeishuOperator.newBuilder()
//                .setType(enFeishuOperatorType)
//                .setBOpen(isopen)
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqFeishuOperator);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuOperator.class));
//            ByteString message=reqFeishuOperator.toByteString();
////            Logger.e("typename"+m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuOperator.class)));
////            Logger.e("发送msg---------" +reqFeishuOperator.toByteArray()+"64转化过后msg-----"
////                    +Base64.encodeToString(reqFeishuOperator.toByteArray(),Base64.DEFAULT));
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 发送飞鼠数据
//     */
//    public void getFeishuXL(int type,int level){
//        FeishuCmd.enFeishuOperatorType enFeishuOperatorType;
//        switch (type) {
//            case 8:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorCleanIntensity;//清洁强度
//                break;
//            case 9:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorWaterYield;//水量
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + type);
//        }
//        FeishuCmd.reqFeishuOperator reqFeishuOperator = FeishuCmd.reqFeishuOperator.newBuilder()
//                .setType(enFeishuOperatorType)
//                .setLevel(level)
//                .build();
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqFeishuOperator);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuOperator.class));
//            ByteString message=reqFeishuOperator.toByteString();
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * 发送洗地或者尘推
//     * @param cleanMode
//     * @param isGet
//     */
//    public void getFeishuSystem(int cleanMode,boolean isGet){
//        FeishuCmd.enCleaningMode enCleaningMode;
//        switch (cleanMode){
//            case 1://洗地
//                enCleaningMode=FeishuCmd.enCleaningMode.enCleaningModeWashTheFloor;
//                break;
//            case 2://尘推
//                enCleaningMode=FeishuCmd.enCleaningMode.enCleaningModePushDust;
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + cleanMode);
//        }
//        FeishuCmd.reqFeishuSystemSet reqFeishuSystemSet=FeishuCmd.reqFeishuSystemSet.newBuilder()
//                .setCleanMode(enCleaningMode)
//                .setBGetData(isGet)
//                .build();
//
//        if (GlobalParameter.isLan){
//            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqFeishuSystemSet);
//        }else {
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(FeishuCmd.reqFeishuSystemSet.class));
//            ByteString message=reqFeishuSystemSet.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//
//    public void postHttpMsg(String typename, ByteString message){
//        Logger.e("返回信息基础user---"+GlobalParameter.getAccount()+"robot_id----"+GlobalParameter.getRobotID()+"head"+typename);
//        try {
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("user",GlobalParameter.getAccount())
//                    .addFormDataPart("token",GlobalParameter.getToken())
//                    .addFormDataPart("head", typename)
//                    .addFormDataPart("robot_id",GlobalParameter.getRobotID())
//                    .addFormDataPart("msg", Base64.encodeToString(message.toByteArray(),Base64.NO_WRAP | Base64.DEFAULT))
//                    .build();
//            Logger.e("返回信息基础发送"+requestBody.toString()+"mag"+message.toStringUtf8()+"token"+GlobalParameter.getToken());
//            String uri="http://47.115.19.32:8089/user/user_msgs/post_protobuf_by_http";
////                String uri="http://192.168.1.52:8089/user/user_msgs/post_protobuf_by_http";
//            okhttpData(requestBody,uri);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * errorMessage
//     * @param errorMessage
//     * @return
//     */
//    private DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");   //用于格式化日期，作为日志文件名的一部分
//    private String saveCrashInfo2File(String errorMessage){
//        File dir=new File(GlobalParameter.ROBOT_FOLDER_LOG);
//        if (dir.exists()){
//            //  Logger.e("文件夹已存在，无须创建");
//        }else {
//            Logger.e("创建文件");
//            dir.mkdirs();
//        }
//        StringBuffer sb=new StringBuffer();
//        sb.append(errorMessage);
//        //存到文件
//        String time=dateFormat.format(new Date());
//        String fileName = "crash-" + time + ".txt";
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            try {
//                Logger.e("建立log文件");
//                File path = new File(GlobalParameter.ROBOT_FOLDER_LOG);
//                FileOutputStream fos = new FileOutputStream(path +"/"+ fileName);
//                fos.write(sb.toString().getBytes());
//                fos.close();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//        }
//        return fileName;
//    }
//
//}


//    public class Base64Utils {
//        // 编码
//        public static String getBase64(String str) {
//            String result = "";
//            if( str != null) {
//                try {
//                    result = new String(Base64.encode(str.getBytes("utf-8"), Base64.NO_WRAP),"utf-8");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            return result;
//        }
//
//        // 解码
//        public static String getFromBase64(String str) {
//            String result = "";
//            if (str != null) {
//                try {
//                    result = new String(Base64.decode(str, Base64.NO_WRAP), "utf-8");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            return result;
//        }
//    }
}

