package com.example.retailmachineclient.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.zoloz.smile2pay.service.Zoloz;
import com.alipay.zoloz.smile2pay.service.ZolozCallback;
import com.bumptech.glide.Glide;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.alipay.api.AlipayCallBack;
import com.example.retailmachineclient.alipay.api.AlipayClient;
import com.example.retailmachineclient.alipay.api.AlipayResponse;
import com.example.retailmachineclient.alipay.api.DefaultAlipayClient;
import com.example.retailmachineclient.alipay.api.request.AlipayTradePayRequest;
import com.example.retailmachineclient.alipay.api.request.TradepayParam;
import com.example.retailmachineclient.alipay.api.request.ZolozAuthenticationCustomerSmilepayInitializeRequest;
import com.example.retailmachineclient.alipay.api.response.ZolozAuthenticationCustomerSmilepayInitializeResponse;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.http.HttpManager;
import com.example.retailmachineclient.model.CreateOderReqModel;
import com.example.retailmachineclient.model.CreateOrderModel;
import com.example.retailmachineclient.model.CreateOrderRspModel;
import com.example.retailmachineclient.model.GoodInfoModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.model.QueryOrderModel;
import com.example.retailmachineclient.model.QueryOrderRspModel;
import com.example.retailmachineclient.model.req.CloseOrderReqModel;
import com.example.retailmachineclient.model.req.TradeSuccessReqModel;
import com.example.retailmachineclient.model.rsp.TradeSuccessRspModel;
import com.example.retailmachineclient.protocobuf.dispatcher.ClientMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.NetUtils;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.TaskUtils;
import com.example.retailmachineclient.util.ThreadPoolManager;
import com.example.retailmachineclient.util.TimeIntervalUtils;
import com.example.retailmachineclient.view.NormalRecyclerViewAdapter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import DDRAIServiceProto.DDRAIServiceCmd;
import android.widget.RelativeLayout;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.example.retailmachineclient.alipay.api.MerchantInfo.appId;
import static com.example.retailmachineclient.alipay.api.MerchantInfo.appKey;
import static com.example.retailmachineclient.alipay.api.MerchantInfo.mockInfo;
import static com.example.retailmachineclient.model.MessageEvent.EventType_CreateOrder_Fail;
import static com.example.retailmachineclient.model.MessageEvent.EventType_CreateOrder_Success;
import static com.example.retailmachineclient.model.MessageEvent.EventType_QueryOrder_Fail;
import static com.example.retailmachineclient.model.MessageEvent.EventType_QueryOrder_Success;
import static com.example.retailmachineclient.model.MessageEvent.EventType_TradeSuccessOrder_Fail;

import android.os.Bundle;

public class PayTypeActivity extends BaseActivity implements View.OnClickListener {
    public static final int IMAGE_SIZE = 800;

//    NOTPAY--未支付 --成功
// true string
//    CLOSED--已关闭
    @BindView(R.id.btEnglish)
    TextView btEnglish;
    @BindView(R.id.btFrench)
    TextView btFrench;
    @BindView(R.id.tvTelephone)
    TextView tvTelephone;
    @BindView(R.id.btChinese)
    TextView btChinese;
    @BindView(R.id.recyclerShopping)
    ListView recyclerShopping;
    @BindView(R.id.number_value)
    TextView tvCommodityName;
    @BindView(R.id.result_value)
    TextView resultPrice;

    @BindView(R.id.weixin_qrcode)
    ImageView weiXinQrCode;

    @BindView(R.id.ali_qrcode)
    public ImageView aliQrCode;

    @BindView(R.id.time)
    public TextView timeView;

    @BindView(R.id.result)
    TextView result;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.tvTelephonetip)
    TextView tvTelephoneTip;
    @BindView(R.id.tvTitle)//购物车
    TextView tvTitle;

    @BindView(R.id.qrcode_tip)
    TextView qrcode_tip;
    @BindView(R.id.face_tip)
    TextView face_tip;
    @BindView(R.id.point_face)
    TextView point_face;
    @BindView(R.id.band_card_tip)
    TextView band_card_tip;
    @BindView(R.id.band_card_des)
    TextView band_card_des;

    @BindView(R.id.zhi_tip)
    TextView zhi_tip;

    @BindView(R.id.wei_tip)
    TextView wei_tip;

    String currentLan = "";
    String salesId = "";
    String phone = "";
    TcpAiClient tcpAiClient = null;
    boolean canPoint = true;
    List<GoodInfoModel> shoppingCarDataList = new ArrayList<GoodInfoModel>();
    NormalRecyclerViewAdapter shoppingCarAdapter;
    private List<GoodInfoModel> dataList;
    QueryOrderModel queryOrderModel;//订单支付状态信息
    CreateOrderModel createOrderModel;//订单详情信息
    float totalPrice;
    boolean isQuery = true;
    Runnable runnable;
    public static final String KEY_INIT_RESP_NAME = "zim.init.resp";
    private Zoloz zoloz;

    static final String CODE_SUCCESS = "1000";
    static final String CODE_EXIT = "1003";
    static final String CODE_TIMEOUT = "1004";
    static final String CODE_OTHER_PAY = "1005";

