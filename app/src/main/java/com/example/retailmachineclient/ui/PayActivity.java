package com.example.retailmachineclient.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easysocket.EasySocket;
import com.easysocket.config.DefaultMessageProtocol;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.easysocket.utils.LogUtil;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.base.GoodsModel;
import com.example.retailmachineclient.mcuSdk.DataProtocol;
import com.example.retailmachineclient.mcuSdk.MCU;
import com.example.retailmachineclient.mcuSdk.SerialPortUtil;
import com.example.retailmachineclient.model.GoodMsgModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.model.TxErrorModel;
import com.example.retailmachineclient.order.OrderRequest;
import com.example.retailmachineclient.protocobuf.CmdSchedule;
import com.example.retailmachineclient.protocobuf.dispatcher.ClientMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.socket.TcpClient;
import com.example.retailmachineclient.socket.UdpClient;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LogcatHelper;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SerialPortManagerUtils;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.Utils;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.OkSocketFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.internal.http2.ErrorCode;
import okio.ByteString;

import static com.example.retailmachineclient.util.ConstantUtils.ORDER_DATA_AND_RETURN;
import static com.example.retailmachineclient.util.ConstantUtils.ORDER_POWER_MACHINE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_CLOSE_DOOR;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_CANNOT_CLOSE_CASE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_CLOSE_DOOR_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_LIFT_EMPTY;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MACHINE_RUN;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MOVE_TARGET_CHECK_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MOVE_TARGET_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_DOOR_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_RESET_TO_7_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_RESET_TO_7_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_RESET_TO_7_OVERTIME;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_START_MACHINE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPEN_DOOR;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPERATE_PALLET;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPERATE_PALLET_TO_7;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPERATE_PROTECT_HAND;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_CLOSE_DOOR_STATUS;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_CLOSE_CASE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS_CLOSE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS_OPEN;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_MACHINE_STATUS_IS_POSITION;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_PALLET_STATUS;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_PALLET_STATUS_IS_POSITION;

