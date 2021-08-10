package com.example.retailmachineclient.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 商品信息基类
 */
public class GoodMsgModel implements Parcelable {
    public String name;
    public int price;
    public String img;
    public int num;
    public String discountMsg;
    public String description;

    //层数
    public int floor;
    //电机编号
    public int machineIndex;
    //电机编号名
    public String machineIndexName;

    public String getMachineIndexName() {
        return machineIndexName;
    }

    public void setMachineIndexName(String machineIndexName) {
        this.machineIndexName = machineIndexName;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getMachineIndex() {
        return machineIndex;
    }

    public void setMachineIndex(int machineIndex) {
        this.machineIndex = machineIndex;
    }

    public static final Creator<GoodMsgModel> CREATOR = new Creator<GoodMsgModel>() {
        @Override
        public GoodMsgModel createFromParcel(Parcel in) {
            return new GoodMsgModel(in);
        }

        @Override
        public GoodMsgModel[] newArray(int size) {
            return new GoodMsgModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDiscountMsg() {
        return discountMsg;
    }

    public void setDiscountMsg(String discountMsg) {
        this.discountMsg = discountMsg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(price);
        parcel.writeString(img);
        parcel.writeInt(num);
        parcel.writeString(discountMsg);
        parcel.writeString(description);

        parcel.writeInt(floor);
        parcel.writeInt(machineIndex);
        parcel.writeString(machineIndexName);
    }

    private GoodMsgModel(Parcel in){
        name = in.readString();
        price = in.readInt();
        img = in.readString();
        num = in.readInt();
        discountMsg = in.readString();
        description = in.readString();

        floor = in.readInt();
        machineIndex = in.readInt();
        machineIndexName = in.readString();
    }

    public GoodMsgModel(){

    }

}
