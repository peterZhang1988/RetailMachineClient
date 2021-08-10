package com.example.retailmachineclient.util;

import android.content.Context;
import android.os.Build;

public class CloseBarUtil {

    /**
     * 关闭底部导航条
     */
    public static void closeBar(Context myContext) {
        Logger.e("导航栏 closeBar ");
//        Intent i = new Intent("com.cdhx.removebar");
//        myContext.sendBroadcast(i);
        try {
            // 需要root 权限
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79";
            if (vr.SDK_INT >= vc.ICE_CREAM_SANDWICH) {
                ProcID = "42"; // ICS AND NEWER
            }
            Logger.e("导航栏 closeBar "+ProcID);
            // 需要root 权限
            Process proc = Runtime.getRuntime().exec(
                    new String[]{
                            "su",
                            "-c",
                            "service call activity " + ProcID
                                    + " s16 com.android.systemui"}); // WAS 79
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示底部导航条
     */
    public static void showBar(Context mCtx) {
        Logger.e("显示底部导航条 start");
        try {
            String Command = "am startservice -n com.android.systemui/.SystemUIService";
            Process proc = Runtime.getRuntime().exec(
                    new String[]{"su", "-c", Command});
            proc.waitFor();
        } catch (Exception e) {
            Logger.e("显示底部导航条 error");
            e.printStackTrace();
        }
    }


}