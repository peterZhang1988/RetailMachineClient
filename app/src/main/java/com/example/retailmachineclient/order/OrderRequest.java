package com.example.retailmachineclient.order;

import android.os.Bundle;
import android.os.Handler;

import com.example.retailmachineclient.base.GoodsModel;
import com.example.retailmachineclient.mcuSdk.DataProtocol;
import com.example.retailmachineclient.mcuSdk.MCU;
import com.example.retailmachineclient.model.GoodMsgModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.model.TxErrorModel;
import com.example.retailmachineclient.util.CRC;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SerialPortManagerUtils;
import com.example.retailmachineclient.util.ThreadPoolManager;
import com.example.retailmachineclient.util.Utils;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.example.retailmachineclient.util.ConstantUtils.ORDER_DATA_AND_RETURN;
import static com.example.retailmachineclient.util.ConstantUtils.ORDER_POWER_MACHINE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_CLOSE_DOOR;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_CANNOT_CLOSE_CASE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_CLOSE_DOOR_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_LIFT_EMPTY;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MACHINE_RUN;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MOVE_TARGET_CHECK_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MOVE_TARGET_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_DOOR_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_RESET_TO_7_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_RESET_TO_7_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_RESET_TO_7_OVERTIME;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_START_MACHINE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPEN_DOOR;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPERATE_PALLET;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPERATE_PALLET_TO_7;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_CLOSE_DOOR_STATUS;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_CLOSE_CASE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS_CLOSE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS_OPEN;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_MACHINE_STATUS_IS_POSITION;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_PALLET_STATUS;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_PALLET_STATUS_IS_POSITION;

public class OrderRequest {
    Handler mHandler;
    public static boolean isExit = false;
    public static boolean isExitTimeOut = true;//超时线程中 是否继续等待 默认true
    public static byte[] requestResult;
    final Bundle bundle = new Bundle();
    Runnable runnable = null;

    public OrderRequest(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public synchronized byte[] requestCard(MCU.PortMCU portMCU,final byte orderId, byte[] questYData, int requestResultId) {
        byte[] sendData = DataProtocol.packSendDataNew(portMCU, orderId, questYData);
        Logger.e("出货流程  指令开始" + Utils.byteBufferToHexString(sendData));
        requestResult = null;
        OnSerialPortDataListener onSerialPortDataListener = new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                requestResult = null;
//                Logger.e(" 出货流程 和板块通信时 onDataReceived");
                if (bytes == null) {
                    Logger.e("出货流程  和板块通信时 返回数据bytes == null");
                    return;
//                    isExit = true;
//                    synchronized (bundle) {
////                        Logger.e("出货流程  onDataReceived1 发送notify");
//                        bundle.notify();
//                    }
                } else if (bytes.length != 20) {
                    Logger.e("出货流程  和板块通信时 返回数据长度不为20 data =" + Utils.byteBufferToHexString(bytes));
                    byte dataOrderReturn = Utils.getBytes(bytes, 1, 1)[0];
                    if(orderId!=dataOrderReturn){
                        Logger.e("出货流程  和板块通信时 返回数据的指令不一致 自带=" + orderId + ",计算=" + dataOrderReturn);
                        return ;
                    }else{
                        Logger.e("出货流程  和板块通信时 返回数据的指令一致 自带=" + orderId + ",计算=" + dataOrderReturn);
                    }

                    isExit = true;
                    synchronized (bundle) {
//                        Logger.e("出货流程  onDataReceived2 发送notify");
                        bundle.notify();
                    }
                } else {
                    //校验是否是当前指令的返回 1字节
                    byte dataOrderReturn = Utils.getBytes(bytes, 1, 1)[0];
                    if(orderId!=dataOrderReturn){
                        Logger.e("出货流程  和板块通信时 返回数据的指令不一致 自带=" + orderId + ",计算=" + dataOrderReturn);
                        return ;
                    }else{
                        Logger.e("出货流程  和板块通信时 返回数据的指令一致 自带=" + orderId + ",计算=" + dataOrderReturn);
                    }
                    byte[] dataMachine = Utils.getBytes(bytes, 0, 18);
                    byte[] checks = CRC.crc16New(dataMachine);

                    byte[] checkMachine = Utils.getBytes(bytes, 18, 2);
                    boolean equalValue = byteEquals(checkMachine, checks);
                    if (!equalValue) {
                        Logger.e("出货流程  和板块通信时 返回数据不一致 自带=" + checkMachine + ",计算=" + checks);
                    }
                    String result = "";
                    isExit = true;
                    result = "\n 接受返回数据" + Utils.byteBufferToHexString(bytes);
                    Logger.e("出货流程  接受返回数据:" + result);
                    mHandler.obtainMessage(ORDER_DATA_AND_RETURN, result).sendToTarget();
                    requestResult = bytes;
                    synchronized (bundle) {
//                        Logger.e(" onDataReceived3 发送notify");
                        bundle.notify();
                    }
                }

            }

            @Override
            public void onDataSent(byte[] bytes) {
                String result = "";
                byte[] check = Utils.getBytes(bytes, 18, 2);
                result = result + "\n" + " 发送数据:" + Utils.byteBufferToHexString(bytes) + "-----:" + Utils.byteBufferToHexString(check);
//                Logger.e("出货流程  onDataSent" + result);
                mHandler.obtainMessage(ORDER_DATA_AND_RETURN, result).sendToTarget();
            }
        };
        SerialPortManager serialPortManager = SerialPortManagerUtils.getInstance();
        serialPortManager.setOnSerialPortDataListener(onSerialPortDataListener);


