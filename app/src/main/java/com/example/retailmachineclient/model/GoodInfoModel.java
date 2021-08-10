package com.example.retailmachineclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

public class GoodInfoModel extends LitePalSupport implements Parcelable {
    int ContainerNum;
    int ContainerFloor;
    int GoodsStatus;
    int GoodsInventory;
    int GoodsMaxInventory;
    float Price;
    String GoodsName1;
    String GoodsName2;
    String GoodsName3;
    String GoodsDescription1;
    String GoodsDescription2;
    String GoodsDescription3;
    String GoodsImage;

    public GoodInfoModel() {

    }

    public String getGoodsID() {
        return GoodsID;
    }

    public void setGoodsID(String goodsID) {
        GoodsID = goodsID;
    }

    public String getClassID() {
        return ClassID;
    }

    public void setClassID(String classID) {
        ClassID = classID;
    }

    String GoodsID;
    String ClassID;
    public GoodInfoModel(Parcel in) {
        ContainerNum = in.readInt();
        ContainerFloor = in.readInt();
        GoodsStatus = in.readInt();
        GoodsInventory = in.readInt();
        GoodsMaxInventory = in.readInt();
        Price = in.readFloat();
        GoodsName1 = in.readString();
        GoodsName2 = in.readString();
        GoodsName3 = in.readString();
        GoodsDescription1 = in.readString();
        GoodsDescription2 = in.readString();
        GoodsDescription3 = in.readString();
        GoodsImage = in.readString();
        GoodsID = in.readString();
        ClassID = in.readString();
    }

    public static final Creator<GoodInfoModel> CREATOR = new Creator<GoodInfoModel>() {
        @Override
        public GoodInfoModel createFromParcel(Parcel in) {
            return new GoodInfoModel(in);
        }

        @Override
        public GoodInfoModel[] newArray(int size) {
            return new GoodInfoModel[size];
        }
    };

    public int getContainerNum() {
        return ContainerNum;
    }

    public void setContainerNum(int containerNum) {
        ContainerNum = containerNum;
    }

    public int getContainerFloor() {
        return ContainerFloor;
    }

    public void setContainerFloor(int containerFloor) {
        ContainerFloor = containerFloor;
    }

    public int getGoodsStatus() {
        return GoodsStatus;
    }

    public void setGoodsStatus(int goodsStatus) {
        GoodsStatus = goodsStatus;
    }

    public int getGoodsInventory() {
        return GoodsInventory;
    }

    public void setGoodsInventory(int goodsInventory) {
        GoodsInventory = goodsInventory;
    }

    public int getGoodsMaxInventory() {
        return GoodsMaxInventory;
    }

    public void setGoodsMaxInventory(int goodsMaxInventory) {
        GoodsMaxInventory = goodsMaxInventory;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public String getGoodsName1() {
        return GoodsName1;
    }

    public void setGoodsName1(String goodsName1) {
        GoodsName1 = goodsName1;
    }

    public String getGoodsName2() {
        return GoodsName2;
    }

    public void setGoodsName2(String goodsName2) {
        GoodsName2 = goodsName2;
    }

    public String getGoodsName3() {
        return GoodsName3;
    }

    public void setGoodsName3(String goodsName3) {
        GoodsName3 = goodsName3;
    }

    public String getGoodsDescription1() {
        return GoodsDescription1;
    }

    public void setGoodsDescription1(String goodsDescription1) {
        GoodsDescription1 = goodsDescription1;
    }

    public String getGoodsDescription2() {
        return GoodsDescription2;
    }

    public void setGoodsDescription2(String goodsDescription2) {
        GoodsDescription2 = goodsDescription2;
    }

    public String getGoodsDescription3() {
        return GoodsDescription3;
    }

    public void setGoodsDescription3(String goodsDescription3) {
        GoodsDescription3 = goodsDescription3;
    }

    public String getGoodsImage() {
        return GoodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        GoodsImage = goodsImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ContainerNum);
        parcel.writeInt(ContainerFloor);
        parcel.writeInt(GoodsStatus);
        parcel.writeInt(GoodsInventory);
        parcel.writeInt(GoodsMaxInventory);
        parcel.writeFloat(Price);
        parcel.writeString(GoodsName1);
        parcel.writeString(GoodsName2);
        parcel.writeString(GoodsName3);
        parcel.writeString(GoodsDescription1);
        parcel.writeString(GoodsDescription2);
        parcel.writeString(GoodsDescription3);
        parcel.writeString(GoodsImage);
        parcel.writeString(GoodsID);
        parcel.writeString(ClassID);
    }


}
