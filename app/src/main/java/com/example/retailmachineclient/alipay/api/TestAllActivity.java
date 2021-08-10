//package com.example.retailmachineclient;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.view.View;
//import android.widget.Toast;
//
//import com.allinpay.aipmis.allinpay.model.RequestData;
//import com.allinpay.aipmis.allinpay.model.ResponseData;
//import com.allinpay.aipmis.allinpay.service.MisPos;
//import com.allinpay.manager.listener.IMessageListener;
//import com.allinpay.manager.model.MessageBean;
//import com.kongqw.serialportlibrary.SerialPortManager;
//
//import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
//
//import com.example.retailmachineclient.mcuSdk.Logger;
//import com.example.retailmachineclient.mcuSdk.MCU;
//
//import java.io.File;
//
//public class TestAllActivity extends Activity implements View.OnClickListener {
//    //返回结果
//    private ResponseData response;
//    SerialPortManager mSerialPortManager;
//    String result="";
//    //消息处理
//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            Toast.makeText(getApplicationContext(), msg.obj + "", Toast.LENGTH_SHORT).show();
//        }
//
//        ;
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.test_layout);
//        findViewById(R.id.register).setOnClickListener(this);
//        findViewById(R.id.consume).setOnClickListener(this);
//        findViewById(R.id.robot).setOnClickListener(this);
//
//    }
//
//    //点击事件
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            //签到
//            case R.id.register:
////                resetControls();
//                MisPos compos = new MisPos(this);
//                //设置自定义监听器（可省略，使用系统默认监听器）
//                compos.setOnMessageListener((IMessageListener) new MessageListenerImpl());
//                RequestData reqData = buildRequestData();
//                reqData.PutValue("TransType", "1");
//                response = new ResponseData();
//                compos.TransProcess(reqData, response);
//                handleResponseData();
//                break;
//
//            //消费
//            case R.id.consume:
////                resetControls();
////                MisPos compos = new MisPos(this);
////                compos.setOnMessageListener(new MessageListenerImpl());
////                RequestData reqData = buildRequestData();
////                reqData.PutValue("TransType", "2");
////                response = new ResponseData();
////                compos.TransProcess(reqData, response);
////                handleResponseData();
//                Logger.e("串口打开！" );
//                mSerialPortManager = new SerialPortManager();
//                mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
//                    @Override
//                    public void onSuccess(File file) {
//                        Logger.e("串口打开成功！" + file.getPath());
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                result = result + "\n" + "串口打开成功";
////                                tvResult.setText(result);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFail(File file, Status status) {
//                        Logger.e("串口打开失败！" + file.getPath());
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                result = result + "\n" + "串口打开失败";
////                                tvResult.setText(result);
//                            }
//                        });
//                    }
//                });
//                mSerialPortManager.openSerialPort(new File(MCU.PortMCU.MCU1.getPath()), MCU.PORT_RATE);
//
//                break;
//
////            case R.id.robot:
////                mSerialPortManager = new SerialPortManager();
////                mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
////                    @Override
////                    public void onSuccess(File file) {
////                        Logger.e("串口打开成功！" + file.getPath());
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                result = result + "\n" + "串口打开成功";
////                                tvResult.setText(result);
////                            }
////                        });
////                    }
////
////                    @Override
////                    public void onFail(File file, Status status) {
////                        Logger.e("串口打开失败！" + file.getPath());
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                result = result + "\n" + "串口打开失败";
////                                tvResult.setText(result);
////                            }
////                        });
////                    }
////                });
////                mSerialPortManager.openSerialPort(new File(MCU.PortMCU.MCU1.getPath()), MCU.PORT_RATE);
////            break;
//
//            default:
//                break;
//        }
//
//    }
//
//    private void handleResponseData() {
//        String kk = response.GetValue("Tips");
//    }
//
//    //自定义消息回调函数
//    class MessageListenerImpl implements IMessageListener {
//
//        private MessageBean msg;
//
//        public MessageBean getMsg() {
//            return msg;
//        }
//
//        public void setMsg(MessageBean msg) {
//            this.msg = msg;
//        }
//
//        @Override
//        public void showMessage() {
//
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    //非UI线程调用UI线程，消息队列初始化
//                    Looper.prepare();
//                    //获取消息对象
//                    Message message = handler.obtainMessage();
//
//                    message.obj = msg.info;
////					if(0x01 == msg.type){
////						message.obj = "自定义消息体";
////					}else{
////						message.obj = msg.info;
////					}
//                    handler.handleMessage(message);
//                    //消息队列poll信息并执行
//                    Looper.loop();
//                }
//            }).start();
//
//        }
//
//    }
//
//    //初始化requestData
//    private RequestData buildRequestData() {
//        RequestData reqData = new RequestData();
//        reqData.PutValue("CardType", "01");
////		reqData.PutValue("appname", "DEBUG");
//        reqData.PutValue("Amount", "0.01");
//        reqData.PutValue("OldTraceNumber", "");
//        reqData.PutValue("HostSerialNumber", "");
//        //日期输入格式MMDD如1227
//        reqData.PutValue("TransDate", "");
//        return reqData;
//    }
//}