public class PayActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tvPushUp)
    Button tvPushUp;
    @BindView(R.id.tvResult)
    TextView tvResult;
    @BindView(R.id.etNumber)
    EditText etNumber;

    @BindView(R.id.etNumber0)
    EditText etNumber0;
    @BindView(R.id.etNumber1)
    EditText etNumber1;
    @BindView(R.id.etNumber2)
    EditText etNumber2;
    @BindView(R.id.etNumber3)
    EditText etNumber3;
    @BindView(R.id.etNumber4)
    EditText etNumber4;
    @BindView(R.id.etNumber5)
    EditText etNumber5;
    @BindView(R.id.etNumber6)
    EditText etNumber6;
    @BindView(R.id.back)
    Button back;
    Button login_out;

    EditText set_value1;
    EditText set_value2;
    EditText set_value3;
    EditText set_value4;
    EditText set_value5;

    EditText set_value6;
    EditText set_value7;
    EditText set_value8;
    EditText set_value9;
    EditText set_value10;

    EditText set_value11;
    EditText set_value12;
    EditText set_value13;
    EditText set_value14;
    EditText set_value15;
    EditText set_value16;

    Button save_set;

    private String result = "";
    private SerialPortManager mSerialPortManager;
    private SerialPortUtil serialPortUtil;
    private OrderRequest orderRequestInstance;
    TcpClient tcpClient;

    public UdpClient udpClient;
    private int port = 28888;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_pay;
    }

    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ORDER_DATA_AND_RETURN:
                    //????????????????????????
                    String str = (String) msg.obj;
                    result = result + "\n" + str;
                    tvResult.setText(result);
                    tvResult.setMovementMethod(ScrollingMovementMethod.getInstance());
                    break;
            }
            super.handleMessage(msg);

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        int goodPosition = 0;
        GoodMsgModel goodMsgModel;
        switch (msgEvent.getType()) {

            case 4:
                TxErrorModel txErrorModel = (TxErrorModel) msgEvent.getTag();
                if (txErrorModel != null && !txErrorModel.isSuccess()) {
                    Toast.makeText(BaseApplication.getContext(), getErrorMsg(txErrorModel.getErrorCode()), Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(BaseApplication.getContext(), "????????????", Toast.LENGTH_SHORT).show();
                break;
            case 5:

                break;
        }
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
        initMyView();
        tvResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void initMyView() {
        tvPushUp = (Button) findViewById(R.id.tvPushUp);
        tvResult = (TextView) findViewById(R.id.tvResult);
        etNumber = (EditText) findViewById(R.id.etNumber);
        etNumber0 = (EditText) findViewById(R.id.etNumber0);
        etNumber1 = (EditText) findViewById(R.id.etNumber1);
        etNumber2 = (EditText) findViewById(R.id.etNumber2);
        etNumber3 = (EditText) findViewById(R.id.etNumber3);
        etNumber4 = (EditText) findViewById(R.id.etNumber4);
        etNumber5 = (EditText) findViewById(R.id.etNumber5);
        etNumber6 = (EditText) findViewById(R.id.etNumber6);
        back = (Button) findViewById(R.id.back);

        login_out  = (Button) findViewById(R.id.login_out);



        set_value1 = (EditText) findViewById(R.id.set_value1);
        set_value2 = (EditText) findViewById(R.id.set_value2);
        set_value3 = (EditText) findViewById(R.id.set_value3);
        set_value4 = (EditText) findViewById(R.id.set_value4);
        set_value5 = (EditText) findViewById(R.id.set_value5);

        set_value6 = (EditText) findViewById(R.id.set_value6);
        set_value7 = (EditText) findViewById(R.id.set_value7);
        set_value8 = (EditText) findViewById(R.id.set_value8);
        set_value9 = (EditText) findViewById(R.id.set_value9);
        set_value10 = (EditText) findViewById(R.id.set_value10);

        set_value11 = (EditText) findViewById(R.id.set_value11);
        set_value12 = (EditText) findViewById(R.id.set_value12);
        set_value13 = (EditText) findViewById(R.id.set_value13);
        set_value14 = (EditText) findViewById(R.id.set_value14);
        set_value15 = (EditText) findViewById(R.id.set_value15);
        set_value16 = (EditText) findViewById(R.id.set_value16);

        findViewById(R.id.save_set).setOnClickListener(this);
        findViewById(R.id.tvPushUp).setOnClickListener(this);
        findViewById(R.id.btPullDown).setOnClickListener(this);
        findViewById(R.id.btcx).setOnClickListener(this);
        findViewById(R.id.btCXLifter).setOnClickListener(this);
        findViewById(R.id.btStart).setOnClickListener(this);
        findViewById(R.id.btClear).setOnClickListener(this);
        findViewById(R.id.btCPushGoods).setOnClickListener(this);
        findViewById(R.id.tvLift0).setOnClickListener(this);
        findViewById(R.id.tvLift1).setOnClickListener(this);
        findViewById(R.id.tvLift2).setOnClickListener(this);
        findViewById(R.id.tvLift3).setOnClickListener(this);
        findViewById(R.id.tvLift4).setOnClickListener(this);

        findViewById(R.id.tvLift5).setOnClickListener(this);
        findViewById(R.id.tvLift6).setOnClickListener(this);
        findViewById(R.id.tvLift7).setOnClickListener(this);
        findViewById(R.id.btCTake).setOnClickListener(this);
        findViewById(R.id.closeDoorCase).setOnClickListener(this);
        findViewById(R.id.query_row).setOnClickListener(this);
        findViewById(R.id.query_is_empty).setOnClickListener(this);
        findViewById(R.id.query_is_protect).setOnClickListener(this);
        findViewById(R.id.startLogin).setOnClickListener(this);
        findViewById(R.id.starttcp).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.login_out).setOnClickListener(this);

        setPageSetValue();
    }

    @Override
    protected void initData() {

        tcpClient = TcpClient.getInstance(context, ClientMessageDispatcher.getInstance());
        //mcuSDK=McuSDK.initSDK(BaseApplication.context);
        /*serialPortUtil=new SerialPortUtil();
        serialPortUtil.open(Mcu.PortMCU.MCU1.getPath(),Mcu.PORT_RATE);*/
        mSerialPortManager = SerialPortManagerUtils.getInstance();
        orderRequestInstance = new OrderRequest(mHandler);

        mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File file) {
                Logger.e("?????????????????????" + file.getPath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result = result + "\n" + "??????????????????";
                        tvResult.setText(result);
                    }
                });
            }

            @Override
            public void onFail(File file, Status status) {
                Logger.e("?????????????????????" + file.getPath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result = result + "\n" + "??????????????????";
                        tvResult.setText(result);
                    }
                });
            }
        });
        mSerialPortManager.openSerialPort(new File(MCU.PortMCU.MCU1.getPath()), MCU.PORT_RATE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        tcpClient.disConnect();
//        LogcatHelper.getInstance(context).stop();
    }

    protected void TCPClient() {
        try {
            //???????????????Socket?????????????????????IP???????????????
//            Socket socket = new Socket("192.168.1.22",1002);

            Socket socket = new Socket();
            InetSocketAddress add = new InetSocketAddress("192.168.1.22", 1002);
            socket.connect(add);
            boolean isConnected = socket.isConnected();


            Logger.e("tcp:isConnected=" + isConnected);
            //??????????????????????????????????????????
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);
            pw.write("???????????????????????????????????????");
            pw.flush();
            //???????????????
            socket.shutdownOutput();

            //????????????????????????????????????????????????
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String data = null;
            //?????????????????????
            while ((data = br.readLine()) != null) {
                System.out.println("?????????????????????????????????????????????" + data);
            }
            //???????????????
            socket.shutdownInput();

            //????????????
            br.close();
            isr.close();
            is.close();
            pw.close();
            os.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     */
//    private void  receiveBroadcast(){
//        udpClient= UdpClient.getInstance(this,ClientMessageDispatcher.getInstance());
//        try {
//            udpClient.connect(port);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    /**
     * ????????????????????????
     */
    private void sendTestMessage() {

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
//        TestMessage testMessage = new TestMessage();
//        testMessage.setMsgId("test_msg");
//        testMessage.setFrom("androiddfsssdfs");
        // ??????
        int value = 21;
        byte[] dfd = new byte[10];
        dfd[0] = (byte) value;
        EasySocket.getInstance().upMessage(dfd);//testMessage.pack()
    }

    /**
     * socket????????????
     */
    private ISocketActionListener socketActionListener = new SocketActionListener() {
        /**
         * socket????????????
         * @param socketAddress
         */
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            LogUtil.d("---> ????????????");
//            controlConnect.setText("socket??????????????????????????????");
//            isConnected = true;
        }

        /**
         * socket????????????
         * @param socketAddress
         * @param isNeedReconnect ??????????????????
         */
        @Override
        public void onSocketConnFail(SocketAddress socketAddress, boolean isNeedReconnect) {
//            controlConnect.setText("socket????????????????????????????????????");
//            isConnected = false;
        }

        /**
         * socket????????????
         * @param socketAddress
         * @param isNeedReconnect ??????????????????
         */
        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, boolean isNeedReconnect) {
            LogUtil.d("---> socket????????????????????????????????????" + isNeedReconnect);
//            controlConnect.setText("socket????????????????????????????????????");
//            isConnected = false;
        }

        /**
         * socket???????????????
         * @param socketAddress
         * @param readData
         */
        @Override
        public void onSocketResponse(SocketAddress socketAddress, String readData) {
            LogUtil.d("SocketActionListener????????????-->" + readData);
        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
            LogUtil.d("SocketActionListener????????????-->" + originReadData.getBodyString());
        }
    };

    private void initEasySocket() {
        // socket??????
        EasySocketOptions options = new EasySocketOptions.Builder()
                // ?????????????????????????????????IP????????????getString???????????????????????????????????????IP??????
                .setSocketAddress(new SocketAddress("192.168.1.22", 10002))
                // ????????????????????????????????? socket????????????????????????
                .setReaderProtocol(new DefaultMessageProtocol())

                .build();

        // ?????????
        EasySocket.getInstance().createConnection(options, this);
        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
//                .options(options) // ????????????
//                .createConnection(this);// ????????????socket??????
    }


    /**
     * ??????sp?????????????????????
     */
