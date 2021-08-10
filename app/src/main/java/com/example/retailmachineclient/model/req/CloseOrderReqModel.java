package com.example.retailmachineclient.model.req;

public class CloseOrderReqModel {

    //售货识别ID 序列号
    String SalesID;
    //订单编号 人脸时用新的订单编号吗？
    String OutTradeNo;
    //0-出货失败 1-出货成功 2-订单结束
    String EndType;
    //备注
    String EndRemarks;

    public String getSalesID() {
        return SalesID;
    }

    public void setSalesID(String salesID) {
        SalesID = salesID;
    }

    public String getOutTradeNo() {
        return OutTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        OutTradeNo = outTradeNo;
    }

    public String getEndType() {
        return EndType;
    }

    public void setEndType(String endType) {
        EndType = endType;
    }

    public String getEndRemarks() {
        return EndRemarks;
    }

    public void setEndRemarks(String endRemarks) {
        EndRemarks = endRemarks;
    }
}
