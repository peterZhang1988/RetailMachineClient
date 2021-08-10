package com.example.retailmachineclient.util;

import android.app.ActivityManager;
import android.content.Context;

import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.model.GoodInfoModel;

import java.text.DecimalFormat;
import java.util.List;

public class Utils {


    /**
     * 初始化sp的值
     */
    public static void setCons2SpSetValue(){
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_GOODS_WAIT_TIME_NUM,ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.PAY_TIME_OUT_NUM,ConstantUtils.PAY_TIME_OUT_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_PAY_STATUS_TIME_OUT_NUM,ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.PAY_BY_CARD_WAIT_TIME_NUM,ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.BUY_SUCCESS_WAIT_TIME_NUM,ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM);

        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.CLOSE_DOOR_LONG_TIME_NUM,ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.SEND_ORDER_TIME_OUT_NUM,ConstantUtils.SEND_ORDER_TIME_OUT_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_LIFT_FLOOR_WAIT_TIME_NUM,ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.MOVE_LIFT_FLOOR_WAIT_TIME_NUM,ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_MATCHINE_WAIT_TIME_NUM,ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM);

        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.OPERATE_MATCHINE_WAIT_TIME_NUM,ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.OPERATE_LIFT_TO7_WAIT_TIME_NUM,ConstantUtils.OPERATE_LIFT_TO7_WAIT_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_DOOR_WAIT_TIME_NUM,ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.OPEN_DOOR_WAIT_TIME_NUM,ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.GET_GOODS_WAIT_TIME_NUM,ConstantUtils.GET_GOODS_WAIT_TIME_NUM);
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.QUERY_PROTECT_WAIT_TIME_NUM,ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM);
    }
    /**
     * 更新sp参数到静态变量
     */
    public static void updateSp2ConsSetValue(){
        ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_GOODS_WAIT_TIME_NUM,0);
        ConstantUtils.PAY_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.PAY_TIME_OUT_NUM,0);
        ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_PAY_STATUS_TIME_OUT_NUM,0);
        ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.PAY_BY_CARD_WAIT_TIME_NUM,0);
        ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM =  SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.BUY_SUCCESS_WAIT_TIME_NUM,0);

        ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.CLOSE_DOOR_LONG_TIME_NUM,0);
        ConstantUtils.SEND_ORDER_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.SEND_ORDER_TIME_OUT_NUM,0);
        ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_LIFT_FLOOR_WAIT_TIME_NUM,0);
        ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.MOVE_LIFT_FLOOR_WAIT_TIME_NUM,0);
        ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_MATCHINE_WAIT_TIME_NUM,0);

        ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPERATE_MATCHINE_WAIT_TIME_NUM,0);
        ConstantUtils.OPERATE_LIFT_TO7_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPERATE_LIFT_TO7_WAIT_TIME_NUM,0);
        ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_DOOR_WAIT_TIME_NUM,0);
        ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPEN_DOOR_WAIT_TIME_NUM,0);
        ConstantUtils.GET_GOODS_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.GET_GOODS_WAIT_TIME_NUM,0);
        ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_PROTECT_WAIT_TIME_NUM,0);

        ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM_USE = ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM*1000;
    }

    /**
     * 更新sp参数到静态变量
     */