//    static final String TXT_EXIT = "已退出刷脸支付";
//    static final String TXT_TIMEOUT = "操作超时";
//    static final String TXT_OTHER_PAY = "已退出刷脸支付";
//    static final String TXT_OTHER = "抱歉未支付成功，请重新支付";

    //刷脸支付相关
    static final String SMILEPAY_CODE_SUCCESS = "10000";
    static final String SMILEPAY_SUBCODE_LIMIT = "ACQ.PRODUCT_AMOUNT_LIMIT_ERROR";
    static final String SMILEPAY_SUBCODE_BALANCE_NOT_ENOUGH = "ACQ.BUYER_BALANCE_NOT_ENOUGH";
    static final String SMILEPAY_SUBCODE_BANKCARD_BALANCE_NOT_ENOUGH = "ACQ.BUYER_BANKCARD_BALANCE_NOT_ENOUGH";
    //    static final String SMILEPAY_TXT_LIMIT = "刷脸支付超出限额，请选用其他支付方式";
//    static final String SMILEPAY_TXT_EBALANCE_NOT_ENOUGH = "账户余额不足，支付失败";
//    static final String SMILEPAY_TXT_BANKCARD_BALANCE_NOT_ENOUGH = "账户余额不足，支付失败";
//    static final String SMILEPAY_TXT_FAIL = "抱歉未支付成功，请重新支付";
//    static final String SMILEPAY_TXT_SUCCESS = "刷脸支付成功";
    int enterPayFaceTime = 1;//每次进入累计1 放到人脸订单上
    int enterPayByCardTime = 1;//每次进入累计1 放到刷卡订单上

    String EndOutTradeNo = "";//人脸支付订单编号
    @BindView(R.id.layout_main_all)
    RelativeLayout LayoutAll;



    boolean isReturnOrder = false;//创建订单接口是否返回
    boolean isCreateOrderSuccess = false;//创建订单接口是否成功

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        int goodPosition = 0;
        GoodInfoModel goodMsgModel;
        switch (msgEvent.getType()) {
            case EventType_CreateOrder_Success:
                createOrderModel = (CreateOrderModel)msgEvent.getTag();
                long start = System.currentTimeMillis();
                aliQrCode.setImageBitmap(createQRImage(createOrderModel.getAliQR(), IMAGE_SIZE, IMAGE_SIZE));
//                Glide.with(PayTypeActivity.this).load(createQRImage(createOrderModel.getAliQR(), IMAGE_SIZE, IMAGE_SIZE)).into(aliQrCode);
                weiXinQrCode.setImageBitmap(createQRImage(createOrderModel.getWeChatQR(), IMAGE_SIZE, IMAGE_SIZE));
//                Logger.e("-------二维码 微信= " + createOrderModel.getWeChatQR());
//                Glide.with(PayTypeActivity.this).load(createQRImage(createOrderModel.getWeChatQR(), IMAGE_SIZE, IMAGE_SIZE)).into(weiXinQrCode);
//                Logger.e("-------二维码耗时 = " + (System.currentTimeMillis() - start));
                //首次查询
                delayQueryStatus();
                timeView.setVisibility(View.VISIBLE);
                delayChangeView(ConstantUtils.PAY_TIME_OUT_NUM);

                isReturnOrder = true;
                isCreateOrderSuccess = true;
                break;
            case EventType_CreateOrder_Fail:
                timeView.setVisibility(View.VISIBLE);
                delayChangeView(ConstantUtils.PAY_TIME_OUT_NUM);
                isReturnOrder = true;
                isCreateOrderSuccess = false;
                break;

            case EventType_QueryOrder_Success:
                queryOrderModel = (QueryOrderModel)msgEvent.getTag();
                if (queryOrderModel != null) {
                    if (!TextUtils.isEmpty(queryOrderModel.getAliRequest())) {
                        if (queryOrderModel.getAliRequest().equals("TRADE_SUCCESS")
                        ) {
                            if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.pay_success_goto_result), Toast.LENGTH_SHORT).show();
                            } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_pay_success_goto_result), Toast.LENGTH_SHORT).show();
                            }
                            isQuery = false;
                            //跳转到出货页面
                            orderTradeSuccess("1", "Ali_TRADE_SUCCESS");
                        } else if (queryOrderModel.getWeChatRequest().equals("SUCCESS")) {
                            if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.pay_success_goto_result), Toast.LENGTH_SHORT).show();
                            } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_pay_success_goto_result), Toast.LENGTH_SHORT).show();
                            }
                            isQuery = false;
                            //跳转到出货页面
                            orderTradeSuccess("0", "Wechat_TRADE_SUCCESS");
                        } else if (queryOrderModel.getAliRequest().equals("WAIT_BUYER_PAY")
                                || queryOrderModel.getWeChatRequest().equals("NOTPAY")) {
                            //继续查询支付状态 不做任务处理 是否停止
                            if (isQuery) {
//                                delayQueryStatus();
                            }
                        } else if (queryOrderModel.getAliRequest().equals("TRADE_CLOSED")
                                || queryOrderModel.getAliRequest().equals("TRADE_FINISHED")
                                || queryOrderModel.getWeChatRequest().equals("CLOSED")) {
                            //跳回到选购页面
                            if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.pay_fail_goto_goods), Toast.LENGTH_SHORT).show();
                            } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_pay_fail_goto_goods), Toast.LENGTH_SHORT).show();
                            }
                            TimeIntervalUtils.lastTime = System.currentTimeMillis();
                            TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                            isQuery = false;
                            Intent intent = new Intent(this, MainActivity.class);
                            ArrayList<GoodInfoModel> sendDatas = new ArrayList<GoodInfoModel>();
                            sendDatas.addAll(shoppingCarDataList);
                            intent.putParcelableArrayListExtra("shoppingData", sendDatas);
                            startActivity(intent);
                            finish();
                        }
                    } else if (!TextUtils.isEmpty(queryOrderModel.getWeChatRequest())) {
                        //微信支付
                    }

                }
                break;
            case MessageEvent.EventType_TradeSuccessOrder_Success:
                //跳转出货页面
