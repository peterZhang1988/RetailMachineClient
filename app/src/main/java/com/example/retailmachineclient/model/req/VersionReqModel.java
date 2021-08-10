package com.example.retailmachineclient.model.req;

public class VersionReqModel {
    String SalesID;

    public String getSalesID() {
        return SalesID;
    }

    public void setSalesID(String salesID) {
        SalesID = salesID;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    int Version;
}
