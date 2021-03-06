package com.example.retailmachineclient.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.base.GoodsModel;
import com.example.retailmachineclient.http.HttpManager;
import com.example.retailmachineclient.mcuSdk.MCU;
import com.example.retailmachineclient.model.CreateOderReqModel;
import com.example.retailmachineclient.model.CreateOrderModel;
import com.example.retailmachineclient.model.CreateOrderRspModel;
import com.example.retailmachineclient.model.GoodInfoModel;
import com.example.retailmachineclient.model.GoodMsgModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.model.TxErrorModel;
import com.example.retailmachineclient.model.req.CloseOrderReqModel;
import com.example.retailmachineclient.model.req.ErrorReportReqModel;
import com.example.retailmachineclient.model.rsp.TradeSuccessRspModel;
import com.example.retailmachineclient.order.OrderRequest;
import com.example.retailmachineclient.protocobuf.dispatcher.ClientMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.NetUtils;
import com.example.retailmachineclient.util.SerialPortManagerUtils;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.TaskUtils;
import com.example.retailmachineclient.util.ThreadPoolManager;
import com.example.retailmachineclient.util.TimeIntervalUtils;
import com.example.retailmachineclient.util.Utils;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;

import android.widget.RelativeLayout;

import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.example.retailmachineclient.model.MessageEvent.EventType_CreateOrder_Fail;
import static com.example.retailmachineclient.model.MessageEvent.EventType_CreateOrder_Success;
import static com.example.retailmachineclient.model.MessageEvent.EventType_ErrorReportOrder_Fail;
import static com.example.retailmachineclient.model.MessageEvent.EventType_QueryOrder_Success;
import static com.example.retailmachineclient.model.MessageEvent.EventType_TradeSuccessOrder_Fail;
import static com.example.retailmachineclient.model.MessageEvent.EventType_TradeSuccessOrder_Success;
import static com.example.retailmachineclient.util.ConstantUtils.ORDER_POWER_MACHINE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_CLOSE_DOOR;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_CANNOT_CLOSE_CASE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_CLOSE_DOOR_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_GOOD_PUSH_MATCHINE_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_LIFT_EMPTY;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_LIFT_MATCHINE_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MACHINE_RUN;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MOVE_TARGET_CHECK_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MOVE_TARGET_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_DOOR_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_RESET_TO_7_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL_CLOSE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_RESET_TO_7_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_RESET_TO_7_OVERTIME;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_START_MACHINE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPEN_DOOR;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPERATE_PALLET;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPERATE_PALLET_TO_7;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPERATE_PROTECT_HAND;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_CLOSE_DOOR_STATUS;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_CLOSE_CASE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS_CLOSE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS_OPEN;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_MACHINE_STATUS_IS_POSITION;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_PALLET_STATUS;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_PALLET_STATUS_IS_POSITION;

import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_GOOD_PUSH_MATCHINE_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL_CLOSE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS;

public class PayResultActivity extends BaseActivity implements View.OnClickListener {
    TextView resultTipView;
    TextView resultTipEnView;
    TextView timeView;
    TextView timeAllView;
    RelativeLayout layoutGotoFirst;
    TextView goto_tip;
    TextView tvTelephone;
    RelativeLayout LayoutAll;
    TextView result_tv;
    ImageView result_iv;

    private SerialPortManager mSerialPortManager;
    OrderRequest orderRequestInstance;
    List<GoodInfoModel> shoppingCarDataList = new ArrayList<GoodInfoModel>();
    int notifyNum = 0;//???????????????????????????????????????????????? ??????1?????????
    Runnable runnable = null;
    CreateOrderModel createOrderModel = null;
    String salesId = "";
    String phone = "";
    TcpAiClient tcpAiClient = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_result;
    }

    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

            }
            super.handleMessage(msg);

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        int goodPosition = 0;
        GoodInfoModel goodMsgModel;
        switch (msgEvent.getType()) {
            case MessageEvent.EventType_GetGoodsFromLift_Success:
                TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enShippingSuccessPage.getNumber());
                TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enShippingSuccessPage.getNumber();
