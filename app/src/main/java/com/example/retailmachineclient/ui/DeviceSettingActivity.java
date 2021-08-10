package com.example.retailmachineclient.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.SpUtil;

import butterknife.BindView;
import butterknife.OnClick;

import android.widget.Toast;

public class DeviceSettingActivity extends BaseActivity implements View.OnClickListener {

    EditText address;
    EditText number;
    Button switch_sales_id;
    Switch lan_cn;
    Switch lan_en;
    Switch lan_fn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
        address = (EditText) findViewById(R.id.address);
        number = (EditText) findViewById(R.id.number);
        switch_sales_id = (Button) findViewById(R.id.switch_sales_id);

        lan_cn = (Switch) findViewById(R.id.lan_cn);
        lan_en = (Switch) findViewById(R.id.lan_en);
        lan_fn = (Switch) findViewById(R.id.lan_fn);

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.switch_sales_id).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }


    @Override
    protected void initData() {
        number.setText("" + SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.NUMBER_BUY, 0));
        address.setText(SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.IP_ADDRESS));
        lan_cn.setChecked(SpUtil.getInstance(BaseApplication.getContext()).getBoolean(SpUtil.LANGUAGE_SETTING_CN));
        lan_en.setChecked(SpUtil.getInstance(BaseApplication.getContext()).getBoolean(SpUtil.LANGUAGE_SETTING_EN));
        lan_fn.setChecked(SpUtil.getInstance(BaseApplication.getContext()).getBoolean(SpUtil.LANGUAGE_SETTING_FN));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.save:
                ConstantUtils.NUMBER_BUY = Integer.valueOf(number.getText().toString());
                ConstantUtils.APP_LOGIN_DOMAIN = address.getText().toString();
                ConstantUtils.LANGUAGE_SETTING_CN = lan_cn.isChecked();
                ConstantUtils.LANGUAGE_SETTING_EN = lan_en.isChecked();
                ConstantUtils.LANGUAGE_SETTING_FN = lan_fn.isChecked();
                setCons2SpSetValue();
                Toast.makeText(DeviceSettingActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                break;

            case R.id.switch_sales_id:
                showNormalDialog();
                break;
        }
    }

    /**
     * 初始化sp的值
     */
    public static void setCons2SpSetValue() {
        SpUtil.getInstance(BaseApplication.getContext()).putInt(SpUtil.NUMBER_BUY, ConstantUtils.NUMBER_BUY);
        SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.IP_ADDRESS, ConstantUtils.APP_LOGIN_DOMAIN);
        SpUtil.getInstance(BaseApplication.getContext()).putBoolean(SpUtil.LANGUAGE_SETTING_CN, ConstantUtils.LANGUAGE_SETTING_CN);
        SpUtil.getInstance(BaseApplication.getContext()).putBoolean(SpUtil.LANGUAGE_SETTING_EN, ConstantUtils.LANGUAGE_SETTING_EN);
        SpUtil.getInstance(BaseApplication.getContext()).putBoolean(SpUtil.LANGUAGE_SETTING_FN, ConstantUtils.LANGUAGE_SETTING_FN);
    }

    /**
     * 更新sp参数到静态变量
     */
    public static void updateSp2ConsSetValue() {
        ConstantUtils.NUMBER_BUY = SpUtil.getInstance(BaseApplication.getContext()).getInt(SpUtil.NUMBER_BUY, 1);
        ConstantUtils.APP_LOGIN_DOMAIN = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.IP_ADDRESS);
        ConstantUtils.LANGUAGE_SETTING_CN = SpUtil.getInstance(BaseApplication.getContext()).getBoolean(SpUtil.LANGUAGE_SETTING_CN);
        ConstantUtils.LANGUAGE_SETTING_EN = SpUtil.getInstance(BaseApplication.getContext()).getBoolean(SpUtil.LANGUAGE_SETTING_EN);
        ConstantUtils.LANGUAGE_SETTING_FN = SpUtil.getInstance(BaseApplication.getContext()).getBoolean(SpUtil.LANGUAGE_SETTING_FN);
    }

    private void showNormalDialog() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(DeviceSettingActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("你是否继续切换账户?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        Intent finishIntent = new Intent("action.exit.finish");
                        finishIntent.putExtra(EXITACTION, 1);
                        sendBroadcast(finishIntent);

                        Intent intent = new Intent(DeviceSettingActivity.this, LoginActivity.class);
                        intent.putExtra("isSwitchSales", true);
                        startActivity(intent);
                        DeviceSettingActivity.this.finish();
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
