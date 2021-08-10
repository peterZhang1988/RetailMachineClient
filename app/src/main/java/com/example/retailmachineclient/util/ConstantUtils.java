package com.example.retailmachineclient.util;

import android.os.Environment;

public class ConstantUtils {
//    public static final String AI_SERVER_IP = "192.168.1.22";//测试地址
//    public static final int AI_SERVER_PORT = 189;

    public static final String AI_SERVER_IP = "192.168.0.96";//正式地址
    public static final int AI_SERVER_PORT = 189;

    public static String ALI_PAY_APP_Id = "";
    public static String ALI_PAY_PRIVATE_KEY_Id = "";

    //取货门上推打开
    public static final int ORDER_DATA_AND_RETURN = 100;

    //取货门上推打开
    public static final int ORDER_UP_PUSH = 101;
    //取货门上推打开结果
    public static final int ORDER_UP_PUSH_RESULT = 102;

    //取货门下推关门
    public static final int ORDER_DOWN_PUSH = 103;
    //取货门下推关门结果
    public static final int ORDER_DOWN_PUSH_RESULT = 104;

    //电机旋转
    public static final int ORDER_POWER_MACHINE = 105;
    //电机旋转结果
    public static final int ORDER_POWER_MACHINE_RESULT = 106;

    //升降机移动
    public static final int ORDER_LIFT_MOVE = 107;
    //升降机移动结果
    public static final int ORDER_LIFT_MOVE_RESULT = 108;

    //货盘是否为空
    public static final int ORDER_PALLET_EMPTY = 109;
    //货盘是否为空结果
    public static final int ORDER_PALLET_EMPTY_RESULT = 110;

    //取货门是否夹手
    public static final int ORDER_PINCH_HANDS = 111;
    //取货门是否夹手结果
    public static final int ORDER_PINCH_HANDS_RESULT = 112;

    //状态机参数类型

    public final static int TYPE_QUERY_PALLET_STATUS = 1;//单独查询升降机状态
    public final static int TYPE_OPERATE_PALLET = 2;//移动到指定的位置
    public final static int TYPE_QUERY_PALLET_STATUS_IS_POSITION = 3;
    public final static int TYPE_QUERY_MACHINE_STATUS_IS_POSITION = 4;
    public final static int TYPE_OPEN_DOOR = 5;//打开舱门
    public final static int TYPE_QUERY_DOOR_STATUS_OPEN = 6;//开门是否完成
    public final static int TYPE_QUERY_DOOR_STATUS_CLOSE = 7;//关门是否完成
    public final static int TYPE_QUERY_DOOR_CLOSE_CASE = 8;//关门状态条件
    public final static int TYPE_CLOSE_DOOR = 9;//关门
    public final static int TYPE_QUERY_CLOSE_DOOR_STATUS = 10;//查询推杆指令和防夹手状态
    public final static int TYPE_OPERATE_PALLET_TO_7 = 11;//复位
    public final static int TYPE_OPERATE_PROTECT_HAND = 12;//查询是否触发防夹手

    public final static int TYPE_QUERY_DOOR_CLOSE_CASE_EMPTY = 13;//关门状态条件 货盘是否为空

    public final static int TYPE_QUERY_DOOR_STATUS = 14;//货柜状态

    public final static int TYPE_QUERY_LIFT_STATUS = 15;//查询升降机是否可以启动

    public final static int TYPE_START_MATCHINE = 16;//启动电机

    //错误代码
    //查询层数是否是第7层时 超时
    public final static int TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME = 1;

    //升降机复位到7层 超时
    public final static int TYPE_ERROR_RESET_TO_7_OVERTIME = 3;

    //升降机复位到7层 一直复位不成功
    public final static int TYPE_ERROR_RESET_TO_7_FAIL = 2;

    //启动升降机到指定取货楼层 失败
    public final static int TYPE_ERROR_MOVE_TARGET_FAIL = 4;

    //启动升降机到指定取货楼层 检查所在货柜层 未移动到指定位置
    public final static int TYPE_ERROR_MOVE_TARGET_CHECK_FAIL = 5;

    //电机启动异常 旋转出货
    public final static int TYPE_ERROR_START_MACHINE = 6;

    //电机正常启动后 执行旋转异常
    public final static int TYPE_ERROR_MACHINE_RUN = 7;

    //出货时 启动升降机 一直复位不成功
    public final static int TYPE_ERROR_OPEN_RESET_TO_7_FAIL = 8;

