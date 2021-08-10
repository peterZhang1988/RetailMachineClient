package com.example.retailmachineclient.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.ResponseBody;
import com.example.retailmachineclient.http.Api;
import com.example.retailmachineclient.model.rsp.VersionRspModel;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.EventBusManager;
import com.example.retailmachineclient.util.Logger;
import  com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.http.HttpManager;


/**
 * desc:下载的服务
 */
public class DownloadServer extends Service {
    private File file;

//    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("onCreate");
    }

    boolean isCancel = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        Logger.e("DownloadServer evenbus"+msgEvent.getType());
        switch (msgEvent.getType()) {

            case MessageEvent.apkDownloadCancel:
                isCancel = true;
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBusManager.register(this);
        Logger.e("onStartCommand");
        isCancel = false;
        String url = intent.getStringExtra("downloadUrl");
//        Logger.e("下载apk URL="+url);
//        RetrofitUrlManager.getInstance().putDomain(Api.APP_UPDATE_DOMAIN_NAME, url);
        RetrofitUrlManager.getInstance().putDomain(Api.APP_UPDATE_DOMAIN_NAME, ConstantUtils.APP_LOGIN_DOMAIN);
        url = url.substring(1);
        Logger.e("下载apk URL="+url);
        HttpManager.getInstance().getHttpServer().downloadApk( url)
                .subscribeOn(Schedulers.io())        //设置被观察者在IO子线程中执行
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("----onSubscribe");
                    }
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            long contentLength=responseBody.contentLength();
                            InputStream inputStream=responseBody.byteStream();
                            File directory=new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + File.separator +"dir");
                            if (!directory.exists()){
                                Logger.e("创建文件");
                                directory.mkdirs();
                            }
                            Logger.e("创建文件jkdfdjk="+directory.getAbsolutePath());
                            file=new File(directory,"RetailMachine.apk");
//                            if(file.ex){
//                                file.delete();
//                                file=new File(directory,"RetailMachine.apk");
//                            }

                            FileOutputStream outputStream=new FileOutputStream(file);
                            byte[] bytes=new byte[1024];
                            int len=0;
                            //循环读取文件的内容，把他放到新的文件目录里面
                            while ((len=inputStream.read(bytes))!=-1){
                                if(isCancel){
                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.apkDownloadCancelShow));
                                    return;
                                }
                                outputStream.write(bytes,0,len);
                                long length =file.length();
                                //获取下载的大小
                                int progress=(int) (length*100/contentLength);
                                Logger.e("---------下载进度："+progress);
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.updateProgress,progress));
                            }
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.apkDownloadSucceed,file));
                        }catch (Exception e){
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.apkDownloadFailed));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:"+e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.apkDownloadFailed));
                    }

                    @Override
                    public void onComplete() {
                        EventBusManager.unregister(this);
                        Logger.e("onComplete订阅事件完成！");
                        stopSelf();
                    }
                });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
