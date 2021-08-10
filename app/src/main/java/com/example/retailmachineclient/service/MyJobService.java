package com.example.retailmachineclient.service;


import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.retailmachineclient.util.Logger;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private int kJobId = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Logger.e( "onStartJob  +++");
        String sName = WorkService.class.getName();
        boolean isLocalServiceWork = StaticMethodTool.isServiceRunning(sName, this);//此处填入服务的全名
        if (!isLocalServiceWork) {
            this.startService(new Intent(this, WorkService.class));
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Logger.e( "onStopJob  +++");
        scheduleJob(getJobInfo());
        return true;
    }

    //将任务作业发送到作业调度中去
    public void scheduleJob(JobInfo t) {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    public JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, new ComponentName(this, MyJobService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        //   builder.setPersisted(true);//手机重启
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        //间隔1000毫秒
        builder.setPeriodic(1000);
        return builder.build();
    }
}
