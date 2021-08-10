package com.example.retailmachineclient.model;

import android.os.Parcel;
import android.os.Parcelable;

public class QueryOrderModel implements Parcelable {
    String AliRequest;
    String WeChatRequest;

    protected QueryOrderModel(Parcel in) {
        AliRequest = in.readString();
        WeChatRequest = in.readString();
    }

    public static final Creator<QueryOrderModel> CREATOR = new Creator<QueryOrderModel>() {
        @Override
        public QueryOrderModel createFromParcel(Parcel in) {
            return new QueryOrderModel(in);
        }

        @Override
        public QueryOrderModel[] newArray(int size) {
            return new QueryOrderModel[size];
        }
    };

    public String getAliRequest() {
        return AliRequest;
    }

    public void setAliRequest(String aliRequest) {
        AliRequest = aliRequest;
    }

    public String getWeChatRequest() {
        return WeChatRequest;
    }

    public void setWeChatRequest(String weChatRequest) {
        WeChatRequest = weChatRequest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(AliRequest);
        parcel.writeString(WeChatRequest);
    }
}
