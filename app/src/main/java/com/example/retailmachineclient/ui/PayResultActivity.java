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
    int notifyNum = 0;//作用是提醒用户取货，更改页面提示 等于1时起效
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
                //5秒定时器
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
                //失败之后 需要倒计时多少秒返回？
                break;
            case MessageEvent.EventType_GetGoodsFromLift_notify:
                if (notifyNum == 1) {
                    resultTipView.setText(PayResultActivity.this.getString(R.string.shipments_success_text));
                    resultTipEnView.setText(PayResultActivity.this.getString(R.string.shipments_success_en_text));
                }
                break;
            case MessageEvent.EventType_GetGoodsFromLift_Timeout:
                //5秒定时器
                if (msgEvent.getPosition() != 0) {
                    timeView.setText("" + msgEvent.getPosition() + "s");
                    delayChangeView(msgEvent.getPosition());
                } else {
                    //跳转首页 不带参数
                    TimeIntervalUtils.lastTime = System.currentTimeMillis();
                    TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                    startActivityFinish(MainActivity.class);
                }
                break;

            case MessageEvent.EventType_CloseOrder_Success:
                Logger.e("出货完成接口返回成功");
                break;
            case MessageEvent.EventType_CloseOrder_Fail:
                Logger.e("出货完成接口返回失败");
                break;
            case MessageEvent.EventType_ErrorReportOrder_Success:
                Logger.e("错误上报接口返回成功");
                break;
            case EventType_ErrorReportOrder_Fail:
                Logger.e("错误上报接口返回失败");
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
                Logger.e("你点击了ConstraintLayout");
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
                Logger.e("串口打开成功！" + file.getPath());
            }

            @Override
            public void onFail(File file, Status status) {
                Logger.e("串口打开失败！" + file.getPath());
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
     * 升降梯移动
     *
     * @param data 层数
     */
    public int liftMoveNew(byte data) {
        byte[] nData = new byte[16];
        nData[0] = (byte) data;
        for (int i = 1; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x12, nData, ORDER_POWER_MACHINE);
        //解析结果
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //已启动
            Logger.e("启动升降机 已启动");
        } else if (resultInt == 1) {
            //已启动
            Logger.e("启动升降机 无效索引");
        } else if (resultInt == 2) {
            //已启动
            Logger.e("启动升降机 正在运行");
        } else if (resultInt == 3) {
            //已启动
            Logger.e("启动升降机 启动失败");
        } else {
            //已启动
            Logger.e("启动升降机 通信异常");
        }
        return resultInt;
    }

    /**
     * 启动电机
     *
     * @return
     */
    public int startMachine(int index) {
        byte[] nData = new byte[16];
        nData[0] = (byte) index;//电机索引号
        nData[1] = (byte) 0x03;//电机类型
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
     * 查询升降机执行状态 13
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
        //解析结果
        int orderRunStatus = 100;
        int currentFloorStatus = 100;
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunStatus = Utils.byteToInt(responseBytes[2]);
            currentFloorStatus = Utils.byteToInt(responseBytes[3]);
            orderRunResult = Utils.byteToInt(responseBytes[4]);
        }
        Logger.e("获取升降机状态成功:orderRunStatus=" + orderRunStatus + ",currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * 查询货盘是否有货物 1d
     *
     * @return 有货:true,无货：false 默认返回：false
     */
    public boolean queryIsHasGoods() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        //解析结果
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
            //货盘空
            return false;
        } else if (resultInt == 1) {
            //货盘有物品
            return true;
        } else if (resultInt == 100) {
            Logger.e("出货流程 货盘是否有货 超时 ");
            return false;
        }
        return false;
    }

    /**
     * 查询货盘是否有货 1c
     *
     * @return
     */
    public int queryIsHasGoodStatus() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        //解析结果
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
            //货盘空
            return resultInt;
        } else if (resultInt == 1) {
            //货盘有物品
            return resultInt;
        } else if (resultInt == 100) {
            Logger.e("出货流程 货盘是否有货 超时 ");
            return resultInt;
        }
        return resultInt;
    }

    /**
     * 查询电机run执行状态
     *
     * @return
     */
    public List<Integer> queryMachineStatus() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