//                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.pay_success_goto_result), Toast.LENGTH_SHORT).show();
                if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                    Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.pay_success_goto_result), Toast.LENGTH_SHORT).show();
                } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                    Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_pay_success_goto_result), Toast.LENGTH_SHORT).show();
                }
                isQuery = false;
                //跳转到出货页面
                TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enShippingPage.getNumber());
                Intent intent = new Intent(this, PayResultActivity.class);
                ArrayList<GoodInfoModel> sendDatas = new ArrayList<GoodInfoModel>();
                sendDatas.addAll(shoppingCarDataList);
                intent.putParcelableArrayListExtra("shoppingData", sendDatas);
                intent.putExtra("queryOrderModel", queryOrderModel);
                intent.putExtra("createOrderModel", createOrderModel);
                startActivity(intent);
                finish();
                break;

            case EventType_TradeSuccessOrder_Fail:
                //待定
                //跳转到出货页面
                TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enShippingPage.getNumber());
                Intent intentFail = new Intent(this, PayResultActivity.class);
                ArrayList<GoodInfoModel> sendDatasFail = new ArrayList<GoodInfoModel>();
                sendDatasFail.addAll(shoppingCarDataList);
                intentFail.putParcelableArrayListExtra("shoppingData", sendDatasFail);
                intentFail.putExtra("queryOrderModel", queryOrderModel);
                intentFail.putExtra("createOrderModel", createOrderModel);
                startActivity(intentFail);
                finish();
                break;

            case MessageEvent.EventType_PayByFace_Success:
                orderTradeSuccess("2", (String) msgEvent.getTag());
                break;

            case MessageEvent.EventType_Pay_Timeout:
                //90秒定时器
                if (msgEvent.getPosition() != 0) {
                    if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                        timeView.setText("" + msgEvent.getPosition() + "s " + PayTypeActivity.this.getString(R.string.back));

                    } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                        timeView.setText("" + msgEvent.getPosition() + "s " + PayTypeActivity.this.getString(R.string.en_back));
                    }
                } else {
                    //跳转首页 不带参数
                    if (canPoint) {
                        //可以点击才去关闭订单
                        closeOrder();
                    }
                }
                break;

            case MessageEvent.EventType_CloseOrder_Success:
            case MessageEvent.EventType_CloseOrder_Fail:
//                Logger.e("支付页面 错误上报接口返回成功");
                TimeIntervalUtils.lastTime = System.currentTimeMillis();
                TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                startActivityFinish(MainActivity.class);//返回购物页
                break;

        }
    }

    public void closeOrder() {
        runnable = new Runnable() {
            @Override
            public void run() {
                NetUtils.getInstance().closeOrder("2",createOrderModel);
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    boolean isContinueTime = true;
    boolean isStopTime = false;//是否暂停
    Runnable timeRunnable = null;

    public void delayChangeView(final int number) {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                int num = number;
                while (isContinueTime) {
                    if (!isStopTime && num > -1) {
                        num = num - 1;
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_Pay_Timeout, num));
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ThreadPoolManager.getInstance().executeRunable(timeRunnable);
    }

    boolean isStopQuery = true;
    long queryTimeOnce = ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM *1000;
    public void delayQueryStatus() {
        runnable = new Runnable() {
            @Override
            public void run() {
                while(isStopQuery){
                    try {
                        Thread.sleep(queryTimeOnce);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    queryOrder();
                }
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    private void initViews() {
        initMyView();
        dataList = new ArrayList<>();
        calculatePrice(shoppingCarDataList);
        setBtnBg();
        LayoutAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Logger.e("你点击了ConstraintLayout");
                TaskUtils.sendPagePoint(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPaymentMethodPage.getNumber());
            }
        });
    }

    private void initMyView() {
        btEnglish = (TextView) findViewById(R.id.btEnglish);
        btFrench = (TextView) findViewById(R.id.btFrench);
        tvTelephone = (TextView) findViewById(R.id.tvTelephone);
        btChinese = (TextView) findViewById(R.id.btChinese);
        recyclerShopping = (ListView) findViewById(R.id.recyclerShopping);

        tvCommodityName = (TextView) findViewById(R.id.number_value);
        resultPrice = (TextView) findViewById(R.id.result_value);
        weiXinQrCode = (ImageView) findViewById(R.id.weixin_qrcode);
        aliQrCode = (ImageView) findViewById(R.id.ali_qrcode);
        timeView = (TextView) findViewById(R.id.time);

        result = (TextView) findViewById(R.id.result);
        number = (TextView) findViewById(R.id.number);
        tvTelephoneTip = (TextView) findViewById(R.id.tvTelephonetip);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        qrcode_tip = (TextView) findViewById(R.id.qrcode_tip);

        face_tip = (TextView) findViewById(R.id.face_tip);
        point_face = (TextView) findViewById(R.id.point_face);
        band_card_tip = (TextView) findViewById(R.id.band_card_tip);
        band_card_des = (TextView) findViewById(R.id.band_card_des);
        zhi_tip = (TextView) findViewById(R.id.zhi_tip);
        wei_tip = (TextView) findViewById(R.id.wei_tip);

        LayoutAll = (RelativeLayout) findViewById(R.id.layout_main_all);

        btChinese.setOnClickListener(this);
        btEnglish.setOnClickListener(this);
        timeView.setOnClickListener(this);
        findViewById(R.id.band_card_coin).setOnClickListener(this);
        findViewById(R.id.face_coin).setOnClickListener(this);
    }

    private void initDatas() {
        isContinueTime = true;
        isStopTime = false;
        TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enPaymentMethodPage.getNumber();
        ThreadPoolManager.getInstance().initThreadPool();
        tcpAiClient = TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance());
        currentLan = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        phone = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.PHONE);
        tvTelephone.setText(phone);
        if (dataList.size() > 0) {
            dataList.clear();
        }
        Intent intent = getIntent();
        ArrayList<GoodInfoModel> goodDataList = intent.getParcelableArrayListExtra("shoppingData");
        if (goodDataList != null) {
            shoppingCarDataList.clear();
            shoppingCarDataList.addAll(goodDataList);
        }
//        recyclerShopping.setLayoutManager(new LinearLayoutManager(this));
        shoppingCarAdapter = new NormalRecyclerViewAdapter(PayTypeActivity.this, shoppingCarDataList, false);
        recyclerShopping.setAdapter(shoppingCarAdapter);
        calculatePrice(shoppingCarDataList);

        createOrder();
    }

    public void queryOrder() {
        runnable = new Runnable() {
            @Override
            public void run() {
                NetUtils.getInstance().queryOrder(createOrderModel);
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    public void createOrder() {
        runnable = new Runnable() {
            @Override
            public void run() {
                NetUtils.getInstance().createOrderNew(totalPrice, shoppingCarDataList);
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
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
        isStopQuery = false;
    }

    @Override
    public void onClick(View view) {
        if (!canPoint) {
            Logger.e("支付成功后当前不可点击");
            return;
        }
        TaskUtils.sendPagePoint(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPaymentMethodPage.getNumber());
        switch (view.getId()) {
            case R.id.band_card_coin:
                if (createOrderModel != null) {
                    isStopTime = true;

                    enterPayByCardTime = enterPayByCardTime + 1;
                    EndOutTradeNo = createOrderModel.getOutTradeNo() + "C" + enterPayByCardTime;//刷卡支付订单编号

//                    TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enFaceRecognitionPage.getNumber());
//                    TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enFaceRecognitionPage.getNumber();
                    Intent intent = new Intent(this, PayByCardActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putFloat("totalPrice", totalPrice);
                    bundle.putString("EndOutTradeNo", EndOutTradeNo);

                    intent.putExtras(bundle);
                    startActivityForResult(intent, 3);
                } else {
                    if (isReturnOrder) {
                        if (isCreateOrderSuccess) {
                            //创建订单中，请稍后
                            if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.create_order_fail_back), Toast.LENGTH_SHORT).show();
                            } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_create_order_fail_back), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //建订单中，请稍后创
                            if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.in_create_order), Toast.LENGTH_SHORT).show();
                            } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_in_create_order), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        //建订单中，请稍后创
                        if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                            Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.in_create_order), Toast.LENGTH_SHORT).show();
                        } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                            Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_in_create_order), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.time:
                closeOrder();
                break;
            case R.id.btEnglish:
                changeLanguage(LanguageType.ENGLISH.getLanguage());
                break;
            case R.id.btFrench:
//                setLanguage(3);
                break;
            case R.id.btChinese:
                changeLanguage(LanguageType.CHINESE.getLanguage());
                break;
            case R.id.face_coin:
                //人脸支付
                if (createOrderModel != null) {
                    isStopTime = true;
                    TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enFaceRecognitionPage.getNumber());
                    TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enFaceRecognitionPage.getNumber();

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            smilePay();
                        }
                    };
                    ThreadPoolManager.getInstance().executeRunable(runnable);

                } else {
                    if (isReturnOrder) {
                        if (isCreateOrderSuccess) {
                            //创建订单中，请稍后
                            if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.create_order_fail_back), Toast.LENGTH_SHORT).show();
                            } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_create_order_fail_back), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //建订单中，请稍后创
                            if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.in_create_order), Toast.LENGTH_SHORT).show();
                            } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_in_create_order), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        //建订单中，请稍后创
                        if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                            Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.in_create_order), Toast.LENGTH_SHORT).show();
                        } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                            Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_in_create_order), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d("刷卡 onActivityResult requestCode=" + requestCode + ",resultCode=" + resultCode + ",data=" + data);
        if (requestCode == 3 && resultCode == RESULT_OK) {
            if (data == null) {
                Logger.e("刷卡data == null");
                return;
            }
            int result = Integer.valueOf(data.getIntExtra("result", 0));
            Logger.d("刷卡result == " + result);
            if (result == PayByCardActivity.Code_consume_success) {
                //支付成功 跳转
                canPoint = false;
                String payByCardResult =data.getStringExtra("payByCardResult");
                orderTradeSuccess("3", payByCardResult);

            } else if (result == PayByCardActivity.Code_consume_cancel) {
                //交易取消
                isStopTime = false;
                canPoint = true;
                currentPage();
            } else if (result == PayByCardActivity.Code_consume_fail) {
                //交易失败
                isStopTime = false;
                canPoint = true;
                currentPage();
            } else if (result == PayByCardActivity.Code_register_fail) {
                //签到失败
                isStopTime = false;
                canPoint = true;
                currentPage();
            } else {
                //其他
                isStopTime = false;
                canPoint = true;
                currentPage();
            }
            String newLan = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
            if (!TextUtils.isEmpty(newLan) && !TextUtils.isEmpty(currentLan) && !newLan.equals(currentLan)) {
                setBtnBg();
                currentLan = newLan;
            }
        }
    }

    /**
     * 更新购物车
     *
     * @param list
     */
    public void calculatePrice(List<GoodInfoModel> list) {
        totalPrice = 0;
        for (GoodInfoModel model : list) {
            totalPrice += model.getPrice();
        }
        tvCommodityName.setText("" + list.size());
        resultPrice.setText(BaseApplication.getContext().getString(R.string.money_type_text) + " " + totalPrice);
    }

    private void changeLanguage(String language) {
        if (language.equals(currentLan)) {
            return;
        }
        SpUtil.getInstance(this).putString(SpUtil.LANGUAGE, language);
        setBtnBg();
    }

    /**
     * 生成二维码
     *
     * @param url    地址
     * @param width  宽
     * @param height 高
     * @return 二维码Bitmap
     */
    public static Bitmap createQRImage(String url, final int width, final int height) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发起刷脸支付请求，先zolozGetMetaInfo获取本地app信息，然后调用服务端获取刷脸付协议.
     */
    private void smilePay() {
        enterPayFaceTime = enterPayFaceTime + 1;
        EndOutTradeNo = createOrderModel.getOutTradeNo() + "F" + enterPayFaceTime;//人脸最新支付订单编号
        zoloz = Zoloz.getInstance(getApplicationContext());
        zoloz.zolozGetMetaInfo(mockInfo(), new ZolozCallback() {
            @Override
            public void response(Map smileToPayResponse) {
                if (smileToPayResponse == null) {
                    promptText(R.string.txt_other);
                    isStopTime = false;
                    canPoint = true;
                    Logger.e("zolozGetMetaInfo= 获取设备信息error");
                    return;
                }

                String code = (String) smileToPayResponse.get("code");
                String metaInfo = (String) smileToPayResponse.get("metainfo");

                //获取metainfo成功 CODE_SUCCESS 1000
                if (CODE_SUCCESS.equalsIgnoreCase(code) && metaInfo != null) {
                    //正式接入请上传metaInfo到服务端，不要忘记UrlEncode，使用服务端使用的sdk从服务端访问openapi获取zimId和zimInitClientData；
                    AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                            appId,
                            appKey,
                            "json",
                            "utf-8",
                            null,
                            "RSA2");
                    ZolozAuthenticationCustomerSmilepayInitializeRequest request
                            = new ZolozAuthenticationCustomerSmilepayInitializeRequest();
                    request.setBizContent(metaInfo);
                    Logger.e("zolozGetMetaInfo= 刷脸初始化");
                    //刷脸初始化
                    //起一个异步线程发起网络请求
                    alipayClient.execute(request,
                            new AlipayCallBack() {
                                @Override
                                public AlipayResponse onResponse(AlipayResponse response) {
                                    Logger.e("zolozGetMetaInfo= 刷脸初始化返回");
                                    if (response != null && SMILEPAY_CODE_SUCCESS.equals(response.getCode())) {
                                        try {
                                            ZolozAuthenticationCustomerSmilepayInitializeResponse zolozResponse;
                                            zolozResponse = (ZolozAuthenticationCustomerSmilepayInitializeResponse) response;
                                            String result = zolozResponse.getResult();
                                            JSONObject resultJson = JSON.parseObject(result);
                                            String zimId = resultJson.getString("zimId");
                                            String zimInitClientData = resultJson.getString("zimInitClientData");
                                            //人脸调用
                                            Logger.e("人脸验证初始化的结果=" + response.toString());
                                            smile(zimId, zimInitClientData, EndOutTradeNo);
                                        } catch (Exception e) {
                                            Logger.e("人脸验证初始化的结果Exception=" + e.toString());
                                            promptText(R.string.txt_other);
                                            canPoint = true;
                                            isStopTime = false;
                                        }
                                    } else {
                                        Logger.e("人脸验证初始化的结果 response = null or not success");
                                        promptText(R.string.txt_other);
                                        canPoint = true;
                                        isStopTime = false;
                                    }
                                    return null;
                                }
                            });
                } else {
                    Logger.e("获取设备信息失败");
                    isStopTime = false;
                    canPoint = true;
                    promptText(R.string.txt_other);
                }
            }
        });
    }

    /**
     * 发起刷脸支付请求.
     *
     * @param zimId    刷脸付token，从服务端获取，不要mock传入
     * @param protocal 刷脸付协议，从服务端获取，不要mock传入
     */
    private void smile(String zimId, String protocal, String newOutTradeNum) {
        Map params = new HashMap();
        params.put(KEY_INIT_RESP_NAME, protocal);
        zoloz.zolozVerify(zimId, params, new ZolozCallback() {
            @Override
            public void response(final Map smileToPayResponse) {
                if (smileToPayResponse == null) {
                    Logger.e("人脸验证的结果=smileToPayResponse = null" );
                    promptText(R.string.txt_other);
                    canPoint = true;
                    isStopTime = false;
                    return;
                }
                String code = String.valueOf(smileToPayResponse.get("code"));
                String fToken = String.valueOf(smileToPayResponse.get("ftoken"));
                String subCode = String.valueOf(smileToPayResponse.get("subCode"));
                String msg = String.valueOf(smileToPayResponse.get("msg"));
                Logger.e("人脸验证的结果=" + smileToPayResponse.toString());
                //刷脸成功
                if (CODE_SUCCESS.equalsIgnoreCase(code) && fToken != null) {
                    //promptText("刷脸成功，返回ftoken为:" + fToken);
                    //这里在Main线程，网络等耗时请求请放在异步线程中 后续这里可以发起支付请求 https://docs.open.alipay.com/api_1/alipay.trade.pay 需要修改两个参数 需要修改两个参数
                    //scene固定为security_code , auth_code为这里获取到的fToken值
                    //auth_code为这里获取到的fToken值

                    try {
                        canPoint = false;
                        pay(fToken, Float.toString(totalPrice), newOutTradeNum);
                    } catch (Exception e) {
                        promptText(R.string.smilepay_txt_fail);
                        canPoint = true;
                        isStopTime = false;
                    }
                } else if (CODE_EXIT.equalsIgnoreCase(code)) {
                    canPoint = true;
                    isStopTime = false;
                    promptText(R.string.txt_exit);
                } else if (CODE_TIMEOUT.equalsIgnoreCase(code)) {

                    canPoint = true;
                    isStopTime = false;
                    promptText(R.string.txt_timeout);
                } else if (CODE_OTHER_PAY.equalsIgnoreCase(code)) {
                    canPoint = true;
                    isStopTime = false;
                    promptText(R.string.txt_other_pay);
                } else {
                    canPoint = true;
                    Logger.e("payfail subCode= " + subCode);
                    isStopTime = false;
                    promptText(R.string.txt_other);
                }
                currentPage();
            }

        });
    }

    /**
     * 发起刷脸支付请求.
     *
     * @param txtId toast文案id
     */
    void promptText(int txtId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), BaseApplication.getContext().getString(txtId), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 发起刷脸支付请求.
     *
     * @param ftoken 刷脸返回的token
     * @param amount 支付金额
     */
    private void pay(String ftoken, String amount, String newOutTradeNum) throws Exception {
        //正式 https://docs.open.alipay.com/api_1/alipay.trade.pay
        //测试 https://openapi.alipay.com/gateway.do
//        String serverUrl =   "https://docs.open.alipay.com/api_1/alipay.trade.pay";
        String serverUrl = "https://openapi.alipay.com/gateway.do";
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl,
                appId,
                appKey,
                "json",
                "utf-8",
                null,
                "RSA2");
        AlipayTradePayRequest alipayTradePayRequest = new AlipayTradePayRequest();
        TradepayParam tradepayParam = new TradepayParam();