//    public void updateSp2ConsSetValue(){
//        ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_GOODS_WAIT_TIME_NUM,ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM);
//        ConstantUtils.PAY_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.PAY_TIME_OUT_NUM,ConstantUtils.PAY_TIME_OUT_NUM);
//        ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_PAY_STATUS_TIME_OUT_NUM,ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM);
//        ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.PAY_BY_CARD_WAIT_TIME_NUM,ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM);
//        ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM =  SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.BUY_SUCCESS_WAIT_TIME_NUM,ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM);
//
//        ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.CLOSE_DOOR_LONG_TIME_NUM,ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM);
//        ConstantUtils.SEND_ORDER_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.SEND_ORDER_TIME_OUT_NUM,ConstantUtils.SEND_ORDER_TIME_OUT_NUM);
//        ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_LIFT_FLOOR_WAIT_TIME_NUM,ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM);
//        ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.MOVE_LIFT_FLOOR_WAIT_TIME_NUM,ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM);
//        ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_MATCHINE_WAIT_TIME_NUM,ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM);
//
//        ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPERATE_MATCHINE_WAIT_TIME_NUM,ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM);
//        ConstantUtils.OPERATE_LIFT_TO7_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPERATE_LIFT_TO7_WAIT_TIME_NUM,ConstantUtils.OPERATE_LIFT_TO7_WAIT_TIME_NUM);
//        ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_DOOR_WAIT_TIME_NUM,ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM);
//        ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPEN_DOOR_WAIT_TIME_NUM,ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM);
//        ConstantUtils.GET_GOODS_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.GET_GOODS_WAIT_TIME_NUM,ConstantUtils.GET_GOODS_WAIT_TIME_NUM);
//        ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_PROTECT_WAIT_TIME_NUM,ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM);
//    }

    /**
     * ??????????????????
     */
    public void setPageSetValue(){
        set_value1.setText(""+ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM);
        set_value2.setText(""+ConstantUtils.PAY_TIME_OUT_NUM);
        set_value3.setText(""+ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM);
        set_value4.setText(""+ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM);
        set_value5.setText(""+ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM);

//        ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_GOODS_WAIT_TIME_NUM,ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM);
//        ConstantUtils.PAY_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.PAY_TIME_OUT_NUM,ConstantUtils.PAY_TIME_OUT_NUM);
//        ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_PAY_STATUS_TIME_OUT_NUM,ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM);
//        ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.PAY_BY_CARD_WAIT_TIME_NUM,ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM);
//        ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM =  SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.BUY_SUCCESS_WAIT_TIME_NUM,ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM);

        set_value6.setText(""+ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM);
        set_value7.setText(""+ConstantUtils.SEND_ORDER_TIME_OUT_NUM);
        set_value8.setText(""+ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM);
        set_value9.setText(""+ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM);
        set_value10.setText(""+ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM);


//        ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.CLOSE_DOOR_LONG_TIME_NUM,ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM);
//        ConstantUtils.SEND_ORDER_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.SEND_ORDER_TIME_OUT_NUM,ConstantUtils.SEND_ORDER_TIME_OUT_NUM);
//        ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_LIFT_FLOOR_WAIT_TIME_NUM,ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM);
//        ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.MOVE_LIFT_FLOOR_WAIT_TIME_NUM,ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM);
//        ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_MATCHINE_WAIT_TIME_NUM,ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM);


        set_value11.setText(""+ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM);
        set_value12.setText(""+ConstantUtils.OPERATE_LIFT_TO7_WAIT_TIME_NUM);
        set_value13.setText(""+ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM);
        set_value14.setText(""+ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM);
        set_value15.setText(""+ConstantUtils.GET_GOODS_WAIT_TIME_NUM);
        set_value16.setText(""+ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM);

//        ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPERATE_MATCHINE_WAIT_TIME_NUM,ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM);
//        ConstantUtils.OPERATE_LIFT_TO7_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPERATE_LIFT_TO7_WAIT_TIME_NUM,ConstantUtils.OPERATE_LIFT_TO7_WAIT_TIME_NUM);
//        ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_DOOR_WAIT_TIME_NUM,ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM);
//        ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPEN_DOOR_WAIT_TIME_NUM,ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM);
//        ConstantUtils.GET_GOODS_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.GET_GOODS_WAIT_TIME_NUM,ConstantUtils.GET_GOODS_WAIT_TIME_NUM);
//        ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_PROTECT_WAIT_TIME_NUM,ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM);
    }

    /**
     * ??????????????????
     */
    public void saveSetValue(){
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_GOODS_WAIT_TIME_NUM,Integer.valueOf(set_value1.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.PAY_TIME_OUT_NUM,Integer.valueOf(set_value2.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_PAY_STATUS_TIME_OUT_NUM,Integer.valueOf(set_value3.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.PAY_BY_CARD_WAIT_TIME_NUM,Integer.valueOf(set_value4.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.BUY_SUCCESS_WAIT_TIME_NUM,Integer.valueOf(set_value5.getText().toString()));

        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.CLOSE_DOOR_LONG_TIME_NUM,Integer.valueOf(set_value6.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.SEND_ORDER_TIME_OUT_NUM,Integer.valueOf(set_value7.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_LIFT_FLOOR_WAIT_TIME_NUM,Integer.valueOf(set_value8.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.MOVE_LIFT_FLOOR_WAIT_TIME_NUM,Integer.valueOf(set_value9.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_MATCHINE_WAIT_TIME_NUM,Integer.valueOf(set_value10.getText().toString()));

        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.OPERATE_MATCHINE_WAIT_TIME_NUM,Integer.valueOf(set_value11.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.OPERATE_LIFT_TO7_WAIT_TIME_NUM,Integer.valueOf(set_value12.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_DOOR_WAIT_TIME_NUM,Integer.valueOf(set_value13.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.OPEN_DOOR_WAIT_TIME_NUM,Integer.valueOf(set_value14.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.GET_GOODS_WAIT_TIME_NUM,Integer.valueOf(set_value15.getText().toString()));
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_PROTECT_WAIT_TIME_NUM,Integer.valueOf(set_value16.getText().toString()));

        Utils.updateSp2ConsSetValue();
    }

    TcpAiClient tcpAiClient;
    int ip = 0;

    @Override
    public void onClick(View view) {
        byte data;
        Runnable runnable;
        switch (view.getId()) {

            case R.id.save_set:
                //?????????????????????????????? ?????? 20s
                saveSetValue();
                break;

            case R.id.login_out:
                Intent intent = new Intent("action.exit");
                intent.putExtra(EXITACTION, 1);
                sendBroadcast(intent);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.starttcp:
//                runnable = new Runnable() {
//                    @Override
//                    public void run() {
////                        TCPClient ();
////                        tcpAiClient.sendSpeed(1212,4545);
////                        sendPageMsg
//                        tcpAiClient.sendData(null, CmdSchedule.localLogin("admin","admin"));
////                        tcpAiClient.sendPageMsg(1,1);
//                    }
//                };
//                new Thread(runnable).start();
                //??????

                setLiftNum(true);

                break;
            case R.id.startLogin:
                //??????

//                tcpAiClient= TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance());
//                tcpAiClient.createConnect(context,"192.168.1.220",189);
                queryLiftNum(true);


                break;
            case R.id.start:
                String str = etNumber.getText().toString();

                if (TextUtils.isEmpty(str)) {
                    Toast.makeText(BaseApplication.getContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                }

                try {
                    ip = Integer.valueOf(str).intValue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(BaseApplication.getContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                }
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        byte[] nData = new byte[16];
                        nData[0] = (byte) ip;
                        nData[1] = (byte) 0x03;
                        for (int i = 2; i < nData.length; i++) {
                            nData[i] = 0x00;
                        }
                        OrderRequest orderRequest = new OrderRequest(mHandler);
                        orderRequest.requestCard(MCU.PortMCU.MCU5, (byte) 0x05, nData, ORDER_POWER_MACHINE);
                        try {
                            Thread.sleep(3000);//.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

//                        if (tcpAiClient == null){
//                            tcpAiClient= TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance());
//                        }
//                        if(tcpAiClient != null)
//                        tcpAiClient.startHeartbeat();
//                        tcpAiClient.sendGetRobotID("4a982ef1cf1d76a3ce28a0a691ec6669");

                    }
                };
                new Thread(runnable).start();
                break;
            case R.id.query_row:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        queryPalletStatusForISReset();
                    }
                };
                new Thread(runnable).start();
                break;

            case R.id.query_is_empty:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        queryIsHasGoodStatus();
                    }
                };
                new Thread(runnable).start();
                break;


            case R.id.query_is_protect:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        queryIsProtectHand();
                    }
                };
                new Thread(runnable).start();
                break;

            case R.id.closeDoorCase:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        processByStatus(TYPE_QUERY_DOOR_CLOSE_CASE, 0, 0, 10, 0);
                    }
                };
                new Thread(runnable).start();

                break;
            case R.id.tvPushUp:
                //serialPortUtil.sendData(DataProtocol.packSendData(Mcu.PortMCU.MCU1,(byte)0x14,(byte)0x01));
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1, (byte) 0x14, (byte) 0x01));
                    }
                };
                new Thread(runnable).start();
                break;
            case R.id.btPullDown:
                //serialPortUtil.sendData(DataProtocol.packSendData(Mcu.PortMCU.MCU1,(byte)0x14,(byte)0x01));
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1, (byte) 0x14, (byte) 0x00));
                    }
                };
                new Thread(runnable).start();

                break;
            case R.id.btcx:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1, (byte) 0x15, (byte) 0x00));
                    }
                };
                new Thread(runnable).start();
                break;
            case R.id.btCXLifter:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1, (byte) 0x13, (byte) 0x00));
                    }
                };
                new Thread(runnable).start();

                break;
            //???????????????
            case R.id.btCTake:
                GoodMsgModel goodsModel = new GoodMsgModel();
                goodsModel.setFloor(3);
                goodsModel.setMachineIndex(30);
                List<GoodMsgModel> goodsList = new ArrayList<GoodMsgModel>();
                goodsList.add(goodsModel);
                goodsModel = new GoodMsgModel();
                goodsModel.setFloor(2);
                goodsModel.setMachineIndex(23);
                goodsList.add(goodsModel);

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        TxErrorModel txError = getGoods(goodsList);
                        MessageEvent event = new MessageEvent(4);
                        event.setType(4);
                        event.setTag(txError);
                        EventBus.getDefault().post(event);
                    }
                };
                new Thread(runnable).start();
                break;

            case R.id.btCPushGoods://??????????????????
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        for (int ip = 0; ip < 60; ip++) {
                            byte[] nData = new byte[16];
                            nData[0] = (byte) ip;
                            nData[1] = (byte) 0x03;
                            for (int i = 2; i < nData.length; i++) {
                                nData[i] = 0x00;
                            }
                            OrderRequest orderRequest = new OrderRequest(mHandler);
                            orderRequest.requestCard(MCU.PortMCU.MCU5, (byte) 0x05, nData, ORDER_POWER_MACHINE);
                            try {
                                Thread.sleep(3000);//.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                new Thread(runnable).start();
                break;
            case R.id.btStart:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        queryMachineStatus();
                    }
                };
                new Thread(runnable).start();
                break;
            case R.id.btClear:
                result = "";
                tvResult.setText("");
                break;

            case R.id.tvLift0:
                data = 0x00;
                liftMove(data);
                break;
            case R.id.tvLift1:
                data = 0x01;
                liftMove(data);
                break;
            case R.id.tvLift2:
                data = 0x02;
                liftMove(data);
                break;
            case R.id.tvLift3:
                data = 0x03;
                liftMove(data);
                break;
            case R.id.tvLift4:
                data = 0x04;
                liftMove(data);
                break;
            case R.id.tvLift5:
                data = 0x05;
                liftMove(data);
                break;
            case R.id.tvLift6:
                data = 0x06;
                liftMove(data);
                break;
            case R.id.tvLift7:
                data = 0x07;
                liftMove(data);
                break;
        }
    }

    public String getErrorMsg(int type) {
        String data = "";
        switch (type) {
            case TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME:
                data = "????????????????????????7?????? ??????";
                break;
            case TYPE_ERROR_RESET_TO_7_OVERTIME:
                data = "??????????????????7??? ??????";
                break;
            case TYPE_ERROR_RESET_TO_7_FAIL:
                data = "??????????????????7??? ?????????????????????";
                break;
            case TYPE_ERROR_MOVE_TARGET_FAIL:
                data = "???????????????????????????????????? ??????";
                break;
            case TYPE_ERROR_MOVE_TARGET_CHECK_FAIL:
                data = "???????????????????????????????????? ????????????????????? ????????????????????????";
                break;
            case TYPE_ERROR_START_MACHINE:
                data = "?????????????????? ????????????";
                break;
            case TYPE_ERROR_MACHINE_RUN:
                data = "????????????????????? ??????????????????";
                break;
            case TYPE_ERROR_OPEN_RESET_TO_7_FAIL:
                data = "????????? ??????????????? ?????????????????????";
                break;
            case TYPE_ERROR_LIFT_EMPTY:
                data = "???????????? ??????????????????????????????";
                break;
            case TYPE_ERROR_OPEN_DOOR_FAIL:
                data = "??????????????????????????????";
                break;
            case TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY:
                data = "??????????????????????????????";
                break;
            case TYPE_ERROR_CANNOT_CLOSE_CASE:
                data = "???????????????????????????";
                break;
            case TYPE_ERROR_CLOSE_DOOR_FAIL:
                data = "????????????????????????";
                break;
        }
        return data;
    }


    /**
     * ???????????????
     *
     * @param data ??????
     */
    public void liftMove(byte data) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                byte[] nData = new byte[16];
                nData[0] = (byte) data;
                for (int i = 1; i < nData.length; i++) {
                    nData[i] = 0x00;
                }
                orderRequestInstance = new OrderRequest(mHandler);
                byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x12, nData, ORDER_POWER_MACHINE);
                if (responseBytes == null) {
                    Logger.e("??????:responseBytes?????????");
                } else {
                    Logger.e("??????:responseBytes:" + Utils.byteBufferToHexString(responseBytes));
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * ???????????????
     *
     * @param data ??????
     */
    public int liftMoveNew(byte data) {
        byte[] nData = new byte[16];
        nData[0] = (byte) data;
        for (int i = 1; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x12, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //?????????
            Logger.e("??????????????? ?????????");
        } else if (resultInt == 1) {
            //?????????
            Logger.e("??????????????? ????????????");
        } else if (resultInt == 2) {
            //?????????
            Logger.e("??????????????? ????????????");
        } else if (resultInt == 3) {
            //?????????
            Logger.e("??????????????? ????????????");
        } else {
            //?????????
            Logger.e("??????????????? ????????????");
        }
        return resultInt;
    }

    /**
     * ????????????
     *
     * @return
     */
    public int startMachine(int index) {
        byte[] nData = new byte[16];
        nData[0] = (byte) index;//???????????????
        nData[1] = (byte) 0x03;//????????????
        for (int i = 2; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU5, (byte) 0x05, nData, ORDER_POWER_MACHINE);
        try {
            Thread.sleep(1000);//.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunResult = Utils.byteToInt(responseBytes[2]);
        }
        return orderRunResult;
    }

    /**
     * ??????????????????????????? 13
     *
     * @return
     */
    public List<Integer> queryLiftStatusTX() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x13, nData, ORDER_POWER_MACHINE);
        //????????????
        int orderRunStatus = 100;
        int currentFloorStatus = 100;
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunStatus = Utils.byteToInt(responseBytes[2]);
            currentFloorStatus = Utils.byteToInt(responseBytes[3]);
            orderRunResult = Utils.byteToInt(responseBytes[4]);
        }
        Logger.e("???????????????????????????:orderRunStatus=" + orderRunStatus + ",currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * ??????????????????????????? 1d
     *
     * @return ??????:true,?????????false ???????????????false
     */
    public boolean queryIsHasGoods() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x1C, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //?????????
            return false;
        } else if (resultInt == 1) {
            //???????????????
            return true;
        }
        return false;
    }

    /**
     * ???????????????????????? 1c
     *
     * @return
     */
    public int queryIsHasGoodStatus() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x1C, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //?????????
            return resultInt;
        } else if (resultInt == 1) {
            //???????????????
            return resultInt;
        }
        return resultInt;
    }

    /**
     * ????????????run????????????
     *
     * @return
     */
    public List<Integer> queryMachineStatus() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
