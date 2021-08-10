package com.example.retailmachineclient.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.allinpay.aipmis.allinpay.model.RequestData;
import com.allinpay.aipmis.allinpay.model.ResponseData;
import com.allinpay.aipmis.allinpay.service.MisPos;
import com.easysocket.EasySocket;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.http.HttpManager;
import com.example.retailmachineclient.model.GetGoodsInfoRspModel;
import com.example.retailmachineclient.model.GoodInfoModel;
import com.example.retailmachineclient.model.GoodMsgModel;
import com.example.retailmachineclient.model.LoginRspModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.model.req.CloseOrderReqModel;
import com.example.retailmachineclient.model.rsp.TradeSuccessRspModel;
import com.example.retailmachineclient.protocobuf.CmdSchedule;
import com.example.retailmachineclient.protocobuf.dispatcher.ClientMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.util.CardUtils;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.LogcatHelper;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.NetUtils;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.TaskUtils;
import com.example.retailmachineclient.util.ThreadPoolManager;
import com.example.retailmachineclient.util.TimeIntervalUtils;
import com.example.retailmachineclient.util.Utils;
import com.example.retailmachineclient.view.CustomDialog;
import com.example.retailmachineclient.view.GridViewAdapter;
import com.example.retailmachineclient.view.NormalRecyclerViewAdapter;
import com.example.retailmachineclient.view.ViewPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import DDRAIServiceProto.DDRAIServiceCmd;

import androidx.viewpager.widget.ViewPager;

import android.widget.RelativeLayout;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import static com.example.retailmachineclient.http.Api.APP_LOGIN_DOMAIN_NAME;
import static com.example.retailmachineclient.model.MessageEvent.EventType_ADD_GOODS_IN_DETAIL;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.InetAddress;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    TextView btEnglish;
    TextView btFrench;
    TextView tvTelephone;
    TextView btChinese;
    TextView tv_empty;
    ViewPager view_pager;
    ListView recyclerShopping;
    TextView tvCommodityName;
    TextView resultPrice;
    TextView emptyCartView;
    TextView back;
    TextView tvTelephoneTip;
    TextView tvCardTitle;
    TextView number;
    TextView result;
    TextView goto_pay;
    RelativeLayout LayoutAll;

    public static int item_grid_num = 8;//每一页中GridView中item的数量
    public static int number_columns = 4;//gridview一行展示的数目
    private ViewPagerAdapter mAdapter;
    private List<GoodInfoModel> dataList;
    private List<GridView> gridList = new ArrayList<>();
    private List<View> dots = new ArrayList<View>();
    List<GoodInfoModel> shoppingCarDataList = new ArrayList<GoodInfoModel>();
    NormalRecyclerViewAdapter shoppingCarAdapter;
    int oldPageIndex = 0;
    int currentPage = 0;
    CustomDialog.Builder builder;
    Dialog addDialog = null;
    String salesId = "";
    String currentLan = "";
    String phone = "";
    float totalPrice;
    Runnable runnable = null;
    boolean isContinue = true;
    List<GoodInfoModel> requestDataList = new ArrayList<GoodInfoModel>();
    List<GoodInfoModel> requestDataListStart = new ArrayList<GoodInfoModel>();
    List<GoodInfoModel> requestNumDataList = new ArrayList<GoodInfoModel>();
    CardUtils cardUtils;

    long backTimeOut = 2 * 60 * 1000;//2分钟
    public static long lastTouchTime = 0;
    long startAndHaveGood = 0;
    final long Clear_time_out = 2 * 60 * 1000;
    boolean isPoint = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
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
            case MessageEvent.EventType_ADD_GOODS:
                lastTouchTime = System.currentTimeMillis();
                goodPosition = msgEvent.getPageIndex() * 8 + msgEvent.getPagePosition();
                goodMsgModel = dataList.get(goodPosition);
                if (goodMsgModel.getGoodsInventory() == 0) {
                    if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                        Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.hasnot_goods), Toast.LENGTH_SHORT).show();
                    } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                        Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_hasnot_goods), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (shoppingCarDataList.size() >= ConstantUtils.NUMBER_BUY) {
                    String str = "";
                    if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                        str = String.format(getResources().getString(R.string.has_three_good_text), ConstantUtils.NUMBER_BUY);
                    } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                        str = String.format(getResources().getString(R.string.en_has_three_good_text), ConstantUtils.NUMBER_BUY);
                    }
                    Toast.makeText(BaseApplication.getContext(), str, Toast.LENGTH_SHORT).show();
                    return;
                }
                shoppingCarDataList.add(goodMsgModel);
                shoppingCarAdapter.update(shoppingCarDataList);
                calculatePrice(shoppingCarDataList);
                startAndHaveGood = System.currentTimeMillis();
                break;

            case MessageEvent.EventType_ADD_GOODS_IN_DETAIL:
                lastTouchTime = System.currentTimeMillis();
                goodPosition = msgEvent.getPageIndex() * 8 + msgEvent.getPagePosition();
                goodMsgModel = dataList.get(goodPosition);
                if (goodMsgModel.getGoodsInventory() == 0) {
                    if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                        Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.hasnot_goods), Toast.LENGTH_SHORT).show();
                    } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                        Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_hasnot_goods), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (shoppingCarDataList.size() >= ConstantUtils.NUMBER_BUY) {
                    String str = "";
                    if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                        str = String.format(getResources().getString(R.string.has_three_good_text), ConstantUtils.NUMBER_BUY);
                    } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                        str = String.format(getResources().getString(R.string.en_has_three_good_text), ConstantUtils.NUMBER_BUY);
                    }
                    Toast.makeText(BaseApplication.getContext(), str, Toast.LENGTH_SHORT).show();
                    return;
                }
                shoppingCarDataList.add(goodMsgModel);
                Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.add_success_text), Toast.LENGTH_SHORT).show();
                shoppingCarAdapter.update(shoppingCarDataList);
                calculatePrice(shoppingCarDataList);
                addDialog.dismiss();

                startAndHaveGood = System.currentTimeMillis();
                break;
            case MessageEvent.EventType_DELETE_SUCCESS:
                lastTouchTime = System.currentTimeMillis();
                tvTelephone.setText(phone);
                int deletePosition = msgEvent.getPosition();
                shoppingCarDataList.remove(deletePosition);
                shoppingCarAdapter.update(shoppingCarDataList);
                calculatePrice(shoppingCarDataList);