//                closeOrder((TxErrorModel) msgEvent.getTag());
                closeOrder(true);
                //5????????????
                layoutGotoFirst.setVisibility(View.VISIBLE);
                goto_tip.setVisibility(View.VISIBLE);
                timeView.setVisibility(View.VISIBLE);
                timeView.setText("" + ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM + "s");
                delayChangeView(ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM);
                break;
            case MessageEvent.EventType_GetGoodsFromLift_Fail:
                TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enShippingFailPage.getNumber();
                layoutGotoFirst.setVisibility(View.VISIBLE);
                goto_tip.setVisibility(View.VISIBLE);

                result_iv.setImageResource(R.mipmap.pay_fail);
                result_tv.setText(PayResultActivity.this.getString(R.string.en_pay_fail_text));

                resultTipView.setText(PayResultActivity.this.getString(R.string.shipments_fail_text) + " " + phone);
                resultTipEnView.setText(PayResultActivity.this.getString(R.string.shipments_fail_en_text) + " " + phone);
                timeView.setVisibility(View.GONE);
//                closeOrder((TxErrorModel) msgEvent.getTag());
                closeOrder(false);
                errorReportOrder((TxErrorModel) msgEvent.getTag());
                //???????????? ?????????????????????????????????
                break;
            case MessageEvent.EventType_GetGoodsFromLift_notify:
                if (notifyNum == 1) {
                    resultTipView.setText(PayResultActivity.this.getString(R.string.shipments_success_text));
                    resultTipEnView.setText(PayResultActivity.this.getString(R.string.shipments_success_en_text));
                }
                break;
            case MessageEvent.EventType_GetGoodsFromLift_Timeout:
                //5????????????
                if (msgEvent.getPosition() != 0) {
                    timeView.setText("" + msgEvent.getPosition() + "s");
                    delayChangeView(msgEvent.getPosition());
                } else {
                    //???????????? ????????????
                    TimeIntervalUtils.lastTime = System.currentTimeMillis();
                    TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                    startActivityFinish(MainActivity.class);
                }
                break;

            case MessageEvent.EventType_CloseOrder_Success:
                Logger.e("??????????????????????????????");
                break;
            case MessageEvent.EventType_CloseOrder_Fail:
                Logger.e("??????????????????????????????");
                break;
            case MessageEvent.EventType_ErrorReportOrder_Success:
                Logger.e("??????????????????????????????");
                break;
            case EventType_ErrorReportOrder_Fail:
                Logger.e("??????????????????????????????");
                break;

            case MessageEvent.EventType_START_GET_GOODS_Timeout:
                timeAllView.setVisibility(View.VISIBLE);
                delayChangeViewGetGood(121);
                break;
            case MessageEvent.EventType_GET_GOODS_Timeout:
                if (msgEvent.getPosition() > -1) {
                    timeAllView.setText("" + msgEvent.getPosition() + "s ");//+ PayResultActivity.this.getString(R.string.back)
                }
                break;
            case MessageEvent.EventType_GET_GOODS_Timeout_Over:
                timeAllView.setVisibility(View.INVISIBLE);
                break;

        }
    }

    public void delayChangeView(int number) {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GetGoodsFromLift_Timeout, number - 1));
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
    }

    @Override
    protected void initData() {
        initViews();
        initDatas();
    }

    private void initMyView() {
        resultTipView = (TextView) findViewById(R.id.result_tip_tv);
        resultTipEnView = (TextView) findViewById(R.id.result_tip_en_tv);
        timeView = (TextView) findViewById(R.id.time);
        timeAllView = (TextView) findViewById(R.id.time_all);
        layoutGotoFirst = (RelativeLayout) findViewById(R.id.bg_goto_first);
        goto_tip = (TextView) findViewById(R.id.goto_tip);
        tvTelephone = (TextView) findViewById(R.id.tvTelephone);
        LayoutAll = (RelativeLayout) findViewById(R.id.layout_main_all);
        result_tv = (TextView) findViewById(R.id.result_tv);
        result_iv = (ImageView) findViewById(R.id.result_iv);

        layoutGotoFirst.setOnClickListener(this);
    }

    private void initViews() {
        initMyView();
        resultTipView.setText(PayResultActivity.this.getString(R.string.shipments_want_text));
        LayoutAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.e("????????????ConstraintLayout");
                TaskUtils.sendPagePoint(tcpAiClient, TimeIntervalUtils.pageValue);
            }
        });
    }

    private void initDatas() {
//        layoutGotoFirst.setVisibility(View.INVISIBLE);
        TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enShippingPage.getNumber();
        tcpAiClient = TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance());

        phone = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.PHONE);
        tvTelephone.setText(phone);
        resultTipView.setText(PayResultActivity.this.getString(R.string.shipments_want_text));
        resultTipEnView.setText(PayResultActivity.this.getString(R.string.shipments_want_en_text));
        salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        mSerialPortManager = SerialPortManagerUtils.getInstance();

        mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File file) {
                Logger.e("?????????????????????" + file.getPath());
            }

            @Override
            public void onFail(File file, Status status) {
                Logger.e("?????????????????????" + file.getPath());
            }
        });
        mSerialPortManager.openSerialPort(new File(MCU.PortMCU.MCU1.getPath()), MCU.PORT_RATE);
        Intent intent = getIntent();
        ArrayList<GoodInfoModel> goodDataList = intent.getParcelableArrayListExtra("shoppingData");
        createOrderModel = (CreateOrderModel) intent.getParcelableExtra("createOrderModel");
        if (goodDataList != null) {
            shoppingCarDataList.clear();
            shoppingCarDataList.addAll(goodDataList);
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TxErrorModel txError = getGoods(shoppingCarDataList);
                if (txError != null && txError.isSuccess()) {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GetGoodsFromLift_Success));
                } else {
                    queryAllLiftStatus("after error");
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GetGoodsFromLift_Fail, txError));
                }
            }
        };
        new Thread(runnable).start();

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
        isContinueTime = false;
    }

    @Override
    public void onClick(View view) {
        TaskUtils.sendPagePoint(tcpAiClient, TimeIntervalUtils.pageValue);
        switch (view.getId()) {
            case R.id.bg_goto_first:
                TimeIntervalUtils.lastTime = System.currentTimeMillis();
                TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                startActivityFinish(MainActivity.class);
                break;
            case R.id.result_tv:
                TxErrorModel txErrorModelTest = new TxErrorModel(true, true, 10);
                closeOrder(true);
                break;

        }
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
//        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU5, (byte) 0x05, nData, ORDER_POWER_MACHINE);
//        try {
//            Thread.sleep(1000);//.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        int orderRunResult = 100;
//        if (responseBytes != null) {
//            orderRunResult = Utils.byteToInt(responseBytes[2]);
//        }

        int time = 1;
        byte[] responseBytes = null;
        int orderRunResult = 100;
        while (time <= ConstantUtils.SEND_ORDER_TIME_OUT_NUM) {
            responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU5, (byte) 0x05, nData, ORDER_POWER_MACHINE);
            if (responseBytes != null) {
                orderRunResult = Utils.byteToInt(responseBytes[2]);
                break;
            } else {
                time++;
                try {
                    Thread.sleep(100);//.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
        //????????????
        byte result = 0;
        int resultInt = 100;

        int time = 1;
        byte[] responseBytes = null;
        while (time <= ConstantUtils.SEND_ORDER_TIME_OUT_NUM) {
            responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x1C, nData, ORDER_POWER_MACHINE);
            if (responseBytes != null) {
                result = responseBytes[2];
                resultInt = Utils.byteToInt(result);
                break;
            } else {
                time++;
                try {
                    Thread.sleep(100);//.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        if (resultInt == 0) {
            //?????????
            return false;
        } else if (resultInt == 1) {
            //???????????????
            return true;
        } else if (resultInt == 100) {
            Logger.e("???????????? ?????????????????? ?????? ");
            return false;
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
        //????????????
        byte result = 0;
        int resultInt = 100;
        int time = 1;
        byte[] responseBytes = null;
        while (time <= ConstantUtils.SEND_ORDER_TIME_OUT_NUM) {
            responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x1C, nData, ORDER_POWER_MACHINE);
            if (responseBytes != null) {
                result = responseBytes[2];
                resultInt = Utils.byteToInt(result);
                break;
            } else {
                time++;
                try {
                    Thread.sleep(100);//.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (resultInt == 0) {
            //?????????
            return resultInt;
        } else if (resultInt == 1) {
            //???????????????
            return resultInt;
        } else if (resultInt == 100) {
            Logger.e("???????????? ?????????????????? ?????? ");
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
        //????????????
        byte result = 0;
        int resultInt = 100;
        orderRequestInstance = new OrderRequest(mHandler);
        int time = 1;
        byte[] responseBytes = null;
        while (time <= ConstantUtils.SEND_ORDER_TIME_OUT_NUM) {
            responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x14, nData, ORDER_POWER_MACHINE);
            if (responseBytes != null) {
                result = responseBytes[2];
                resultInt = Utils.byteToInt(result);
                Logger.e("???????????? ???????????????????????? ?????????:isOpen = " + isOpen + " ,orderRunStatus=" + resultInt);
                break;
            } else {
                //?????????
                time = time + 1;
                Logger.e("???????????? ???????????????????????? ?????????:isOpen = " + isOpen + " ,orderRunStatus=" + resultInt);
                try {
                    Thread.sleep(100);//.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Logger.e("???????????? ????????????????????????:isOpen = " + isOpen + " ,orderRunStatus=" + resultInt);
        return resultInt;
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
        Logger.e("???????????? ??????????????????????????????:orderRunStatus=" + orderRunStatus + " ,currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
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
        beforeTxErrorModel.setStartTime(System.currentTimeMillis());
        TxErrorModel txErrorModel;
        statusWhile:
        while (true) {
            switch (type) {

                case ConstantUtils.TYPE_START_MATCHINE:
                    //???????????? ????????????
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, 0);
                    }
                    txErrorModel = startMatchineTask(position);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }else{
                        startTime = startTime+1;
                    }
                    break;

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

                case TYPE_QUERY_DOOR_STATUS:
                    //????????????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask();
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
                    //???????????????????????? ??????????????????????????????20
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
                            //modify 20210728 ????????????????????????
                            if (txErrorModel.getZ1() == 0 || txErrorModel.getZ1() == 2) {
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

                    txErrorModel = queryCloseDoorTask(beforeTxErrorModel);
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
     * ??????????????????????????????????????????
     *
     * @return
     */
    public TxErrorModel queryCloseDoorTask(TxErrorModel beforeTxError) {
        long before = System.currentTimeMillis();

        int value = queryIsProtectHand();// 1:????????? 0?????????
        if (value == 1) {
            Logger.e("????????? ??????????????????????????? ?????????");
            return new TxErrorModel(false, true, 22);
        }

        if (beforeTxError.getStartTime() + 5000 < System.currentTimeMillis()) {
            //???????????????5????????????????????????????????????????????????????????????
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
        }
        sleepWait(before, 50);
        return null;
    }

    long waitTimeCloseTotal = (ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM-5)*1000;
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
        if (result != 0 && result != 100) {
            //??????
            if (beforeTx.getStartTime() + waitTimeCloseTotal > System.currentTimeMillis()) {
                //???????????? ????????????2?????????120-5???
                beforeTx.setStamp(System.currentTimeMillis());
            } else {

            }
        } else {
            //??????????????????  ????????????????????????
        }
        sleepWait(before, 100);
        before = System.currentTimeMillis();
        int resultHand = queryIsProtectHand();
        if (resultHand != 0 && resultHand != 100) {
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
     * ??????????????????????????????
     *
     * @param
     * @return
     */
    public TxErrorModel queryProtectHandTask() {
        long before = System.currentTimeMillis();
        int resultHand = queryIsProtectHand();
        if (resultHand != 0 && resultHand != 100) {
            return new TxErrorModel(false, true, 21);
        }
        sleepWait(before, 100);
        return null;//????????????
    }

    public TxErrorModel startMatchineTask(int index) {
        long before = System.currentTimeMillis();
        int resultInt = startMachine(index);
        if(resultInt!=0){
            sleepWait(before, 150);
        }else{
            return new TxErrorModel(true, true, 0);
        }
        return null;
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

    public TxErrorModel queryDoorStatusTask() {
        long before = System.currentTimeMillis();
        List<Integer> list = queryDoorStatus();
        if (list.size() == 3) {
            if (list.get(2) != 0 && list.get(2) != 3) {
                //????????????
                return new TxErrorModel(true, true, TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL_CLOSE);
            } else if (list.get(0) == 0 || list.get(0) == 2) {
                return new TxErrorModel(true, true, 0);
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

    long QuerysStatusWaitTime = ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM*1000;
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
        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(),  QuerysStatusWaitTime, 0, line);
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
        //????????????
        byte result = 0;
        int resultInt = 100;
        int time = 1;
        byte[] responseBytes = null;
        while (time <= ConstantUtils.SEND_ORDER_TIME_OUT_NUM) {
            responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x18, nData, ORDER_POWER_MACHINE);
            if (responseBytes != null) {
                result = responseBytes[2];
                resultInt = Utils.byteToInt(result);
                break;
            } else {
                time++;
            }
        }
        Logger.e("???????????? ???????????????????????? ??????:" + resultInt);
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
        Logger.d("????????????run run getOneGood");
        queryAllLiftStatus("before error getOneGood");
        //??????????????????
        //??????????????????  150ms ?????? ???10 ?????????
        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(),  QuerysStatusWaitTime, 0, 7);
        boolean isReset = false;
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            if (txErrorModel.getZ3() != 0 && txErrorModel.getZ3() != 3) {
                //?????? ??????????????????
                if (txErrorModel != null) {
                    txErrorModel.setSuccess(false);
                }
                return txErrorModel;
            } else if (txErrorModel.getZ2() == 7) {
                isReset = true;
            }
        } else {
            //??????
            if (txErrorModel != null) {
                txErrorModel.setSuccess(false);
            }
            return txErrorModel;
        }

        boolean resetResult = true;
        if (!isReset) {
            //????????????
            Logger.d("????????????run ??????????????? ???????????? ");
            txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, 7);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                if (txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                    txErrorModel.setSuccess(false);
                    return txErrorModel;
                }
                int tempLine = (line + 2) % 5;
                liftMoveNew((byte) tempLine);
                //??????3???
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, 7);
                if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                    Logger.e("????????????run ??????????????? ?????????????????????");
                    if (txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                        txErrorModel.setSuccess(false);
                        return txErrorModel;
                    }
                    //?????????????????????
                    txErrorModel.setSuccess(false);
                    txErrorModel.setErrorCode(TYPE_ERROR_RESET_TO_7_FAIL);
                    return txErrorModel;
                } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                    txErrorModel.setSuccess(false);
                    return txErrorModel;
                }
            } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                txErrorModel.setSuccess(false);
                return txErrorModel;
            }
        }
        //????????????????????????
        //???????????????????????? ????????????
        txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, line);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("????????????run ?????????????????????????????? ??????");
            //?????????????????????
//            int tempLine = (line + 2) % 5;
            int tempLine = 2;
            liftMoveNew((byte) tempLine);
            //??????3???
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, line);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_FAIL);
            } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                txErrorModel.setSuccess(false);
                return txErrorModel;
            }

        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
            txErrorModel.setSuccess(false);
            return txErrorModel;
        }
        Logger.d("????????????run ??????????????? ???????????? ?????????????????????????????????");
        //?????????????????????????????????

        //??????????????????????????????????????????
        txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS, System.currentTimeMillis(), QuerysStatusWaitTime, 0, line);
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            if (txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                txErrorModel.setSuccess(false);
                return txErrorModel;
            }

            int compare = 0;
            if (txErrorModel.getZ2() < 5) {
                compare = 4 - txErrorModel.getZ2();
            } else {
                compare = txErrorModel.getZ2();
            }
            if (compare == line) {
                Logger.d("????????????run ??????????????? ?????????????????????");
            } else {
                Logger.d("????????????run ??????????????? ????????????????????????");
                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
            }
        } else {
            return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
        }
        //???????????????????????????
        Logger.d("????????????run ?????????????????????????????????");
