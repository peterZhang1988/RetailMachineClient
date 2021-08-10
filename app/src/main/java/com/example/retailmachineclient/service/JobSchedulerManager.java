package com.example.retailmachineclient.service;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

public class JobSchedulerManager {
    private static final int JOB_ID = 1;
    private static JobSchedulerManager mJobManager;
    private JobScheduler mJobScheduler;
    private static Context mContext;

    private JobSchedulerManager(Context ctxt) {
        this.mContext = ctxt;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mJobScheduler = (JobScheduler) ctxt.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
    }

    public final static JobSchedulerManager getJobSchedulerInstance(Context ctxt) {
        if (mJobManager == null) {
            mJobManager = new JobSchedulerManager(ctxt);
        }
        return mJobManager;
    }

    @TargetApi(21)
    public void startJobScheduler() {
        if (AliveJobService.isJobServiceAlive() || isBelowLOLLIPOP()) {
            return;        // 如果JobService已经启动或API<21，返回
        }
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(mContext, AliveJobService.class));
        builder.setPeriodic(20000); // 设置每20秒执行一下任务
        builder.setPersisted(true);  // 设置设备重启时，执行该任务
        builder.setRequiresCharging(true); // 当插入充电器，执行该任务
        JobInfo info = builder.build();
        mJobScheduler.schedule(info);
    }

    @TargetApi(21)
    public void stopJobScheduler() {
        if (isBelowLOLLIPOP())
            return;
        mJobScheduler.cancelAll();
    }

    private boolean isBelowLOLLIPOP() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP; // API< 21
    }
}