//        nData[0]=(byte)index;//???????????????
//        nData[1]=(byte)0x03;//????????????
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU5, (byte) 0x03, nData, ORDER_POWER_MACHINE);
        try {
            Thread.sleep(1000);//.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int orderRunStatus = 100;
        int currentFloorStatus = 100;
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunStatus = Utils.byteToInt(responseBytes[2]);
            currentFloorStatus = Utils.byteToInt(responseBytes[3]);
            orderRunResult = Utils.byteToInt(responseBytes[4]);
        }
        Logger.e("??????????????????????????????:orderRunStatus=" + orderRunStatus + ",currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * ?????? isOpen ??????????????? false:??????
     *
     * @return
     */
    public int openDoor(boolean isOpen) {
        byte[] nData = new byte[16];
        if (isOpen) {
            nData[0] = (byte) 0x01;
        } else {
            nData[0] = (byte) 0x00;
        }
        for (int i = 1; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x14, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }

        Logger.e("????????????????????????:isOpen = " + isOpen + " ,orderRunStatus=" + resultInt);
        return resultInt;
    }


    /**
     * byte????????????int???????????????????????????(???????????????????????????)???????????????intToBytes2??????????????????
     */
    public static int bytesToInt2(byte[] src, int offset) {
//        byte high, byte low
        return (((0x000000ff & src[0]) << 8) & 0x0000ff00) | (0x000000ff & src[1]);
    }

    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[2];
//        src[0] = (byte) ((value>>24) & 0xFF);
//        src[1] = (byte) ((value>>16)& 0xFF);
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }


    /**
     * ????????????????????????(Set Coded Value)?????? 10H?????????????????????
     *
     * @param isOpen
     * @return
     */
    public byte[] setLiftNum(boolean isOpen) {
        byte[] nData = new byte[16];
        nData[0] = (byte) 0x00;

        byte[] value = null;
        value = intToBytes2(Integer.valueOf(etNumber0.getText().toString()));
        if (value != null && value.length == 2) {
            nData[1] = value[0];
            nData[2] = value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber1.getText().toString()));
        if (value != null && value.length == 2) {
            nData[3] = value[0];
            nData[4] = value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber2.getText().toString()));
        if (value != null && value.length == 2) {
            nData[5] = value[0];
            nData[6] = value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber3.getText().toString()));
        if (value != null && value.length == 2) {
            nData[7] = value[0];
            nData[8] = value[1];
        }
        value = intToBytes2(Integer.valueOf(etNumber4.getText().toString()));
        if (value != null && value.length == 2) {
            nData[9] = value[0];
            nData[10] = value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber5.getText().toString()));
        if (value != null && value.length == 2) {
            nData[11] = value[0];
            nData[12] = value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber6.getText().toString()));
        if (value != null && value.length == 2) {
            nData[13] = value[0];
            nData[14] = value[1];
        }

        for (int i = 15; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        Logger.e("??????????????????1:isOpen = " + isOpen + " ,responseBytes=" + Utils.byteBufferToHexString(nData));
        orderRequestInstance = new OrderRequest(mHandler);
//        byte[] responseBytes = null;
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x11, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;

        if (responseBytes != null) {
            Logger.e("??????????????????2:isOpen = " + isOpen + " ,responseBytes=" + Utils.byteBufferToHexString(responseBytes));
            //0
            resultInt = responseBytes[2];
        }


        return responseBytes;
    }


    /**
     * ????????????????????????(Set Coded Value)?????? 10H?????????????????????
     *
     * @param isOpen
     * @return
     */
    public byte[] queryLiftNum(boolean isOpen) {
        byte[] nData = new byte[16];
        nData[0] = (byte) 0x00;
        for (int i = 1; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x10, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        int value = 0;
        if (responseBytes != null) {
            //0
            byte[] checkMachine = Utils.getBytes(responseBytes, 3, 2);
            value = bytesToInt2(checkMachine, 0);
            etNumber0.setText("" + value);
            //1
            checkMachine = Utils.getBytes(responseBytes, 5, 2);
            value = bytesToInt2(checkMachine, 0);
            etNumber1.setText("" + value);
            //2
            checkMachine = Utils.getBytes(responseBytes, 7, 2);
            value = bytesToInt2(checkMachine, 0);
            etNumber2.setText("" + value);
            //3
            checkMachine = Utils.getBytes(responseBytes, 9, 2);
            value = bytesToInt2(checkMachine, 0);
            etNumber3.setText("" + value);
            //4
            checkMachine = Utils.getBytes(responseBytes, 11, 2);
            value = bytesToInt2(checkMachine, 0);
            etNumber4.setText("" + value);
            //5
            checkMachine = Utils.getBytes(responseBytes, 13, 2);
            value = bytesToInt2(checkMachine, 0);
            etNumber5.setText("" + value);
            //6
            checkMachine = Utils.getBytes(responseBytes, 15, 2);
            value = bytesToInt2(checkMachine, 0);
            etNumber6.setText("" + value);
            //7
//            checkMachine = Utils.getBytes(responseBytes, 17, 2);
//            value = bytesToInt2(checkMachine,0);
//            etNumber7.setText(""+value);
        }


//001000049A04860473044B042504250425009575
        if (responseBytes != null)
            Logger.e("??????????????????:isOpen = " + isOpen + " ,responseBytes=" + Utils.byteBufferToHexString(responseBytes));
        return responseBytes;
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public List<Integer> queryDoorStatus() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x15, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        //
        int orderRunStatus = 100;
        int currentFloorStatus = 100;
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunStatus = Utils.byteToInt(responseBytes[2]);
            currentFloorStatus = Utils.byteToInt(responseBytes[3]);
            orderRunResult = Utils.byteToInt(responseBytes[4]);
        }
        Logger.e("??????????????????????????????:orderRunStatus=" + orderRunStatus + " ,currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * ???????????????
     *
     * @param type
     * @param beforeTime
     * @param overtime
     * @param repeatTimes
     * @return
     */
    public TxErrorModel processByStatus(Integer type, long beforeTime, long overtime, int repeatTimes, int position) {
        int startTime = 0;
        TxErrorModel beforeTxErrorModel = new TxErrorModel(false, false, 0);
        TxErrorModel txErrorModel;
        statusWhile:
        while (true) {
            switch (type) {
                case TYPE_OPERATE_PROTECT_HAND:
                    //???????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(true, true, 1, 0, 0, 0);
                    }
                    txErrorModel = queryProtectHandTask();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_PALLET_STATUS://TYPE_QUERY_PALLET_STATUS
                    //????????????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryPalletStatusForISReset();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_DOOR_STATUS_OPEN:
                    //???????????????????????? ??????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask(true);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_DOOR_CLOSE_CASE:
                    //???????????????????????? ??????????????????????????????
                    if (beforeTxErrorModel.getStamp() != 0 && ((System.currentTimeMillis() - beforeTxErrorModel.getStamp()) > 5000)) {
                        return new TxErrorModel(true, true, 0);
                    }
                    beforeTxErrorModel = queryCloseDoorCase(beforeTxErrorModel);
                    break;

                case TYPE_QUERY_DOOR_STATUS_CLOSE:
                    //???????????????????????? ??????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask(false);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_PALLET_STATUS_IS_POSITION://TYPE_QUERY_PALLET_STATUS
                    //?????????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME, 0, 0, 0);
                    }
                    txErrorModel = queryPalletStatusISPosition(position);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;
                case TYPE_QUERY_MACHINE_STATUS_IS_POSITION://TYPE_QUERY_PALLET_STATUS
                    //??????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryMachineStatusFor();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_OPERATE_PALLET:
                    //???????????????????????????
                    //???????????????????????? 3??????????????????????????????????????????
                    Logger.e("???????????????resetLiftTask TYPE_OPERATE_PALLET :" + startTime + "," + repeatTimes);
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, 3, 0, 0, 0);
                    }
                    txErrorModel = resetLiftTask(beforeTxErrorModel, position);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isSuccess()) {
                            Logger.e("???????????????resetLiftTask  ??????????????????");
                            //??????????????????
                            return txErrorModel;
                        } else {
                            if (txErrorModel.getZ1() == 0 || txErrorModel.getZ1() == 2) {
                                Logger.e("???????????????resetLiftTask  ??????startTime+1");
                                startTime = startTime + 1;
                            } else {
                                Logger.e("???????????????resetLiftTask  ?????? startTime");
                                startTime = 0;
                            }
                        }
                    } else {
                        Logger.e("???????????????resetLiftTask  not sucess");
                    }
                    break;

                case TYPE_OPERATE_PALLET_TO_7:
                    //???????????????????????? 3??????????????????????????????????????????
                    Logger.e("???????????????resetLiftTask TYPE_OPERATE_PALLET_TO_7 :" + startTime + "," + repeatTimes);
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, TYPE_ERROR_RESET_TO_7_OVERTIME, 0, 0, 0);
                    }
                    txErrorModel = resetLiftTask(beforeTxErrorModel, 7);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isSuccess()) {
                            //??????????????????
                            return txErrorModel;
                        } else {
                            //????????????
                            if (txErrorModel.getZ1() == 0) {
                                startTime = startTime + 1;
                            } else {
                                startTime = 0;
                            }
                        }
                    }
                    break;

                case TYPE_OPEN_DOOR:
                    //???????????????????????? 10????????????????????????
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, 10, 0, 0, 0);
                    }
                    txErrorModel = openDoorTask(true, 3000);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isTaskSuccess()) {
                            return txErrorModel;
                        } else {
                            startTime = startTime + 1;
                        }
                    }
                    break;

                case TYPE_CLOSE_DOOR:
                    //????????????????????????
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, 10, 0, 0, 0);
                    }
                    txErrorModel = openDoorTask(false, 2000);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isTaskSuccess()) {
                            return txErrorModel;
                        } else {
                            startTime = startTime + 1;
                        }
                    }
                    break;

                case TYPE_QUERY_CLOSE_DOOR_STATUS:
                    //?????????????????? ?????????
                    txErrorModel = queryCloseDoorTask();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isTaskSuccess()) {
                            return txErrorModel;
                        }
                    }
                    break;

            }
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param
     * @return
     */
    public TxErrorModel queryProtectHandTask() {
        long before = System.currentTimeMillis();
        int resultHand = queryIsProtectHand();
        if (resultHand != 0) {
            return new TxErrorModel(false, true, 21);
        }
        sleepWait(before, 100);
        return null;//????????????
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    public TxErrorModel queryCloseDoorTask() {
        long before = System.currentTimeMillis();

        int value = queryIsProtectHand();// 1:????????? 0?????????
        if (value == 1) {
            Logger.e("????????? ??????????????????????????? ?????????");
            return new TxErrorModel(false, true, 22);
        }

        List<Integer> list = queryDoorStatus();
        if (list.size() == 3) {
            if (list.get(0) == 0 || list.get(0) == 2) {
                if (list.get(1) == 0) {
                    //????????????????????????
                    Logger.e("????????? ?????????????????????????????? ?????????");
                    return new TxErrorModel(true, true, 21);
                }
            }
        }

        sleepWait(before, 50);
        return null;
    }

    /**
     * ??????????????????????????????
     *
     * @param beforeTx
     * @return
     */
    public TxErrorModel queryCloseDoorCase(TxErrorModel beforeTx) {
        long before = System.currentTimeMillis();
        //???????????? ????????? ??????00???
        int result = queryIsHasGoodStatus();
        if (result != 0) {
            beforeTx.setStamp(System.currentTimeMillis());
        }
        sleepWait(before, 100);
        before = System.currentTimeMillis();
        int resultHand = queryIsProtectHand();
        if (resultHand != 0) {
            beforeTx.setStamp(System.currentTimeMillis());
        }
        if (beforeTx.getStamp() == 0 && result == 0 && resultHand == 0) {
            //?????????
            beforeTx.setStamp(System.currentTimeMillis());
        }
        sleepWait(before, 100);
        return beforeTx;
    }

    /**
     * ????????????????????????????????????
     *
     * @param isOpen
     * @return
     */
    public TxErrorModel queryDoorStatusTask(boolean isOpen) {
        long before = System.currentTimeMillis();
        List<Integer> list = queryDoorStatus();
        if (list.size() == 3) {
            if (list.get(2) != 0) {
                //????????????
                return new TxErrorModel(false, false, 15);
            } else if (list.get(0) == 0 || list.get(0) == 2) {
                if (isOpen && list.get(1) == 1) {
                    //????????????????????????
                    return new TxErrorModel(false, true, 0);
                } else if (!isOpen && list.get(1) == 0) {
                    //????????????????????????
                    return new TxErrorModel(false, true, 0);
                } else {
                    sleepWait(before, 150);
                }
            } else {
                //????????????
                sleepWait(before, 150);
            }
        } else {
            sleepWait(before, 150);
        }
        return null;
    }

    /**
     * ???????????????????????? ????????????
     *
     * @param isOpen
     * @return
     */
    public TxErrorModel openDoorTask(boolean isOpen, long delayTime) {
        int result = openDoor(isOpen);
        if (result == 0) {
            //0 ????????????
            return new TxErrorModel(true, true, 0);
        } else {
            //3 ????????????
            //1 ????????????????????????????????????
            //2 ?????????????????????
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * ??????????????? ?
     *
     * @return
     */
    public TxErrorModel resetLiftTask(TxErrorModel beforeTx, int line) {
        Logger.e("???????????????resetLiftTask");
        int value = liftMoveNew((byte) line);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //???????????? ??????????????????
        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(), 10000, 0, line);
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            int compare = 0;
            if (line < 5) {
                compare = 4 - line;
            } else {
                compare = line;
            }
            if (txErrorModel.getZ2() == compare) {
                //???????????????????????????
                Logger.e("???????????????resetLiftTask ????????????????????? curent:" + txErrorModel.getZ2() + ",want Position:" + line);
                return new TxErrorModel(true, true, 0);
            } else {
                //???????????????????????????
                Logger.e("???????????????resetLiftTask ??????????????????????????? curent:" + txErrorModel.getZ2() + ",want Position:" + line);
                return new TxErrorModel(false, true, 0, txErrorModel.getZ1(), txErrorModel.getZ2(), txErrorModel.getZ3(), 0);
            }
        } else {
            Logger.e("???????????????resetLiftTask ?????????????????????");
            return new TxErrorModel(false, false, 0);
        }
    }

    /**
     * ????????????
     *
     * @param before
     * @param delay
     */
    public void sleepWait(long before, long delay) {
        if ((before + delay) > System.currentTimeMillis()) {
            try {
                Thread.sleep((before + delay) - System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ???????????????????????? ???????????????????????? ?????????
     *
     * @return
     */
    public TxErrorModel queryPalletStatusForISReset() {
        Logger.e("run queryPalletStatusForISReset ");
        long currentTimeMillis = System.currentTimeMillis();
        boolean isSleep = false;
        List<Integer> list = queryLiftStatusTX();
        if (list != null && list.size() > 0) {
            if (list.get(2) != 0) {
                //
                sleepWait(currentTimeMillis, 250);
                isSleep = true;
            } else {
                if (list.get(0) == 0 || list.get(0) == 2) {
                    //????????????????????????
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //???????????????
                    sleepWait(currentTimeMillis, 250);
                    isSleep = true;
                }
            }
        }
        return null;
    }

    /**
     * ???????????????????????? ?????????????????????????????????
     *
     * @return
     */
    public TxErrorModel queryMachineStatusFor() {
        long currentTimeMillis = System.currentTimeMillis();
        boolean isSleep = false;
        List<Integer> list = queryMachineStatus();
        if (list != null && list.size() > 0) {
            if (list.get(2) != 0) {
                //???????????? error
                sleepWait(currentTimeMillis, 150);
                isSleep = true;
            } else {
                if (list.get(0) == 0 || list.get(0) == 2) {
                    //????????????????????????
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //???????????????
                    sleepWait(currentTimeMillis, 150);
                    isSleep = true;
                }
            }
        }
        return null;
    }

    /**
     * ?????????????????????????????????
     *
     * @param position
     * @return
     */
    public TxErrorModel queryPalletStatusISPosition(int position) {
        Logger.e("run queryPalletStatusISPosition = " + position);
        long currentTimeMillis = System.currentTimeMillis();
        boolean isSleep = false;
        List<Integer> list = queryLiftStatusTX();
        if (list != null && list.size() > 0) {
            if (list.get(0) == 0 || list.get(0) == 2) {
                //????????????????????????
                if (list.get(1) == position) {
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //
                    return new TxErrorModel(false, true, 0, list.get(0), list.get(1), list.get(2));
                }
            } else {
                //???????????????
                sleepWait(currentTimeMillis, 150);
                isSleep = true;
            }
        }
        return null;
    }

    /**
     * ????????????????????????????????????
     *
     * @return N
     */
    public int queryIsProtectHand() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x18, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        Logger.e("???????????????????????? ??????:" + resultInt);
        return resultInt;
    }

    /**
     * ?????????????????????
     *
     * @param line
     * @param row
     * @return
     */
    public TxErrorModel getOneGood(int line, int row) {//??????????????? errorCode
        Logger.e("run getOneGood");

        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(), 10000, 0, 7);
        boolean isReset = false;
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            if (txErrorModel.getZ2() == 7) {
                isReset = true;
            }
        } else {
            if (txErrorModel != null) {
                txErrorModel.setSuccess(false);
            }
            return txErrorModel;
        }

        boolean resetResult = true;
        if (!isReset) {
            //????????????
            Logger.e("??????????????? ???????????? ");
            txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                int tempLine = (line + 2) % 5;
                liftMoveNew((byte) tempLine);
                txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
                if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                    Logger.e("??????????????? ?????????????????????");
                    //?????????????????????
                    txErrorModel.setSuccess(false);
                    txErrorModel.setErrorCode(TYPE_ERROR_RESET_TO_7_FAIL);
                    return txErrorModel;
                }
            }
        }
        //????????????????????????
        //???????????????????????? ????????????
        txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, 3, line);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("?????????????????????????????? ??????");
            //?????????????????????
            int tempLine = (line + 2) % 5;
            liftMoveNew((byte) tempLine);

            txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, 3, line);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_FAIL);
            }

        }
        Logger.e("??????????????? ???????????? ?????????????????????????????????");
        //?????????????????????????????????

        txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS, System.currentTimeMillis(), 10000, 0, line);
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            int compare = 0;
            if (txErrorModel.getZ2() < 5) {
                compare = 4 - txErrorModel.getZ2();
            } else {
                compare = txErrorModel.getZ2();
            }
            if (compare == line) {
                Logger.e("??????????????? ?????????????????????");
            } else {
                Logger.e("??????????????? ????????????????????????");
                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
            }
        } else {
            return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
        }
        //???????????????????????????
        Logger.e("???????????????????????????????????????????????????");
        //????????????
        int value = startMachine(row);
        if (value != 0) {
            //??????????????????
            return new TxErrorModel(false, false, TYPE_ERROR_START_MACHINE);
        }
        Logger.e("??????????????? ????????????????????????");
        txErrorModel = processByStatus(TYPE_QUERY_MACHINE_STATUS_IS_POSITION, System.currentTimeMillis(), 5000, 0, 0);
        if (!txErrorModel.isTaskSuccess()) {
            //??????
            Logger.e("??????????????? ??????????????????");
            return new TxErrorModel(false, false, TYPE_ERROR_MACHINE_RUN);
        }
        return new TxErrorModel(true, true, 0);
    }

    /**
     * ??????
     *
     * @param goodsList
     * @return
     */
    public TxErrorModel getGoods(List<GoodMsgModel> goodsList) {

        List<TxErrorModel> resultList = new ArrayList();
        for (GoodMsgModel goodsModel : goodsList) {
            TxErrorModel txErrorModel = getOneGood(goodsModel.getFloor(), goodsModel.getMachineIndex());
            if (!txErrorModel.isSuccess()) {
                return txErrorModel;
            }

//            MessageEvent event = new MessageEvent();
//            event.setType(5);
//            event.setTag(txErrorModel);
//            EventBus.getDefault().post(event);

            resultList.add(txErrorModel);
        }

        //??????????????? ??????????????????
        TxErrorModel txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("??????????????? ?????????????????????");
            //?????????????????????
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_RESET_TO_7_FAIL);
            return txErrorModel;
        }
        //??????????????????
        boolean isHasGoods = queryIsHasGoods();
        if (!isHasGoods) {
            Logger.e("???????????? ??????????????????????????????");
            return new TxErrorModel(false, false, TYPE_ERROR_LIFT_EMPTY);
        }
        return openDoorAfterGood();
    }

    public TxErrorModel openDoorAfterGood() {
        Logger.e("???????????? openDoorAfterGood");
        //????????????
        TxErrorModel txErrorModel = processByStatus(TYPE_OPEN_DOOR, 0, 0, 60, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("??????????????????????????????");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_FAIL);
            return txErrorModel;
        }
        //?????????????????????????????? ??????2???
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        Logger.e("??????????????????????????????");
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_STATUS_OPEN, System.currentTimeMillis(), 30000, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("??????????????????????????????");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY);
            return txErrorModel;
        }
        Logger.e("???????????????????????? ??????????????????");
