package com.example.retailmachineclient.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.widget.ImageView;
import android.content.Intent;

import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.protocobuf.dispatcher.ClientMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SpUtil;
import com.bumptech.glide.Glide;
import com.example.retailmachineclient.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import com.example.retailmachineclient.model.CardPayMsgModel;

import DDRAIServiceProto.DDRAIServiceCmd;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import com.allinpay.aipmis.allinpay.model.RequestData;
import com.allinpay.aipmis.allinpay.model.ResponseData;
import com.allinpay.aipmis.allinpay.service.MisPos;
import com.example.retailmachineclient.util.CardUtils;
import com.allinpay.manager.listener.IMessageListener;
import com.allinpay.manager.model.MessageBean;
import com.example.retailmachineclient.util.TaskUtils;
import com.example.retailmachineclient.util.ThreadPoolManager;
import com.example.retailmachineclient.util.TimeIntervalUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.RelativeLayout;

public class PayByCardActivity extends BaseActivity implements View.OnClickListener {
    public static final int Code_register_fail = 1;
    public static final int Code_consume_fail = 2;
    public static final int Code_consume_success = 3;
    public static final int Code_consume_cancel = 4;
    String currentLan = "";
    CardUtils cardUtils;
    //返回结果
    private ResponseData response;
    boolean isContinue = false;
    float totalPrice;
    TextView btChinese;
    TextView btEnglish;

    TextView btFrench;
    TextView tvTelephone;
    TextView tvTelephonetip;

    ImageView point_img;

    TextView point_pos = null;
    TextView choice_tip = null;
    TextView choice_tip1 = null;
    TextView choice_tip2 = null;
    TextView choice_tip3 = null;

    TextView edit_psd_tip = null;
    ImageView edit_psd = null;
    TextView post_card_tip = null;
    ImageView post_card = null;

    ImageView cancel_image = null;
    TextView cancel_image_tip = null;
    Context myContext;
    String payNum = "";
    String EndOutTradeNo = "";
    String payByCardResult = "";//交易返回的结果信息
    RelativeLayout layout_all;
    TcpAiClient tcpAiClient = null;
    //消息处理
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    timeRunnable = new Runnable() {
                        @Override
                        public void run() {
                            doSomething();
                        }
                    };
                    ThreadPoolManager.getInstance().executeRunable(timeRunnable);

                    break;