//                Toast.makeText(BaseApplication.getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                break;

            case MessageEvent.EventType_QUERY_GOODS_SUCCESS:
                phone = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.PHONE);
                requestDataList = (List<GoodInfoModel>) msgEvent.getTag();
                if (requestDataList != null && requestDataList.size() > 0) {
                    tv_empty.setVisibility(View.GONE);
                    view_pager.setVisibility(View.VISIBLE);

                    dataList.clear();
                    updateListView();
                    dataList.addAll(requestDataList);
                    updateListView();
                }
                break;
            case MessageEvent.EventType_QUERY_GOODS_FAIL:

                break;
            case MessageEvent.EventType_QUERY_GOODS_NUM_SUCCESS:
                tvTelephone.setText(phone);
                Logger.e("执行数据比较操作");
                break;
            case MessageEvent.EventType_QUERY_GOODS_NUM_FAIL:
                break;
            case MessageEvent.EventType_QUERY_GOODS_NUM_OPERATE:
                //执行数据比较操作后返回
//                Logger.e("执行数据比较操作后返回");
                if ((boolean) msgEvent.getTag()) {
//                    Logger.e("执行数据比较操作后返回 有改动");
                    dataList.clear();
                    updateListView();

                    dataList.addAll(requestDataList);
                    updateListView();
                } else {
//                    LogUtil.e("执行数据比较操作后返回 没有改动");
                }
                break;

            case MessageEvent.EventType_LOGIN_AI_SUCCESS:
                //登录成功 获取机器id
                Logger.e("接口 登录ai成功 mainactivity");
                if (tcpAiClient != null) {
                    tcpAiClient.setLand(true);
                }

                break;
            case MessageEvent.EventType_LOGIN_AI_FAIL:
                Logger.e("接口 登录ai失败 mainactivity");
                if (tcpAiClient != null) {
                    tcpAiClient.setLand(false);
                }
                break;
            case MessageEvent.EventType_TCP_CONNECTED:
                Logger.e("接口 tcp连接成功 mainactivity");
                //登录
                break;
            case MessageEvent.EventType_GET_GetRobotID:

                break;

            case MessageEvent.EventType_QUERY_NET_ERROR:
                isNetError = true;
                dataList.clear();
                updateListView();
