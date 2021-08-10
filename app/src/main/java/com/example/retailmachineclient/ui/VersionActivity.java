package com.example.retailmachineclient.ui;

import android.app.ProgressDialog;
import android.os.Environment;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.net.Uri;

import com.example.retailmachineclient.BuildConfig;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.model.GoodInfoModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.model.rsp.VersionRspModel;
import com.example.retailmachineclient.util.CloseBarUtil;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.NetUtils;
import com.example.retailmachineclient.util.SpUtil;
import com.example.retailmachineclient.util.TaskUtils;
import com.example.retailmachineclient.util.ThreadPoolManager;
import com.example.retailmachineclient.util.TimeIntervalUtils;

import DDRAIServiceProto.DDRAIServiceCmd;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.ArrayList;

import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;

import DDRAIServiceProto.DDRAIServiceCmd;
import butterknife.OnClick;

import com.example.retailmachineclient.util.CloseBarUtil;
import com.example.retailmachineclient.util.TimeIntervalUtils;

import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.File;

import com.example.retailmachineclient.service.DownloadServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class VersionActivity extends BaseActivity implements View.OnClickListener {
    TextView tv_current = null;
    TextView tv_latest = null;
    Button bt_download = null;

    String downloadUrl;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMainThread(MessageEvent msgEvent) {
        switch (msgEvent.getType()) {

            case MessageEvent.EventType_GET_VERSIONH_SUCCESS:
                VersionRspModel versionRspModel = (VersionRspModel) msgEvent.getTag();
                if (versionRspModel != null && versionRspModel.getData() != null) {
                    downloadUrl = versionRspModel.getData().getRoute();//ConstantUtils.APP_LOGIN_DOMAIN +
                    tv_latest.setText("最新版本:" + versionRspModel.getData().getName());
                    bt_download.setClickable(true);
                    bt_download.setVisibility(View.VISIBLE);
                }
                break;

            case MessageEvent.EventType_GET_VERSIONH_FAIL:
                tv_latest.setText("当前版本已是最新版本");
                break;

            case MessageEvent.EventType_GET_VERSIONH_ERROR:
//                isStopTask = true;
                tv_latest.setText("检测版本失败，请稍后再试");
                break;
            case MessageEvent.apkDownloadSucceed:

                dismissWaitingDialog();
                String apkPath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "dir" + File.separator + "RetailMachine.apk";

                bt_download.setText("正在安装");
                bt_download.setClickable(false);
//                slientInstall(apkPath);
//                installApp(apkPath);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                // 由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                        "application/vnd.android.package-archive");
                context.startActivity(intent);
                break;
            case MessageEvent.apkDownloadFailed:
//                isStopTask = true;
                dismissWaitingDialog();
                break;
            case MessageEvent.updateProgress:
                int progress = msgEvent.getPosition();

                if (System.currentTimeMillis() - viewTime > 1000) {
//                    Logger.e("下载进度dd=" + progress);
                    viewTime = System.currentTimeMillis();
                    waitingDialog.setProgress(progress);
                }
                break;
            case MessageEvent.apkDownloadCancelShow:
//                isStopTask = true;
                dismissWaitingDialog();
                Toast.makeText(BaseApplication.getContext(), "取消下载", Toast.LENGTH_SHORT).show();
                break;


        }
    }

    long viewTime = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_version;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.download).setOnClickListener(this);
        tv_current = (TextView) findViewById(R.id.current);
        tv_latest = (TextView) findViewById(R.id.latest);
        bt_download = (Button) findViewById(R.id.download);
    }

    @Override
    protected void initData() {
//        IntentFilter filterFinish = new IntentFilter();
//        filterFinish.addAction("action.exit.finish");
//        registerReceiver(exitFinishReceiver, filterFinish);
        ThreadPoolManager.getInstance().initThreadPool();
        tv_current.setText("当前版本:" + getAppVersionName(VersionActivity.this));
        int versionCode = getAppVersionCode(VersionActivity.this);
        Runnable runnableQuery = new Runnable() {
            @Override
            public void run() {
                NetUtils.getInstance().getVersion(versionCode);
            }
        };
        ThreadPoolManager.getInstance().executeRunable(runnableQuery);


    }

    /**
     * 返回当前程序版本号
     */
    public static int getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            // versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
//            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
//            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.download:
                showWaitingDialog();
                Intent intentOne = new Intent(this, DownloadServer.class);
                Logger.e("下载apk地址=" + downloadUrl);
                intentOne.putExtra("downloadUrl", downloadUrl);
                startService(intentOne);
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
                new AlertDialog.Builder(VersionActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("你是否继续退出APP?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        normalDialog.dismiss();
                        CloseBarUtil.showBar(VersionActivity.this);
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
//                        dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }

    ProgressDialog waitingDialog = null;

    private void showWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        if (waitingDialog == null) {
            waitingDialog =
                    new ProgressDialog(VersionActivity.this);
            waitingDialog.setTitle("下载提示");
            waitingDialog.setMessage("下载中...");
            waitingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitingDialog.setMax(100);
            waitingDialog.setProgress(0);

            waitingDialog.setIndeterminate(false);
            waitingDialog.setCancelable(false);


            waitingDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.apkDownloadCancel));
                    //
                }
            });
        }
        waitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                isStopTask = true;
            }
        });
        if (!waitingDialog.isShowing()) {
            waitingDialog.show();
        }

    }

    private void dismissWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        if (waitingDialog != null) {
            waitingDialog.dismiss();
        }
    }


    public static boolean installApp(String apkPath) {
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
//            process = new ProcessBuilder("pm", "install","-i","com.example.retailmachineclient", "-r", apkPath).start();
            process = new ProcessBuilder("pm", "install", "-r", apkPath).start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {

        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {

            }
            if (process != null) {
                process.destroy();
            }
        }
        Logger.e("安装结果result" + errorMsg.toString());
//        Toast.makeText(VersionActivity.this,+errorMsg.toString()+"  "+successMsg , Toast.LENGTH_SHORT).show();
        //如果含有“success”单词则认为安装成功
        return successMsg.toString().equalsIgnoreCase("success");
    }

    public boolean slientInstall(String url) {
        List<String> apkList = new ArrayList<String>();
        apkList.add(url);
//        apkList = extractApkToLocal(); //获取apk本地路径
        boolean result = false;
        for (String path : apkList) {
            File file = new File(path);
            Process process = null;
            OutputStream out = null;
            if (file.exists()) {
                try {
                    process = Runtime.getRuntime().exec("su");
                    out = process.getOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(out);

                    // 更改本地apk文件权限，方便执行安装操作
                    dataOutputStream.writeBytes("chmod 777 " + file.getPath()
                            + "\n");

                    // 进行安装
                    dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
                            + file.getPath());
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    out.close();
                    int value = process.waitFor();

                    // 成功
                    if (value == 0) {
                        result = true;
                        // 失败
                    } else if (value == 1) {
                        result = false;
                        // 未知情况
                    } else {
                        result = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!result) {
                    result = true;
                }
            }
        }
        Logger.e("安装结果result" + result);
        return result;
    }


}