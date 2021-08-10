package com.example.retailmachineclient.model;

public class TxErrorModel {
    public final static int ErrorCodePallet = 1;
    boolean isSuccess = false;//步骤是否成功
    boolean isTaskSuccess = false;//实际任务是否成功 控制是否循环
    int errorCode;//错误代码
    int Z1;
    int Z2;
    int Z3;

    int times;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    long startTime;//任务开始时间



    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    long stamp;//时间戳

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }


    public TxErrorModel(boolean isSuccess, boolean isTaskSuccess, int errorCode, long stamp){
        this.isSuccess = isSuccess;
        this.isTaskSuccess = isTaskSuccess;
        this.stamp = stamp;
        this.errorCode = errorCode;
    }

    public TxErrorModel(boolean isSuccess, boolean isTaskSuccess, int errorCode){
        this.isSuccess = isSuccess;
        this.isTaskSuccess = isTaskSuccess;
        this.errorCode = errorCode;
    }

    public TxErrorModel(boolean isSuccess, boolean isTaskSuccess, int errorCode, int Z1, int Z2, int Z3){
        this.isSuccess = isSuccess;
        this.isTaskSuccess = isTaskSuccess;
        this.errorCode = errorCode;
        this.Z1 = Z1;
        this.Z2 = Z2;
        this.Z3 = Z3;
    }

    public TxErrorModel(boolean isSuccess, boolean isTaskSuccess, int errorCode, int Z1, int Z2, int Z3, int times){
        this.isSuccess = isSuccess;
        this.isTaskSuccess = isTaskSuccess;
        this.errorCode = errorCode;
        this.Z1 = Z1;
        this.Z2 = Z2;
        this.Z3 = Z3;
        this.times = times;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isTaskSuccess() {
        return isTaskSuccess;
    }

    public void setTaskSuccess(boolean taskSuccess) {
        isTaskSuccess = taskSuccess;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getZ1() {
        return Z1;
    }

    public void setZ1(int z1) {
        Z1 = z1;
    }

    public int getZ2() {
        return Z2;
    }

    public void setZ2(int z2) {
        Z2 = z2;
    }

    public int getZ3() {
        return Z3;
    }

    public void setZ3(int z3) {
        Z3 = z3;
    }

}
