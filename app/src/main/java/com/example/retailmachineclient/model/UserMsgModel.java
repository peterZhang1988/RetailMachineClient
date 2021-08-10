package com.example.retailmachineclient.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserMsgModel implements Parcelable {
    String SalesID;
    String RobotName;
    int Activation;
    String LastReportedTime;
    String AliPayAppID;
    String AliPayPrivateKey;



    public String getSalesID() {
        return SalesID;
    }

    public void setSalesID(String salesID) {
        SalesID = salesID;
    }

    public String getRobotName() {
        return RobotName;
    }

    public void setRobotName(String robotName) {
        RobotName = robotName;
    }

    public int getActivation() {
        return Activation;
    }

    public void setActivation(int activation) {
        Activation = activation;
    }

    public String getLastReportedTime() {
        return LastReportedTime;
    }

    public void setLastReportedTime(String lastReportedTime) {
        LastReportedTime = lastReportedTime;
    }

    public String getAliPayAppID() {
        return AliPayAppID;
    }

    public void setAliPayAppID(String aliPayAppID) {
        AliPayAppID = aliPayAppID;
    }

    public String getAliPayPrivateKey() {
        return AliPayPrivateKey;
    }

    public void setAliPayPrivateKey(String aliPayPrivateKey) {
        AliPayPrivateKey = aliPayPrivateKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(SalesID);
        parcel.writeString(RobotName);
        parcel.writeInt(Activation);
        parcel.writeString(LastReportedTime);
        parcel.writeString(AliPayAppID);
        parcel.writeString(AliPayPrivateKey);
    }

    protected UserMsgModel(Parcel in) {
        SalesID = in.readString();
        RobotName = in.readString();
        Activation = in.readInt();
        LastReportedTime = in.readString();
        AliPayAppID = in.readString();
        AliPayPrivateKey = in.readString();
    }

    public static final Creator<UserMsgModel> CREATOR = new Creator<UserMsgModel>() {
        @Override
        public UserMsgModel createFromParcel(Parcel in) {
            return new UserMsgModel(in);
        }

        @Override
        public UserMsgModel[] newArray(int size) {
            return new UserMsgModel[size];
        }
    };
}