//    public static void updateSp2ConsSetValue(){
//        ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_GOODS_WAIT_TIME_NUM,ConstantUtils.QUERY_GOODS_WAIT_TIME_NUM);
//        ConstantUtils.PAY_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.PAY_TIME_OUT_NUM,ConstantUtils.PAY_TIME_OUT_NUM);
//        ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_PAY_STATUS_TIME_OUT_NUM,ConstantUtils.QUERY_PAY_STATUS_TIME_OUT_NUM);
//        ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.PAY_BY_CARD_WAIT_TIME_NUM,ConstantUtils.PAY_BY_CARD_WAIT_TIME_NUM);
//        ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM =  SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.BUY_SUCCESS_WAIT_TIME_NUM,ConstantUtils.BUY_SUCCESS_WAIT_TIME_NUM);
//
//        ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.CLOSE_DOOR_LONG_TIME_NUM,ConstantUtils.CLOSE_DOOR_LONG_TIME_NUM);
//        ConstantUtils.SEND_ORDER_TIME_OUT_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.SEND_ORDER_TIME_OUT_NUM,ConstantUtils.SEND_ORDER_TIME_OUT_NUM);
//        ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_LIFT_FLOOR_WAIT_TIME_NUM,ConstantUtils.QUERY_LIFT_FLOOR_WAIT_TIME_NUM);
//        ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.MOVE_LIFT_FLOOR_WAIT_TIME_NUM,ConstantUtils.MOVE_LIFT_FLOOR_WAIT_TIME_NUM);
//        ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_MATCHINE_WAIT_TIME_NUM,ConstantUtils.QUERY_MATCHINE_WAIT_TIME_NUM);
//
//        ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPERATE_MATCHINE_WAIT_TIME_NUM,ConstantUtils.OPERATE_MATCHINE_WAIT_TIME_NUM);
//        ConstantUtils.OPERATE_LIFT_TO7_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPERATE_LIFT_TO7_WAIT_TIME_NUM,ConstantUtils.OPERATE_LIFT_TO7_WAIT_TIME_NUM);
//        ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_DOOR_WAIT_TIME_NUM,ConstantUtils.QUERY_DOOR_WAIT_TIME_NUM);
//        ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.OPEN_DOOR_WAIT_TIME_NUM,ConstantUtils.OPEN_DOOR_WAIT_TIME_NUM);
//        ConstantUtils.GET_GOODS_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.GET_GOODS_WAIT_TIME_NUM,ConstantUtils.GET_GOODS_WAIT_TIME_NUM);
//        ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.QUERY_PROTECT_WAIT_TIME_NUM,ConstantUtils.QUERY_PROTECT_WAIT_TIME_NUM);
//    }

    public static void wrapGood(GoodInfoModel ne, GoodInfoModel old){
        ne.setContainerNum(old.getContainerNum());
        ne.setContainerFloor(old.getContainerFloor());
        ne.setGoodsStatus(old.getGoodsStatus());
        ne.setGoodsInventory(old.getGoodsInventory());
        ne.setGoodsMaxInventory(old.getGoodsMaxInventory());
        ne.setPrice(old.getPrice());
        ne.setGoodsName1(old.getGoodsName1());
        ne.setGoodsName2(old.getGoodsName2());
        ne.setGoodsName3(old.getGoodsName3());
        ne.setGoodsDescription1(old.getGoodsDescription1());
        ne.setGoodsDescription2(old.getGoodsDescription2());
        ne.setGoodsDescription3(old.getGoodsDescription3());
        ne.setGoodsImage(old.getGoodsImage());
        ne.setClassID(old.getClassID());
        ne.setGoodsID(old.getGoodsID());
    }

    public static byte[] getBytes(byte[] buffer, int offset, int length) {
        int num = 0;
        byte[] dstbyte = new byte[length];
        for (num = 1; num <= length; num++) {
            dstbyte[num - 1] = buffer[offset + num - 1];
        }
        return dstbyte;
    }

    public static int putBytes(byte[] dst, int offset, byte[] source, int length) {
        int num = 0;
        for (num = 0; num < length; num++) {
            dst[offset + num] = source[num];
        }
        return offset + num;
    }

    public static byte[] getBytes(Byte[] buffer, int offset, int length) {
        int num = 0;
        byte[] dstbyte = new byte[length];
        for (num = 1; num <= length; num++) {
            dstbyte[num - 1] = buffer[offset + num - 1];
        }
        return dstbyte;
    }

    /**
     * 把数组中指定长度的内容转换为字符丿
     *
     * @param buffer
     * @param length
     * @return
     */
    public static String byteBufferToHexString(byte[] buffer, int length) {
        StringBuilder mBuilder = new StringBuilder();
        int startIndex = 0;
        for (int i = startIndex; i < length; i++) {
            int v = buffer[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                mBuilder.append(0);
            }
            mBuilder.append(hv);
        }
        return "len:" + length + " [ " + mBuilder.toString().toUpperCase()
                + " ] ";
    }

    /**
     * @param buffer
     * @return
     */
    public static int byte2int(byte buffer) {
        return buffer & 0xFF;
    }

    public static String int2byte(byte[] buffer, int length) {
        StringBuilder mBuilder = new StringBuilder();
        int startIndex = 0;
        for (int i = startIndex; i < length; i++) {
            int v = buffer[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                mBuilder.append(0);
            }
            mBuilder.append(hv);
        }
        return "len:" + length + " [ " + mBuilder.toString().toUpperCase()
                + " ] ";
    }

    /**
     * 数组转为16进制字符串输凿
     *
     * @param buffer
     * @return
     */
    public static String byteBufferToHexString(byte[] buffer) {
        if(buffer!= null){
            return byteBufferToHexString(buffer, buffer.length);
        }else{
            return "data is null";
        }

    }

    /**
     * 把一个整数变化为丿个小敿
     * 妿115-> 0.115
     * 50->0.5
     *
     * @param v
     * @return
     */
    public static double intToDec(int v) {
        if (v < 10) {
            return v / 10.0;
        } else if (v < 100) {
            return v / 100.0;
        } else if (v < 1000) {
            return v / 1000.0;
        } else {
            return v / 10000.0;
        }
    }


    //byte ԫ int քРۥתۻ
    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static int byteToInt(byte b) {
        return b & 0xFF;
    }


    public static String intToFloat(int num) {
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String s = df.format(num);//返回的是String类型
        return s;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isAPPALive(Context mContext) {
        ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        // "你的包名";
        String str = "com.example.retailmachineclient";
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(str)//如果想要手动输入的话可以str换成<span style="font-family: Arial, Helvetica, sans-serif;">MY_PKG_NAME，下面相同</span>
                    || info.baseActivity.getPackageName().equals(str)) {
                isAppRunning = true;
                break;
            }
        }
        Logger.e("isAPPALive value =" + isAppRunning);

        return isAppRunning;
    }


    /**
     * 返回当前的应用是否处于前台显示状态
     *
     * @param
     * @return
     */
    public static boolean isTopActivity(Context mContext) {
        //_context是一个保存的上下文
        ActivityManager activityManager = (ActivityManager) mContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
        if (list.size() == 0) return false;
        String str = "com.example.retailmachineclient";
        for (ActivityManager.RunningAppProcessInfo process : list) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    process.processName.equals(str)) {
                return true;
            }
        }
        return false;
    }
}
