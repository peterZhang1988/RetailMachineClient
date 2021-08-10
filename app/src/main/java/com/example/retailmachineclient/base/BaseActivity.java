package com.example.retailmachineclient.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.retailmachineclient.util.ActivityStackManager;
import com.example.retailmachineclient.util.CloseBarUtil;
import com.example.retailmachineclient.util.EventBusManager;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.LanguageUtil;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.MyContextWrapper;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.ThreadPoolManager;

//import androidx.annotation.Nullable;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import android.core.app..v4.app.AppCompatActivity;
import android.app.Activity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
/**
 * desc:正常Activity基类
 * time:2020/08/07
 */
public abstract class BaseActivity extends Activity {
    private Unbinder mButterKnife;
    protected boolean isStatusBarEnabled;
    protected Context context;
    public static final String EXITACTION = "action.exit";
    private ExitReceiver exitReceiver = new ExitReceiver();
    @Override
    protected void onCreate( Bundle savedInstanceState) {//@Nullable
        super.onCreate(savedInstanceState);
        Logger.d("-------------" + this.getClass().getSimpleName());
        context = BaseApplication.context;
        this.requestWindowFeature(1);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(EXITACTION);
        registerReceiver(exitReceiver, filter);

        mButterKnife = ButterKnife.bind(this);
        EventBusManager.register(this);
        ActivityStackManager.getInstance().onCreated(this);
        initView();
        initData();
        if (isStatusBarEnabled) {
            initState(this);
        }
        CloseBarUtil.closeBar(this);
    }

    /**
     * 是否启用沉浸式状态栏
     *
     * @param statusBarEnabled true启动
     */
    public void setStatusBarEnabled(boolean statusBarEnabled) {
        isStatusBarEnabled = statusBarEnabled;
    }

    /**
     * 沉浸式状态栏（已适配 ）
     */
    private void initState(Activity activity) {
        Logger.i("启动沉浸式状态栏");
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


    /**
     * 获取布局
     *
     * @return 布局ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 初始化参数
     */
    protected abstract void initData();


    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d("-------------" + this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityStackManager.getInstance().onResume(this);
        Logger.d("-------------" + this.getClass().getSimpleName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d("-------------" + this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("-------------" + this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("-------------" + this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mButterKnife != null)
            mButterKnife.unbind();
        EventBusManager.unregister(this);
        unregisterReceiver(exitReceiver);
//        ThreadPoolManager.getInstance().shutdownExecutor();//待定方案，关闭页面时关闭线程池
        ActivityStackManager.getInstance().onDestroyed(this);
        Logger.d("-------------" + this.getClass().getSimpleName());
    }

    /**
     * startActivity 方法优化
     */
    public void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

    /**
     * 跳转并销毁当前activity
     *
     * @param cls
     */
    public void startActivityFinish(Class<? extends Activity> cls) {
        startActivityFinish(new Intent(this, cls));
    }

    public void startActivityFinish(Intent intent) {
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
//        String language = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.LANGUAGE);
//        Logger.e("BaseActivity:"+language);
//        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language));
    }

    class ExitReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            BaseActivity.this.finish();
        }

    }

}