        isExit = false;
        serialPortManager.sendBytes(sendData);
        synchronized (bundle) {
            long timeOutMillis = System.currentTimeMillis() + 200;
            while (System.currentTimeMillis() < timeOutMillis) {
                //没有超时
                if (requestResult != null) {
                    break;
                }
            }
        }
        Logger.e("出货流程  指令返回 send=" + Utils.byteBufferToHexString(sendData) + "\n return=" + Utils.byteBufferToHexString(requestResult));
        return requestResult;
    }

    public void delayChangeView(long timeOutMillis, String strValue) {
        runnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = 0;
                while (isExitTimeOut) {
                    currentTime = System.currentTimeMillis();
                    if (currentTime > timeOutMillis) {
                        break;
                    }
                }
                isExit = true;
                if (isExitTimeOut) {
                    synchronized (bundle) {
                        Logger.e("出货流程  和板块通信时 命令执行返回超时 发送notify order=" + strValue + ",cur= " + currentTime + ",timeout=" + timeOutMillis);
                        bundle.notify();
                    }
                }

            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnable);
    }

    private boolean byteEquals(byte[] b1, byte[] b2) {
        if (b1 == null || b2 == null) return false;
        if (b1.length != b2.length) return false;
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 升降梯移动
     *
     * @param data 层数
     */
    public void liftMove(byte data) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                byte[] nData = new byte[16];
                nData[0] = (byte) data;
                for (int i = 1; i < nData.length; i++) {
                    nData[i] = 0x00;
                }
                byte[] responseBytes = requestCard(MCU.PortMCU.MCU1, (byte) 0x12, nData, ORDER_POWER_MACHINE);
                if (responseBytes == null) {
                    Logger.e("成功:responseBytes为空！");
                } else {
                    Logger.e("成功:responseBytes:" + Utils.byteBufferToHexString(responseBytes));
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * 升降梯移动
     *
     * @param data 层数
     */
    public int liftMoveNew(byte data) {
        byte[] nData = new byte[16];
        nData[0] = (byte) data;
        for (int i = 1; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        byte[] responseBytes = requestCard(MCU.PortMCU.MCU1, (byte) 0x12, nData, ORDER_POWER_MACHINE);
        //解析结果
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //已启动
            Logger.e("启动升降机 已启动");
        } else if (resultInt == 1) {
            //已启动
            Logger.e("启动升降机 无效索引");
        } else if (resultInt == 2) {
            //已启动
            Logger.e("启动升降机 正在运行");
        } else if (resultInt == 3) {
            //已启动
            Logger.e("启动升降机 启动失败");
        } else {
            //已启动
            Logger.e("启动升降机 通信异常");
        }
        return resultInt;
    }

    /**
     * 启动电机
     *
     * @return
     */
    public int startMachine(int index) {
        byte[] nData = new byte[16];
        nData[0] = (byte) index;//电机索引号
        nData[1] = (byte) 0x03;//电机类型
        for (int i = 2; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        byte[] responseBytes = requestCard(MCU.PortMCU.MCU5, (byte) 0x05, nData, ORDER_POWER_MACHINE);
        try {
            Thread.sleep(1000);//.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunResult = Utils.byteToInt(responseBytes[2]);
        }
        return orderRunResult;
    }

    /**
     * 查询升降机执行状态 13
     *
     * @return
     */
    public List<Integer> queryLiftStatusTX() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        byte[] responseBytes = requestCard(MCU.PortMCU.MCU1, (byte) 0x13, nData, ORDER_POWER_MACHINE);
        //解析结果
        int orderRunStatus = 100;
        int currentFloorStatus = 100;
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunStatus = Utils.byteToInt(responseBytes[2]);
            currentFloorStatus = Utils.byteToInt(responseBytes[3]);
            orderRunResult = Utils.byteToInt(responseBytes[4]);
        }
        Logger.e("获取升降机状态成功:orderRunStatus=" + orderRunStatus + ",currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * 查询货盘是否有货物 1d
     *
     * @return 有货:true,无货：false 默认返回：false
     */
    public boolean queryIsHasGoods() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        byte[] responseBytes = requestCard(MCU.PortMCU.MCU1, (byte) 0x1C, nData, ORDER_POWER_MACHINE);
        //解析结果
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //货盘空
            return false;
        } else if (resultInt == 1) {
            //货盘有物品
            return true;
        }
        return false;
    }

    /**
     * 查询货盘是否有货 1c
     *
     * @return
     */
    public int queryIsHasGoodStatus() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        byte[] responseBytes = requestCard(MCU.PortMCU.MCU1, (byte) 0x1C, nData, ORDER_POWER_MACHINE);
        //解析结果
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //货盘空
            return resultInt;
        } else if (resultInt == 1) {
            //货盘有物品
            return resultInt;
        }
        return resultInt;
    }

    /**
     * 查询电机run执行状态
     *
     * @return
     */
    public List<Integer> queryMachineStatus() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