//        txErrorModel = processByStatus(TYPE_QUERY_MACHINE_STATUS_IS_POSITION, System.currentTimeMillis(), ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM*1000, 0, 0);
//        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
//            //??????
//            Logger.e("????????????run ??????????????? ??????");
//            return new TxErrorModel(false, false, TYPE_ERROR_MACHINE_RUN);
//        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_GOOD_PUSH_MATCHINE_FAIL) {
//            txErrorModel.setSuccess(false);
//            return txErrorModel;
//        }
        Logger.d("????????????run ????????????");
//        int value = startMachine(row);
//        if (value != 0) {
//            //??????????????????
//            return new TxErrorModel(false, false, TYPE_ERROR_START_MACHINE);
//        }

        txErrorModel = processByStatus(ConstantUtils.TYPE_START_MATCHINE, System.currentTimeMillis(), 0, ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM, row);
        if(txErrorModel != null && !txErrorModel.isTaskSuccess()){
            Logger.e("????????????run ??????????????????");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_START_MACHINE);
            return txErrorModel;
        }

        //??????1???
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.d("????????????run ??????????????? ????????????????????????");
        txErrorModel = processByStatus(TYPE_QUERY_MACHINE_STATUS_IS_POSITION, System.currentTimeMillis(),  ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM*1000, 0, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            //??????
            Logger.e("???????????????run ???????????? ??????????????????");
            return new TxErrorModel(false, false, TYPE_ERROR_MACHINE_RUN);
        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_GOOD_PUSH_MATCHINE_FAIL) {
            txErrorModel.setSuccess(false);
            return txErrorModel;
        }
        return new TxErrorModel(true, true, 0);
    }

    /**
     * ??????
     *
     * @param goodsList
     * @return
     */
    public TxErrorModel getGoods(List<GoodInfoModel> goodsList) {
        List<TxErrorModel> resultList = new ArrayList();
        for (GoodInfoModel goodsModel : goodsList) {
            Logger.e("????????????run run ???????????? size=" + goodsList.size() + ",floor=:" + goodsModel.getContainerFloor() + ",matchineIndex=:" + goodsModel.getContainerNum());
            TxErrorModel txErrorModel = getOneGood(goodsModel.getContainerFloor(), goodsModel.getContainerNum());
            if (!txErrorModel.isSuccess()) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GetGoodsFromLift_Fail, txErrorModel));
                return txErrorModel;
            }
            resultList.add(txErrorModel);
        }

        // ??????????????? ?????????????????? ?????????????????? ???????????????
        Logger.d("????????????run run  ????????? ?????????????????? ??????????????? ");
        TxErrorModel txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, 7);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            if (txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                txErrorModel.setSuccess(false);
                return txErrorModel;
            }
            int tempLine = 2;
            liftMoveNew((byte) tempLine);
            //??????3???
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, 7);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                Logger.e("????????????run ??????????????? ?????????????????????");
                if (txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                    txErrorModel.setSuccess(false);
                    return txErrorModel;
                }
                //?????????????????????
                txErrorModel.setSuccess(false);
                txErrorModel.setErrorCode(TYPE_ERROR_RESET_TO_7_FAIL);
                return txErrorModel;
            } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                txErrorModel.setSuccess(false);
                return txErrorModel;
            }
        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
            txErrorModel.setSuccess(false);
            return txErrorModel;
        }

        //??????????????????
        boolean isHasGoods = queryIsHasGoods();
        if (!isHasGoods) {
            Logger.e("????????????run run???????????? ??????????????????????????????");
            return new TxErrorModel(false, false, TYPE_ERROR_LIFT_EMPTY);
        }
        return openDoorAfterGood();
    }

    public TxErrorModel openDoorAfterGood() {
        Logger.d("????????????run run openDoorAfterGood ???????????? ??????????????????");
        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_DOOR_STATUS, 0, 0, ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM, 0);
        if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL) {
            Logger.e("????????????run run ?????????????????? ??????????????????");
            txErrorModel.setSuccess(false);
            return txErrorModel;
        }

        Logger.d("????????????run run ????????????");
        //???????????? 10??? ????????????3s
        txErrorModel = processByStatus(TYPE_OPEN_DOOR, 0, 0, ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("????????????run ??????????????????????????????");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_FAIL);
            return txErrorModel;
        }
        //?????????????????????????????? ??????2???
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        Logger.d("????????????run run ?????????????????????????????? ??????30s");
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_STATUS_OPEN, System.currentTimeMillis(), ConstantUtils.GET_GOODS_WAIT_TIME_NUM*1000, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("????????????run run ??????????????????????????????");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY);
            return txErrorModel;
        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL) {
            Logger.e("????????????run run ????????????????????????");
            txErrorModel.setSuccess(false);
            return txErrorModel;
        }
        //??????
        notifyNum = notifyNum + 1;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GetGoodsFromLift_notify));

        Logger.d("????????????run run ???????????????????????? ??????????????????");
        isContinueTime = true;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_START_GET_GOODS_Timeout));
