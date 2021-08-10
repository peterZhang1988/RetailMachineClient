package com.example.retailmachineclient.model;

public class MessageEvent {
    public MessageEvent(int type){
        this.type = type;
    }

    public MessageEvent(int type, int position) {
        this.type = type;
        this.position = position;
    }

    public MessageEvent(int type, Object tag) {
        this.type = type;
        this.tag = tag;
    }

    public static final int EventType_ADD_GOODS = 1;
    public static final int EventType_ADD_GOODS_IN_DETAIL = 2;
    public static final int EventType_DELETE_SUCCESS = 3;
    public static final int EventType_QUERY_GOODS_SUCCESS = 4;
    public static final int EventType_QUERY_GOODS_FAIL = 9;
    public static final int EventType_CreateOrder_Success = 5;
    public static final int EventType_CreateOrder_Fail = 6;

    public static final int EventType_QueryOrder_Success = 7;
    public static final int EventType_QueryOrder_Fail = 8;

    public static final int EventType_PayByFace_Success = 10;//人脸支付成功
    public static final int EventType_TradeSuccessOrder_Success = 11;
    public static final int EventType_TradeSuccessOrder_Fail = 12;

    public static final int EventType_GetGoodsFromLift_Success = 13;
    public static final int EventType_GetGoodsFromLift_Fail = 14;
    public static final int EventType_GetGoodsFromLift_notify = 15;//提醒取货
    public static final int EventType_GetGoodsFromLift_Timeout = 16;//定时器

    public static final int EventType_CloseOrder_Success = 17;
    public static final int EventType_CloseOrder_Fail = 18;

    public static final int EventType_ErrorReportOrder_Success = 19;
    public static final int EventType_ErrorReportOrder_Fail = 20;

    public static final int EventType_Pay_Timeout = 21;


    public static final int EventType_QUERY_GOODS_NUM_SUCCESS = 23;
    public static final int EventType_QUERY_GOODS_NUM_FAIL = 24;

    public static final int EventType_QUERY_GOODS_NUM_OPERATE = 25;

    public static final int EventType_LOGIN_SUCCESS = 27;
    public static final int EventType_LOGIN_FAIL = 28;


    public static final int EventType_LOGIN_AI_SUCCESS = 29;
    public static final int EventType_LOGIN_AI_FAIL = 30;
    public static final int EventType_TCP_CONNECTED = 31;
    public static final int EventType_GET_GetRobotID = 32;

    public static final int EventType_GET_GOODS_Timeout = 33;
    public static final int EventType_START_GET_GOODS_Timeout = 34;
    public static final int EventType_GET_GOODS_Timeout_Over = 35;

    public static final int EventType_Card_Start_task = 36;
    public static final int EventType_Card_Start_Time = 37;

    public static final int EventType_LOGIN_SUCCESS_Auto = 40;
    public static final int EventType_LOGIN_FAIL_Auto = 38;
    public static final int EventType_LOGIN_FAIL_Error_Auto = 39;
    public static final int EventType_Test = 10000;
    public static final int EventType_LOGIN_FAIL_Error = 41;


    public static final int updateProgress = 42;
    public static final int apkDownloadSucceed = 43;
    public static final int apkDownloadFailed = 44;
    public static final int apkDownloadCancel = 45;
    public static final int apkDownloadCancelShow = 46;

    public static final int refund_successed = 51;
    public static final int refund_fail = 52;

    public static final int EventType_CHANGE_SPPINER_ITEM =53;
    public static final int EventType_Refresh_listview =54;

        public static final int EventType_SHOW_PROGRESS =61;
    public static final int EventType_DISMISS_PROGRESS =62;


    public static final int EventType_GET_VERSIONH_SUCCESS =63;
    public static final int EventType_GET_VERSIONH_FAIL =64;
    public static final int EventType_GET_VERSIONH_ERROR =65;

    public static final int EventType_QUERY_NET_ERROR = 66;
//    updateProgress,
//    apkDownloadSucceed,
//    apkDownloadFailed
//    public static final int EventType_LOGIN_FAIL = 28;
//    public static final int EventType_CreateOrder_Fail = 6;
//    public static final int EventType_CreateOrder_Fail = 6;
//    public static final int EventType_CreateOrder_Fail = 6;
//    public static final int EventType_CreateOrder_Fail = 6;
//    public static final int EventType_CreateOrder_Fail = 6;
//    public static final int EventType_CreateOrder_Fail = 6;



    //消息类型 1：商品列表点击购物 2：商品详情页点击添加 3：商品购物车列表点击删除 4

    int type;
    //对象参数
    Object tag;

    public Object getTagHelper() {
        return tagHelper;
    }

    public void setTagHelper(Object tagHelper) {
        this.tagHelper = tagHelper;
    }

    Object tagHelper;
    //商品id
    int id;
    //简单参数 页码
    int pageIndex;
    //简单参数 当前页中位置
    int pagePosition;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(int pagePosition) {
        this.pagePosition = pagePosition;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;//简单参数 当前位置


}
