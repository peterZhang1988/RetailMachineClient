package com.example.retailmachineclient.model;

import java.util.ArrayList;
import java.util.List;

public class CreateOderReqModel {
    String SalesID;
    float PaymentAmount;
    String[] GoodsIDList;
    String[] ClassIDList;
    String[] ContainerNumList;
    List<CretaeOrderDataModel> Data = new ArrayList<CretaeOrderDataModel>();

    public String getSalesID() {
        return SalesID;
    }

    public void setSalesID(String salesID) {
        SalesID = salesID;
    }

    public float getPaymentAmount() {
        return PaymentAmount;
    }

    public void setPaymentAmount(float paymentAmount) {
        PaymentAmount = paymentAmount;
    }

    public String[] getGoodsIDList() {
        return GoodsIDList;
    }

    public void setGoodsIDList(String[] goodsIDList) {
        GoodsIDList = goodsIDList;
    }

    public String[] getClassIDList() {
        return ClassIDList;
    }

    public void setClassIDList(String[] classIDList) {
        ClassIDList = classIDList;
    }

    public String[] getContainerNumList() {
        return ContainerNumList;
    }

    public void setContainerNumList(String[] containerNumList) {
        ContainerNumList = containerNumList;
    }

    public List<CretaeOrderDataModel> getData() {
        return Data;
    }

    public void setData(List<CretaeOrderDataModel> data) {
        Data = data;
    }
}