//        //???????????????????????? ??????????????????
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_CLOSE_CASE, 0, 0, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("???????????????????????????");
            //?????????????????????
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CANNOT_CLOSE_CASE);
            return txErrorModel;
        }
        Logger.e("????????????????????????");
//        //??????
        txErrorModel = processByStatus(TYPE_CLOSE_DOOR, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            //????????????????????????
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CLOSE_DOOR_FAIL);
            return txErrorModel;
        }

        Logger.e("?????????????????????????????? ??? ?????????");
        txErrorModel = processByStatus(TYPE_QUERY_CLOSE_DOOR_STATUS, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isSuccess()) {
            Logger.e("??????????????? ?????? ??????????????? ?????????????????? ");
            //??????????????? ?????????????????????
            return openDoorAfterGood();
        } else {
            Logger.e("??????????????????");
            //??????????????????
            //???????????????3???
            txErrorModel = processByStatus(TYPE_OPERATE_PROTECT_HAND, System.currentTimeMillis(), 3000, 0, 0);
            if (txErrorModel != null && txErrorModel.isSuccess()) {
                //??????????????????
                txErrorModel.setSuccess(true);
                txErrorModel.setErrorCode(0);
                return txErrorModel;
            } else {
                //???????????????
                return openDoorAfterGood();
            }
        }
    }

}
