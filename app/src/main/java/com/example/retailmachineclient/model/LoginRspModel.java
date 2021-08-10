package com.example.retailmachineclient.model;

public class LoginRspModel {
    String Code;
    String ErrorMsg;

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

    public UserMsgModel getUserMsgModel() {
        return Data;
    }

    public void setUserMsgModel(UserMsgModel userMsgModel) {
        this.Data = userMsgModel;
    }

    UserMsgModel Data;
}