//                tv_empty.setVisibility(View.VISIBLE);
//                view_pager.setVisibility(View.GONE);
                if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                    tv_empty.setText(BaseApplication.getContext().getString(R.string.net_error));
//                    Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                    tv_empty.setText(BaseApplication.getContext().getString(R.string.en_net_error));
//                    Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_net_error), Toast.LENGTH_SHORT).show();
                }
//                tv_empty.setText("网络异常，请检查网络状态");
                tv_empty.setVisibility(View.VISIBLE);
                view_pager.setVisibility(View.GONE);

                emptyCartView.setVisibility(View.VISIBLE);
                recyclerShopping.setVisibility(View.INVISIBLE);
                shoppingCarDataList.clear();
                calculatePrice(shoppingCarDataList);
                break;

        }
    }

    boolean isNetError = false;


    public void showDetailDialog(GoodInfoModel goodMsgModel, int pageValue, int pagePosition) {
        builder = new CustomDialog.Builder(MainActivity.this);
        builder.setPagePostion(pagePosition);
        builder.setPageValue(pageValue);
        builder.setGoodMsgModel(goodMsgModel);
        addDialog = builder.create();
        addDialog.show();
        addDialog.getWindow().setLayout(1000, 600);
        addDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TaskUtils.sendPagePoint(TcpAiClient.getInstance(BaseApplication.getContext(), ClientMessageDispatcher.getInstance()), DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                MainActivity.lastTouchTime = System.currentTimeMillis();
            }
        });
    }

    private void initMyView() {
        btEnglish = (TextView) findViewById(R.id.btEnglish);
        btFrench = (TextView) findViewById(R.id.btFrench);
        tvTelephone = (TextView) findViewById(R.id.tvTelephone);
        btChinese = (TextView) findViewById(R.id.btChinese);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        view_pager = (ViewPager) findViewById(R.id.recyclerCommodity);
        recyclerShopping = (ListView) findViewById(R.id.recyclerShopping);
        tvCommodityName = (TextView) findViewById(R.id.number_value);
        resultPrice = (TextView) findViewById(R.id.result_value);
        emptyCartView = (TextView) findViewById(R.id.cart_empty_layout);
        back = (TextView) findViewById(R.id.back);
        tvTelephoneTip = (TextView) findViewById(R.id.tvTelephonetip);
        tvCardTitle = (TextView) findViewById(R.id.tvTitle);
        number = (TextView) findViewById(R.id.number);
        result = (TextView) findViewById(R.id.result);
        goto_pay = (TextView) findViewById(R.id.goto_pay);
        LayoutAll = (RelativeLayout) findViewById(R.id.layout_main_all);
        tvTelephoneTip.setOnClickListener(this);
        goto_pay.setOnClickListener(this);
        btChinese.setOnClickListener(this);
        btEnglish.setOnClickListener(this);
        btFrench.setOnClickListener(this);
    }

    private void initViews() {
        initMyView();
        LayoutAll.setOnClickListener(this);
        mAdapter = new ViewPagerAdapter();
        view_pager.setHorizontalScrollBarEnabled(true);
        view_pager.setAdapter(mAdapter);
        dots = new ArrayList<View>();
        dots.add(findViewById(R.id.dot1));
        dots.add(findViewById(R.id.dot2));
        dots.add(findViewById(R.id.dot3));
        dots.add(findViewById(R.id.dot4));

        dots.get(0).setVisibility(View.INVISIBLE);
        dots.get(1).setVisibility(View.INVISIBLE);
        dots.get(2).setVisibility(View.INVISIBLE);
        dots.get(3).setVisibility(View.INVISIBLE);

        tv_empty.setVisibility(View.VISIBLE);
        view_pager.setVisibility(View.GONE);

        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                dots.get(oldPageIndex).setBackgroundResource(
                        R.drawable.circle_not_selected);
                dots.get(position)
                        .setBackgroundResource(R.drawable.circle_selected);
                oldPageIndex = position;
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        view_pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Logger.e("点击:你点击了view_pager OnTouchListener");
                lastTouchTime = System.currentTimeMillis();
                return false;
            }
        });
        dataList = new ArrayList<>();
        calculatePrice(shoppingCarDataList);
        setBtnBg();
    }

    TcpAiClient tcpAiClient = null;

    private void initDatas() {
        startAndHaveGood = 0;
        salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        currentLan = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        phone = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.PHONE);
        tvTelephone.setText(phone);

        //设置当前页面
        TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber();
        ThreadPoolManager.getInstance().initThreadPool();

        tcpAiClient = TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance());
        if (!tcpAiClient.isConnected()) {
            Logger.d("调用tcp连接 初始化main");
            tcpAiClient.createConnect(context, ConstantUtils.AI_SERVER_IP, ConstantUtils.AI_SERVER_PORT);
        } else {
            Logger.d("tcp已连接");
        }

        if (dataList.size() > 0) {
            dataList.clear();
        }
        if (gridList.size() > 0) {
            gridList.clear();
        }
        dataList = LitePal.findAll(GoodInfoModel.class);
        if (dataList == null) {
            dataList = new ArrayList<GoodInfoModel>();
        }

        //计算viewpager一共显示几页
        int pageSize = dataList.size() % item_grid_num == 0
                ? dataList.size() / item_grid_num
                : dataList.size() / item_grid_num + 1;
        Logger.e("run pageSize = " + pageSize);
        for (int i = 0; i < pageSize; i++) {
            GridView gridView = new GridView(this);
            gridView.setDrawSelectorOnTop(true);
            gridView.setSelector(R.color.transparent);
            GridViewAdapter adapter = new GridViewAdapter(context, dataList, i);
            gridView.setNumColumns(number_columns);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    lastTouchTime = System.currentTimeMillis();
                    MessageEvent event = new MessageEvent(MessageEvent.EventType_ADD_GOODS);
                    event.setType(MessageEvent.EventType_ADD_GOODS);
                    event.setPageIndex(currentPage);
                    event.setPagePosition(i);
                    EventBus.getDefault().post(event);
                    TaskUtils.sendPagePoint(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                }
            });

            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    lastTouchTime = System.currentTimeMillis();
                    int index = 8 * currentPage + position;
                    GoodInfoModel goodMsgModel = dataList.get(index);
                    showDetailDialog(goodMsgModel, currentPage, position);
                    TaskUtils.sendPagePoint(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                    return true;
                }
            });
            gridView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
