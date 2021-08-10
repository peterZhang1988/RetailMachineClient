package com.example.retailmachineclient.order;//package com.example.retailmachineclient.order;
//
//import android.annotation.SuppressLint;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.example.retailmachineclient.R;
//import com.example.retailmachineclient.base.BaseActivity;
//import com.example.retailmachineclient.base.GoodsModel;
//import com.example.retailmachineclient.mcuSdk.DataProtocol;
//import com.example.retailmachineclient.mcuSdk.MCU;
//import com.example.retailmachineclient.mcuSdk.SerialPortUtil;
//import com.example.retailmachineclient.util.LogcatHelper;
//import com.example.retailmachineclient.util.Logger;
//import com.example.retailmachineclient.util.SerialPortManagerUtils;
//import com.example.retailmachineclient.util.Utils;
//import com.kongqw.serialportlibrary.SerialPortManager;
//import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//
//class LiftMoveOrder {
//package com.example.retailmachineclient.ui;
//
//import android.annotation.SuppressLint;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.example.retailmachineclient.R;
//import com.example.retailmachineclient.base.BaseActivity;
//import com.example.retailmachineclient.base.GoodsModel;
//import com.example.retailmachineclient.mcuSdk.DataProtocol;
//import com.example.retailmachineclient.mcuSdk.MCU;
//import com.example.retailmachineclient.mcuSdk.SerialPortUtil;
//import com.example.retailmachineclient.order.OrderRequest;
//import com.example.retailmachineclient.util.LogcatHelper;
//import com.example.retailmachineclient.util.Logger;
//import com.example.retailmachineclient.util.SerialPortManagerUtils;
//import com.example.retailmachineclient.util.Utils;
//import com.kongqw.serialportlibrary.SerialPortManager;
//import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//
//    public class PayActivity extends BaseActivity {
//        //取货门上推打开
//        public static final int ORDER_UP_PUSH = 101;
//        //取货门上推打开结果
//        public static final int ORDER_UP_PUSH_RESULT = 102;
//
//        //取货门下推关门
//        public static final int ORDER_DOWN_PUSH = 103;
//        //取货门下推关门结果
//        public static final int ORDER_DOWN_PUSH_RESULT = 104;
//
//        //电机旋转
//        public static final int ORDER_POWER_MACHINE = 105;
//        //电机旋转结果
//        public static final int ORDER_POWER_MACHINE_RESULT = 106;
//
//        //升降机移动
//        public static final int ORDER_LIFT_MOVE = 107;
//        //升降机移动结果
//        public static final int ORDER_LIFT_MOVE_RESULT = 108;
//
//        //货盘是否为空
//        public static final int ORDER_PALLET_EMPTY = 109;
//        //货盘是否为空结果
//        public static final int ORDER_PALLET_EMPTY_RESULT = 110;
//
//        //取货门是否夹手
//        public static final int ORDER_PINCH_HANDS = 111;
//        //取货门是否夹手结果
//        public static final int ORDER_PINCH_HANDS_RESULT = 112;
//
//        @BindView(R.id.tvPushUp)
//        Button tvPushUp;
//        @BindView(R.id.tvResult)
//        TextView tvResult;
//        @BindView(R.id.etNumber)
//        EditText etNumber;;
//        private String result="";
//        private SerialPortManager mSerialPortManager;
//        private SerialPortUtil serialPortUtil;
//
//        @Override
//        protected int getLayoutId() {
//            return R.layout.activity_test_pay;
//        }
//
//        @SuppressLint("HandlerLeak")
//        final Handler mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//
//                switch (msg.what){
//                    case ORDER_UP_PUSH:
//
//                        break;
//                }
//                super.handleMessage(msg);
//
//            }
//        };
//
//
//        @Override
//        protected void initView() {
//            setStatusBarEnabled(true);
//        }
//
//        @Override
//        protected void initData() {
//            //mcuSDK=McuSDK.initSDK(BaseApplication.context);
//        /*serialPortUtil=new SerialPortUtil();
//        serialPortUtil.open(Mcu.PortMCU.MCU1.getPath(),Mcu.PORT_RATE);*/
//            mSerialPortManager= SerialPortManagerUtils.getInstance();
//
//            mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
//                @Override
//                public void onSuccess(File file) {
//                    Logger.e("串口打开成功！"+file.getPath());
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            result=result+"\n"+"串口打开成功";
//                            tvResult.setText(result);
//                        }
//                    });
//                }
//
//                @Override
//                public void onFail(File file, Status status) {
//                    Logger.e("串口打开失败！"+file.getPath());
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            result=result+"\n"+"串口打开失败";
//                            tvResult.setText(result);
//                        }
//                    });
//                }
//            });
//            mSerialPortManager.openSerialPort(new File(MCU.PortMCU.MCU1.getPath()), MCU.PORT_RATE);
//        }
//
//        @Override
//        protected void onRestart() {
//            super.onRestart();
//        }
//
//        @Override
//        protected void onResume() {
//            super.onResume();
//        }
//
//        @Override
//        protected void onStart() {
//            super.onStart();
//        }
//
//        @Override
//        protected void onPause() {
//            super.onPause();
//        }
//
//        @Override
//        protected void onStop() {
//            super.onStop();
//        }
//
//        @Override
//        protected void onDestroy() {
//            super.onDestroy();
//            LogcatHelper.getInstance(context).stop();
//        }
//
//
//        @OnClick({R.id.tvPushUp,R.id.btPullDown,R.id.btcx,R.id.btCXLifter,R.id.btStart,R.id.btClear,R.id.btCPushGoods
//                ,R.id.tvLift0,R.id.tvLift1,R.id.tvLift2,R.id.tvLift3,R.id.tvLift4,R.id.tvLift5,R.id.tvLift6,R.id.tvLift7,R.id.btCTake})
//        public void onViewClicked(View view) {
//            byte data;
//            switch(view.getId()){
//                case R.id.tvPushUp:
//                    //serialPortUtil.sendData(DataProtocol.packSendData(Mcu.PortMCU.MCU1,(byte)0x14,(byte)0x01));
//                    mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1,(byte)0x14,(byte)0x01));
//                    break;
//                case R.id.btPullDown:
//                    //serialPortUtil.sendData(DataProtocol.packSendData(Mcu.PortMCU.MCU1,(byte)0x14,(byte)0x01));
//                    mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1,(byte)0x14,(byte)0x00));
//                    break;
//                case R.id.btcx:
//                    mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1,(byte)0x15,(byte)0x00));
//                    break;
//                case R.id.btCXLifter:
//                    mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1,(byte)0x13,(byte)0x00));
//                    break;
//                //出货全流程
//                case R.id.btCTake:
//                    GoodsModel goodsModel = new GoodsModel();
//                    goodsModel.setLine(2);
//                    goodsModel.setRow(30);
//                    List<GoodsModel> goodsList = new ArrayList<GoodsModel>();
//                    goodsList.add(goodsModel);
//                    goodsModel  = new GoodsModel();
//                    goodsModel.setLine(2);
//                    goodsModel.setRow(31);
//                    goodsList.add(goodsModel);
//                    pickUpGoods(goodsList);
////                 getGoods(2,31);
//                    break;
//
//                case R.id.btCPushGoods:
//                    for (int ip = 30; ip < 40; ip++) {
//                        byte[] nData=new byte[16];
//                        nData[0]=(byte)ip;
//                        nData[1]=(byte)0x03;
//                        for (int i=2;i<nData.length;i++){
//                            nData[i]=0x00;
//                        }
//                        OrderRequest orderRequest  = new OrderRequest(mHandler);
//                        orderRequest.requestCard(MCU.PortMCU.MCU5,(byte)0x05,nData,ORDER_POWER_MACHINE);
////                    mSerialPortManager.sendBytes(DataProtocol.packSendDataNew(MCU.PortMCU.MCU5,(byte)0x05,nData));
//                        try {
//                            Thread.sleep(1000);//.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    break;
//                case R.id.btStart:
//
//                    break;
//                case R.id.btClear:
//                    result="";
//                    break;
//
//                case R.id.tvLift0:
//                    data=0x00;
//                    liftMove(data);
//                    break;
//                case R.id.tvLift1:
//                    data=0x01;
//                    liftMove(data);
//                    break;
//                case R.id.tvLift2:
//                    data=0x02;
//                    liftMove(data);
//                    break;
//                case R.id.tvLift3:
//                    data=0x03;
//                    liftMove(data);
//                    break;
//                case R.id.tvLift4:
//                    data=0x04;
//                    liftMove(data);
//                    break;
//                case R.id.tvLift5:
//                    data=0x05;
//                    liftMove(data);
//                    break;
//                case R.id.tvLift6:
//                    data=0x06;
//                    liftMove(data);
//                    break;
//                case R.id.tvLift7:
//                    data=0x07;
//                    liftMove(data);
//                    break;
//            }
//        }
//
//        int currentState = 1;//开始取货
//        public void handleResult(){
//            switch(currentState){
//                //复位阶段
//                case 1:
//
//                    break;
//            }
//
//        }
//
//        public void pickUpGoods(List<GoodsModel> goodsList){
//            for(GoodsModel goodsModel :goodsList){
//                getGoods(goodsModel.getLine(),goodsModel.getRow());
//            }
//            //升降梯复位
//            //货盘是否有货
//            boolean isReset = false;
//            isReset = isResetLift();//yes
//            if(isReset){
//                Logger.e("货盘已经复位");
//            }else{
//                Logger.e("货盘没有复位");
//            }
//            boolean isHasGoods = queryIsHasGoods();
//            if(isHasGoods){
//                Logger.e("货盘有货");
//            }else{
//                Logger.e("货盘没货");
//            }
//            if(isReset || isHasGoods){
//                Logger.e("满足打开舱门的条件");
//                openDoor();
//                //检查舱门是否完成打开
//
//            }else{
//                Logger.e("不满足打开舱门的条件");
//            }
//            //有货打开舱门
//            //检查舱门是否完成打开
//
//            //检查是否取走货物 货盘是否有货
//            isHasGoods = queryIsHasGoods();
//            for(int repeatQueryHasGoods =0;repeatQueryHasGoods<20;repeatQueryHasGoods++){
//                isHasGoods = queryIsHasGoods();
//                if(!isHasGoods){
//                    //已取走货物
//                    Logger.e("已取走货物");
//                    break;
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if(isHasGoods){
//                //货物一直没有被取走
//                Logger.e("货物一直没有被取走");
//                return ;
//            }
//
//            int switchStatus = 100;
//            while(true){
//                switchStatus = queryIsProtectHand();//0的可以关闭
//                if(switchStatus ==0){
//                    break;
//                }
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(switchStatus == 0){
//                closeDoor();
//            }
//
//            // 没有货物时 检查防夹手开关是否打开若没有可以关闭舱门
//
//            //是否接触开关打开 打开：不能关闭  关闭：可以关闭窗门
//        }
//
//
//
//        //2,30
//        public void getGoods(int line,int row){
//            Logger.e("启动 getGoods");
//            //查询是否复位
//            boolean isReset = false;
//            isReset = isResetLift();//yes
//            boolean resetResult =true;
//            if(!isReset){
//                //没有复位
//                Logger.e("启动升降机 没有复位 ");
//                resetResult = resetLift();
//            }
//
//            if(!resetResult){
//                Logger.e("启动升降机 一直复位不成功");
//                //一直复位不成功
//                return;
//            }
//            //已经复位
//            //复位完成
//            //升降移动指定位置
//            int moveResult = 100;
//            for(int repeat =0;repeat<50;repeat++){
//                moveResult = liftMoveNew((byte)line);
//                if(moveResult == 0){
//                    Logger.e("启动getGoods 2");
//                    break;
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(moveResult == 0){
//
//            }else{
//                Logger.e("启动升降机 5次后失败");
//                return;
//            }
//            Logger.e("启动升降机 正常启动 升降机是否到达指定位置");
//            //升降机是否到达指定位置
//            for(int repeatQueryPosition =0;repeatQueryPosition<100;repeatQueryPosition++){
//                List<Integer> list = queryLiftStatus();
//                if(list != null&&list.size()==3){
//                    if(list.get(1) == line){
//                        Logger.e("启动升降机 移动到指定位置");
//                        break;
//                    }else{
//                        Logger.e("启动升降机 未移动到指定位置");
//                    }
//                }else{
//                    Logger.e("查询升降机状态通信异常");
//                }
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //查询升降机当前位置
//            Logger.e("电机启动前准备检查");
//            //?
//
//            List<Integer> machineStatusList = queryMachineStatus();
//            if(machineStatusList!= null && machineStatusList.size()==3){
//                if((machineStatusList.get(0)==0||machineStatusList.get(0)==0)
//                        && machineStatusList.get(2)==0
//                ){
//                    Logger.e("电机启动前准备检查 是否read Yes");
//                }
//            }
//
//
//
//            startMachine(row);
//            Logger.e("电机启动后 查询执行旋转结果");
//            List<Integer> machineStartStatusList = queryMachineStatus();
//            if(machineStartStatusList!= null && machineStartStatusList.size()==3){
//                if((machineStartStatusList.get(0)==0||machineStartStatusList.get(0)==0)
//                        && machineStartStatusList.get(2)==0
//                ){
//                    Logger.e("电机启动后 正常执行旋转完毕");
//                }else{
//                    Logger.e("电机启动后 正常执行旋转异常");
//                }
//            }
//
//            //货盘复位 不停复位
//            resetResult = false;
//            for(int repeatLift = 0 ;repeatLift<20;repeatLift++){
//                resetResult = resetLiftCheckSuccess();
//                if(resetResult){
//                    //
//                    break;
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(resetResult){
//                Logger.e("获取货物后，货盘复位成功");
//            }else{
//                Logger.e("获取货物后，货盘复位不成功");
//            }
//
//            //已经复位
//            //没有复位
//            //复位 如果没有复位继续复位
//            //复位完成
//            //升降移动指定位置
//            //0 启动正常 1 无效索引 返回 2 正在运行 延时等待重新发送指令 3 启动失败 返回提示
//            //查询升降机执行状态
//            //升降机是否到达指定位置
//            //升降机已经到达指定位置
//            //升降机没有到达指定位置
//            //查询电机run执行状态
//            //0:空闲，1：执行中，2：执行完毕
//            //电机空闲
//            //启动电机
//            //启动电机结果0：已启动，1：无效索引，2：另一台电机在运行
//            //正常启动电机
//            //查询电机run执行状态
//            //0:空闲，1：执行中，2：执行完毕
//            //拣货完成 打开取货门
//            //打开取货门是否正常
//            //打开取货门正常
//            //打开取货门没正常 继续打开取货门
//            //获取货盘状态是否为空 延时查询直至获取结果
//            //获取是否有障碍 防夹手 延时查询直至获取结果
//            //货盘为空 无夹手时
//            //关闭取货门
//            //关闭取货门是否正常 不正常继续关闭
//        }
//
//        /**
//         * 升降梯移动
//         * @param data 层数
//         *
//         */
//        public void liftMove(byte data){
//            byte[] nData=new byte[16];
//            nData[0]=(byte)data;
//            for (int i=1;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes = orderRequest.requestCard(MCU.PortMCU.MCU1,(byte)0x12,nData,ORDER_POWER_MACHINE);
//            if(responseBytes == null){
//                Logger.e("成功:responseBytes为空！");
//            }else{
//                Logger.e("成功:responseBytes:"+ Utils.byteBufferToHexString(responseBytes));
//            }
//        }
//
//        /**
//         * 是否复位
//         * @return
//         */
//        public boolean isResetLift(){
//            List<Integer> list = queryLiftStatus();
//            if(list != null &&list.size()>0){
//                if((list.get(0) ==0 ||list.get(0)==2)
//                        && list.get(1)==7
//                        && list.get(2)==0)
//                {
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        /**
//         * 复位操作
//         * @return 是否正常启动
//         */
//        public boolean resetLift(){
//            int result = liftMoveNew((byte)7);
//            return true;
//        }
//
//        /**
//         * 复位操作同时检查是否复位到初始位置
//         * @return 复位成功
//         */
//        public boolean resetLiftCheckSuccess(){
//            boolean isSuccess = false;
//            int result = liftMoveNew((byte)7);
//            if(result == 0){
//                for(int repeatQueryPosition =0;repeatQueryPosition<50;repeatQueryPosition++){
//                    List<Integer> list = queryLiftStatus();
//                    if(list != null&&list.size()==3){
//                        if(list.get(1) == 7){
//                            isSuccess = true;
//                            Logger.e("启动升降机 移动到指定位置 复位成功");
//                            break;
//                        }else{
//                            Logger.e("启动升降机 未移动到指定位置 复位失败");
//                        }
//                    }else{
//                        Logger.e("查询升降机状态通信异常");
//                    }
//
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            return isSuccess;
//        }
//
//        /**
//         * 升降梯移动
//         * @param data 层数
//         *
//         */
//        public int liftMoveNew(byte data){
//            byte[] nData=new byte[16];
//            nData[0]=(byte)data;
//            for (int i=1;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes = orderRequest.requestCard(MCU.PortMCU.MCU1,(byte)0x12,nData,ORDER_POWER_MACHINE);
//            if(responseBytes == null){
//                Logger.e("成功:responseBytes为空！");
//            }else{
//                Logger.e("成功:responseBytes:"+Utils.byteBufferToHexString(responseBytes));
//            }
//            //解析结果
//
//            byte result = 0;
//            int resultInt = 100;
//            if (responseBytes != null) {
//                result = responseBytes[2];
//                resultInt = Utils.byteToInt(result);
//            }
//
//            if (resultInt == 0) {
//                //已启动
//                Logger.e("启动升降机 已启动");
//            } else if (resultInt == 1) {
//                //已启动
//                Logger.e("启动升降机 无效索引");
//            } else if (resultInt == 2) {
//                //已启动
//                Logger.e("启动升降机 正在运行");
//            }  else if (resultInt == 3) {
//                //已启动
//                Logger.e("启动升降机 启动失败");
//            } else {
//                //已启动
//                Logger.e("启动升降机 通信异常");
//            }
//            return resultInt;
//        }
//
//        /**
//         * 启动电机
//         * @return
//         */
//        public int startMachine(int index){
//            byte[] nData=new byte[16];
//            nData[0]=(byte)index;//电机索引号
//            nData[1]=(byte)0x03;//电机类型
//            for (int i=2;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes  = orderRequest.requestCard(MCU.PortMCU.MCU5,(byte)0x05,nData,ORDER_POWER_MACHINE);
////                    mSerialPortManager.sendBytes(DataProtocol.packSendDataNew(MCU.PortMCU.MCU5,(byte)0x05,nData));
//            try {
//                Thread.sleep(1000);//.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            int orderRunResult = 100;
//            if (responseBytes != null) {
//                orderRunResult = Utils.byteToInt(responseBytes[2]);
//
//            }
//
//            return orderRunResult;
//        }
//
//        /**
//         * 查询升降机执行状态 13
//         * @return
//         */
//        public List<Integer> queryLiftStatus(){
//            List<Integer> resultList = new ArrayList<Integer>();
//            byte[] nData=new byte[16];
////        nData[0]=(byte)data;
//            for (int i=0;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes = orderRequest.requestCard(MCU.PortMCU.MCU1,(byte)0x13,nData,ORDER_POWER_MACHINE);
//            if(responseBytes == null){
//                Logger.e("成功:responseBytes为空！");
//            }else{
//                Logger.e("成功:responseBytes:"+Utils.byteBufferToHexString(responseBytes));
//            }
//            //解析结果
//
//            byte result = 0;
//            int resultInt = 100;
//            //
//            int orderRunStatus = 100;
//            int currentFloorStatus = 100;
//            int orderRunResult = 100;
//            if (responseBytes != null) {
//                orderRunStatus = Utils.byteToInt(responseBytes[2]);
//                currentFloorStatus = Utils.byteToInt(responseBytes[3]);
//                orderRunResult = Utils.byteToInt(responseBytes[4]);
//            }
//            Logger.e("获取升降机状态成功:orderRunStatus="+orderRunStatus+",currentFloorStatus="+currentFloorStatus+",orderRunResult="+orderRunResult);
//            resultList.add(orderRunStatus);
//            resultList.add(currentFloorStatus);
//            resultList.add(orderRunResult);
//
//            return resultList;
//        }
//
//        /**
//         * 查询光幕是否遮挡，防夹手
//         * @return N
//         */
//        public int queryIsProtectHand(){
//            byte[] nData=new byte[16];
//            for (int i=0;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes = orderRequest.requestCard(MCU.PortMCU.MCU1,(byte)0x19,nData,ORDER_POWER_MACHINE);
//            if(responseBytes == null){
//                Logger.e("成功:responseBytes为空！");
//            }else{
//                Logger.e("成功:responseBytes:"+Utils.byteBufferToHexString(responseBytes));
//            }
//            //解析结果
//            byte result = 0;
//            int resultInt = 100;
//            if (responseBytes != null) {
//                result = responseBytes[2];
//                resultInt = Utils.byteToInt(result);
//            }
//            Logger.e("获取接近开关状态 成功:"+resultInt);
//            if (resultInt == 0) {
//                //货盘空
////            return false;
//            } else if (resultInt == 1) {
//                //货盘有物品
////            return true;
//            }
//            return 0;
//        }
//
//        /**
//         * 查询货盘是否有货物 1d
//         * @return 有货:true,无货：false 默认返回：false
//         */
//        public boolean queryIsHasGoods(){
//            byte[] nData=new byte[16];
//            for (int i=0;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes = orderRequest.requestCard(MCU.PortMCU.MCU1,(byte)0x1C,nData,ORDER_POWER_MACHINE);
//            if(responseBytes == null){
//                Logger.e("成功:responseBytes为空！");
//            }else{
//                Logger.e("成功:responseBytes:"+Utils.byteBufferToHexString(responseBytes));
//            }
//            //解析结果
//            byte result = 0;
//            int resultInt = 100;
//            if (responseBytes != null) {
//                result = responseBytes[2];
//                resultInt = Utils.byteToInt(result);
//            }
//            if (resultInt == 0) {
//                //货盘空
//                return false;
//
//            } else if (resultInt == 1) {
//                //货盘有物品
//                return true;
//            }
//            return false;
//        }
//
//        /**
//         * 查询电机run执行状态
//         * @return
//         */
//        public List<Integer> queryMachineStatus(){
//            List<Integer> resultList = new ArrayList<Integer>();
//            byte[] nData=new byte[16];
////        nData[0]=(byte)index;//电机索引号
////        nData[1]=(byte)0x03;//电机类型
//            for (int i=0;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes  = orderRequest.requestCard(MCU.PortMCU.MCU5,(byte)0x03,nData,ORDER_POWER_MACHINE);
////                    mSerialPortManager.sendBytes(DataProtocol.packSendDataNew(MCU.PortMCU.MCU5,(byte)0x05,nData));
//            try {
//                Thread.sleep(1000);//.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            int orderRunStatus = 100;
//            int currentFloorStatus = 100;
//            int orderRunResult = 100;
//            if (responseBytes != null) {
//                orderRunStatus = Utils.byteToInt(responseBytes[2]);
//                currentFloorStatus = Utils.byteToInt(responseBytes[3]);
//                orderRunResult = Utils.byteToInt(responseBytes[4]);
//            }
//            Logger.e("获取电机执行状态成功:orderRunStatus="+orderRunStatus+",currentFloorStatus="+currentFloorStatus+",orderRunResult="+orderRunResult);
//
//            resultList.add(orderRunStatus);
//            resultList.add(currentFloorStatus);
//            resultList.add(orderRunResult);
//            return resultList;
//
//        }
//
//        /**
//         * 开门
//         * @return
//         */
//        public boolean openDoor(){
////        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1,(byte)0x14,(byte)0x01));
//            byte[] nData=new byte[16];
//            nData[0] = (byte)0x01;
//            for (int i=1;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes = orderRequest.requestCard(MCU.PortMCU.MCU1,(byte)0x14,nData,ORDER_POWER_MACHINE);
//            if(responseBytes == null){
//                Logger.e("成功:responseBytes为空！");
//            }else{
//                Logger.e("成功:responseBytes:"+Utils.byteBufferToHexString(responseBytes));
//            }
//            //解析结果
//            byte result = 0;
//            int resultInt = 100;
//            if (responseBytes != null) {
//                result = responseBytes[2];
//                resultInt = Utils.byteToInt(result);
//            }
//            Logger.e("开门返回结果:orderRunStatus="+resultInt);
//            if (resultInt == 0) {
//                //已启动
//                return true;
//            } else  {
//                //未启动
//                return false;
//            }
//        }
//
//        /**
//         * 关门
//         * @return
//         */
//        public boolean closeDoor(){
////        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1,(byte)0x14,(byte)0x01));
//            byte[] nData=new byte[16];
//            nData[0] = (byte)0x00;
//            for (int i=0;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes = orderRequest.requestCard(MCU.PortMCU.MCU1,(byte)0x14,nData,ORDER_POWER_MACHINE);
//            if(responseBytes == null){
//                Logger.e("成功:responseBytes为空！");
//            }else{
//                Logger.e("成功:responseBytes:"+Utils.byteBufferToHexString(responseBytes));
//            }
//            //解析结果
//            byte result = 0;
//            int resultInt = 100;
//            if (responseBytes != null) {
//                result = responseBytes[2];
//                resultInt = Utils.byteToInt(result);
//            }
//            Logger.e("关门返回结果:orderRunStatus="+resultInt);
//            if (resultInt == 0) {
//                //已启动
//                return true;
//            } else  {
//                //未启动
//                return false;
//            }
//        }
//
//        /**
//         * 获取推杆指令执行状态
//         * @return
//         */
//
//        public List<Integer> queryDoorStatus(){
//            List<Integer> resultList = new ArrayList<Integer>();
//            byte[] nData=new byte[16];
////        nData[0]=(byte)data;
//            for (int i=0;i<nData.length;i++){
//                nData[i]=0x00;
//            }
//            OrderRequest orderRequest  = new OrderRequest(mHandler);
//            byte[] responseBytes = orderRequest.requestCard(MCU.PortMCU.MCU1,(byte)0x15,nData,ORDER_POWER_MACHINE);
//            if(responseBytes == null){
//                Logger.e("成功:responseBytes为空！");
//            }else{
//                Logger.e("成功:responseBytes:"+Utils.byteBufferToHexString(responseBytes));
//            }
//            //解析结果
//
//            byte result = 0;
//            int resultInt = 100;
//            //
//            int orderRunStatus = 100;
//            int currentFloorStatus = 100;
//            int orderRunResult = 100;
//            if (responseBytes != null) {
//                orderRunStatus = Utils.byteToInt(responseBytes[2]);
//                currentFloorStatus = Utils.byteToInt(responseBytes[3]);
//                orderRunResult = Utils.byteToInt(responseBytes[4]);
//            }
//            Logger.e("获取舱门推杆状态成功:orderRunStatus="+orderRunStatus+",currentFloorStatus="+currentFloorStatus+",orderRunResult="+orderRunResult);
//            resultList.add(orderRunStatus);
//            resultList.add(currentFloorStatus);
//            resultList.add(orderRunResult);
//
//            return resultList;
//        }
//
//
//        public static int TYPE_QUERY_PALLET_STATUS = 1;
//        public static int TYPE_OPERATE_PALLET = 1;
////    public static int TYPE_QUERY_PALLET_STATUS = 1;
////    public static int TYPE_QUERY_PALLET_STATUS = 1;
////    public static int TYPE_QUERY_PALLET_STATUS = 1;
////    public static int TYPE_QUERY_PALLET_STATUS = 1;
//
//        /**
//         * 出货开始
//         */
//        public void getGoods(List<GoodsModel> goodsList){
//            for(GoodsModel goodsModel :goodsList){
//                getGoods(goodsModel.getLine(),goodsModel.getRow());
//            }
//            //升降梯复位
//            //货盘是否有货
//            boolean isReset = false;
//            isReset = isResetLift();//yes
//            if(isReset){
//                Logger.e("货盘已经复位");
//            }else{
//                Logger.e("货盘没有复位");
//            }
//            boolean isHasGoods = queryIsHasGoods();
//            if(isHasGoods){
//                Logger.e("货盘有货");
//            }else{
//                Logger.e("货盘没货");
//            }
//            if(isReset || isHasGoods){
//                Logger.e("满足打开舱门的条件");
//                openDoor();
//                //检查舱门是否完成打开
//
//            }else{
//                Logger.e("不满足打开舱门的条件");
//            }
//            //有货打开舱门
//            //检查舱门是否完成打开
//
//            //检查是否取走货物 货盘是否有货
//            isHasGoods = queryIsHasGoods();
//            for(int repeatQueryHasGoods =0;repeatQueryHasGoods<20;repeatQueryHasGoods++){
//                isHasGoods = queryIsHasGoods();
//                if(!isHasGoods){
//                    //已取走货物
//                    Logger.e("已取走货物");
//                    break;
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if(isHasGoods){
//                //货物一直没有被取走
//                Logger.e("货物一直没有被取走");
//                return ;
//            }
//
//            int switchStatus = 100;
//            while(true){
//                switchStatus = queryIsProtectHand();//0的可以关闭
//                if(switchStatus ==0){
//                    break;
//                }
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(switchStatus == 0){
//                closeDoor();
//            }
//
//            // 没有货物时 检查防夹手开关是否打开若没有可以关闭舱门
//
//            //是否接触开关打开 打开：不能关闭  关闭：可以关闭窗门
//        }
//        public void runStatusMachine(int type){
//            while(true){
//                switch(type){
//                    case TYPE_QUERY_PALLET_STATUS:
//
//                        break;
//                    case TYPE_QUERY_PALLET_STATUS:
//
//                        break;
//                }
//            }
//
//
//        }
//
//        //2,30
//        public void getOneGood(int line,int row){
//            Logger.e("启动 getGoods");
//            //查询是否复位
//            boolean isReset = false;
//            isReset = isResetLift();//yes
//            boolean resetResult =true;
//            if(!isReset){
//                //没有复位
//                Logger.e("启动升降机 没有复位 ");
//                resetResult = resetLift();
//            }
//
//            if(!resetResult){
//                Logger.e("启动升降机 一直复位不成功");
//                //一直复位不成功
//                return;
//            }
//            //已经复位
//            //复位完成
//            //升降移动指定位置
//            int moveResult = 100;
//            for(int repeat =0;repeat<50;repeat++){
//                moveResult = liftMoveNew((byte)line);
//                if(moveResult == 0){
//                    Logger.e("启动getGoods 2");
//                    break;
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(moveResult == 0){
//
//            }else{
//                Logger.e("启动升降机 5次后失败");
//                return;
//            }
//            Logger.e("启动升降机 正常启动 升降机是否到达指定位置");
//            //升降机是否到达指定位置
//            for(int repeatQueryPosition =0;repeatQueryPosition<100;repeatQueryPosition++){
//                List<Integer> list = queryLiftStatus();
//                if(list != null&&list.size()==3){
//                    if(list.get(1) == line){
//                        Logger.e("启动升降机 移动到指定位置");
//                        break;
//                    }else{
//                        Logger.e("启动升降机 未移动到指定位置");
//                    }
//                }else{
//                    Logger.e("查询升降机状态通信异常");
//                }
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            //查询升降机当前位置
//            Logger.e("电机启动前准备检查");
//            //?
//
//            List<Integer> machineStatusList = queryMachineStatus();
//            if(machineStatusList!= null && machineStatusList.size()==3){
//                if((machineStatusList.get(0)==0||machineStatusList.get(0)==0)
//                        && machineStatusList.get(2)==0
//                ){
//                    Logger.e("电机启动前准备检查 是否read Yes");
//                }
//            }
//
//
//
//            startMachine(row);
//            Logger.e("电机启动后 查询执行旋转结果");
//            List<Integer> machineStartStatusList = queryMachineStatus();
//            if(machineStartStatusList!= null && machineStartStatusList.size()==3){
//                if((machineStartStatusList.get(0)==0||machineStartStatusList.get(0)==0)
//                        && machineStartStatusList.get(2)==0
//                ){
//                    Logger.e("电机启动后 正常执行旋转完毕");
//                }else{
//                    Logger.e("电机启动后 正常执行旋转异常");
//                }
//            }
//
//            //货盘复位 不停复位
//            resetResult = false;
//            for(int repeatLift = 0 ;repeatLift<20;repeatLift++){
//                resetResult = resetLiftCheckSuccess();
//                if(resetResult){
//                    //
//                    break;
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(resetResult){
//                Logger.e("获取货物后，货盘复位成功");
//            }else{
//                Logger.e("获取货物后，货盘复位不成功");
//            }
//
//            //已经复位
//            //没有复位
//            //复位 如果没有复位继续复位
//            //复位完成
//            //升降移动指定位置
//            //0 启动正常 1 无效索引 返回 2 正在运行 延时等待重新发送指令 3 启动失败 返回提示
//            //查询升降机执行状态
//            //升降机是否到达指定位置
//            //升降机已经到达指定位置
//            //升降机没有到达指定位置
//            //查询电机run执行状态
//            //0:空闲，1：执行中，2：执行完毕
//            //电机空闲
//            //启动电机
//            //启动电机结果0：已启动，1：无效索引，2：另一台电机在运行
//            //正常启动电机
//            //查询电机run执行状态
//            //0:空闲，1：执行中，2：执行完毕
//            //拣货完成 打开取货门
//            //打开取货门是否正常
//            //打开取货门正常
//            //打开取货门没正常 继续打开取货门
//            //获取货盘状态是否为空 延时查询直至获取结果
//            //获取是否有障碍 防夹手 延时查询直至获取结果
//            //货盘为空 无夹手时
//            //关闭取货门
//            //关闭取货门是否正常 不正常继续关闭
//        }
//
//
//
//    }
//
//
//}
