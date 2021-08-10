package com.example.retailmachineclient.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;

import DDRAIServiceProto.DDRAIServiceCmd;
import butterknife.OnClick;


import com.example.retailmachineclient.util.CloseBarUtil;
import com.example.retailmachineclient.util.TimeIntervalUtils;

public class SettingCategoryActivity extends BaseActivity implements View.OnClickListener {

    private ExitFinishReceiver exitFinishReceiver = new ExitFinishReceiver();

    class ExitFinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SettingCategoryActivity.this.finish();
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_category;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.device_test).setOnClickListener(this);
        findViewById(R.id.device_setting).setOnClickListener(this);
        findViewById(R.id.loginout).setOnClickListener(this);
        findViewById(R.id.refund).setOnClickListener(this);
        findViewById(R.id.version).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        IntentFilter filterFinish = new IntentFilter();
        filterFinish.addAction("action.exit.finish");
        registerReceiver(exitFinishReceiver, filterFinish);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {

            case R.id.refund:
                intent = new Intent(SettingCategoryActivity.this, PayByCardTestActivity.class);
                startActivity(intent);
                break;

            case R.id.back:
                TimeIntervalUtils.pageValue = DDRAIServiceCmd.AndroidPage.eAndroidPage.enPurchasePage.getNumber();
                finish();
                break;
            case R.id.device_test:
                intent = new Intent(SettingCategoryActivity.this, DeviceTestActivity.class);
                startActivity(intent);
                break;
            case R.id.device_setting:
                intent = new Intent(SettingCategoryActivity.this, DeviceSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.loginout:
                showNormalDialog();
                break;
            case R.id.version:
                intent = new Intent(SettingCategoryActivity.this, VersionActivity.class);
                startActivity(intent);
                break;


        }
    }

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(SettingCategoryActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("你是否继续退出APP?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        normalDialog.dismiss();
                        CloseBarUtil.showBar(SettingCategoryActivity.this);
                        Intent finishIntent = new Intent("action.exit");
                        finishIntent.putExtra(EXITACTION, 1);
                        sendBroadcast(finishIntent);
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        normalDialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }
}