    //货盘没货 不满足打开舱门的条件
    public final static int TYPE_ERROR_LIFT_EMPTY = 9;

    //打开开门指令执行失败
    public final static int TYPE_ERROR_OPEN_DOOR_FAIL = 10;

    //舱门打开步骤没有完成
    public final static int TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY = 11;

    //不满足条件执行关门
    public final static int TYPE_ERROR_CANNOT_CLOSE_CASE = 12;

    //关门指令发送失败
    public final static int TYPE_ERROR_CLOSE_DOOR_FAIL = 13;


    //升降机指令异常
    public final static int TYPE_ERROR_LIFT_MATCHINE_FAIL = 21;
    //旋转电机指令异常
    public final static int TYPE_ERROR_GOOD_PUSH_MATCHINE_FAIL = 22;
    //开门前查询推杆指令异常
    public final static int TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL = 23;
    //关门后查询推杆指令异常
    public final static int TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL_CLOSE = 24;

    //用户一次可以选购物品的数量
    public static int NUMBER_BUY = 1;
    public static String  APP_LOGIN_DOMAIN="http://101.37.124.215:88";//ip
    //是否演示测试
    public static boolean IS_TEST = false;

    //中文 英文 法文

    public static final int LAN_TYPE_CHINESE = 1;
    public static final int LAN_TYPE_ENGLISH = 2;
    public static final int LAN_TYPE_FRENCH = 3;

    //选购页面查询货物间隔 默认 20000ms
    public static  int QUERY_GOODS_WAIT_TIME_NUM = 20;

    //选购页面查询货物间隔 默认 20000ms
    public static  long  QUERY_GOODS_WAIT_TIME_NUM_USE = QUERY_GOODS_WAIT_TIME_NUM*1000;

    //支付页，倒计时返回首页 默认90s
    public static  int PAY_TIME_OUT_NUM = 90;

    //支付页，查询支付状态间隔 默认5s
    public static  int QUERY_PAY_STATUS_TIME_OUT_NUM = 5;

    //刷卡页，倒计时 默认60s
    public static  int PAY_BY_CARD_WAIT_TIME_NUM = 60;

    //出货成功后，倒计时返回首页 默认5s
    public static  int BUY_SUCCESS_WAIT_TIME_NUM = 5;
    
    //出货页面，关门倒计时 默认 120s 关门总倒计时
    public static  int CLOSE_DOOR_LONG_TIME_NUM = 120;

    //机器指令单独执行3次超时就返回失败 指令超时重试次数 默认3次
    public static  int SEND_ORDER_TIME_OUT_NUM = 3;

    //货盘楼层查询指令  150ms 一次 ，10 秒超时
    public static  int QUERY_LIFT_FLOOR_WAIT_TIME_NUM = 10;

    //移动到指定层次数 3次
    public static  int MOVE_LIFT_FLOOR_WAIT_TIME_NUM = 3;

    //查询电机超时 5m
    public static  int QUERY_MATCHINE_WAIT_TIME_NUM = 5;

    //操作电机超时 5次
    public static  int OPERATE_MATCHINE_WAIT_TIME_NUM = 5;

    // 货盘复位超时(次) 3次 不使用
    public static  int OPERATE_LIFT_TO7_WAIT_TIME_NUM = 3;
    
    // 查询推杆超时(次) 10次
    public static  int QUERY_DOOR_WAIT_TIME_NUM = 10;

    // 开门指令执行超时(次) 10次
    public static  int OPEN_DOOR_WAIT_TIME_NUM = 10;

    // 舱门打开超时(秒) 30秒
    public static  int GET_GOODS_WAIT_TIME_NUM = 30;

    //出货页面，关门后查询防夹手时长 默认 5s
    public static  int QUERY_PROTECT_WAIT_TIME_NUM = 5;

//    // 关门超时(次) 5次
//    public static  int CLOSE_DOOR_WAIT_TIME_NUM = 5;

    //关闭货门时，检查推杆状态 和 防夹手
//    public static  int QUERY_DOOR_AND_PROTECT_WAIT_TIME_NUM = 5;

    public static boolean LANGUAGE_SETTING_CN=true;
    public static boolean LANGUAGE_SETTING_EN=true;
    public static boolean LANGUAGE_SETTING_FN=false;
    public static final String ROBOT_FOLDER_DOWNLOAD= Environment.getExternalStorageDirectory().getPath()+"/"+"RetailMachineApkDownload"+"/";
}