//        tradepayParam.setOut_trade_no(UUID.randomUUID().toString());
        tradepayParam.setOut_trade_no(newOutTradeNum);
        //auth_code和scene填写需要注意
        tradepayParam.setAuth_code(ftoken);
        tradepayParam.setScene("security_code");
        tradepayParam.setSubject("smilepay");
        tradepayParam.setStore_id("smilepay test");//
        tradepayParam.setTimeout_express("5m");
        tradepayParam.setTotal_amount(amount);
        alipayTradePayRequest.setBizContent(JSON.toJSONString(tradepayParam));
        alipayClient.execute(alipayTradePayRequest,
                new AlipayCallBack() {
                    @Override
                    public AlipayResponse onResponse(AlipayResponse response) {
                        isStopTime = false;
                        if (response != null && SMILEPAY_CODE_SUCCESS.equals(response.getCode())) {
                            canPoint = false;
                            promptText(R.string.smilepay_txt_success);
                            Logger.e("人脸支付的结果=" + response.toString());
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_PayByFace_Success, response.toString()));

                        } else {
                            canPoint = true;
                            if (response != null) {
                                String subCode = response.getSubCode();
                                if (SMILEPAY_SUBCODE_LIMIT.equalsIgnoreCase(subCode)) {
                                    promptText(R.string.smilepay_txt_limit);
                                } else if (SMILEPAY_SUBCODE_BALANCE_NOT_ENOUGH.equalsIgnoreCase(subCode)) {
                                    promptText(R.string.smilepay_txt_ebalance_not_enough);
                                } else if (SMILEPAY_SUBCODE_BANKCARD_BALANCE_NOT_ENOUGH.equalsIgnoreCase(subCode)) {
                                    promptText(R.string.smilepay_txt_bankcard_ebalance_not_enough);
                                } else {
                                    promptText(R.string.smilepay_txt_limit);
                                }
                            } else {
                                promptText(R.string.smilepay_txt_fail);
                            }
                        }
                        return null;
                    }
                });
        return;
    }

    public void setBtnBg() {
        String current = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        if (current.equals(LanguageType.CHINESE.getLanguage())) {
            btChinese.setBackground(BaseApplication.getContext().getResources().getDrawable(R.drawable.bt_bg_lan));
            btEnglish.setBackground(null);
            btFrench.setBackground(null);
            btChinese.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.textSelectColor));
            btEnglish.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
            btFrench.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
        } else if (current.equals(LanguageType.ENGLISH.getLanguage())) {
            btChinese.setBackground(null);
            btEnglish.setBackground(BaseApplication.getContext().getResources().getDrawable(R.drawable.bt_bg_lan));
            btFrench.setBackground(null);

            btChinese.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
            btEnglish.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.textSelectColor));
            btFrench.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
        } else if (current.equals(LanguageType.FRENCH.getLanguage())) {
            btChinese.setBackground(null);
            btEnglish.setBackground(null);
            btFrench.setBackground(BaseApplication.getContext().getResources().getDrawable(R.drawable.bt_bg_lan));
            btChinese.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
            btEnglish.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
            btFrench.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.textSelectColor));
        }
        changeLanguage();
    }

    public void orderTradeSuccess(String type, String result) {
        final String typeReq = type;
        final String resultReq = result;
        runnable = new Runnable() {
            @Override
            public void run() {
                NetUtils.getInstance().orderTradeSuccess(typeReq, resultReq,createOrderModel,EndOutTradeNo);
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    public void changeLanguage() {
        String current = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        currentLan = current;
        btChinese.setText(PayTypeActivity.this.getString(R.string.chinese_text));
        btEnglish.setText(PayTypeActivity.this.getString(R.string.en_english_text));
        btFrench.setText(PayTypeActivity.this.getString(R.string.en_french_text));
        if (current.equals(LanguageType.CHINESE.getLanguage())) {
            result.setText(PayTypeActivity.this.getString(R.string.goods_total_money_text));
            number.setText(PayTypeActivity.this.getString(R.string.goods_num_text));
            tvTelephoneTip.setText(PayTypeActivity.this.getString(R.string.urgent_tel_text));
            tvTitle.setText(PayTypeActivity.this.getString(R.string.shopping_cart));

            qrcode_tip.setText(PayTypeActivity.this.getString(R.string.pay_by_code_text));
            face_tip.setText(PayTypeActivity.this.getString(R.string.pay_by_face_text));
            point_face.setText(PayTypeActivity.this.getString(R.string.point_look_face_text));
            band_card_tip.setText(PayTypeActivity.this.getString(R.string.pay_by_band_card_text));
            band_card_des.setText(PayTypeActivity.this.getString(R.string.use_band_card_tip_text));
            zhi_tip.setText(PayTypeActivity.this.getString(R.string.zhi_tip));
            wei_tip.setText(PayTypeActivity.this.getString(R.string.wei_tip));
        } else if (current.equals(LanguageType.ENGLISH.getLanguage())) {
            result.setText(PayTypeActivity.this.getString(R.string.en_goods_total_money_text));
            number.setText(PayTypeActivity.this.getString(R.string.en_goods_num_text));
            tvTelephoneTip.setText(PayTypeActivity.this.getString(R.string.en_urgent_tel_text));
            tvTitle.setText(PayTypeActivity.this.getString(R.string.en_shopping_cart));

            qrcode_tip.setText(PayTypeActivity.this.getString(R.string.en_pay_by_code_text));
            face_tip.setText(PayTypeActivity.this.getString(R.string.en_pay_by_face_text));
            point_face.setText(PayTypeActivity.this.getString(R.string.en_point_look_face_text));
            band_card_tip.setText(PayTypeActivity.this.getString(R.string.en_pay_by_band_card_text));
            band_card_des.setText(PayTypeActivity.this.getString(R.string.en_use_band_card_tip_text));
            zhi_tip.setText(PayTypeActivity.this.getString(R.string.en_zhi_tip));
            wei_tip.setText(PayTypeActivity.this.getString(R.string.en_wei_tip));
        } else if (current.equals(LanguageType.FRENCH.getLanguage())) {

        }
        List<GoodInfoModel> oldShoppingCarDataList = new ArrayList<>();
        if (shoppingCarDataList.size() > 0) {
            oldShoppingCarDataList.addAll(shoppingCarDataList);
            shoppingCarDataList.clear();
            shoppingCarAdapter.update(shoppingCarDataList);

            shoppingCarDataList.addAll(oldShoppingCarDataList);
            shoppingCarAdapter.update(shoppingCarDataList);
        }
    }

    public void currentPage() {
        TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPaymentMethodPage.getNumber());
        TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enPaymentMethodPage.getNumber();
    }

}
