package com.example.retailmachineclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.protocobuf.dispatcher.ClientMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.util.Logger;

/**
 * 用于正式运行处理事件的服务
 */
public class WorkService extends Service {
    long i = 0L;

    public void onCreate() {
        super.onCreate();
        i = 0;
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
//                    TcpAiClient.getInstance(BaseApplication.getContext(), ClientMessageDispatcher.getInstance());
                    Logger.e("WorkService 服务没有死" + (i++));
                }
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