//                    Logger.e("2点击:12你点击了gridView setOnTouchListener");
                    lastTouchTime = System.currentTimeMillis();
                    return false;
                }
            });

            gridList.add(gridView);
        }
        mAdapter.add(gridList);
        view_pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Logger.e("2点击:你点击了32 view_pager OnTouchListener");
                lastTouchTime = System.currentTimeMillis();
                return false;
            }
        });
//        Logger.d("run NormalRecyclerViewAdapter start go =" + shoppingCarDataList.size());
        shoppingCarAdapter = new NormalRecyclerViewAdapter(context, shoppingCarDataList, true);
        recyclerShopping.setAdapter(shoppingCarAdapter);
        recyclerShopping.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Logger.e("2点击:你点击了recyclerShopping OnTouchListener22");
                lastTouchTime = System.currentTimeMillis();
                return false;
            }
        });
        requestGoods(true);
        delayQueryGoodsNum();
        registPos();
    }

    public void registPos() {
        runnable = new Runnable() {
            @Override
            public void run() {
                cardUtils = CardUtils.getInstance();
                if (cardUtils.getMisPos() == null) {
                    cardUtils.setMisPos(new MisPos(MainActivity.this));
                }
                if (!cardUtils.isRegister()) {
                    boolean result = register(cardUtils.getMisPos());
                    cardUtils.setRegister(result);
                }
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    ResponseData response;

    public boolean register(MisPos compos) {
        RequestData reqData = buildRequestData();
        reqData.PutValue("TransType", "1");
        response = new ResponseData();
        compos.TransProcess(reqData, response);
        return handleRegisterResponseData();
    }

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

    private boolean handleRegisterResponseData() {
        Logger.d("刷卡all1 RejCode:" + response.GetValue("RejCode"));//00
        Logger.d("刷卡all1 RejCodeExplain:" + response.GetValue("RejCodeExplain"));//交易成功
        if (!response.GetValue("RejCode").equals("00")) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Logger.d("peterzhang onResume");
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
        isContinue = false;
        super.onDestroy();

    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    int pointTimes = 0;

    @Override
    public void onClick(View view) {
        lastTouchTime = System.currentTimeMillis();
        TaskUtils.sendPagePoint(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
        switch (view.getId()) {
            case R.id.layout_main_all:
//                Logger.d("你点击了ConstraintLayout");
                TaskUtils.sendPagePoint(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                break;
            case R.id.tvTelephonetip:
                pointTimes = pointTimes + 1;
                if (pointTimes > 10) {
                    pointTimes = 0;
                    TimeIntervalUtils.pageValue = 1000;
                    Intent intentLogin = new Intent(this, LoginTestPageActivity.class);
                    startActivity(intentLogin);
                }
                break;
            case R.id.btEnglish:
                changeLanguage(LanguageType.ENGLISH.getLanguage());
                break;
            case R.id.btFrench:
                Intent intentLogin = new Intent(this, VersionActivity.class);
                startActivity(intentLogin);
                break;
            case R.id.btChinese:
                changeLanguage(LanguageType.CHINESE.getLanguage());
                break;
            case R.id.goto_pay:
                if (shoppingCarDataList.size() == 0) {
                    Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.empty_cart_not_pay), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!NetUtils.getInstance().checkOnlineState(context)) {
                    if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
//                        tv_empty.setText(BaseApplication.getContext().getString(R.string.net_error));
                        Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                    } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
//                        tv_empty.setText(BaseApplication.getContext().getString(R.string.en_net_error));
                        Toast.makeText(BaseApplication.getContext(), BaseApplication.getContext().getString(R.string.en_net_error), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPaymentMethodPage.getNumber());
                ArrayList<GoodInfoModel> sendDatas = new ArrayList<>();
                sendDatas.addAll(shoppingCarDataList);
                Intent intent = new Intent(this, PayTypeActivity.class);
                intent.putParcelableArrayListExtra("shoppingData", sendDatas);
                startActivityFinish(intent);
                break;
        }

    }

    /**
     * 更新购物车
     *
     * @param list
     */
    public void calculatePrice(List<GoodInfoModel> list) {
        if (list.size() == 0) {
            recyclerShopping.setVisibility(View.INVISIBLE);
            emptyCartView.setVisibility(View.VISIBLE);
        } else {
            recyclerShopping.setVisibility(View.VISIBLE);
            emptyCartView.setVisibility(View.GONE);
        }
        totalPrice = 0;
        for (GoodInfoModel model : list) {
            totalPrice += model.getPrice();
        }
        tvCommodityName.setText("" + list.size());
        resultPrice.setText(BaseApplication.getContext().getString(R.string.money_type_text) + " " + totalPrice);
    }

    Runnable runnableQuery = null;

    public void requestGoods(final boolean isDetail) {
        isNetError = false;//每次复位 默认值false
        runnableQuery = new Runnable() {
            @Override
            public void run() {
                NetUtils.getInstance().requestGoods(isDetail, context);
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnableQuery);
    }

    public void updatePagePoint(int pageSize) {
        if (pageSize == 4) {
            dots.get(0).setVisibility(View.VISIBLE);
            dots.get(1).setVisibility(View.VISIBLE);
            dots.get(2).setVisibility(View.VISIBLE);
            dots.get(3).setVisibility(View.VISIBLE);
        } else if (pageSize == 3) {
            dots.get(0).setVisibility(View.VISIBLE);
            dots.get(1).setVisibility(View.VISIBLE);
            dots.get(2).setVisibility(View.VISIBLE);
            dots.get(3).setVisibility(View.GONE);
        } else if (pageSize == 2) {
            dots.get(0).setVisibility(View.VISIBLE);
            dots.get(1).setVisibility(View.VISIBLE);
            dots.get(2).setVisibility(View.GONE);
            dots.get(3).setVisibility(View.GONE);
        } else if (pageSize == 1) {
            dots.get(0).setVisibility(View.VISIBLE);
            dots.get(1).setVisibility(View.GONE);
            dots.get(2).setVisibility(View.GONE);
            dots.get(3).setVisibility(View.GONE);
        } else if (pageSize == 0) {
            dots.get(0).setVisibility(View.GONE);
            dots.get(1).setVisibility(View.GONE);
            dots.get(2).setVisibility(View.GONE);
            dots.get(3).setVisibility(View.GONE);
        }

    }

    public void updateListView() {
        int pageSize = dataList.size() % item_grid_num == 0
                ? dataList.size() / item_grid_num
                : dataList.size() / item_grid_num + 1;
        Logger.e("run pageSize = " + pageSize);
        gridList.clear();
        for (int i = 0; i < pageSize; i++) {
            GridView gridView = new GridView(this);
            gridView.setDrawSelectorOnTop(true);
            gridView.setSelector(R.color.transparent);
            GridViewAdapter adapter = new GridViewAdapter(context, dataList, i);
            gridView.setNumColumns(number_columns);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    Logger.e("点击:12你点击了onItemClick");
                    lastTouchTime = System.currentTimeMillis();
                    isPoint = true;
                    MessageEvent event = new MessageEvent(MessageEvent.EventType_ADD_GOODS);
                    event.setType(MessageEvent.EventType_ADD_GOODS);
                    event.setPageIndex(currentPage);
                    event.setPagePosition(i);
                    EventBus.getDefault().post(event);
                    TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                }
            });

            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    Logger.e("点击:12你点击了onItemLongClick");
                    lastTouchTime = System.currentTimeMillis();
                    isPoint = true;
                    int index = 8 * currentPage + position;
                    GoodInfoModel goodMsgModel = dataList.get(index);
                    showDetailDialog(goodMsgModel, currentPage, position);
                    TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                    return true;
                }
            });

            gridView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN
                    ) {
                        lastTouchTime = System.currentTimeMillis();
                    }
                    return false;
                }
            });

            gridList.add(gridView);
        }
        mAdapter.add(gridList);

        if (dataList.size() > 0) {
            //有商品
            tv_empty.setVisibility(View.GONE);
            view_pager.setVisibility(View.VISIBLE);
        } else {
            if(isNetError){
                //网络异常
                if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                    tv_empty.setText(BaseApplication.getContext().getString(R.string.net_error));
                } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                    tv_empty.setText(BaseApplication.getContext().getString(R.string.en_net_error));
                }
            }else{
                if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                    tv_empty.setText(BaseApplication.getContext().getString(R.string.empty_good_list));
                } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                    tv_empty.setText(BaseApplication.getContext().getString(R.string.en_empty_good_list));
                }
            }

            tv_empty.setVisibility(View.VISIBLE);
            view_pager.setVisibility(View.GONE);

        }

        updatePagePoint(pageSize);

        if (lastTouchTime == 0) {
            lastTouchTime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - lastTouchTime > backTimeOut) {
            Logger.e("点击还原:run isPoint1 = " + isPoint);
            view_pager.setCurrentItem(0);

            shoppingCarDataList.clear();
            shoppingCarAdapter.update(shoppingCarDataList);
            startAndHaveGood = 0;
        } else {
            Logger.e("点击还原:run isPoint2 = " + isPoint);
            shoppingCarAdapter.update(shoppingCarDataList);
        }
    }

    public void changeLanguage() {
        String current = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        currentLan = current;
        if (current.equals("")) {
            SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.LANGUAGE, LanguageType.CHINESE.getLanguage());
            current = LanguageType.CHINESE.getLanguage();
        }
        btChinese.setText(MainActivity.this.getString(R.string.chinese_text));
        btEnglish.setText(MainActivity.this.getString(R.string.en_english_text));
        btFrench.setText(MainActivity.this.getString(R.string.en_french_text));
        if (current.equals(LanguageType.CHINESE.getLanguage())) {

            back.setText(MainActivity.this.getString(R.string.back));
            emptyCartView.setText(MainActivity.this.getString(R.string.empty_cart_not_pay));
            tvTelephoneTip.setText(MainActivity.this.getString(R.string.urgent_tel_text));
            tvCardTitle.setText(MainActivity.this.getString(R.string.shopping_cart));
            number.setText(MainActivity.this.getString(R.string.goods_num_text));
            result.setText(MainActivity.this.getString(R.string.goods_total_money_text));
            goto_pay.setText(MainActivity.this.getString(R.string.goto_pay_text));
            tv_empty.setText(MainActivity.this.getString(R.string.empty_good_list));
        } else if (current.equals(LanguageType.ENGLISH.getLanguage())) {

            back.setText(MainActivity.this.getString(R.string.en_back));
            emptyCartView.setText(MainActivity.this.getString(R.string.en_empty_cart_not_pay));
            tvTelephoneTip.setText(MainActivity.this.getString(R.string.en_urgent_tel_text));
            tvCardTitle.setText(MainActivity.this.getString(R.string.en_shopping_cart));
            number.setText(MainActivity.this.getString(R.string.en_goods_num_text));
            result.setText(MainActivity.this.getString(R.string.en_goods_total_money_text));
            goto_pay.setText(MainActivity.this.getString(R.string.en_goto_pay_text));
            tv_empty.setText(MainActivity.this.getString(R.string.en_empty_good_list));
        } else if (current.equals(LanguageType.FRENCH.getLanguage())) {

        }

        findViewById(R.id.layout_shop_title).requestLayout();

        //更新购物商品
        if (dataList.size() > 0) {
            if (requestDataList.size() == 0) {
                dataList.clear();
                updateListView();

                requestDataList.clear();
                requestDataList.addAll(LitePal.findAll(GoodInfoModel.class));
                dataList.addAll(requestDataList);
                updateListView();
            } else {
                dataList.clear();
                updateListView();

                dataList.addAll(requestDataList);
                updateListView();
            }
        }

        if(isNetError){
            //网络异常
            if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                tv_empty.setText(BaseApplication.getContext().getString(R.string.net_error));
            } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                tv_empty.setText(BaseApplication.getContext().getString(R.string.en_net_error));
            }
        }else{
            if (currentLan.equals(LanguageType.CHINESE.getLanguage())) {
                tv_empty.setText(BaseApplication.getContext().getString(R.string.empty_good_list));
            } else if (currentLan.equals(LanguageType.ENGLISH.getLanguage())) {
                tv_empty.setText(BaseApplication.getContext().getString(R.string.en_empty_good_list));
            }
        }

        //更新购物车
        List<GoodInfoModel> oldShoppingCarDataList = new ArrayList<>();
        if (shoppingCarDataList.size() > 0) {
            oldShoppingCarDataList.addAll(shoppingCarDataList);
            shoppingCarDataList.clear();
            shoppingCarAdapter.update(shoppingCarDataList);

            shoppingCarDataList.addAll(oldShoppingCarDataList);
            shoppingCarAdapter.update(shoppingCarDataList);
        }

        if (dataList.size() > 0) {
            //有商品
            tv_empty.setVisibility(View.GONE);
            view_pager.setVisibility(View.VISIBLE);
        } else {
            tv_empty.setVisibility(View.VISIBLE);
            view_pager.setVisibility(View.GONE);
        }
    }

    public void setBtnBg() {
        String current = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        if (current.equals("")) {
            SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.LANGUAGE, LanguageType.CHINESE.getLanguage());
            current = LanguageType.CHINESE.getLanguage();
        }

        if (current.equals(LanguageType.CHINESE.getLanguage())) {
//            btChinese.setBackground(BaseApplication.getContext().getResources().getDrawable(R.mipmap.bt_bg));
            btChinese.setBackground(BaseApplication.getContext().getResources().getDrawable(R.drawable.bt_bg_lan));
            btEnglish.setBackground(null);
            btFrench.setBackground(null);
            btChinese.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.textSelectColor));
            btEnglish.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
            btFrench.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
        } else if (current.equals(LanguageType.ENGLISH.getLanguage())) {
            btChinese.setBackground(null);
//            btEnglish.setBackground(BaseApplication.getContext().getResources().getDrawable(R.mipmap.bt_bg));
            btEnglish.setBackground(BaseApplication.getContext().getResources().getDrawable(R.drawable.bt_bg_lan));
            btFrench.setBackground(null);

            btChinese.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
            btEnglish.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.textSelectColor));
            btFrench.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
        }
        changeLanguage();
    }

    private void changeLanguage(String language) {
        if (language.equals(currentLan)) {
            return;
        }
        SpUtil.getInstance(this).putString(SpUtil.LANGUAGE, language);
        setBtnBg();
    }

    public void delayQueryGoodsNum() {
        runnable = new Runnable() {
            @Override
            public void run() {
                while (isContinue) {
                    try {
                        Thread.sleep(ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM_USE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    requestGoods(true);
                }
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }


}
