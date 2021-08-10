package com.example.retailmachineclient.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.allinpay.aipmis.allinpay.model.RequestData;
import com.allinpay.aipmis.allinpay.model.ResponseData;
import com.allinpay.aipmis.allinpay.service.MisPos;
import com.allinpay.manager.listener.IMessageListener;
import com.allinpay.manager.model.MessageBean;
import com.bumptech.glide.Glide;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.model.CardPayMsgModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.util.CardUtils;
import com.example.retailmachineclient.util.CloseBarUtil;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.TaskUtils;
import com.example.retailmachineclient.util.ThreadPoolManager;
import com.example.retailmachineclient.view.TestListviewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import android.widget.ListView;

import com.example.retailmachineclient.util.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.ContentValues;

import android.widget.Toast;

import DDRAIServiceProto.DDRAIServiceCmd;

public class PayByCardTestActivity extends BaseActivity implements View.OnClickListener {
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
    ListView listview;
    ListView listviewDeleted;
    TestListviewAdapter adapter;
    TestListviewAdapter adapterDeleted;
    TextView result;
    Spinner spinner;

    long nowTime = 0;
    long beforeTime = 0;

    String queryStr = "";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        switch (msgEvent.getType()) {

            case MessageEvent.EventType_CHANGE_SPPINER_ITEM:
                Logger.e("执行MessageEvent.EventType_CHANGE_SPPINER_ITEM");
                changeQueryStr(spinner.getSelectedItem().toString());
                break;

            case MessageEvent.EventType_Refresh_listview:
                list = (List<CardPayMsgModel>) msgEvent.getTag();
                listDeleted = (List<CardPayMsgModel>) msgEvent.getTagHelper();
                //更新list
                if (list == null) {
                    list = new ArrayList<CardPayMsgModel>();
                }
                if (listDeleted == null) {
                    listDeleted = new ArrayList<CardPayMsgModel>();
                }
                adapter.update(list);
                adapterDeleted.update(listDeleted);
                break;

            case MessageEvent.refund_successed:
                String payNumdel = (String) msgEvent.getTag();
                Toast.makeText(BaseApplication.getContext(), "取消订单成功", Toast.LENGTH_SHORT).show();

                ContentValues values = new ContentValues();
                values.put("isDeleted", "1");
                LitePal.updateAll(CardPayMsgModel.class, values, "payNum = ?", payNumdel);
                result.setText("交易退款成功 ID = " + payNumdel);
                Logger.e("执行 交易退款成功");
                changeQueryStr(spinner.getSelectedItem().toString());
                break;

            case MessageEvent.refund_fail:
                Toast.makeText(BaseApplication.getContext(), "取消订单失败", Toast.LENGTH_SHORT).show();
                result.setText("交易退款失败");
                break;

            case MessageEvent.EventType_LOGIN_SUCCESS:
                break;
            case MessageEvent.EventType_Card_Start_Time:
                //90秒定时器
                if (msgEvent.getPosition() != 0) {
                    timeView.setVisibility(View.VISIBLE);
                    if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                        timeView.setVisibility(View.VISIBLE);
                        timeView.setText("" + msgEvent.getPosition() + "s " + PayByCardTestActivity.this.getString(R.string.back));
                    } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                        timeView.setText("" + msgEvent.getPosition() + "s " + PayByCardTestActivity.this.getString(R.string.en_back));
                    }
                } else {
                    delayChangeView(60);
                }
                break;
        }
    }

    //消息处理
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Logger.d("刷卡消息 handleMessage:" + msg.what);
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
        //初始化 CardUtils
        cardUtils = CardUtils.getInstance();
        //初始化 MisPos
        if (cardUtils.getMisPos() == null) {
            cardUtils.setMisPos(new MisPos(PayByCardTestActivity.this));
        }
        cardUtils.getMisPos().setOnMessageListener((IMessageListener) new MessageListenerImpl());
        //没有签到 执行签到
        if (!cardUtils.isRegister()) {
            Logger.d("刷卡 开始签到:");
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

    }


    public void setView() {
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

        switch (view.getId()) {

            case R.id.point_pos:
                //测试撤销
                if (cardUtils != null) {
//                    cancelOrder(cardUtils.getMisPos());
                }

                break;
            case R.id.time:
                finish();
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
                Logger.d("刷卡 修改语言:btEnglish：" + currentLan);
                changeLanguage(LanguageType.ENGLISH.getLanguage());
                break;
            case R.id.btFrench:
                break;
            case R.id.btChinese:
                Logger.d("刷卡 修改语言:btChinese：" + currentLan);
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
        current = "ch";
//        Logger.d("刷卡 setBtnBg：" + current + ",currentLan:" + currentLan);
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
        btChinese.setText(PayByCardTestActivity.this.getString(R.string.chinese_text));
        btEnglish.setText(PayByCardTestActivity.this.getString(R.string.en_english_text));
        btFrench.setText(PayByCardTestActivity.this.getString(R.string.en_french_text));
        if (current.equals(LanguageType.CHINESE.getLanguage())) {
            tvTelephonetip.setText(PayByCardTestActivity.this.getString(R.string.urgent_tel_text));
            point_pos.setText(myContext.getString(R.string.point_pos));

            edit_psd_tip.setText(myContext.getString(R.string.edit_psd_tip));
            post_card_tip.setText(myContext.getString(R.string.post_card_tip));
            cancel_image_tip.setText(myContext.getString(R.string.cancel_image_tip));
            edit_psd.setImageResource(R.mipmap.pos_edit_psd_cn);
            post_card.setImageResource(R.mipmap.pos_post_card_cn);
            cancel_image.setImageResource(R.mipmap.cn_pos_cancel);

        } else if (current.equals(LanguageType.ENGLISH.getLanguage())) {
            tvTelephonetip.setText(PayByCardTestActivity.this.getString(R.string.en_urgent_tel_text));
            point_pos.setText(myContext.getString(R.string.en_point_pos));
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
        return R.layout.pay_by_card_test;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
    }

    @Override
    protected void initData() {
        myContext = PayByCardTestActivity.this;
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
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MessageEvent event = new MessageEvent(MessageEvent.EventType_CHANGE_SPPINER_ITEM);
                event.setType(MessageEvent.EventType_CHANGE_SPPINER_ITEM);
                EventBus.getDefault().post(event);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        result = (TextView) findViewById(R.id.result);
        listview = (ListView) findViewById(R.id.list);
        listviewDeleted = (ListView) findViewById(R.id.list_deleted);
        timeView = (TextView) findViewById(R.id.time);
        btChinese = (TextView) findViewById(R.id.btChinese);
        btEnglish = (TextView) findViewById(R.id.btEnglish);
        btFrench = (TextView) findViewById(R.id.btFrench);

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
        point_pos.setOnClickListener(this);
        findViewById(R.id.consume).setOnClickListener(this);
        tvTelephone = (TextView) findViewById(R.id.tvTelephone);
        tvTelephonetip = (TextView) findViewById(R.id.tvTelephonetip);
        cancel_image = (ImageView) findViewById(R.id.cancel_image);
        cancel_image_tip = (TextView) findViewById(R.id.cancel_image_tip);
    }


    private void showNormalDialog(CardPayMsgModel cardPayMsgModel) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(PayByCardTestActivity.this);
//        normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("你是否继续退款?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        normalDialog.dismiss();
                        timeRunnable = new Runnable() {
                            @Override
                            public void run() {
                                cancelOrder(cardUtils.getMisPos(), cardPayMsgModel);
                            }
                        };
                        ThreadPoolManager.getInstance().executeRunable(timeRunnable);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        normalDialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }

    CardPayMsgModel oldOrder = null;
    List<CardPayMsgModel> list = null;
    List<CardPayMsgModel> listDeleted = null;

    public long getBeforeTime(String queryStr) {
//        <item >1个月</item>
//        <item >3个月</item>
//        <item >6个月</item>
//        <item >一年</item>
//        <item >所有</item>
        long dataTime = 0;
        switch (queryStr) {
            case "1个月":
                dataTime = DateUtils.getBeforeTime(-1);
                break;

            case "3个月":
                dataTime = DateUtils.getBeforeTime(-3);
                break;

            case "6个月":
                dataTime = DateUtils.getBeforeTime(-6);
                break;

            case "一年":
                dataTime = DateUtils.getBeforeTime(-12);
                break;
            default:
                dataTime = 0;
                break;
        }
        return dataTime;
    }


    Runnable runnable;

    public void changeQueryStr(final String newStr) {
        runnable = new Runnable() {
            @Override
            public void run() {
                List<CardPayMsgModel> listCopy;
                List<CardPayMsgModel> listDeletedCopy;
                if (newStr.equals("所有")) {
                    listCopy = LitePal.where("msgType = ? and isDeleted = ? order by timestamp desc", "2", "0").find(CardPayMsgModel.class);
                    listDeletedCopy = LitePal.where("msgType = ? and isDeleted = ?  order by timestamp desc", "2", "1").find(CardPayMsgModel.class);
                } else {
                    beforeTime = getBeforeTime(newStr);
                    Logger.e("刷卡 查询开始时间1:before=" + beforeTime + "，now=" + System.currentTimeMillis());
                    //listCopy = LitePal.where("msgType = ? and isDeleted = ? and timestamp > ? and timestamp < ? order by timestamp desc", "2", "0", ""+beforeTime, ""+System.currentTimeMillis()).find(CardPayMsgModel.class);
                    //listDeletedCopy = LitePal.where("msgType = ? and isDeleted = ? and timestamp > ? and timestamp < ? order by timestamp desc", "2", "1", ""+beforeTime, ""+System.currentTimeMillis()).find(CardPayMsgModel.class);

                    listCopy = LitePal.where("msgType = ? and isDeleted = ? and timestamp > ? order by timestamp desc", "2", "0", "" + beforeTime).find(CardPayMsgModel.class);
                    listDeletedCopy = LitePal.where("msgType = ? and isDeleted = ? and timestamp > ? order by timestamp desc", "2", "1", "" + beforeTime).find(CardPayMsgModel.class);
                }

                MessageEvent event = new MessageEvent(MessageEvent.EventType_Refresh_listview);
                event.setType(MessageEvent.EventType_Refresh_listview);
                event.setTag(listCopy);
                event.setTagHelper(listDeletedCopy);
                EventBus.getDefault().post(event);
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    private void initDatas() {
        initMyViews();
        currentLan = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        currentLan = "ch";
        phone = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.PHONE);
//        phone = "123456789";//测试
        tvTelephone.setText(phone);
//        list = LitePal.findAll(CardPayMsgModel.class);

        CardPayMsgModel model1 = new CardPayMsgModel();
        model1.setPayNum("003");
        model1.setOrderNum("003C233");
        model1.setTransDate("20210504");
        model1.setCardNum("555555555555");
        model1.setIsDeleted(0);
        model1.setTimestamp(1625352691433L);
        model1.setMsgType("2");
//
        CardPayMsgModel model2 = new CardPayMsgModel();
        model2.setPayNum("004");
        model2.setTransDate("20210504");
        model2.setCardNum("555555555555");
        model2.setIsDeleted(1);
        model2.setOrderNum("004C21");
        model2.setTimestamp(1625352691433L);
        model2.setMsgType("2");
        List<CardPayMsgModel> modellist = new ArrayList<CardPayMsgModel>();
        modellist.add(model1);
        modellist.add(model2);
        LitePal.saveAll(modellist);

        queryStr = spinner.getSelectedItem().toString();
        if (queryStr.equals("所有")) {
            list = LitePal.where("msgType = ? and isDeleted = ? order by timestamp desc", "2", "0").find(CardPayMsgModel.class);
            listDeleted = LitePal.where("msgType = ? and isDeleted = ?  order by timestamp desc", "2", "1").find(CardPayMsgModel.class);
        } else {
            beforeTime = getBeforeTime(spinner.getSelectedItem().toString());
            Logger.e("刷卡 查询开始时间2:before=" + beforeTime + "，now=" + System.currentTimeMillis());
            list = LitePal.where("msgType = ? and isDeleted = ? and timestamp > ? order by timestamp desc", "2", "0", "" + beforeTime).find(CardPayMsgModel.class);
            listDeleted = LitePal.where("msgType = ? and isDeleted = ? and timestamp > ? order by timestamp desc", "2", "1", "" + beforeTime).find(CardPayMsgModel.class);
        }

        if (list == null) {
            Logger.d("刷卡 消费记录数量:null");
            list = new ArrayList<CardPayMsgModel>();
        } else {
            Logger.d("刷卡 消费记录数量:" + list.size());
        }

        adapter = new TestListviewAdapter(PayByCardTestActivity.this, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showNormalDialog(list.get(position));
            }
        });

        if (listDeleted == null) {
            Logger.d("刷卡 已退款消费记录数量:null");
            listDeleted = new ArrayList<CardPayMsgModel>();
        } else {
            Logger.d("刷卡 已退款消费记录数量:" + listDeleted.size());
        }
        adapterDeleted = new TestListviewAdapter(PayByCardTestActivity.this, listDeleted);
        listviewDeleted.setAdapter(adapterDeleted);

        totalPrice = getIntent().getFloatExtra("totalPrice", 0f);
        EndOutTradeNo = getIntent().getStringExtra("EndOutTradeNo");
        Logger.d("刷卡totalPrice:" + totalPrice);
        cardUtils = CardUtils.getInstance();
        if (cardUtils.getMisPos() == null) {
            cardUtils.setMisPos(new MisPos(PayByCardTestActivity.this));
        }
        cardUtils.getMisPos().setOnMessageListener((IMessageListener) new MessageListenerImpl());
//        handler.sendEmptyMessageDelayed(1, 2000);//

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


    public boolean cancelOrder(MisPos compos, CardPayMsgModel oldOrder) {
        RequestData reqData = new RequestData();
        reqData.PutValue("CardType", "01");
        reqData.PutValue("TransType", "3");
        reqData.PutValue("Amount", oldOrder.getPayAmount());
        reqData.PutValue("OldTraceNumber", oldOrder.getPayNum());
        response = new ResponseData();
        compos.TransProcess(reqData, response);
        return handleCancelResponseData(oldOrder);
    }

    private boolean handleCancelResponseData(CardPayMsgModel oldOrder) {
//        saveData("register", response.getResponse().values().toString());
        Logger.d("刷卡all1 取消 RejCode:" + response.GetValue("RejCode"));//00
        Logger.d("刷卡all1 取消RejCodeExplain:" + response.GetValue("RejCodeExplain"));//交易成功
        Logger.d("刷卡all1 取消all:" + response.getResponse().values().toString());//20210519
//        Toast.makeText(PayByCardTestActivity.this,"data="+response.GetValue("RejCodeExplain"),Toast.LENGTH_SHORT).show();
//        if (!response.GetValue("RejCode").equals("00")) {
//            setReturnData(PayByCardTestActivity.Code_register_fail);
//        }
        if (response.GetValue("RejCode").equals("00")) {
            //成功
            EventBus.getDefault().post(new MessageEvent(MessageEvent.refund_successed, oldOrder.getPayNum()));
        } else {
            //失败
            EventBus.getDefault().post(new MessageEvent(MessageEvent.refund_fail));
        }
        return true;
    }

    private boolean handleRegisterResponseData() {
//        saveData("1", response.getResponse().values().toString());
        Logger.d("刷卡all1 RejCode:" + response.GetValue("RejCode"));//00
        Logger.d("刷卡all1 RejCodeExplain:" + response.GetValue("RejCodeExplain"));//交易成功
//        Logger.d("刷卡all1 all:" + response.getResponse().values().toString());//20210519
        if (!response.GetValue("RejCode").equals("00")) {
            setReturnData(PayByCardTestActivity.Code_register_fail);
            return false;
        } else {
            return true;
        }

    }

    //初始化requestData
    private RequestData buildRequestData() {
        RequestData reqData = new RequestData();
        reqData.PutValue("CardType", "01");
//		reqData.PutValue("appname", "DEBUG");
        reqData.PutValue("Amount", "");
        reqData.PutValue("OldTraceNumber", "");
        reqData.PutValue("HostSerialNumber", "");
        //日期输入格式MMDD如1227
        reqData.PutValue("TransDate", "");
        return reqData;
    }

    //处理response
    private void handleResponseData() {
        payNum = response.GetValue("TransId");
        saveData("2", response.getResponse().values().toString());
        Logger.d("刷卡all2 RejCode:" + response.GetValue("RejCode"));//00
        Logger.d("刷卡all2 RejCode:" + response.GetValue("RejCode"));//00
        Logger.d("刷卡all2 RejCodeExplain:" + response.GetValue("RejCodeExplain"));//交易成功
        Logger.d("刷卡all2 all:" + response.getResponse().values().toString());//20210519
        if (response.GetValue("RejCode").equals("00")) {
            payByCardResult = response.getResponse().values().toString();
            setReturnData(PayByCardTestActivity.Code_consume_success);

        } else if (
                response.GetValue("RejCode").equals("68")
                        || response.GetValue("RejCode").equals("79")
                        || response.GetValue("RejCode").equals("-105")
        ) {
            //交易取消
            setReturnData(PayByCardTestActivity.Code_consume_cancel);
        }
        if (response.GetValue("RejCode").equals("77")) {
            //请重新签到
            if (cardUtils != null) {
                cardUtils.setRegister(false);
            }
            doSomething();
        } else {
            //交易失败
            setReturnData(PayByCardTestActivity.Code_consume_fail);
        }
    }

    public void consume(MisPos compos) {

        RequestData reqData = buildRequestData();
        //业务类型
        reqData.PutValue("CardType", "01");//交易类型
        reqData.PutValue("TransType", "2");//交易类型
        reqData.PutValue("Amount", "" + totalPrice);

        // 订单编号 EndOutTradeNo
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
            setReturnData(PayByCardTestActivity.Code_consume_cancel);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void saveData(String type, String msg) {
        CardPayMsgModel cardPayMsgModel = new CardPayMsgModel();
        cardPayMsgModel.setMsgType(type);
        cardPayMsgModel.setPayAmount("" + totalPrice);
//        cardPayMsgModel.setDeleted(false);
        cardPayMsgModel.setTimestamp(System.currentTimeMillis());
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
                    Logger.d("刷卡消息 showMessage:" + msg.type);
                }
            }).start();
        }
    }
}