package com.example.retailmachineclient.model.req;

public class TradeSuccessReqModel {
    //售货识别ID 序列号
    String SalesID;
    //订单编号
    String OutTradeNo;
    //最终支付订单编号
    String EndOutTradeNo;
    //订单结束回复 "TRADE_SUCCESS"
    String EndOrderResult;

    public String getEndStatus() {
        return EndStatus;
    }

    public void setEndStatus(String endStatus) {
        EndStatus = endStatus;
    }

    //订单结束标签：0-微信；1-支付宝；2-支付宝人脸；3-刷卡
    String EndStatus;


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

    public String getEndOutTradeNo() {
        return EndOutTradeNo;
    }

    public void setEndOutTradeNo(String endOutTradeNo) {
        EndOutTradeNo = endOutTradeNo;
    }

    public String getEndOrderResult() {
        return EndOrderResult;
    }

    public void setEndOrderResult(String endOrderResult) {
        EndOrderResult = endOrderResult;
    }
}
