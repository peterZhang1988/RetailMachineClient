package com.example.retailmachineclient.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CreateOrderModel implements Parcelable {
    String WeChatQR;
    String AliQR;
    String OutTradeNo;

    protected CreateOrderModel(Parcel in) {
        WeChatQR = in.readString();
        AliQR = in.readString();
        OutTradeNo = in.readString();
        OrderName = in.readString();
    }

    public static final Creator<CreateOrderModel> CREATOR = new Creator<CreateOrderModel>() {
        @Override
        public CreateOrderModel createFromParcel(Parcel in) {
            return new CreateOrderModel(in);
        }

        @Override
        public CreateOrderModel[] newArray(int size) {
            return new CreateOrderModel[size];
        }
    };

    public String getWeChatQR() {
        return WeChatQR;
    }

    public void setWeChatQR(String weChatQR) {
        WeChatQR = weChatQR;
    }

    public String getAliQR() {
        return AliQR;
    }

    public void setAliQR(String aliQR) {
        AliQR = aliQR;
    }

    public String getOutTradeNo() {
        return OutTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        OutTradeNo = outTradeNo;
    }

    public String getOrderName() {
        return OrderName;
    }

    public void setOrderName(String orderName) {
        OrderName = orderName;
    }

    String OrderName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(WeChatQR);
        parcel.writeString(AliQR);
        parcel.writeString(OutTradeNo);
        parcel.writeString(OrderName);
    }
}
