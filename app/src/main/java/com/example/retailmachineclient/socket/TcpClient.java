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
 * ??????OkSocket??????TCP?????????
 * create 2019/10/16
 */
public class TcpClient extends BaseSocketConnection {
    public Context context;
    public static TcpClient tcpClient;
    private ConnectionInfo info;
    public IConnectionManager manager;
    private boolean isConnected; //????????????
    private SocketCallBack socketCallBack;
    private byte[] heads = new byte[4];  //???????????????????????????????????????
    private byte[] bodyLenths = new byte[4];        //??????body??????????????????
    private XToast xToast;

    /**
     * ???????????????
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
        this.context = context.getApplicationContext();         //??????Application???context ????????????????????????
        m_MessageRoute = new MessageRoute(context, this, baseMessageDispatcher);
    }

    /**
     * ??????????????????
     *
     * @param ip
     * @param port
     */
    public synchronized void createConnect(String ip, int port) {
        Logger.e("??????tcp:" + ip + ";" + port);
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
     * ?????????????????????
     */
    public class SocketCallBack extends SocketActionAdapter {
//        private BaseDialog waitDialog;

        public SocketCallBack() {
            super();
        }

        /**
         * ?????????????????????????????????????????????
         *
         * @param info
         * @param action
         */
        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            isConnected = true;
            Logger.e("--------????????????---------");
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
         * ?????????????????????????????????
         *
         * @param info
         * @param action
         * @param e
         */
        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            Logger.e("--------??????tcp ??????:" + e.toString());
            isConnected = false;
        }

        /**
         * ????????????????????????????????????
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
                    Logger.e("??????activity" + ActivityStackManager.getInstance().getTopActivity().getLocalClassName());
//                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.TcpAndUdpFailed));
                    disConnect();
                } else {
                    if (xToast != null) {
                        xToast.cancel();
                    }
                    Logger.e("?????????????????????????????????" + activity.getLocalClassName());
//                    LogcatHelper.getInstance(context).stop();//??????????????????????????????
//                    showXToast(activity);
                }
            }
        }

        /**
         * ?????????tcp?????????????????????????????????
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
     * ??????????????????
     */
    public class ReaderProtocol implements IReaderProtocol {

        /**
         * ???????????????????????????
         *
         * @return
         */
        @Override
        public int getHeaderLength() {
            return 12;
        }

        /**
         * ?????????????????????body?????????
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
     * ??????????????????
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
////                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //???????????????????????????activity????????????
////                                toast.startActivity(intent);
//                            }
//                        })
//                        .show();
//
////                Intent intent=new Intent(activity,LoginActivity.class);
////                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //???????????????????????????activity????????????
////                xToast.startActivity(intent);
////                disConnect();
////                xToast.cancel();
//            }
//        });
//    }


    /**
     * ??????????????????byte[]??????int
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
     * ?????????????????????????????????????????????????????????,????????????????????????,????????????????????????????????????.
     */
    public void feedDog() {
        if (manager != null) {
            manager.getPulseManager().feed();
            Logger.d("---??????");
        }
    }

    /**
     * ????????????
     */
    public void disConnect() {
        if (manager != null) {
            manager.unRegisterReceiver(socketCallBack);
            manager.disconnect();
            isConnected = false;
            manager = null;
            Logger.e("??????tcp??????");
        }
    }


//    /**
//     * ????????????
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
//     * ??????????????????
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
//                        Logger.d("???????????????");
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
//     * ??????????????????????????????
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
//            Logger.e("?????????"+reqGetDDRVLNMapEx.toByteString());
//            postHttpMsg(typename,message);
//        }
//        Logger.e("??????????????????");
//    }
//
//
//    /**
//     * ?????????????????????????????????????????????????????????????????????????????????
//     */
//    public void getAllLidarMap(){
//        Logger.e("????????????????????????");
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
//     * ???????????????txt???png) ??????????????????
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
//        Logger.e("???????????????....");
//    }
//
//
////    /**
////     * ???????????????????????????
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
//     * ???????????????????????????
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
//     * ??????????????????
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
//     * ????????????????????????????????????
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
//     * ????????????????????????(????????????????????????????????????)
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
//     * ????????????????????????????????????????????????????????????????????????
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
//     * ????????????????????????????????????bkPic_obs.png????????????
//     * type=6 ->???????????????????????????????????????????????? reqEditorLidarMap ?????? vlSet???
//     * type=7 -> ???????????????????????????????????????????????? reqEditorLidarMap ?????? vlSet???
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
//     * ???????????????????????????
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
//     * ??????????????????????????????????????????
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
//     * ????????????????????????????????????
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
//        Logger.e("----------??????size:"+pathLineItemExes.size());
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
//     * ????????????????????????????????????
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
//            Logger.e("????????????"+taskMode.getWeekList().size());
//            for (int k=0;k<taskMode.getWeekList().size();k++){
//                int v=taskMode.getWeekList().get(k);
//                integerList.add(v);
//                Logger.e("??????"+taskMode.getWeekList().get(k));
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
//        Logger.e("???????????????????????????size:"+taskItemExes.size());
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx1=DDRVLNMap.reqDDRVLNMapEx.newBuilder()
//                .setBasedata(reqDDRVLNMapEx.getBasedata())
//                .setSpacedata(reqDDRVLNMapEx.getSpacedata())
//                .setTargetPtdata(reqDDRVLNMapEx.getTargetPtdata())
//                .addAllTaskSet(taskItemExes)
//                .setPathSet(reqDDRVLNMapEx.getPathSet())
//                .build();
//        if (GlobalParameter.isLan){
//            Logger.e("?????????---");
//            sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqDDRVLNMapEx1);
//        }else {
//            Logger.e("?????????---");
//            String typename=m_MessageRoute.javaClass2ProtoTypeName(String.valueOf(DDRVLNMap.reqDDRVLNMapEx.class));
//            ByteString message=reqDDRVLNMapEx1.toByteString();
//            postHttpMsg(typename,message);
//        }
//
//    }
//
//
//    /**
//     * ??????????????????????????????
//     */
//    public void saveDataToServer(DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx,List<TargetPoint> targetPoints,List<PathLine> pathLines,List<TaskMode> taskModes){
//        /*****************************?????????????????????????????????********************************************/
//        List<DDRVLNMap.targetPtItem> targetPtItems=new ArrayList<>();
//        for (int i=0;i<targetPoints.size();i++){
//            TargetPoint targetPoint=targetPoints.get(i);
//            DDRVLNMap.space_pointEx space_pointEx=DDRVLNMap.space_pointEx.newBuilder()
//                    .setX(targetPoint.getX())
//                    .setY(targetPoint.getY())
//                    .setTheta(targetPoint.getTheta())
//                    .build();
//            Logger.e("?????????????????????"+targetPoint.getName());
//            DDRVLNMap.targetPtItem targetPtItem=DDRVLNMap.targetPtItem.newBuilder()
//                    .setPtName(ByteString.copyFromUtf8(targetPoint.getName()))
//                    .setTargetPtTypeValue(targetPoint.getPointType().getTypeValue())
//                    .setPtData(space_pointEx).build();
//            targetPtItems.add(targetPtItem);
//        }
//        DDRVLNMap.DDRMapTargetPointData targetPointData=DDRVLNMap.DDRMapTargetPointData.newBuilder()
//                .addAllTargetPt(targetPtItems)
//                .build();
//        Logger.e("??????????????????????????????size:"+targetPtItems.size());
//
//        /****************************??????????????????????????????**********************************************/
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
//        Logger.e("----------??????size:"+pathLineItemExes.size());
//        DDRVLNMap.DDRMapPathDataEx ddrMapPathDataEx=DDRVLNMap.DDRMapPathDataEx.newBuilder()
//                .addAllPathLineData(pathLineItemExes)
//                .build();
//        /**********************************************??????????????????????????????***************************************/
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
//        Logger.e("-------------???????????????????????????size:"+taskItemExes.size());
//        /***************************************??????????????????????????????******************************************/
//        List<DDRVLNMap.space_item> space_items=new ArrayList<>();           //????????????????????????
//        List<SpaceItem> spaceItems=MapFileStatus.getInstance().getSpaceItems();
//        Logger.e("-------????????????:"+spaceItems.size());
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
//     * ???????????????????????????
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
//     * ???????????????????????????
//     * @param modeType
//     * @param pointName  ???????????????
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
//        Logger.e("----modeType:"+modeType+"----name:"+reqDDRVLNMapEx.getBasedata().getName().toStringUtf8()+"ab????????????"+abSpeed);
//    }
//
//    /**
//     * ???????????????????????????
//     * @param
//     */
//    public void saveSpaceToServer(){
//        DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx=MapFileStatus.getInstance().getReqDDRVLNMapEx();
//        List<DDRVLNMap.space_item> space_items=new ArrayList<>();           //????????????????????????
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
//     * ?????????????????????
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
//     * ????????????
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
//     * ??????or??????
//     * @param eCmdIPCMode
//     */
//    public void reqCmdIpcMethod(BaseCmd.eCmdIPCMode eCmdIPCMode ){
//        BaseCmd.reqCmdIPC reqCmdIPC=BaseCmd.reqCmdIPC.newBuilder()
//                .setMode(eCmdIPCMode)
//                .build();
//        sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdIPC);
//    }
//    /**
//     * ???????????????/????????????
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
//        Logger.e("???????????????/????????????");
//    }
//
//    /**
//     * ?????????
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
//     * ???????????????????????????
//     */
//    public void getFeishuDBData(int type,boolean isGetImage,int errorType){
//        Logger.e("??????????????????????????????");
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
//     * ???????????????????????????
//     */
//    public void getFeishuDBData(int type,boolean isGetImage,int errorType,ByteString sql){
//        Logger.e("??????????????????????????????");
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
//     * ??????????????????
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
//     * ??????????????????????????????
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
//     * ??????????????????
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
//     * ??????????????????
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
//     * ??????????????????
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
//     * ??????????????????
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
//     * ????????????
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
//     *  ?????????????????????
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
//    //??????????????????
//    public void getNaparmeter(){
//        BaseCmd.eConfigItemOptType eConfigItemOptType;
//        eConfigItemOptType=BaseCmd.eConfigItemOptType.eConfigOptTypeGetData;//????????????
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
//     * ????????????
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
//     * ??????????????????????????????
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
//     * ??????????????????????????????
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
//     * ???????????????
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
//     * ????????????
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
//     * ???????????????????????????
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
//     * ??????????????????
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
//     * ????????????http??????
//     */
//    private String jsondata;
//    private void okhttpData(RequestBody requestBody,String url){
//        Logger.e("--ok-");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Logger.e("--??????-");
//                OkHttpClient client=new OkHttpClient();
//                //Form???????????????????????????
//                Request request = new Request
//                        .Builder()
//                        .post(requestBody)//Post?????????????????????
//                        .url(url)
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Logger.e("--????????????--"+e.getMessage()+"---b"+url);
////                        okhttpData(requestBody,url);
//                        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.postFailure));
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        //????????????????????????????????????????????????????????????UI?????????
//
//                        jsondata = response.body().string();
//                        Logger.e("--??????????????????-"+jsondata);
//                        try {
//                            JSONObject jsonObject = new JSONObject(jsondata);
////                            String msg="Cu+\\/vQgKC09uZVJvdXRlXzI1EiBBdXRvbWF0aWNhbGx5IGdlbmVyYXRlZCBtYXAgaGVyZRoUFSQ577+977+9He+\\/ve+\\/vTRDJTJm77+977+9NSVm77+9QyLvv70IChUIARDvv73vv73Yhy4yCENvcm5lcl8xOAIKHwgDEO+\\/ve+\\/vdiHLh3vv70RZDolH++\\/ve+\\/vTkyCEF1dG9QdF8xOAEKHwgDENOy77+92IcuHSJhGUAlfQfvv73vv70yCEF1dG9QdF8yOAEKJAgBEM+K77+92IcuHe+\\/ve+\\/vRtAJQbvv71gPi1Z3b4\\/MghDb3JuZXJfMjgCCh8IAxDpubTYhy4d77+9bixAJR7vv70xQDIIQXV0b1B0XzM4AQokCAEQ77+977+977+92IcuHT\\/vv73vv70\\/JcOeOkAtaO+\\/vT5AMghDb3JuZXJfMzgCCh8IAxDuobXYhy4dAe+\\/ve+\\/vT8l77+9LDxAMghBdXRvUHRfNDgBCiQIARDvv73vv73vv73Yhy4d77+9Pm4+Je+\\/vTBALe+\\/vUU777+9MghDb3JuZXJfNDgCCiQIARDvv73JtdiHLh3vv73DviXvv719KUAtYi0877+9MghDb3JuZXJfNTgCCh8IAxDvv73JtdiHLh1zQh\\/vv70lI0ksQDIIQXV0b1B0XzU4AQofCAMQ77+977+977+92IcuHSYBCEAlHxFFQDIIQXV0b1B0XzY4AQofCAMQ77+977+977+92IcuHQfvv71tQCUGCV1AMghBdXRvUHRfNzgBCiQIARDvv73Tt9iHLh3vv71w77+9QCXvv71sLEAt77+9YO+\\/ve+\\/vTIIQ29ybmVyXzY4AgofCAMQ77+937fYhy4dLlrvv71AJe+\\/ve+\\/vQNAMghBdXRvUHRfODgBCiQIARDvv73vv73vv73Yhy4d77+9Ge+\\/vUAl77+9ZhBALWHvv70HPzIIQ29ybmVyXzc4AgofCAMQ77+977+977+92IcuHTXcvkAlFO+\\/vVhAMghBdXRvUHRfOTgBCiQIARDvv73vv73vv73Yhy4d77+9E++\\/vUAl77+977+9TkAt77+9BFc\\/MghDb3JuZXJfODgCCiQIARDvv73vv73vv73Yhy4d77+9Iu+\\/vUAl77+977+9RUAtCAMH77+9MghDb3JuZXJfOTgCCiUIARDur7nYhy4dW++\\/ve+\\/vUAlCu+\\/vQRALVxaCe+\\/vTIJQ29ybmVyXzEwOAIKIAgDEO6vudiHLh3vv71i77+9QCVO77+977+9PzIJQXV0b1B0XzEwOAEKJQgBEO+\\/ve+\\/vdiHLh3vv73vv73vv71AJR9qIz8tCe+\\/vcO\\/MglDb3JuZXJfMTE4AgogCAMQ77+977+92IcuHUp877+9QCV6fu+\\/vT4yCUF1dG9QdF8xMTgBCiAIAxDvv73NutiHLh3vv73vv73vv71AJe+\\/vX0DQDIJQXV0b1B0XzEyOAEKJQgBEO+\\/ve+\\/ve+\\/vdiHLh1Zbu+\\/vUAlBg0HQC3vv73vv71CQDIJQ29ybmVyXzEyOAIKIAgDEO+\\/ve+\\/ve+\\/vdiHLh3vv73vv70rQCXvv70mG0AyCUF1dG9QdF8xMzgBCiUIARDvv73cu9iHLh3vv71pH0Al77+9egBALe+\\/vVXvv73vv70yCUNvcm5lcl8xMzgCCiAIAxDvv73vv73vv73Yhy4d77+977+977+9PyVrHRQ+MglBdXRvUHRfMTQ4AQolCAEQ77+92bzYhy4d77+9Bk0\\/JShJFTwtIe+\\/vUPvv70yCUNvcm5lcl8xNDgCCiAIAxDvv73vv73vv73Yhy4d77+9Sk3vv70l77+9CnI8MglBdXRvUHRfMTU4AQolCAEQ77+977+92IcuHe+\\/ve+\\/vUE7Je+\\/vdqu77+9LR3vv70kPTIJQ29ybmVyXzE1OAIo77+937\\/Yhy4yBWFkbWluSAISABodChsKDemNkua\\/hu6dkOmQkD8SCg3vv70RZDoVH++\\/ve+\\/vTkiSwoRRERSVGFza19hdXRvLnRhc2sSFggBEhLlr6Tlk4TmtZjnkrruiJrnt54SEQgCEg3pjZLmv4bunZDpkJA\\/GggIARABGBcgOyDvv70HIiIKD0REUlRhc2tfSGgudGFzaxIGCAESAkhoGgAg77+9BygBMAIq77+9Agrvv70CChLlr6Tlk4TmtZjnkrruiJrnt54SDgoKDe+\\/vRFkOhUf77+977+9ORAIEg4KCg0iYRlAFX0H77+977+9EAgSDgoKDe+\\/vW4sQBUe77+9MUAQCBIOCgoNAe+\\/ve+\\/vT8V77+9LDxAEAgSDgoKDXNCH++\\/vRUjSSxAEAgSDgoKDSYBCEAVHxFFQBAIEg4KCg0H77+9bUAVBgldQBAIEg4KCg0uWu+\\/vUAV77+977+9A0AQCBIOCgoNNdy+QBUU77+9WEAQCBIOCgoN77+9Yu+\\/vUAVTu+\\/ve+\\/vT8QCBIOCgoNSnzvv71AFXp+77+9PhAIEg4KCg3vv73vv73vv71AFe+\\/vX0DQBAIEg4KCg3vv73vv70rQBXvv70mG0AQCBIOCgoN77+977+977+9PxVrHRQ+EAgSEwoKDe+\\/vUpN77+9Fe+\\/vQpyPBAHHQAA77+9Px3vv73vv73vv70+IEAqADAB";
////                            byte[] bytes=Base64.decode(msg.getBytes(),Base64.NO_WRAP | Base64.DEFAULT);
////                            Logger.e("???????????????????????????"+bytes.toString()+"??????"+new String(bytes,"US-ASCII"));
////                            String type = jsonObject.getString("head");
////                            String msg = jsonObject.getString("msg");
////                            msg = new String(Base64.decode(msg.getBytes(),Base64.DEFAULT));
////                            m_MessageRoute.processReceive1(type,msg);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                        //??????
//                        response.body().close();
//                    }
//                });
//            }
//        }).start();
//    }
//
//    /**
//     * ??????http???????????????????????????
//     */
//    private void okhttpDataHead(RequestBody requestBody,String url){
////        Logger.e("--ok-");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpClient client=new OkHttpClient();
//                //Form???????????????????????????
//                Request request = new Request
//                        .Builder()
//                        .post(requestBody)//Post?????????????????????
//                        .url(url)
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Logger.e("??????????????????--??????????????????--"+e.getMessage());
//                        if (timer!=null){
//                            Logger.e("????????????????????????????????????");
//                            timer.cancel();
//                            timer=null;
//                        }
//                        if (task!=null){
//                            task.cancel();
//                            Logger.e("????????????????????????-----????????????");
//                            task=null;
//                        }
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        //????????????????????????????????????????????????????????????UI?????????
//                        String jsondata = response.body().string();
////                        Logger.e("--??????????????????-"+jsondata);
//                        try {
//                            if (jsondata.contains("html")){
//                                Logger.e("???????????????????????????????????????");
//                            }else {
//                                JSONObject jsonObject = new JSONObject(jsondata);
//                                JSONArray userArray =jsonObject.getJSONArray("user_message");
//                                if (userArray.length()<=0){
////                                Toast.makeText(context,"?????????????????????",Toast.LENGTH_SHORT);
//                                    Logger.e("??????????????????????????????????????????");
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
//                        //??????
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
//        Logger.e("????????????ID"+GlobalParameter.getRobotID());
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
//                            Logger.e("??????????????????????????????token"+GlobalParameter.getToken());
////                    okhttpDataHead(requestBody,uri);
//                            OkHttpClient client=new OkHttpClient.Builder()
//                                    .readTimeout(10, TimeUnit.SECONDS)
//                                    .build();
//
//                            //Form???????????????????????????
//                            Request request = new Request
//                                    .Builder()
//                                    .post(requestBody)//Post?????????????????????
//                                    .url(uri)
//                                    .build();
//                            isReceive=false;
//                            Logger.e("--------??????------???"+requestBody.contentLength());
//                            Call call=client.newCall(request);
//                            Response response=call.execute();
//                            //????????????????????????????????????????????????????????????UI?????????
//                            String jsondata = response.body().string();
////                             Logger.e("--??????????????????-"+jsondata);
//                            try {
//                                if (jsondata.contains("html")){
//                                    Logger.e("???????????????????????????????????????");
//                                }else {
//                                    JSONObject jsonObject = new JSONObject(jsondata);
//                                    JSONArray userArray =jsonObject.getJSONArray("user_message");
//                                    if (userArray.length()<=0){
////                                Toast.makeText(context,"?????????????????????",Toast.LENGTH_SHORT);
//                                        Logger.e("??????????????????????????????????????????");
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
//                                    Logger.e("????????????");
//                                }else {
//                                    Logger.e("??????????????????");
//                                    Logger.e("??????"+e.getMessage());
//                                    e.printStackTrace();
//                                }
//                            }
//                            //??????
//                            response.body().close();
////                        client.newCall(request).enqueue(new Callback() {
////                            @Override
////                            public void onFailure(Call call, IOException e) {
////                                //isWorking=false;
////                                Logger.e("??????????????????--??????????????????--"+isWorking+e.getMessage());
////                              return;
////                            }
////
////                            @Override
////                            public void onResponse(Call call, Response response) throws IOException {
////                                //????????????????????????????????????????????????????????????UI?????????
////                                String jsondata = response.body().string();
//////                        Logger.e("--??????????????????-"+jsondata);
////                                try {
////                                    if (jsondata.contains("html")){
////                                        Logger.e("???????????????????????????????????????");
////                                    }else {
////                                        JSONObject jsonObject = new JSONObject(jsondata);
////                                        JSONArray userArray =jsonObject.getJSONArray("user_message");
////                                        if (userArray.length()<=0){
//////                                Toast.makeText(context,"?????????????????????",Toast.LENGTH_SHORT);
////                                            Logger.e("??????????????????????????????????????????");
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
////                                //??????
////                                response.body().close();
////                            }
////                        });
//                        }
//                        catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        try {
//                            Thread.sleep(500);
//                            Logger.e("------??????---");
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
//     * ???????????????2000??????????????????
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
////                    //Form???????????????????????????
////                    Request request = new Request
////                            .Builder()
////                            .post(requestBody)//Post?????????????????????
////                            .url(uri)
////                            .build();
////                    Logger.e("--------??????------???"+requestBody.contentLength());
////                    client.newCall(request).enqueue(new Callback() {
////                        @Override
////                        public void onFailure(Call call, IOException e) {
////                            Logger.e("??????????????????--??????????????????--"+e.getMessage());
////                            if (timer!=null){
////                                Logger.e("????????????????????????????????????");
////                                timer.cancel();
////                                timer=null;
////                            }
////                            if (task!=null){
////                                task.cancel();
////                                Logger.e("????????????????????????-----????????????");
////                                task=null;
////                            }
////                        }
////
////                        @Override
////                        public void onResponse(Call call, Response response) throws IOException {
////                            //????????????????????????????????????????????????????????????UI?????????
////                            String jsondata = response.body().string();
//////                        Logger.e("--??????????????????-"+jsondata);
////                            try {
////                                if (jsondata.contains("html")){
////                                    Logger.e("???????????????????????????????????????");
////                                }else {
////                                    JSONObject jsonObject = new JSONObject(jsondata);
////                                    JSONArray userArray =jsonObject.getJSONArray("user_message");
////                                    if (userArray.length()<=0){
//////                                Toast.makeText(context,"?????????????????????",Toast.LENGTH_SHORT);
////                                        Logger.e("??????????????????????????????????????????");
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
////                            //??????
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
//     * ??????????????????
//     */
//    public void getFeishuOpera(int type,boolean isopen){
//        FeishuCmd.enFeishuOperatorType enFeishuOperatorType;
//        switch (type) {
//            case 1:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorParking;//??????
//                break;
//            case 2:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorSideBrush;//??????
//                break;
//            case 3:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorWaterAbsorption;//??????
//                break;
//            case 4:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorTest;//??????
//                break;
//            case 5:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorWashTheFloor;//??????
//                break;
//            case 6:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorPushDust;//??????
//                break;
//            case 7:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorWaterPurificationConversion;//????????????
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
////            Logger.e("??????msg---------" +reqFeishuOperator.toByteArray()+"64????????????msg-----"
////                    +Base64.encodeToString(reqFeishuOperator.toByteArray(),Base64.DEFAULT));
//            postHttpMsg(typename,message);
//        }
//    }
//
//    /**
//     * ??????????????????
//     */
//    public void getFeishuXL(int type,int level){
//        FeishuCmd.enFeishuOperatorType enFeishuOperatorType;
//        switch (type) {
//            case 8:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorCleanIntensity;//????????????
//                break;
//            case 9:
//                enFeishuOperatorType = FeishuCmd.enFeishuOperatorType.enFeishuOperatorWaterYield;//??????
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
//     * ????????????????????????
//     * @param cleanMode
//     * @param isGet
//     */
//    public void getFeishuSystem(int cleanMode,boolean isGet){
//        FeishuCmd.enCleaningMode enCleaningMode;
//        switch (cleanMode){
//            case 1://??????
//                enCleaningMode=FeishuCmd.enCleaningMode.enCleaningModeWashTheFloor;
//                break;
//            case 2://??????
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
//        Logger.e("??????????????????user---"+GlobalParameter.getAccount()+"robot_id----"+GlobalParameter.getRobotID()+"head"+typename);
//        try {
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("user",GlobalParameter.getAccount())
//                    .addFormDataPart("token",GlobalParameter.getToken())
//                    .addFormDataPart("head", typename)
//                    .addFormDataPart("robot_id",GlobalParameter.getRobotID())
//                    .addFormDataPart("msg", Base64.encodeToString(message.toByteArray(),Base64.NO_WRAP | Base64.DEFAULT))
//                    .build();
//            Logger.e("????????????????????????"+requestBody.toString()+"mag"+message.toStringUtf8()+"token"+GlobalParameter.getToken());
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
//    private DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");   //?????????????????????????????????????????????????????????
//    private String saveCrashInfo2File(String errorMessage){
//        File dir=new File(GlobalParameter.ROBOT_FOLDER_LOG);
//        if (dir.exists()){
//            //  Logger.e("?????????????????????????????????");
//        }else {
//            Logger.e("????????????");
//            dir.mkdirs();
//        }
//        StringBuffer sb=new StringBuffer();
//        sb.append(errorMessage);
//        //????????????
//        String time=dateFormat.format(new Date());
//        String fileName = "crash-" + time + ".txt";
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            try {
//                Logger.e("??????log??????");
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
//        // ??????
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
//        // ??????
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