//        nData[0]=(byte)index;//电机索引号
//        nData[1]=(byte)0x03;//电机类型
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        byte[] responseBytes = requestCard(MCU.PortMCU.MCU5, (byte) 0x03, nData, ORDER_POWER_MACHINE);
        try {
            Thread.sleep(1000);//.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int orderRunStatus = 100;
        int currentFloorStatus = 100;
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunStatus = Utils.byteToInt(responseBytes[2]);
            currentFloorStatus = Utils.byteToInt(responseBytes[3]);
            orderRunResult = Utils.byteToInt(responseBytes[4]);
        }
        Logger.e("获取电机执行状态成功:orderRunStatus=" + orderRunStatus + ",currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * 操作 isOpen 是否打开门 false:关门
     *
     * @return
     */
    public int openDoor(boolean isOpen) {
        byte[] nData = new byte[16];
        if (isOpen) {
            nData[0] = (byte) 0x01;
        } else {
            nData[0] = (byte) 0x00;
        }
        for (int i = 1; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        byte[] responseBytes = requestCard(MCU.PortMCU.MCU1, (byte) 0x14, nData, ORDER_POWER_MACHINE);
        //解析结果
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        Logger.e("操作货门返回结果:isOpen = " + isOpen + " ,orderRunStatus=" + resultInt);
        return resultInt;
    }

    /**
     * 获取推杆指令执行状态
     *
     * @return
     */
    public List<Integer> queryDoorStatus() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        byte[] responseBytes = requestCard(MCU.PortMCU.MCU1, (byte) 0x15, nData, ORDER_POWER_MACHINE);
        //解析结果
        byte result = 0;
        int resultInt = 100;
        //
        int orderRunStatus = 100;
        int currentFloorStatus = 100;
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunStatus = Utils.byteToInt(responseBytes[2]);
            currentFloorStatus = Utils.byteToInt(responseBytes[3]);
            orderRunResult = Utils.byteToInt(responseBytes[4]);
        }
        Logger.e("获取舱门推杆状态成功:orderRunStatus=" + orderRunStatus + " ,currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * 执行状态机
     *
     * @param type
     * @param beforeTime
     * @param overtime
     * @param repeatTimes
     * @return
     */
    public TxErrorModel processByStatus(Integer type, long beforeTime, long overtime, int repeatTimes, int position) {
        int startTime = 0;
        TxErrorModel beforeTxErrorModel = new TxErrorModel(false, false, 0);
        TxErrorModel txErrorModel;
        statusWhile:
        while (true) {
            switch (type) {
                case TYPE_QUERY_PALLET_STATUS://TYPE_QUERY_PALLET_STATUS
                    //查询货盘是否复位
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryPalletStatusForISReset();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_DOOR_STATUS_OPEN:
                    //查询撑杆指令状态 是否完成打开
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask(true);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_DOOR_CLOSE_CASE:
                    //查询撑杆指令状态 查询关门是否满足条件
                    if (beforeTxErrorModel.getStamp() != 0 && ((System.currentTimeMillis() - beforeTxErrorModel.getStamp()) > 5000)) {
                        return new TxErrorModel(true, true, 0);
                    }
                    beforeTxErrorModel = queryCloseDoorCase(beforeTxErrorModel);
                    break;

                case TYPE_QUERY_DOOR_STATUS_CLOSE:
                    //查询撑杆指令状态 是否完成关闭
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask(false);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_PALLET_STATUS_IS_POSITION://TYPE_QUERY_PALLET_STATUS
                    //是否在指定位置
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME, 0, 0, 0);
                    }
                    txErrorModel = queryPalletStatusISPosition(position);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;
                case TYPE_QUERY_MACHINE_STATUS_IS_POSITION://TYPE_QUERY_PALLET_STATUS
                    //查询电机状态
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryMachineStatusFor();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_OPERATE_PALLET:
                    //发送货盘到指定的层
                    //发送货盘复位任务 3次处于空闲并无法到底指定位置
                    Logger.e("启动升降机resetLiftTask TYPE_OPERATE_PALLET :" + startTime + "," + repeatTimes);
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, 3, 0, 0, 0);
                    }
                    txErrorModel = resetLiftTask(beforeTxErrorModel, position);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isSuccess()) {
                            Logger.e("启动升降机resetLiftTask  跳出循环返回");
                            //跳出循环返回
                            return txErrorModel;
                        } else {
                            if (txErrorModel.getZ1() == 0 || txErrorModel.getZ1() == 2) {
                                Logger.e("启动升降机resetLiftTask  失败startTime+1");
                                startTime = startTime + 1;
                            } else {
                                Logger.e("启动升降机resetLiftTask  重置 startTime");
                                startTime = 0;
                            }
                        }
                    } else {
                        Logger.e("启动升降机resetLiftTask  not sucess");
                    }
                    break;

                case TYPE_OPERATE_PALLET_TO_7:
                    //发送货盘复位任务 3次处于空闲并无法到底指定位置
                    Logger.e("启动升降机resetLiftTask TYPE_OPERATE_PALLET_TO_7 :" + startTime + "," + repeatTimes);
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, TYPE_ERROR_RESET_TO_7_OVERTIME, 0, 0, 0);
                    }
                    txErrorModel = resetLiftTask(beforeTxErrorModel, 7);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isSuccess()) {
                            //跳出循环返回
                            return txErrorModel;
                        } else {
                            //空闲状态
                            if (txErrorModel.getZ1() == 0) {
                                startTime = startTime + 1;
                            } else {
                                startTime = 0;
                            }
                        }
                    }
                    break;

                case TYPE_OPEN_DOOR:
                    //发送打开货门任务 10次启动失败后退出
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, 10, 0, 0, 0);
                    }
                    txErrorModel = openDoorTask(true, 3000);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isTaskSuccess()) {
                            return txErrorModel;
                        } else {
                            startTime = startTime + 1;
                        }
                    }
                    break;

                case TYPE_CLOSE_DOOR:
                    //发送关闭货门任务
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, 10, 0, 0, 0);
                    }
                    txErrorModel = openDoorTask(false, 2000);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isTaskSuccess()) {
                            return txErrorModel;
                        } else {
                            startTime = startTime + 1;
                        }
                    }
                    break;

                case TYPE_QUERY_CLOSE_DOOR_STATUS:
                    //查询推杆指令 防夹手
                    txErrorModel = queryCloseDoorTask();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isTaskSuccess()) {
                            return txErrorModel;
                        }
                    }
                    break;

            }
        }
    }

    /**
     * 关门时一直检查是否触发防夹手
     *
     * @return
     */
    public TxErrorModel queryCloseDoorTask() {
        long before = System.currentTimeMillis();
        int value = queryIsProtectHand();// 1:打开了 0：关闭
        if (value == 1) {
            Logger.e("关门时 查询是否启动防夹手 已触发");
            return new TxErrorModel(false, true, 22);
        }

        List<Integer> list = queryDoorStatus();
        if (list.size() == 3) {
            if (list.get(0) == 0 || list.get(0) == 2) {
                if (list.get(1) == 0) {
                    //撑杆已经完全打开
                    Logger.e("关门时 查询推杆货门是否关闭 已完成");
                    return new TxErrorModel(true, true, 21);
                }
            }
        }

        sleepWait(before, 50);
        return null;
    }

    /**
     * 查询是否满足关门条件
     *
     * @param beforeTx
     * @return
     */
    public TxErrorModel queryCloseDoorCase(TxErrorModel beforeTx) {
        long before = System.currentTimeMillis();
        //判断货盘 防夹手 都为00时
        int result = queryIsHasGoodStatus();
        if (result != 0) {
            beforeTx.setStamp(System.currentTimeMillis());
        }
        sleepWait(before, 100);
        before = System.currentTimeMillis();
        int resultHand = queryIsProtectHand();
        if (resultHand != 0) {
            beforeTx.setStamp(System.currentTimeMillis());
        }
        if (beforeTx.getStamp() == 0 && result == 0 && resultHand == 0) {
            //初始化
            beforeTx.setStamp(System.currentTimeMillis());
        }
        sleepWait(before, 100);
        return beforeTx;
    }

    /**
     * 查询舱门是否打开关闭完成
     *
     * @param isOpen
     * @return
     */
    public TxErrorModel queryDoorStatusTask(boolean isOpen) {
        long before = System.currentTimeMillis();
        List<Integer> list = queryDoorStatus();
        if (list.size() == 3) {
            if (list.get(2) != 0) {
                //指令错误
                return new TxErrorModel(false, false, 15);
            } else if (list.get(0) == 0 || list.get(0) == 2) {
                if (isOpen && list.get(1) == 1) {
                    //撑杆已经完全打开
                    return new TxErrorModel(false, true, 0);
                } else if (!isOpen && list.get(1) == 0) {
                    //撑杆已经完全关闭
                    return new TxErrorModel(false, true, 0);
                } else {
                    sleepWait(before, 150);
                }
            } else {
                //继续查询
                sleepWait(before, 150);
            }
        } else {
            sleepWait(before, 150);
        }
        return null;
    }

    /**
     * 操作舱门打开关闭 有疑问？
     *
     * @param isOpen
     * @return
     */
    public TxErrorModel openDoorTask(boolean isOpen, long delayTime) {
        int result = openDoor(isOpen);
        if (result == 0) {
            //0 已经启动
            return new TxErrorModel(true, true, 0);
        } else {
            //3 启动失败
            //1 无效推杆动作重试发送指令
            //2 其他设备运行中
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 复位升降机 ?
     *
     * @return
     */
    public TxErrorModel resetLiftTask(TxErrorModel beforeTx, int line) {
        Logger.e("启动升降机resetLiftTask");
        int value = liftMoveNew((byte) line);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //获取状态 查看是否复位
        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(), 10000, 0, line);
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            int compare = 0;
            if (line < 5) {
                compare = 4 - line;
            } else {
                compare = line;
            }
            if (txErrorModel.getZ2() == compare) {
                //是否移动到指定的层
                Logger.e("启动升降机resetLiftTask 移动到指定的层 curent:" + txErrorModel.getZ2() + ",want Position:" + line);
                return new TxErrorModel(true, true, 0);
            } else {
                //没有移动到指定的层
                Logger.e("启动升降机resetLiftTask 没有移动到指定的层 curent:" + txErrorModel.getZ2() + ",want Position:" + line);
                return new TxErrorModel(false, true, 0, txErrorModel.getZ1(), txErrorModel.getZ2(), txErrorModel.getZ3(), 0);
            }
        } else {
            Logger.e("启动升降机resetLiftTask 没有查询到状态");
            return new TxErrorModel(false, false, 0);
        }
    }

    /**
     * 睡眠等待
     *
     * @param before
     * @param delay
     */
    public void sleepWait(long before, long delay) {
        if ((before + delay) > System.currentTimeMillis()) {
            try {
                Thread.sleep((before + delay) - System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询货盘位置指令 用于查看是否复位 取货前
     *
     * @return
     */
    public TxErrorModel queryPalletStatusForISReset() {
        Logger.e("run queryPalletStatusForISReset ");
        long currentTimeMillis = System.currentTimeMillis();
        boolean isSleep = false;
        List<Integer> list = queryLiftStatusTX();
        if (list != null && list.size() > 0) {
            if (list.get(2) != 0) {
                //
                sleepWait(currentTimeMillis, 250);
                isSleep = true;
            } else {
                if (list.get(0) == 0 || list.get(0) == 2) {
                    //获取状态是成功的
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //机器运行中
                    sleepWait(currentTimeMillis, 250);
                    isSleep = true;
                }
            }
        }
        return null;
    }

    /**
     * 查询货盘位置指令 用于查看是否复位取货前
     *
     * @return
     */
    public TxErrorModel queryMachineStatusFor() {
        long currentTimeMillis = System.currentTimeMillis();
        boolean isSleep = false;
        List<Integer> list = queryMachineStatus();
        if (list != null && list.size() > 0) {
            if (list.get(2) != 0) {
                //启动失败 error
                sleepWait(currentTimeMillis, 150);
                isSleep = true;
            } else {
                if (list.get(0) == 0 || list.get(0) == 2) {
                    //获取状态是成功的
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //机器运行中
                    sleepWait(currentTimeMillis, 150);
                    isSleep = true;
                }
            }
        }
        return null;
    }

    /**
     * 查询货盘是否在指定层上
     *
     * @param position
     * @return
     */
    public TxErrorModel queryPalletStatusISPosition(int position) {
        Logger.e("run queryPalletStatusISPosition = " + position);
        long currentTimeMillis = System.currentTimeMillis();
        boolean isSleep = false;
        List<Integer> list = queryLiftStatusTX();
        if (list != null && list.size() > 0) {
            if (list.get(0) == 0 || list.get(0) == 2) {
                //获取状态是成功的
                if (list.get(1) == position) {
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //
                    return new TxErrorModel(false, true, 0, list.get(0), list.get(1), list.get(2));
                }
            } else {
                //机器运行中
                sleepWait(currentTimeMillis, 150);
                isSleep = true;
            }
        }
        return null;
    }

    /**
     * 查询光幕是否遮挡，防夹手
     *
     * @return N
     */
    public int queryIsProtectHand() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        byte[] responseBytes = requestCard(MCU.PortMCU.MCU1, (byte) 0x18, nData, ORDER_POWER_MACHINE);
        //解析结果
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        Logger.e("获取接近开关状态 成功:" + resultInt);
        return resultInt;
    }

    /**
     * 单独取一个货物
     *
     * @param line
     * @param row
     * @return
     */
    public TxErrorModel getOneGood(int line, int row) {//检验错误码 errorCode
        Logger.e("run getOneGood");

        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(), 10000, 0, 7);
        boolean isReset = false;
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            if (txErrorModel.getZ2() == 7) {
                isReset = true;
            }
        } else {
            if (txErrorModel != null) {
                txErrorModel.setSuccess(false);
            }
            return txErrorModel;
        }

        boolean resetResult = true;
        if (!isReset) {
            //没有复位
            Logger.e("启动升降机 没有复位 ");
            txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                int tempLine = (line + 2) % 5;
                liftMoveNew((byte) tempLine);
                txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
                if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                    Logger.e("启动升降机 一直复位不成功");
                    //一直复位不成功
                    txErrorModel.setSuccess(false);
                    txErrorModel.setErrorCode(TYPE_ERROR_RESET_TO_7_FAIL);
                    return txErrorModel;
                }
            }
        }
        //已经复位复位完成
        //升降移动指定位置 连续多次
        txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, 3, line);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("启动升降机到指定楼层 失败");
            //做一次兜底动作
            int tempLine = (line + 2) % 5;
            liftMoveNew((byte) tempLine);

            txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, 3, line);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_FAIL);
            }

        }
        Logger.e("启动升降机 正常启动 升降机是否到达指定位置");
        //升降机是否到达指定位置

        txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS, System.currentTimeMillis(), 10000, 0, line);
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            int compare = 0;
            if (txErrorModel.getZ2() < 5) {
                compare = 4 - txErrorModel.getZ2();
            } else {
                compare = txErrorModel.getZ2();
            }
            if (compare == line) {
                Logger.e("启动升降机 移动到指定位置");
            } else {
                Logger.e("启动升降机 未移动到指定位置");
                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
            }
        } else {
            return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
        }
        //查询升降机当前位置
        Logger.e("电机启动前准备检查是否需要检查状态");
        //启动电机
        int value = startMachine(row);
        if (value != 0) {
            //电机启动异常
            return new TxErrorModel(false, false, TYPE_ERROR_START_MACHINE);
        }
        Logger.e("电机启动后 查询执行旋转结果");
        txErrorModel = processByStatus(TYPE_QUERY_MACHINE_STATUS_IS_POSITION, System.currentTimeMillis(), 5000, 0, 0);
        if (!txErrorModel.isTaskSuccess()) {
            //电机
            Logger.e("电机启动后 执行旋转异常");
            return new TxErrorModel(false, false, TYPE_ERROR_MACHINE_RUN);
        }
        return new TxErrorModel(true, true, 0);
    }

    /**
     * 取货
     *
     * @param goodsList
     * @return
     */
    public TxErrorModel getGoods(List<GoodMsgModel> goodsList) {

        List<TxErrorModel> resultList = new ArrayList();
        for (GoodMsgModel goodsModel : goodsList) {
            TxErrorModel txErrorModel = getOneGood(goodsModel.getFloor(), goodsModel.getMachineIndex());
            if (!txErrorModel.isSuccess()) {
                return txErrorModel;
            }

//            MessageEvent event = new MessageEvent();
//            event.setType(5);
//            event.setTag(txErrorModel);
//            EventBus.getDefault().post(event);

            resultList.add(txErrorModel);
        }

        //升降梯复位 检查是否复位
        TxErrorModel txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("启动升降机 一直复位不成功");
            //一直复位不成功
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_RESET_TO_7_FAIL);
            return txErrorModel;
        }
        //货盘是否有货
        boolean isHasGoods = queryIsHasGoods();
        if (!isHasGoods) {
            Logger.e("货盘没货 不满足打开舱门的条件");
            return new TxErrorModel(false, false, TYPE_ERROR_LIFT_EMPTY);
        }
        return openDoorAfterGood();
    }

    public TxErrorModel openDoorAfterGood() {
        Logger.e("打开舱门 openDoorAfterGood");
        //打开舱门
        TxErrorModel txErrorModel = processByStatus(TYPE_OPEN_DOOR, 0, 0, 60, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("打开开门指令执行失败");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_FAIL);
            return txErrorModel;
        }
        //检查舱门是否完成打开 先睡2秒
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        Logger.e("检查舱门是否完成打开");
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_STATUS_OPEN, System.currentTimeMillis(), 30000, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("舱门打开步骤没有完成");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY);
            return txErrorModel;
        }
        Logger.e("检查是否取走货物 货盘是否有货");
//        //检查是否取走货物 货盘是否有货
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_CLOSE_CASE, 0, 0, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("不满足条件执行关门");
            //不满足关门条件
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CANNOT_CLOSE_CASE);
            return txErrorModel;
        }
        Logger.e("满足条件执行关门");
//        //关门
        txErrorModel = processByStatus(TYPE_CLOSE_DOOR, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            //关门指令发送失败
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CLOSE_DOOR_FAIL);
            return txErrorModel;
        }

        Logger.e("关闭货门时，检查货兜 和 防夹手");
        txErrorModel = processByStatus(TYPE_QUERY_CLOSE_DOOR_STATUS, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isSuccess()) {
            Logger.e("货兜不为空 或者 触发防夹手 回到打开货门 ");
            //触发防夹手 回到打开门取货
            return openDoorAfterGood();
        } else {
            Logger.e("正常关门完毕");
            //正常关门完毕
            txErrorModel.setSuccess(true);
            txErrorModel.setErrorCode(0);
            return txErrorModel;
        }
    }

}
