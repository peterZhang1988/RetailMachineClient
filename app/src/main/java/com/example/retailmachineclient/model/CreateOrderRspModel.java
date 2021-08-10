package com.example.retailmachineclient.model;

public class CreateOrderRspModel {
    String Code;
    String ErrorMsg;
    CreateOrderModel Data;

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

    public CreateOrderModel getData() {
        return Data;
    }

    public void setData(CreateOrderModel data) {
        Data = data;
    }

}
