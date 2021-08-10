package com.example.retailmachineclient.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.List;

class StaticMethodTool {
    /**
     * 启动服务
     *
     * @param mAct
     */
    public static void starSer(Context mAct) {
        if (Build.VERSION.SDK_INT > 20 && Build.VERSION.SDK_INT < 24) {//JobService只支持21,22,23 也就是5.0  5.1  6.0的系统
            if (!isServiceRunning("" + WorkService.class.getName(), mAct)) {
                Intent intent1 = new Intent(mAct, MyJobService.class);
                intent1.setAction("myaction");
                mAct.startService(intent1);
            } else {
                Log.e("JOBSERVICE", "5.0以上手机版本  服务运行中+++");
            }
        } else {
            if (!isServiceRunning(WorkService.class.getName(), mAct)) {
                Intent intent1 = new Intent(mAct, WorkService.class);
                mAct.startService(intent1);
            } else {
                Log.e("JOBSERVICE", "5.1以下的手机版本  服务运行中+++");
            }
        }
    }

    /**
     * 判断服务是否处于运行状态.
     *
     * @param servicename
     * @param context
     * @return
     */
    public static boolean isServiceRunning(String servicename, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            if (servicename.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
