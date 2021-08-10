package com.example.retailmachineclient.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easysocket.EasySocket;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.http.Api;
import com.example.retailmachineclient.http.HttpManager;
import com.example.retailmachineclient.model.GoodInfoModel;
import com.example.retailmachineclient.model.LoginRspModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.protocobuf.CmdSchedule;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import DDRAIServiceProto.DDRAIServiceCmd;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;

import android.app.ProgressDialog;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    EditText salesId;
    String salesIdValue = "";
    String machineNameValue = "";
    TcpAiClient tcpAiClient = null;
    int pointTimes = 0;
    static final int PointTimeOut = 10;//重复点击阈值
    public boolean isStopTask = true;
    public boolean isInAutoLogin = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        switch (msgEvent.getType()) {
            case MessageEvent.EventType_LOGIN_SUCCESS:
            case MessageEvent.EventType_LOGIN_SUCCESS_Auto:
                dismissWaitingDialog();
                //切换账号后登录成功后清除本地商品缓存
                if (getIntent().getBooleanExtra("isSwitchSales", false)) {
                    LitePal.deleteAll(GoodInfoModel.class);
                }
                if (tcpAiClient != null) {
                    tcpAiClient.sendGetRobotID(salesIdValue);
                }
                Toast.makeText(BaseApplication.getContext(), LoginActivity.this.getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                TaskUtils.sendPage(tcpAiClient, DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber());
                startActivityFinish(MainActivity.class);
                break;

            case MessageEvent.EventType_LOGIN_FAIL:
                Toast.makeText(BaseApplication.getContext(), LoginActivity.this.getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.ROBOT_Id, "");
                SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.SALES_Id, "");
                break;

            case MessageEvent.EventType_LOGIN_FAIL_Error:
                Toast.makeText(BaseApplication.getContext(), LoginActivity.this.getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                break;

            //自动登录失败
            case MessageEvent.EventType_LOGIN_FAIL_Auto:
                isInAutoLogin = false;
                dismissWaitingDialog();
                Logger.e("登录页清空缓存");
                break;

            case MessageEvent.EventType_LOGIN_FAIL_Error_Auto:
                Logger.d("登录超时error继续登录");
                if (!isStopTask) {
                    autoLogin();
                }
                break;
            case MessageEvent.EventType_LOGIN_AI_SUCCESS:
                Logger.d("接口 登录ai成功 loginactivity 获取机器id");
                if (tcpAiClient != null) {
                    tcpAiClient.setLand(true);
                    tcpAiClient.sendGetRobotID("");
                }
                break;
            case MessageEvent.EventType_LOGIN_AI_FAIL:
                Logger.d("接口 登录ai失败 loginactivity");
                if (tcpAiClient != null) {
                    tcpAiClient.setLand(false);
                }
                break;
            case MessageEvent.EventType_TCP_CONNECTED:
                Logger.d("接口 tcp连接成功 loginactivity");
                break;
            case MessageEvent.EventType_GET_GetRobotID:
                machineNameValue = (String) msgEvent.getTag();
                SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.ROBOT_Id, machineNameValue);
                break;
            case MessageEvent.EventType_SHOW_PROGRESS:
                showWaitingDialog();
                break;
            case MessageEvent.EventType_DISMISS_PROGRESS:
                isStopTask = true;
                break;

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_layout;
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

    private void initViews() {
        //初始化ViewPager
        salesId = findViewById(R.id.sales_id);
        findViewById(R.id.startLogin).setOnClickListener(this);
        findViewById(R.id.tv_username).setOnClickListener(this);

    }

    ProgressDialog waitingDialog = null;

    private void showWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        if (waitingDialog == null) {
            waitingDialog =
                    new ProgressDialog(LoginActivity.this);
            waitingDialog.setTitle("自动登录");
            waitingDialog.setMessage("等待中...");
            waitingDialog.setIndeterminate(true);
            waitingDialog.setCancelable(true);

        }
        waitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isStopTask = true;
            }
        });
        if (!waitingDialog.isShowing()) {
            waitingDialog.show();
        }

    }

    private void dismissWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        if (waitingDialog != null) {
            waitingDialog.dismiss();
        }
    }


    private void initDatas() {
        ThreadPoolManager.getInstance().initThreadPool();
        tcpAiClient = TcpAiClient.getInstance(context, ClientMessageDispatcher.getInstance());
        if (!tcpAiClient.isConnected()) {
            Logger.e("------- initDatas tcp 没有连接 ");
            tcpAiClient.createConnect(context, ConstantUtils.AI_SERVER_IP, ConstantUtils.AI_SERVER_PORT);
        } else {
            Logger.e("------- initDatas tcp 已经连接 ");
        }
        String current = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
        if (current.equals("")) {
            //默认中文
            SpUtil.getInstance(this).putString(SpUtil.LANGUAGE, LanguageType.CHINESE.getLanguage());
        }

        boolean isSwitchSales = getIntent().getBooleanExtra("isSwitchSales", false);
        if (!isSwitchSales) {
            final String salesIdAuto = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
            final String machineNameValueAuto = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.ROBOT_Id);
            if (!TextUtils.isEmpty(salesIdAuto) && !TextUtils.isEmpty(machineNameValueAuto)) {
                isInAutoLogin = true;
                salesId.setText(salesIdAuto);
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    final String salesIdAuto = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
                    final String machineNameValueAuto = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.ROBOT_Id);
                    if (!TextUtils.isEmpty(salesIdAuto) && !TextUtils.isEmpty(machineNameValueAuto)) {
                        isStopTask = false;
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_SHOW_PROGRESS));
                    }
                    autoLogin();
                }
            };
            ThreadPoolManager.getInstance().executeRunable(runnable);
        }
    }


    public void autoLogin() {
        final String salesIdAuto = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        final String machineNameValueAuto = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.ROBOT_Id);
        if (!TextUtils.isEmpty(salesIdAuto) && !TextUtils.isEmpty(machineNameValueAuto)) {
            isInAutoLogin = true;
            Logger.e("-------自动登录 salesId machineNameValue 不为空 ");
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    NetUtils.getInstance().loginBgAuto(salesIdAuto, machineNameValueAuto);
                }
            };
            ThreadPoolManager.getInstance().executeRunable(runnable);
        } else {
            Logger.e("salesId machineNameValue 为空 machineNameValue = " + machineNameValueAuto + ",salesId=" + salesIdAuto);
        }
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
        dismissWaitingDialog();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (tcpAiClient != null) {
                tcpAiClient.disConnect(false);
            }
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    Runnable runnable = null;

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_username:
                pointTimes = pointTimes + 1;
                if (pointTimes > PointTimeOut) {
                    pointTimes = 0;
                    Intent intentLogin = new Intent(this, LoginTestPageActivity.class);
                    startActivity(intentLogin);
                }
                break;
            case R.id.startLogin:
//                if (isInAutoLogin) {
//                    Logger.e("自动登录");
//                    String salesIdValueOld = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
//                    String machineNameValueOld = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.ROBOT_Id);
//
//                    if (!TextUtils.isEmpty(salesIdValueOld) && !TextUtils.isEmpty(machineNameValueOld)) {
//                        salesIdValue = salesIdValueOld;
//                        machineNameValue = machineNameValueOld;
//                    } else {
//                        isInAutoLogin = false;
//                        salesIdValue = salesId.getText().toString();
//                    }
//                } else {
//                    Logger.e("手动登录");
//                    salesIdValue = salesId.getText().toString();
//                    //现场测试使用账号
////                    salesIdValue = "d0d23700b2984fc5b0a2232329cf3b90";
////                    machineNameValue = "大道测试";
//
//                    //本地测试使用账号
////                    machineNameValue = "测试用";
////                    salesIdValue = "932c4216670bb9cc3070f35c1873af09";
//                }

                Logger.e("手动登录");
                salesIdValue = salesId.getText().toString();

//                machineNameValue = "测试用";
//                salesIdValue = "932c4216670bb9cc3070f35c1873af09";

                if (machineNameValue == null || machineNameValue.equals("")) {
                    Logger.e("登录 machineNameValue 为空 ");
                    Toast.makeText(BaseApplication.getContext(), "请稍后重试...", Toast.LENGTH_SHORT).show();
                    if (tcpAiClient != null) {
                        tcpAiClient.sendGetRobotID("");
                    }
                    return;
                }
                final String saleId = salesIdValue;
                final String robotId = machineNameValue;
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        NetUtils.getInstance().loginBg(saleId, robotId);
                    }
                };
                ThreadPoolManager.getInstance().executeRunable(runnable);
                break;
        }
    }
}