                default:
                    break;
            }
        }

        ;
    };

    public void doSomething() {
        isContinue = false;
        cardUtils = CardUtils.getInstance();
        //初始化 MisPos
        if (cardUtils.getMisPos() == null) {
            cardUtils.setMisPos(new MisPos(PayByCardActivity.this));
        }
        cardUtils.getMisPos().setOnMessageListener((IMessageListener) new MessageListenerImpl());
        //没有签到 执行签到
        if (!cardUtils.isRegister()) {
//            Logger.d("刷卡 开始签到:");
            isContinue = register(cardUtils.getMisPos());
            cardUtils.setRegister(isContinue);
        } else {
            isContinue = true;
        }
        if (!isContinue) {
            return;
        } else {
            cardUtils.setRegister(true);
        }
        delayChangeView(ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 消费
        consume(cardUtils.getMisPos());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        switch (msgEvent.getType()) {
            case MessageEvent.EventType_LOGIN_SUCCESS:
                break;
            case MessageEvent.EventType_Card_Start_Time:
                //90秒定时器
                if (msgEvent.getPosition() != 0) {
                    timeView.setVisibility(View.VISIBLE);
                    if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                        timeView.setVisibility(View.VISIBLE);
                        timeView.setText("" + msgEvent.getPosition() + "s " + PayByCardActivity.this.getString(R.string.back));
                    } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                        timeView.setText("" + msgEvent.getPosition() + "s " + PayByCardActivity.this.getString(R.string.en_back));
                    }
                } else {
                    delayChangeView(60);
                }
                break;
        }
    }

    public void setView() {
        choice_tip.setVisibility(View.INVISIBLE);
        choice_tip1.setVisibility(View.INVISIBLE);
        choice_tip2.setVisibility(View.INVISIBLE);
        choice_tip3.setVisibility(View.INVISIBLE);
        edit_psd_tip.setVisibility(View.INVISIBLE);
        edit_psd.setVisibility(View.INVISIBLE);
        post_card_tip.setVisibility(View.INVISIBLE);
        post_card.setVisibility(View.INVISIBLE);

        cancel_image.setVisibility(View.VISIBLE);
        cancel_image_tip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        byte data;
        Runnable runnable;
//        TaskUtils.sendPagePoint(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPayByCardPage.getNumber());
        switch (view.getId()) {

            case R.id.layout_all:

                break;
            case R.id.point_pos:
                //测试撤销
//                if (cardUtils != null) {
//                    cancelOrder(cardUtils.getMisPos());
//                }
                break;
            case R.id.time:
                setView();
                break;
            case R.id.register:
                timeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        register(cardUtils.getMisPos());
                    }
                };
                ThreadPoolManager.getInstance().executeRunable(timeRunnable);
                break;
            case R.id.consume:
                timeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        consume(cardUtils.getMisPos());
                    }
                };
                ThreadPoolManager.getInstance().executeRunable(timeRunnable);

                break;

            case R.id.btEnglish:
                changeLanguage(LanguageType.ENGLISH.getLanguage());
                break;
            case R.id.btFrench:
                break;
            case R.id.btChinese:
                changeLanguage(LanguageType.CHINESE.getLanguage());
                break;
        }
    }

    private void changeLanguage(String language) {
        if (language.equals(currentLan)) {
            return;
        }
        SpUtil.getInstance(this).putString(SpUtil.LANGUAGE, language);
        setBtnBg();
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

    String phone = "";

    public void changeLanguage() {
        String current = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        currentLan = current;
        btChinese.setText(PayByCardActivity.this.getString(R.string.chinese_text));
        btEnglish.setText(PayByCardActivity.this.getString(R.string.en_english_text));
        btFrench.setText(PayByCardActivity.this.getString(R.string.en_french_text));
        if (current.equals(LanguageType.CHINESE.getLanguage())) {
            tvTelephonetip.setText(PayByCardActivity.this.getString(R.string.urgent_tel_text));
            point_pos.setText(myContext.getString(R.string.point_pos));
            choice_tip.setText(myContext.getString(R.string.choice_tip));
            choice_tip1.setText(myContext.getString(R.string.choice_tip1));
            choice_tip2.setText(myContext.getString(R.string.choice_tip2));
            choice_tip3.setText(myContext.getString(R.string.choice_tip3));
            edit_psd_tip.setText(myContext.getString(R.string.edit_psd_tip));
            post_card_tip.setText(myContext.getString(R.string.post_card_tip));
            cancel_image_tip.setText(myContext.getString(R.string.cancel_image_tip));
            edit_psd.setImageResource(R.mipmap.pos_edit_psd_cn);
            post_card.setImageResource(R.mipmap.pos_post_card_cn);
            cancel_image.setImageResource(R.mipmap.cn_pos_cancel);

        } else if (current.equals(LanguageType.ENGLISH.getLanguage())) {
            tvTelephonetip.setText(PayByCardActivity.this.getString(R.string.en_urgent_tel_text));
            point_pos.setText(myContext.getString(R.string.en_point_pos));
            choice_tip.setText(myContext.getString(R.string.en_choice_tip));
            choice_tip1.setText(myContext.getString(R.string.en_choice_tip1));
            choice_tip2.setText(myContext.getString(R.string.en_choice_tip2));
            choice_tip3.setText(myContext.getString(R.string.en_choice_tip3));
            edit_psd_tip.setText(myContext.getString(R.string.en_edit_psd_tip));
            post_card_tip.setText(myContext.getString(R.string.en_post_card_tip));
            cancel_image_tip.setText(myContext.getString(R.string.en_cancel_image_tip));
            edit_psd.setImageResource(R.mipmap.pos_edit_psd_en);
            post_card.setImageResource(R.mipmap.pos_post_card_en);
            cancel_image.setImageResource(R.mipmap.en_pos_cancel);
        } else if (current.equals(LanguageType.FRENCH.getLanguage())) {

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_layout_paybycard;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
    }

    @Override
    protected void initData() {
        myContext = PayByCardActivity.this;
        initViews();
        initDatas();
    }

    private void initViews() {
//        setLanguage(1);
        //初始化ViewPager
    }

    Runnable timeRunnable = null;
    boolean isContinueTime = true;
    boolean isStopTime = false;
    public TextView timeView;

    public void delayChangeView(final int number) {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                int num = number;
                while (isContinueTime) {
                    if (!isStopTime && num > -1) {
                        num = num - 1;
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_Card_Start_Time, num));
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

    public void initMyViews() {
        timeView = (TextView) findViewById(R.id.time);
        btChinese = (TextView) findViewById(R.id.btChinese);
        btEnglish = (TextView) findViewById(R.id.btEnglish);
        btFrench = (TextView) findViewById(R.id.btFrench);
        layout_all = (RelativeLayout) findViewById(R.id.layout_all);
        point_img = (ImageView) findViewById(R.id.point_img);
        point_pos = (TextView) findViewById(R.id.point_pos);
        choice_tip = (TextView) findViewById(R.id.choice_tip);
        choice_tip1 = (TextView) findViewById(R.id.choice_tip1);
        choice_tip2 = (TextView) findViewById(R.id.choice_tip2);
        choice_tip3 = (TextView) findViewById(R.id.choice_tip3);
        edit_psd_tip = (TextView) findViewById(R.id.edit_psd_tip);
        edit_psd = (ImageView) findViewById(R.id.edit_psd);
        post_card_tip = (TextView) findViewById(R.id.post_card_tip);
        post_card = (ImageView) findViewById(R.id.post_card);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.btChinese).setOnClickListener(this);
        findViewById(R.id.btEnglish).setOnClickListener(this);
        timeView.setOnClickListener(this);
        findViewById(R.id.consume).setOnClickListener(this);
        tvTelephone = (TextView) findViewById(R.id.tvTelephone);
        tvTelephonetip = (TextView) findViewById(R.id.tvTelephonetip);
        cancel_image = (ImageView) findViewById(R.id.cancel_image);
        cancel_image_tip = (TextView) findViewById(R.id.cancel_image_tip);
        layout_all.setOnClickListener(this);
    }

    CardPayMsgModel oldOrder = null;

    private void initDatas() {

        initMyViews();
        tcpAiClient = TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance());
        TaskUtils.sendPagePoint(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPayByCardPage.getNumber());
        TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enPayByCardPage.getNumber();
        currentLan = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        phone = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.PHONE);
        tvTelephone.setText(phone);
        List<CardPayMsgModel> list = LitePal.findAll(CardPayMsgModel.class);
        if (list != null && list.size() > 0) {
            Logger.d("刷卡 消费记录数量:" + list.size());
            oldOrder = list.get(0);
        } else {
            Logger.d("刷卡 消费记录数量:null");
        }
        totalPrice = getIntent().getFloatExtra("totalPrice", 0f);
        EndOutTradeNo = getIntent().getStringExtra("EndOutTradeNo");
        cardUtils = CardUtils.getInstance();
        if (cardUtils.getMisPos() == null) {
            cardUtils.setMisPos(new MisPos(PayByCardActivity.this));
        }
        cardUtils.getMisPos().setOnMessageListener((IMessageListener) new MessageListenerImpl());
        handler.sendEmptyMessageDelayed(1, 2000);//
        Glide.with(this).load(R.drawable.pos_position).into(point_img);
        setBtnBg();
    }

    public void setReturnData(int resultCode) {
        isContinueTime = false;
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putInt("result", resultCode);
        bundle.putString("payByCardResult", payByCardResult);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    public boolean register(MisPos compos) {
        RequestData reqData = buildRequestData();
        reqData.PutValue("TransType", "1");
        response = new ResponseData();
        compos.TransProcess(reqData, response);
        return handleRegisterResponseData();
    }

    public boolean cancelOrder(MisPos compos) {
        RequestData reqData = new RequestData();
        reqData.PutValue("CardType", "01");
        reqData.PutValue("TransType", "3");
        reqData.PutValue("Amount", oldOrder.getPayAmount());
        reqData.PutValue("OldTraceNumber", oldOrder.getPayNum());
        response = new ResponseData();
        compos.TransProcess(reqData, response);
        return handleCancelResponseData();
    }

    private boolean handleCancelResponseData() {
        Logger.d("刷卡all1 取消 RejCode:" + response.GetValue("RejCode"));//00
        Logger.d("刷卡all1 取消RejCodeExplain:" + response.GetValue("RejCodeExplain"));//交易成功
        Logger.d("刷卡all1 取消all:" + response.getResponse().values().toString());//20210519
        return true;
    }

    private boolean handleRegisterResponseData() {
        Logger.d("刷卡all1 all:" + response.getResponse().values().toString());//20210519
        if (!response.GetValue("RejCode").equals("00")) {
            setReturnData(PayByCardActivity.Code_register_fail);
            return false;
        } else {
            return true;
        }
    }

    //初始化requestData
    private RequestData buildRequestData() {
        RequestData reqData = new RequestData();
        reqData.PutValue("CardType", "01");
        reqData.PutValue("Amount", "");
        reqData.PutValue("OldTraceNumber", "");
        reqData.PutValue("HostSerialNumber", "");
        //日期输入格式MMDD如1227
        reqData.PutValue("TransDate", "");
        return reqData;
    }

    //处理response
    private void handleResponseData() {
        payNum = response.GetValue("PosTraceNumber");
        Logger.d("刷卡all2 all:" + response.getResponse().values().toString());//20210519
        if (response.GetValue("RejCode").equals("00")) {
            saveData("2", response.getResponse().values().toString(), response.GetValue("TransDate"), response.GetValue("CardNumber"), response.GetValue("OrderNumber"));
            payByCardResult = response.getResponse().values().toString();
            setReturnData(PayByCardActivity.Code_consume_success);
        } else if (
                response.GetValue("RejCode").equals("68")
                        || response.GetValue("RejCode").equals("79")
                        || response.GetValue("RejCode").equals("-105")
        ) {
            //交易取消
            setReturnData(PayByCardActivity.Code_consume_cancel);
        }
        if (response.GetValue("RejCode").equals("77")) {
            //请重新签到
            if (cardUtils != null) {
                cardUtils.setRegister(false);
            }
            doSomething();
        } else {
            //交易失败
            setReturnData(PayByCardActivity.Code_consume_fail);
        }
    }

    public void consume(MisPos compos) {
        RequestData reqData = buildRequestData();
        //业务类型
        reqData.PutValue("CardType", "01");//交易类型
        reqData.PutValue("TransType", "2");//交易类型
        reqData.PutValue("Amount", "" + totalPrice);
        reqData.PutValue("OrderNumber", EndOutTradeNo);
        response = new ResponseData();
        compos.TransProcess(reqData, response);
        handleResponseData();
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setReturnData(PayByCardActivity.Code_consume_cancel);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void saveData(String type, String msg, String transDate, String cardNum, String orderNum) {
        CardPayMsgModel cardPayMsgModel = new CardPayMsgModel();
        cardPayMsgModel.setMsgType(type);
        cardPayMsgModel.setPayAmount("" + totalPrice);
        cardPayMsgModel.setIsDeleted(0);
        cardPayMsgModel.setCardNum(cardNum);
        cardPayMsgModel.setTimestamp(System.currentTimeMillis());
        cardPayMsgModel.setOrderNum(orderNum);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        Logger.d("刷卡all2 simpleDateFormat:" + simpleDateFormat.format(date));//00
        cardPayMsgModel.setTransDate("" + simpleDateFormat.format(date));
        cardPayMsgModel.setPayNum(payNum);
        cardPayMsgModel.setMessage(msg);
        List<CardPayMsgModel> list = new ArrayList<>();
        list.add(cardPayMsgModel);
        LitePal.saveAll(list);
    }

    //自定义消息回调函数
    class MessageListenerImpl implements IMessageListener {

        private MessageBean msg;

        public MessageBean getMsg() {
            return msg;
        }

        public void setMsg(MessageBean msg) {
            this.msg = msg;
        }

        @Override
        public void showMessage() {

            new Thread(new Runnable() {

                @Override
                public void run() {
                    //非UI线程调用UI线程，消息队列初始化
                    Looper.prepare();
                    //获取消息对象
                    Message message = handler.obtainMessage();
                    message.obj = msg.info;
                    handler.handleMessage(message);
                    //消息队列poll信息并执行
                    Looper.loop();
//                    Logger.d("刷卡消息 showMessage:" + msg.type);
                }
            }).start();
        }
    }
}