package com.example.retailmachineclient.model;


import java.util.List;

public class GetGoodsInfoRspModel {
    String Code;
    String ErrorMsg;
    String Phone;

    public List<GoodInfoModel> getUserMsgModel() {
        return Data;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
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

    public void setUserMsgModel(List<GoodInfoModel> userMsgModel) {
        this.Data = userMsgModel;
    }

    List<GoodInfoModel> Data;
}
