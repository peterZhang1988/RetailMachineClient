package com.example.retailmachineclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

public class CardPayMsgModel extends LitePalSupport implements Parcelable {
    String message;//交易结果信息 所有

    String payNum;//交易单号
    String cardNum;//交易卡号
    long timestamp;
    String transDate;

    String msgType;//交易类型

    String payAmount;//交易金额

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    String orderNum;
    int isDeleted;//记录是否删除，默认0 ，1时数据为删除

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }



    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }





//    public int isDeleted() {
//        return isDeleted;
//    }
//
//    public void setDeleted(int deleted) {
//        isDeleted = deleted;
//    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayNum() {
        return payNum;
    }

    public void setPayNum(String payNum) {
        this.payNum = payNum;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public CardPayMsgModel() {

    }
    @Override
    public int describeContents() {
        return 0;
    }

    public CardPayMsgModel(Parcel in) {
        message = in.readString();
        payNum = in.readString();
        timestamp = in.readLong();
        msgType = in.readString();
    }

    public static final Creator<CardPayMsgModel> CREATOR = new Creator<CardPayMsgModel>() {
        @Override
        public CardPayMsgModel createFromParcel(Parcel in) {
            return new CardPayMsgModel(in);
        }

        @Override
        public CardPayMsgModel[] newArray(int size) {
            return new CardPayMsgModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeString(payNum);
        parcel.writeLong(timestamp);
        parcel.writeString(msgType);
    }

}