//        nData[0]=(byte)index;//电机索引号
//        nData[1]=(byte)0x03;//电机类型
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
        Logger.e("获取电机执行状态成功:orderRunStatus=" + orderRunStatus + ",currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * 操作 isOpen 是否打开门 false:关门
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
        //解析结果
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
                Logger.e("出货流程 操作货门返回结果 有返回:isOpen = " + isOpen + " ,orderRunStatus=" + resultInt);
                break;
            } else {
                //无返回
                time = time + 1;
                Logger.e("出货流程 操作货门返回结果 无返回:isOpen = " + isOpen + " ,orderRunStatus=" + resultInt);
                try {
                    Thread.sleep(100);//.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Logger.e("出货流程 操作货门返回结果:isOpen = " + isOpen + " ,orderRunStatus=" + resultInt);
        return resultInt;
    }

    /**
     * 获取推杆指令执行状态
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
        //解析结果
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
        Logger.e("出货流程 获取舱门推杆状态成功:orderRunStatus=" + orderRunStatus + " ,currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * 执行状态机
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
                    //启动电机 是否执行
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
                    //查询防夹手
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(true, true, 1, 0, 0, 0);
                    }
                    txErrorModel = queryProtectHandTask();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_DOOR_STATUS:
                    //查询撑杆指令状态
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_PALLET_STATUS://TYPE_QUERY_PALLET_STATUS
                    //查询货盘是否复位
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryPalletStatusForISReset();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_DOOR_STATUS_OPEN:
                    //查询撑杆指令状态 是否完成打开
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask(true);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_DOOR_CLOSE_CASE:
                    //查询撑杆指令状态 查询关门是否满足条件20
                    if (beforeTxErrorModel.getStamp() != 0 && ((System.currentTimeMillis() - beforeTxErrorModel.getStamp()) > 5000)) {
                        return new TxErrorModel(true, true, 0);
                    }
                    beforeTxErrorModel = queryCloseDoorCase(beforeTxErrorModel);
                    break;

                case TYPE_QUERY_DOOR_STATUS_CLOSE:
                    //查询撑杆指令状态 是否完成关闭
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask(false);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_PALLET_STATUS_IS_POSITION://TYPE_QUERY_PALLET_STATUS
                    //是否在指定位置
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME, 0, 0, 0);
                    }
                    txErrorModel = queryPalletStatusISPosition(position);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;
                case TYPE_QUERY_MACHINE_STATUS_IS_POSITION://TYPE_QUERY_PALLET_STATUS
                    //查询电机状态
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryMachineStatusFor();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_OPERATE_PALLET:
                    //发送货盘到指定的层
                    //发送货盘复位任务 3次处于空闲并无法到底指定位置
                    Logger.e("启动升降机resetLiftTask TYPE_OPERATE_PALLET :" + startTime + "," + repeatTimes);
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, 3, 0, 0, 0);
                    }
                    txErrorModel = resetLiftTask(beforeTxErrorModel, position);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isSuccess()) {
                            Logger.e("启动升降机resetLiftTask  跳出循环返回");
                            //跳出循环返回
                            return txErrorModel;
                        } else {
                            if (txErrorModel.getZ1() == 0 || txErrorModel.getZ1() == 2) {
                                Logger.e("启动升降机resetLiftTask  失败startTime+1");
                                startTime = startTime + 1;
                            } else {
                                Logger.e("启动升降机resetLiftTask  重置 startTime");
                                startTime = 0;
                            }
                        }
                    } else {
                        Logger.e("启动升降机resetLiftTask  not sucess");
                    }
                    break;

                case TYPE_OPERATE_PALLET_TO_7:
                    //发送货盘复位任务 3次处于空闲并无法到底指定位置
                    Logger.e("启动升降机resetLiftTask TYPE_OPERATE_PALLET_TO_7 :" + startTime + "," + repeatTimes);
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, TYPE_ERROR_RESET_TO_7_OVERTIME, 0, 0, 0);
                    }
                    txErrorModel = resetLiftTask(beforeTxErrorModel, 7);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isSuccess()) {
                            //跳出循环返回
                            return txErrorModel;
                        } else {
                            //空闲状态
                            //modify 20210728 一直复位没有退出
                            if (txErrorModel.getZ1() == 0 || txErrorModel.getZ1() == 2) {
                                startTime = startTime + 1;
                            } else {
                                startTime = 0;
                            }
                        }
                    }
                    break;

                case TYPE_OPEN_DOOR:
                    //发送打开货门任务 10次启动失败后退出
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
                    //发送关闭货门任务
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
                    //查询推杆指令 防夹手

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
     * 关门时一直检查是否触发防夹手
     *
     * @return
     */
    public TxErrorModel queryCloseDoorTask(TxErrorModel beforeTxError) {
        long before = System.currentTimeMillis();

        int value = queryIsProtectHand();// 1:打开了 0：关闭
        if (value == 1) {
            Logger.e("关门时 查询是否启动防夹手 已触发");
            return new TxErrorModel(false, true, 22);
        }

        if (beforeTxError.getStartTime() + 5000 < System.currentTimeMillis()) {
            //关门开始后5秒不去查询关门状态，之后再去查询关门状态
            List<Integer> list = queryDoorStatus();
            if (list.size() == 3) {
                if (list.get(0) == 0 || list.get(0) == 2) {
                    if (list.get(1) == 0) {
                        //撑杆已经完全打开
                        Logger.e("关门时 查询推杆货门是否关闭 已完成");
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
     * 查询是否满足关门条件
     *
     * @param beforeTx
     * @return
     */
    public TxErrorModel queryCloseDoorCase(TxErrorModel beforeTx) {
        long before = System.currentTimeMillis();
        //判断货盘 防夹手 都为00时
        int result = queryIsHasGoodStatus();
        if (result != 0 && result != 100) {
            //有货
            if (beforeTx.getStartTime() + waitTimeCloseTotal > System.currentTimeMillis()) {
                //有货而且 没有超过2分钟（120-5）
                beforeTx.setStamp(System.currentTimeMillis());
            } else {

            }
        } else {
            //无货或者超时  不用重置统计时间
        }
        sleepWait(before, 100);
        before = System.currentTimeMillis();
        int resultHand = queryIsProtectHand();
        if (resultHand != 0 && resultHand != 100) {
            beforeTx.setStamp(System.currentTimeMillis());
        }
        if (beforeTx.getStamp() == 0 && result == 0 && resultHand == 0) {
            //初始化
            beforeTx.setStamp(System.currentTimeMillis());
        }
        sleepWait(before, 100);
        return beforeTx;
    }

    /**
     * 查询是否满足关门条件
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
        return null;//继续查询
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
     * 查询舱门是否打开关闭完成
     *
     * @param isOpen
     * @return
     */
    public TxErrorModel queryDoorStatusTask(boolean isOpen) {
        long before = System.currentTimeMillis();
        List<Integer> list = queryDoorStatus();
        if (list.size() == 3) {
            if (list.get(2) != 0) {
                //指令错误
                return new TxErrorModel(false, false, 15);
            } else if (list.get(0) == 0 || list.get(0) == 2) {
                if (isOpen && list.get(1) == 1) {
                    //撑杆已经完全打开
                    return new TxErrorModel(false, true, 0);
                } else if (!isOpen && list.get(1) == 0) {
                    //撑杆已经完全关闭
                    return new TxErrorModel(false, true, 0);
                } else {
                    sleepWait(before, 150);
                }
            } else {
                //继续查询
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
                //指令错误
                return new TxErrorModel(true, true, TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL_CLOSE);
            } else if (list.get(0) == 0 || list.get(0) == 2) {
                return new TxErrorModel(true, true, 0);
            } else {
                //继续查询
                sleepWait(before, 150);
            }
        } else {
            sleepWait(before, 150);
        }
        return null;
    }

    /**
     * 操作舱门打开关闭 有疑问？
     *
     * @param isOpen
     * @return
     */
    public TxErrorModel openDoorTask(boolean isOpen, long delayTime) {
        int result = openDoor(isOpen);
        if (result == 0) {
            //0 已经启动
            return new TxErrorModel(true, true, 0);
        } else {
            //3 启动失败
            //1 无效推杆动作重试发送指令
            //2 其他设备运行中
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
     * 复位升降机 ?
     *
     * @return
     */
    public TxErrorModel resetLiftTask(TxErrorModel beforeTx, int line) {
        Logger.e("启动升降机resetLiftTask");
        int value = liftMoveNew((byte) line);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //获取状态 查看是否复位
        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(),  QuerysStatusWaitTime, 0, line);
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            int compare = 0;
            if (line < 5) {
                compare = 4 - line;
            } else {
                compare = line;
            }
            if (txErrorModel.getZ2() == compare) {
                //是否移动到指定的层
                Logger.e("启动升降机resetLiftTask 移动到指定的层 curent:" + txErrorModel.getZ2() + ",want Position:" + line);
                return new TxErrorModel(true, true, 0);
            } else {
                //没有移动到指定的层
                Logger.e("启动升降机resetLiftTask 没有移动到指定的层 curent:" + txErrorModel.getZ2() + ",want Position:" + line);
                return new TxErrorModel(false, true, 0, txErrorModel.getZ1(), txErrorModel.getZ2(), txErrorModel.getZ3(), 0);
            }
        } else {
            Logger.e("启动升降机resetLiftTask 没有查询到状态");
            return new TxErrorModel(false, false, 0);
        }
    }

    /**
     * 睡眠等待
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
     * 查询货盘位置指令 用于查看是否复位 取货前
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
                    //获取状态是成功的
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //机器运行中
                    sleepWait(currentTimeMillis, 250);
                    isSleep = true;
                }
            }
        }
        return null;
    }

    /**
     * 查询货盘位置指令 用于查看是否复位取货前
     *
     * @return
     */
    public TxErrorModel queryMachineStatusFor() {
        long currentTimeMillis = System.currentTimeMillis();
        boolean isSleep = false;
        List<Integer> list = queryMachineStatus();
        if (list != null && list.size() > 0) {
            if (list.get(2) != 0) {
                //启动失败 error
                sleepWait(currentTimeMillis, 150);
                isSleep = true;
            } else {
                if (list.get(0) == 0 || list.get(0) == 2) {
                    //获取状态是成功的
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //机器运行中
                    sleepWait(currentTimeMillis, 150);
                    isSleep = true;
                }
            }
        }
        return null;
    }

    /**
     * 查询货盘是否在指定层上
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
                //获取状态是成功的
                if (list.get(1) == position) {
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //
                    return new TxErrorModel(false, true, 0, list.get(0), list.get(1), list.get(2));
                }
            } else {
                //机器运行中
                sleepWait(currentTimeMillis, 150);
                isSleep = true;
            }
        }
        return null;
    }

    /**
     * 查询光幕是否遮挡，防夹手
     *
     * @return N
     */
    public int queryIsProtectHand() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        //解析结果
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
        Logger.e("出货流程 获取接近开关状态 成功:" + resultInt);
        return resultInt;
    }

    /**
     * 单独取一个货物
     *
     * @param line
     * @param row
     * @return
     */
    public TxErrorModel getOneGood(int line, int row) {//检验错误码 errorCode
        Logger.d("出货流程run run getOneGood");
        queryAllLiftStatus("before error getOneGood");
        //是否在出货口
        //货盘查询指令  150ms 一次 ，10 秒超时
        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(),  QuerysStatusWaitTime, 0, 7);
        boolean isReset = false;
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            if (txErrorModel.getZ3() != 0 && txErrorModel.getZ3() != 3) {
                //异常 货盘电机出错
                if (txErrorModel != null) {
                    txErrorModel.setSuccess(false);
                }
                return txErrorModel;
            } else if (txErrorModel.getZ2() == 7) {
                isReset = true;
            }
        } else {
            //超时
            if (txErrorModel != null) {
                txErrorModel.setSuccess(false);
            }
            return txErrorModel;
        }

        boolean resetResult = true;
        if (!isReset) {
            //没有复位
            Logger.d("出货流程run 启动升降机 没有复位 ");
            txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, 7);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                if (txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                    txErrorModel.setSuccess(false);
                    return txErrorModel;
                }
                int tempLine = (line + 2) % 5;
                liftMoveNew((byte) tempLine);
                //睡眠3秒
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, 7);
                if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                    Logger.e("出货流程run 启动升降机 一直复位不成功");
                    if (txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                        txErrorModel.setSuccess(false);
                        return txErrorModel;
                    }
                    //一直复位不成功
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
        //已经复位复位完成
        //升降移动指定位置 连续多次
        txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, line);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("出货流程run 启动升降机到指定楼层 失败");
            //做一次兜底动作
//            int tempLine = (line + 2) % 5;
            int tempLine = 2;
            liftMoveNew((byte) tempLine);
            //睡眠3秒
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
        Logger.d("出货流程run 启动升降机 正常启动 升降机是否到达指定位置");
        //升降机是否到达指定位置

        //查询升降梯是否移动到指定位置
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
                Logger.d("出货流程run 启动升降机 移动到指定位置");
            } else {
                Logger.d("出货流程run 启动升降机 未移动到指定位置");
                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
            }
        } else {
            return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
        }
        //查询升降机当前位置
        Logger.d("出货流程run 电机启动前检查电机状态");
