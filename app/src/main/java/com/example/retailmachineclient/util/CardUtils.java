package com.example.retailmachineclient.util;

import android.content.Context;

import com.allinpay.aipmis.allinpay.service.MisPos;
import com.example.retailmachineclient.protocobuf.dispatcher.BaseMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.socket.TcpClient;

public class CardUtils {

    MisPos misPos;
    public static CardUtils cardUtils;

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }

    boolean isRegister = false;
    public static CardUtils getInstance() {
        if (cardUtils == null) {
            synchronized (CardUtils.class) {
                if (cardUtils == null) {
                    cardUtils = new CardUtils();
                }
            }
        }
        return cardUtils;
    }

    public MisPos getMisPos() {
        return misPos;
    }

    public void setMisPos(MisPos misPos) {
        this.misPos = misPos;
    }

}
