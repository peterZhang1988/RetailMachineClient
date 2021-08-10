package com.example.retailmachineclient.model;

public class CretaeOrderDataModel {
    String ContainerNum;//货柜号
    float Univalent;//价格
    String GoodsID;
    String ClassID;

    public String getContainerNum() {
        return ContainerNum;
    }

    public void setContainerNum(String containerNum) {
        ContainerNum = containerNum;
    }

    public float getUnivalent() {
        return Univalent;
    }

    public void setUnivalent(float univalent) {
        Univalent = univalent;
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


}