//        txErrorModel = processByStatus(TYPE_QUERY_MACHINE_STATUS_IS_POSITION, System.currentTimeMillis(), ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM*1000, 0, 0);
//        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
//            //电机
//            Logger.e("出货流程run 电机启动前 异常");
//            return new TxErrorModel(false, false, TYPE_ERROR_MACHINE_RUN);
//        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_GOOD_PUSH_MATCHINE_FAIL) {
//            txErrorModel.setSuccess(false);
//            return txErrorModel;
//        }
        Logger.d("出货流程run 启动电机");
//        int value = startMachine(row);
//        if (value != 0) {
//            //电机启动异常
//            return new TxErrorModel(false, false, TYPE_ERROR_START_MACHINE);
//        }

        txErrorModel = processByStatus(ConstantUtils.TYPE_START_MATCHINE, System.currentTimeMillis(), 0, ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM, row);
        if(txErrorModel != null && !txErrorModel.isTaskSuccess()){
            Logger.e("出货流程run 电机启动异常");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_START_MACHINE);
            return txErrorModel;
        }

        //睡眠1秒
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.d("出货流程run 电机启动后 查询执行旋转结果");
        txErrorModel = processByStatus(TYPE_QUERY_MACHINE_STATUS_IS_POSITION, System.currentTimeMillis(),  ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM*1000, 0, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            //电机
            Logger.e("电出货流程run 机启动后 执行旋转异常");
            return new TxErrorModel(false, false, TYPE_ERROR_MACHINE_RUN);
        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_GOOD_PUSH_MATCHINE_FAIL) {
            txErrorModel.setSuccess(false);
            return txErrorModel;
        }
        return new TxErrorModel(true, true, 0);
    }

    /**
     * 取货
     *
     * @param goodsList
     * @return
     */
    public TxErrorModel getGoods(List<GoodInfoModel> goodsList) {
        List<TxErrorModel> resultList = new ArrayList();
        for (GoodInfoModel goodsModel : goodsList) {
            Logger.e("出货流程run run 执行出货 size=" + goodsList.size() + ",floor=:" + goodsModel.getContainerFloor() + ",matchineIndex=:" + goodsModel.getContainerNum());
            TxErrorModel txErrorModel = getOneGood(goodsModel.getContainerFloor(), goodsModel.getContainerNum());
            if (!txErrorModel.isSuccess()) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GetGoodsFromLift_Fail, txErrorModel));
                return txErrorModel;
            }
            resultList.add(txErrorModel);
        }

        // 升降梯复位 检查是否复位 检查是否复位 升降梯复位
        Logger.d("出货流程run run  开门前 检查是否复位 升降梯复位 ");
        TxErrorModel txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, 7);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            if (txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                txErrorModel.setSuccess(false);
                return txErrorModel;
            }
            int tempLine = 2;
            liftMoveNew((byte) tempLine);
            //睡眠3秒
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM, 7);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                Logger.e("出货流程run 启动升降机 一直复位不成功");
                if (txErrorModel.getErrorCode() == TYPE_ERROR_LIFT_MATCHINE_FAIL) {
                    txErrorModel.setSuccess(false);
                    return txErrorModel;
                }
                //一直复位不成功
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

        //货盘是否有货
        boolean isHasGoods = queryIsHasGoods();
        if (!isHasGoods) {
            Logger.e("出货流程run run货盘没货 不满足打开舱门的条件");
            return new TxErrorModel(false, false, TYPE_ERROR_LIFT_EMPTY);
        }
        return openDoorAfterGood();
    }

    public TxErrorModel openDoorAfterGood() {
        Logger.d("出货流程run run openDoorAfterGood 开门步骤 查询推杆状态");
        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_DOOR_STATUS, 0, 0, ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM, 0);
        if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL) {
            Logger.e("出货流程run run 查询推杆状态 指令执行失败");
            txErrorModel.setSuccess(false);
            return txErrorModel;
        }

        Logger.d("出货流程run run 打开舱门");
        //打开舱门 10次 每次间隔3s
        txErrorModel = processByStatus(TYPE_OPEN_DOOR, 0, 0, ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("出货流程run 打开开门指令执行失败");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_FAIL);
            return txErrorModel;
        }
        //检查舱门是否完成打开 先睡2秒
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        Logger.d("出货流程run run 检查舱门是否完成打开 超时30s");
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_STATUS_OPEN, System.currentTimeMillis(), ConstantUtils.GET_GOODS_WAIT_TIME_NUM*1000, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("出货流程run run 舱门打开步骤没有完成");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY);
            return txErrorModel;
        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL) {
            Logger.e("出货流程run run 舱门打开步骤异常");
            txErrorModel.setSuccess(false);
            return txErrorModel;
        }
        //提醒
        notifyNum = notifyNum + 1;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GetGoodsFromLift_notify));

        Logger.d("出货流程run run 检查是否取走货物 货盘是否有货");
        isContinueTime = true;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_START_GET_GOODS_Timeout));
