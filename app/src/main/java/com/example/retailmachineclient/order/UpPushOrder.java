package com.example.retailmachineclient.order;

import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SerialPortManagerUtils;
import com.example.retailmachineclient.util.Utils;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

class UpPushOrder {
    SerialPortManager serialPortManager;
    public boolean request(String requestId){
        SerialPortManager serialPortManager = SerialPortManagerUtils.getInstance();

        return true;
    }
}
