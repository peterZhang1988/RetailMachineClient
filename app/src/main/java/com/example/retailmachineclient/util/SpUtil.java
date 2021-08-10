package com.example.retailmachineclient.util;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * desc：用于保存设置的参数
 */
public class SpUtil {
    public static final String LANGUAGE = "language";
    private static final String SP_NAME = "poemTripSpref";
    public static final String CHARGE_STATUS="chargeStatus";
    public static final String LOGIN_PASSWORD="password";  //登录密码
    public static final String LOGIN_ACCOUNT="account";    //登录账号
    public static final String TCP_PORT="tcpPort";       //tcp 端口
    public static final String TCP_IP="tcpIp";           //tcp IP
    private static SpUtil spUtil;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    public static final String HW_ID="io.device.dadao.robot";//华为ID
    public static final String HW_APP_KEY="rnjiKsIkJOh/wVj17AItCw==";//华为APPKEY
    public static final String SALES_Id = "salesId";
    public static final String PHONE = "phone";
    public static final String ROBOT_Id = "robotId";

    public static final String LANGUAGE_SETTING_CN = "LANGUAGE_SETTING_CN";//控制是否可以点击 开关
    public static final String LANGUAGE_SETTING_EN = "LANGUAGE_SETTING_EN";//控制是否可以点击 开关
    public static final String LANGUAGE_SETTING_FN = "LANGUAGE_SETTING_FN";//控制是否可以点击 开关
    public static final String IP_ADDRESS = "IP_ADDRESS";
    public static final String NUMBER_BUY = "NUMBER_BUY";

    public static final String ALI_PAY_APP_Id = "AliPayAppID";
    public static final String ALI_PAY_PRIVATE_KEY_Id = "AliPayPrivateKey";




    //1选购页面查询货物间隔 默认 20s
//    public static  int QUERY_GOODS_WAIT_TIME_NUM = 20;
    public static final String QUERY_GOODS_WAIT_TIME_NUM = "QUERY_GOODS_WAIT_TIME_NUM";

    //2支付页，倒计时返回首页 默认90s
//    public static  int PAY_TIME_OUT_NUM = 90;
    public static final String PAY_TIME_OUT_NUM = "PAY_TIME_OUT_NUM";

    //3支付页，查询支付状态间隔 默认5s
//    public static  int QUERY_PAY_STATUS_TIME_OUT_NUM = 5;
    public static final String QUERY_PAY_STATUS_TIME_OUT_NUM = "QUERY_PAY_STATUS_TIME_OUT_NUM";

    //4刷卡页，倒计时 默认60s
//    public static  int PAY_BY_CARD_WAIT_TIME_NUM = 60;
    public static final String PAY_BY_CARD_WAIT_TIME_NUM = "PAY_BY_CARD_WAIT_TIME_NUM";

    //5出货成功后，倒计时返回首页 默认5s
//    public static  int BUY_SUCCESS_WAIT_TIME_NUM = 5;
    public static final String BUY_SUCCESS_WAIT_TIME_NUM = "BUY_SUCCESS_WAIT_TIME_NUM";

    //6出货页面，关门倒计时 默认 120s 关门总倒计时
//    public static  int CLOSE_DOOR_LONG_TIME_NUM = 120;
    public static final String CLOSE_DOOR_LONG_TIME_NUM = "CLOSE_DOOR_LONG_TIME_NUM";

    //7机器指令单独执行3次超时就返回失败 指令超时重试次数 默认3次
//    public static final int SEND_ORDER_TIME_OUT_NUM = 4;
    public static final String SEND_ORDER_TIME_OUT_NUM = "SEND_ORDER_TIME_OUT_NUM";

    //8货盘楼层查询指令  150ms 一次 ，10 秒超时
//    public static  int QUERY_LIFT_FLOOR_WAIT_TIME_NUM = 10;
    public static final String QUERY_LIFT_FLOOR_WAIT_TIME_NUM = "QUERY_LIFT_FLOOR_WAIT_TIME_NUM";

    //9移动到指定层次数 3次
//    public static  int MOVE_LIFT_FLOOR_WAIT_TIME_NUM = 3;
    public static final String MOVE_LIFT_FLOOR_WAIT_TIME_NUM = "MOVE_LIFT_FLOOR_WAIT_TIME_NUM";

    //10查询电机超时 5次
//    public static  int QUERY_MATCHINE_WAIT_TIME_NUM = 5;
    public static final String QUERY_MATCHINE_WAIT_TIME_NUM = "QUERY_MATCHINE_WAIT_TIME_NUM";

    //11操作电机超时 5次
//    public static  int OPERATE_MATCHINE_WAIT_TIME_NUM = 5;
    public static final String OPERATE_MATCHINE_WAIT_TIME_NUM = "OPERATE_MATCHINE_WAIT_TIME_NUM";

    // 12货盘复位超时(次) 3次
//    public static  int OPERATE_LIFT_TO7_WAIT_TIME_NUM = 3;
    public static final String OPERATE_LIFT_TO7_WAIT_TIME_NUM = "OPERATE_LIFT_TO7_WAIT_TIME_NUM";

    // 13查询推杆超时(次) 10次
//    public static  int QUERY_DOOR_WAIT_TIME_NUM = 10;
    public static final String QUERY_DOOR_WAIT_TIME_NUM = "QUERY_DOOR_WAIT_TIME_NUM";

    //14 开门超时(次) 10次
//    public static  int OPEN_DOOR_WAIT_TIME_NUM = 10;
    public static final String OPEN_DOOR_WAIT_TIME_NUM = "OPEN_DOOR_WAIT_TIME_NUM";

    // 15取货超时(次) 30秒
//    public static  int GET_GOODS_WAIT_TIME_NUM = 30;
    public static final String GET_GOODS_WAIT_TIME_NUM = "GET_GOODS_WAIT_TIME_NUM";

    //16出货页面，关门后查询防夹手时长 默认 5s
//    public static  int QUERY_PROTECT_WAIT_TIME_NUM = 5;
    public static final String QUERY_PROTECT_WAIT_TIME_NUM = "QUERY_PROTECT_WAIT_TIME_NUM";


    private SpUtil(Context context) {
        context=context.getApplicationContext();
        sharedPreferences = context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SpUtil getInstance(Context context) {
        if (spUtil == null) {
            synchronized (SpUtil.class) {
                if (spUtil == null) {
                    spUtil = new SpUtil(context);
                }
            }
        }
        return spUtil;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
        Logger.e("设置的语言："+value);
    }

    public void putBoolean(String key, boolean value){
        editor.putBoolean(key,value);
        editor.commit();
    }

    public void putInt(String key, int value){
        editor.putInt(key,value);
        editor.commit();
    }


    public String getString(String key) {
        return sharedPreferences.getString(key,"");
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,true);
    }

    public int getInt(String key,int defaultData){
        return sharedPreferences.getInt(key,defaultData);
    }
}
