package com.example.retailmachineclient.model;

public class QueryOrderRspModel {
    String Code;
    String ErrorMsg;
    QueryOrderModel Data;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        ErrorMsg = errorMsg;
    }

    public QueryOrderModel getData() {
        return Data;
    }

    public void setData(QueryOrderModel data) {
        Data = data;
    }
}
