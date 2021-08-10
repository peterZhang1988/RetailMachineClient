package com.example.retailmachineclient.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.retailmachineclient.BuildConfig;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.http.HttpManager;
import com.example.retailmachineclient.model.LoginRspModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.service.JobSchedulerManager;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.LogcatHelper;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.NetUtils;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.ThreadPoolManager;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;

//import static com.example.retailmachineclient.http.Api.APP_LOGIN_DOMAIN;
import static com.example.retailmachineclient.http.Api.APP_LOGIN_DOMAIN_NAME;

import com.example.retailmachineclient.util.Utils;

public class SplashActivity extends BaseActivity implements Animation.AnimationListener, OnPermission, View.OnClickListener {
    private static final int ANIM_TIME = 1000;//
    @BindView(R.id.iv_splash)
    ImageView ivSplash;

    int loginTime = 1;

    int pointTimes = 0;
    static final int PointTimeOut = 15;//重复点击阈值

    private String[] permission = new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.SYSTEM_ALERT_WINDOW};

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        switch (msgEvent.getType()) {
            case MessageEvent.EventType_LOGIN_SUCCESS_Auto:
                startActivityFinish(MainActivity.class);
                break;
            case MessageEvent.EventType_LOGIN_FAIL_Auto:
                Logger.e("登录失败 跳转LoginActivity");
                startActivityFinish(LoginActivity.class);
                break;

            case MessageEvent.EventType_LOGIN_FAIL_Error_Auto:
                Logger.e("登录异常失败 跳转LoginActivity");
                if (loginTime <= 3) {
                    loginTime = loginTime + 1;
                    login();
                } else {
                    startActivityFinish(LoginActivity.class);
                }
                break;

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        ivSplash = findViewById(R.id.iv_splash);
        setStatusBarEnabled(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.4f, 1.0f);
        alphaAnimation.setDuration(ANIM_TIME);
        alphaAnimation.setAnimationListener(this);
        ivSplash.startAnimation(alphaAnimation);
        ivSplash.setOnClickListener(this);
        JobSchedulerManager.getJobSchedulerInstance(getApplicationContext()).startJobScheduler();
    }

    @Override
    protected void initData() {
        Logger.e("版本时间：" + BuildConfig.BUILD_TIME);
        if (SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_GOODS_WAIT_TIME_NUM, 0) > 0) {
            //有数据
            Logger.e("参数：有数据");
            Utils.updateSp2ConsSetValue();
        } else {
            //么有数据
            Logger.e("参数：么有数据");
            Utils.setCons2SpSetValue();
        }
        if (SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.NUMBER_BUY, 0) > 0) {
            DeviceSettingActivity.updateSp2ConsSetValue();
        } else {
            DeviceSettingActivity.setCons2SpSetValue();
        }
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        Logger.e("请求权限！");
        XXPermissions.with(this)
                .permission(permission)
                .request(this);
    }

    /**
     * 权限通过
     *
     * @param granted
     * @param isAll
     */
    @Override
    public void hasPermission(List<String> granted, boolean isAll) {
        ThreadPoolManager.getInstance().initThreadPool();
        Logger.d("权限请求成功");
        LogcatHelper.getInstance(context).start();
        login();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_splash:
                loginTime = loginTime + 1;
                break;

        }
    }

    public void login() {
        final String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        final String machineNameValue = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.ROBOT_Id);
        if (!TextUtils.isEmpty(salesId) && !TextUtils.isEmpty(machineNameValue)) {
            Logger.e("-------salesId machineNameValue 不为空 ");
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    NetUtils.getInstance().loginBgAuto(salesId, machineNameValue);
                }
            };
            ThreadPoolManager.getInstance().executeRunable(runnable);
        } else {
            Logger.e("salesId machineNameValue 为空 machineNameValue = " + machineNameValue + ",salesId=" + salesId);
            SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.ROBOT_Id, "");
            startActivityFinish(LoginActivity.class);
        }
    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {
        if (quick) {
            XXPermissions.gotoPermissionSettings(SplashActivity.this, true);
        } else {
            requestPermission();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        requestPermission();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onBackPressed() {
        //禁用返回键
        //super.onBackPressed();
    }
}
