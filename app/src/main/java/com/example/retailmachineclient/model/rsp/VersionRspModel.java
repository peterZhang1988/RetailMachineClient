package com.example.retailmachineclient.model.rsp;

public class VersionRspModel {

    String Code;
    String ErrorMsg;
    VersionMsgModel Data;

    public VersionMsgModel getData() {
        return Data;
    }

    public void setData(VersionMsgModel data) {
        Data = data;
    }

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
}
