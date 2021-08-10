package com.example.retailmachineclient.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.example.retailmachineclient.util.Logger;

public class StaticCustomReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Logger.e("-------接口 广播接收StaticCustomReceiver   = ");
//        String msg = intent.getStringExtra("msg");
//        Toast.makeText(context, "I see you111", Toast.LENGTH_SHORT).show();
    }
}