//        //检查关门条件
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_CLOSE_CASE, 0, 0, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("出货流程run run 不满足条件执行关门");
            //不满足关门条件
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CANNOT_CLOSE_CASE);
            return txErrorModel;
        }
        Logger.d("出货流程run run 满足条件执行关门");
//        //关门
        txErrorModel = processByStatus(TYPE_CLOSE_DOOR, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            //关门指令发送失败
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CLOSE_DOOR_FAIL);
            return txErrorModel;
        }

        Logger.d("出货流程run run 关闭货门时，检查推杆状态 和 防夹手");
        txErrorModel = processByStatus(TYPE_QUERY_CLOSE_DOOR_STATUS, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isSuccess()) {
            Logger.e("出货流程run run  或者 触发防夹手 回到打开货门 ");
            //触发防夹手 回到打开门取货
            isContinueTime = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return openDoorAfterGood();
        } else if (txErrorModel != null && txErrorModel.getErrorCode() == TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL_CLOSE) {
            Logger.e("出货流程run run 执行关门 查询推杆指令异常  ");
            txErrorModel.setSuccess(false);
            return txErrorModel;
        } else {
            Logger.d("出货流程run run 执行关门指令完毕  ");
            //正常关门完毕
            //查询防夹手5秒
            txErrorModel = processByStatus(TYPE_OPERATE_PROTECT_HAND, System.currentTimeMillis(), ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM*1000, 0, 0);
            if (txErrorModel != null && txErrorModel.isSuccess()) {
                Logger.e("出货流程run run 正常关门完毕");
                txErrorModel.setSuccess(true);
                txErrorModel.setErrorCode(0);
                return txErrorModel;
            } else {
                //触发防夹手
                Logger.e("出货流程run run 关门后继续检查5s防夹手 触发防夹手");
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

    //查询升降机是否可以启动
    public int queryAllLiftStatus(String type) {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        //解析结果
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
            Logger.d("出货流程run 查询升降机是否可以启动 成功:type = " + type + ",返回 data=" + Utils.byteBufferToHexString(responseBytes));
        } else {
            Logger.d("出货流程run 查询升降机是否可以启动 失败:");
        }

        return resultInt;
    }
}
