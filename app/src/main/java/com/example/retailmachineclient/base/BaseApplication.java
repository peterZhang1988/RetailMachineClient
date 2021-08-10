package com.example.retailmachineclient.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
//import com.example.retailmachineclient.100dp.ImageLoader;
//import com.example.retailmachineclient.glide.ImageLoader;
import com.example.retailmachineclient.ui.CrashActivity;
import com.example.retailmachineclient.ui.SplashActivity;
import com.example.retailmachineclient.util.EventBusManager;
import com.example.retailmachineclient.util.LanguageType;
import com.example.retailmachineclient.util.LanguageUtil;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.MyContextWrapper;
import com.example.retailmachineclient.util.SpUtil;
import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastWhiteStyle;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

//import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * time :  2019/10/28
 * desc :  项目中的 application 基类
 */
public class BaseApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        LitePal.initialize(this);
//        String language = SpUtil.getInstance(this).getString(SpUtil.LANGUAGE);
        Resources resources=getResources();//获得res资源对象
        Configuration config=resources.getConfiguration();//获得设置对象
        DisplayMetrics dm=resources.getDisplayMetrics();//获得屏幕参数：主要是分辨率像素等。
        Logger.e("----屏幕参数1："+dm.widthPixels+"height ="+dm.heightPixels);
//        Logger.e("----屏幕参数2："+dm.width+"height ="+dm.height);
//        SpUtil.getInstance(this).putString(SpUtil.LANGUAGE, language);
//        if(language.equals("en")){
//            config.locale= Locale.ENGLISH;
//        }else if(language.equals("cn")){
//            config.locale= Locale.SIMPLIFIED_CHINESE;
//        }else{
//            //3
//            config.locale= Locale.FRENCH;
//        }
//        resources.updateConfiguration(config,dm);

//        OkSocketOptions.setIsDebug(false);
        initSDK(this);
    }

    public void initSDK(Application application) {
        // 这个过程专门用于堆分析的 leak 金丝雀
        // 你不应该在这个过程中初始化你的应用程序
       /* if (LeakCanary.isInAnalyzerProcess(application)) {
            return;
        }*/
        // 图片加载器
//        ImageLoader.init(application);

        // 内存泄漏检测
        //LeakCanary.install(application);
        // 设置 Toast 拦截器
        ToastUtils.setToastInterceptor(new ToastInterceptor() {
            @Override
            public boolean intercept(Toast toast, CharSequence text) {
                boolean intercept = super.intercept(toast, text);
                if (intercept) {
                    Log.e("Toast", "空 Toast");
                } else {
                    Log.i("Toast", text.toString());
                }
                return intercept;
            }
        });
        // 吐司工具类
        ToastUtils.init(application, new ToastWhiteStyle(application));
//        LitePal.initialize(this);
        // EventBus 事件总线
        EventBusManager.init();
        // Crash 捕捉界面
        // Crash 捕捉界面
//        CaocConfig.Builder.create()
//                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
//                .enabled(true)
//                .trackActivities(true)
//                .minTimeBetweenCrashesMs(2000)
//                // 重启的 Activity
//                .restartActivity(SplashActivity.class)
//                .errorActivity(CrashActivity.class)
//                // 设置监听器
//                //.eventListener(new YourCustomEventListener())
//                .apply();

//        ARouter.init(this);


    }

    public static Context getContext() {
        return context;
    }



}




