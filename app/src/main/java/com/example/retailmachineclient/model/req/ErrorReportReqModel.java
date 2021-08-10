package com.example.retailmachineclient.model.req;

public class ErrorReportReqModel {
    //售货识别ID 序列号
    String SalesID;
    //错误数据
    String LogMsg;
    //错误编号
    String ErrorCode;

    public String getSalesID() {
        return SalesID;
    }

    public void setSalesID(String salesID) {
        SalesID = salesID;
    }

    public String getLogMsg() {
        return LogMsg;
    }

    public void setLogMsg(String logMsg) {
        LogMsg = logMsg;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }
}
