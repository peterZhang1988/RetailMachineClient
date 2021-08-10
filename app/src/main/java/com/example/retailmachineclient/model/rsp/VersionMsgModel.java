package com.example.retailmachineclient.model.rsp;

import android.os.Parcel;
import android.os.Parcelable;

public class VersionMsgModel implements Parcelable {
    String Route;
    int VersionCode;
    String AddTime;
    String Describe;
    String Name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Route);
        parcel.writeInt(VersionCode);
        parcel.writeString(AddTime);
        parcel.writeString(Describe);
        parcel.writeString(Name);
    }

    protected VersionMsgModel(Parcel in) {
        Route = in.readString();
        VersionCode = in.readInt();
        AddTime = in.readString();
        Describe = in.readString();
        Name = in.readString();
    }

    public static final Creator<VersionMsgModel> CREATOR = new Creator<VersionMsgModel>() {
        @Override
        public VersionMsgModel createFromParcel(Parcel in) {
            return new VersionMsgModel(in);
        }

        @Override
        public VersionMsgModel[] newArray(int size) {
            return new VersionMsgModel[size];
        }
    };

    public String getRoute() {
        return Route;
    }

    public void setRoute(String route) {
        Route = route;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }

    public String getAddTime() {
        return AddTime;
    }

    public void setAddTime(String addTime) {
        AddTime = addTime;
    }

    public String getDescribe() {
        return Describe;
    }

    public void setDescribe(String describe) {
        Describe = describe;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
