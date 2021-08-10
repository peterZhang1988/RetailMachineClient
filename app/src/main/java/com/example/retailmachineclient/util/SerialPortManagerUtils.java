package com.example.retailmachineclient.util;

import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

public class SerialPortManagerUtils {
    static SerialPortManager instance;
    public static SerialPortManager getInstance(){
        if(instance == null){
            synchronized(SerialPortManager.class){
                if(instance == null){
                    instance = new SerialPortManager();

                }
            }
        }
        return instance;
    }
}