//        //??????????????????
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_CLOSE_CASE, 0, 0, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("????????????run run ???????????????????????????");
            //?????????????????????
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CANNOT_CLOSE_CASE);
            return txErrorModel;
        }
        Logger.d("????????????run run ????????????????????????");
//        //??????
        txErrorModel = processByStatus(TYPE_CLOSE_DOOR, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            //????????????????????????
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CLOSE_DOOR_FAIL);
            return txErrorModel;
        }

        Logger.d("????????????run run ???????????????????????????????????? ??? ?????????");
        txErrorModel = processByStatus(TYPE_QUERY_CLOSE_DOOR_STATUS, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isSuccess()) {
            Logger.e("????????????run run  ?????? ??????????????? ?????????????????? ");
            //??????????????? ?????????????????????
            isContinueTime = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return openDoorAfterGood();
        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL_CLOSE) {
            Logger.e("????????????run run ???????????? ????????????????????????  ");
            txErrorModel.setSuccess(false);
            return txErrorModel;
        } else {
            Logger.d("????????????run run ????????????????????????  ");
            //??????????????????
            //???????????????5???
            txErrorModel = processByStatus(TYPE_OPERATE_PROTECT_HAND, System.currentTimeMillis(), ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM*1000, 0, 0);
            if (txErrorModel != null && txErrorModel.isSuccess()) {
                Logger.e("????????????run run ??????????????????");
                txErrorModel.setSuccess(true);
                txErrorModel.setErrorCode(0);
                return txErrorModel;
            } else {
                //???????????????
                Logger.e("????????????run run ?????????????????????5s????????? ???????????????");
                isContinueTime = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return openDoorAfterGood();
            }

        }
    }

    public void closeOrder(final boolean isSuccess) {
        runnable = new Runnable() {
            @Override
            public void run() {
                if(isSuccess){
                    NetUtils.getInstance().closeOrder("1", createOrderModel);
                }else{
                    NetUtils.getInstance().closeOrder("0", createOrderModel);
                }
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    public void errorReportOrder(TxErrorModel txErrorModel) {
        runnable = new Runnable() {
            @Override
            public void run() {
                NetUtils.getInstance().errorReportOrder(txErrorModel);
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    Runnable timeRunnable;
    boolean isContinueTime = true;

    public void delayChangeViewGetGood(final int number) {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                int num = number;
                while (isContinueTime) {
                    num = num - 1;
                    if (num > -1) {
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GET_GOODS_Timeout, num));
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GET_GOODS_Timeout_Over, num));
            }
        };
        ThreadPoolManager.getInstance().executeRunable(timeRunnable);
    }

    //?????????????????????????????????
    public int queryAllLiftStatus(String type) {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        //????????????
        byte result = 0;
        int resultInt = 100;
        int time = 1;
        byte[] responseBytes = null;
        while (time <= ConstantUtils.SEND_ORDER_TIME_OUT_NUM) {
            responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x1D, nData, ORDER_POWER_MACHINE);
            if (responseBytes != null) {
                result = responseBytes[2];
                resultInt = Utils.byteToInt(result);
                break;
            } else {
                time++;
            }
        }

        if (responseBytes != null) {
            Logger.d("????????????run ????????????????????????????????? ??????:type = " + type + ",?????? data=" + Utils.byteBufferToHexString(responseBytes));
        } else {
            Logger.d("????????????run ????????????????????????????????? ??????:");
        }

        return resultInt;
    }
}
