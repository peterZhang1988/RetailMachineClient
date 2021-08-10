package com.example.retailmachineclient.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.example.retailmachineclient.ui.SplashActivity;
import com.example.retailmachineclient.util.Utils;

@TargetApi(21)
public class AliveJobService extends JobService {
    private static final int MESSAGE_ID_TASK = 0x01;
    // 告知编译器，这个变量不能被优化
    private volatile static Service mKeepAliveService = null;

    public static boolean isJobServiceAlive() {
        return mKeepAliveService != null;
    }

    static long notBeforeTime = 0;//时间戳
    static long appOutTime = 0;
    long TimeOutMillis = 5 * 60 * 1000;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            appOutTime = 0;
            if (Utils.isAPPALive(getApplicationContext())) {
                appOutTime = 0;
                //APP活着的
                if (!Utils.isTopActivity(getApplicationContext())) {
                    if (notBeforeTime == 0) {
                        notBeforeTime = System.currentTimeMillis();
                    } else if (System.currentTimeMillis() - notBeforeTime > TimeOutMillis) {
                        notBeforeTime = 0;
                        //不在前台
                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                } else {
                    notBeforeTime = 0;
                }
            } else {
                if (appOutTime == 0) {
                    appOutTime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - appOutTime > TimeOutMillis) {
                    appOutTime=0;
                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //APP被杀死后重启
                }
            }
            jobFinished((JobParameters) msg.obj, false); // 通知系统任务执行结束
            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters params) {
        mKeepAliveService = this;
        Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
        mHandler.sendMessage(msg);
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeMessages(MESSAGE_ID_TASK);
        return false;
    }
}