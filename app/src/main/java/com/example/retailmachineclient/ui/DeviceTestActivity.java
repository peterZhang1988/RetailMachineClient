package com.example.retailmachineclient.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.mcuSdk.DataProtocol;
import com.example.retailmachineclient.mcuSdk.MCU;
import com.example.retailmachineclient.mcuSdk.SerialPortUtil;
import com.example.retailmachineclient.model.GoodInfoModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.model.TxErrorModel;
import com.example.retailmachineclient.order.OrderRequest;
import com.example.retailmachineclient.util.CRC;
import com.example.retailmachineclient.util.ConstantUtils;
import com.example.retailmachineclient.util.Logger;
import com.example.retailmachineclient.util.SerialPortManagerUtils;
import com.example.retailmachineclient.util.Utils;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_OPERATE_PROTECT_HAND;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_CLOSE_DOOR_STATUS;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_CLOSE_CASE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS_CLOSE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_DOOR_STATUS_OPEN;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_MACHINE_STATUS_IS_POSITION;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_PALLET_STATUS;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_QUERY_PALLET_STATUS_IS_POSITION;

public class DeviceTestActivity extends BaseActivity  implements View.OnClickListener{
    @BindView(R.id.etNumber0)
    EditText etNumber0;
    @BindView(R.id.etNumber1)
    EditText etNumber1;
    @BindView(R.id.etNumber2)
    EditText etNumber2;
    @BindView(R.id.etNumber3)
    EditText etNumber3;
    @BindView(R.id.etNumber4)
    EditText etNumber4;
    @BindView(R.id.etNumber5)
    EditText etNumber5;
    @BindView(R.id.etNumber6)
    EditText etNumber6;


    @BindView(R.id.number_lift)
    Spinner number_lift;
    @BindView(R.id.number_matchine)
    Spinner number_matchine;

    @BindView(R.id.cur_floor)
    Spinner cur_floor;
    @BindView(R.id.cur_matchine)
    Spinner cur_matchine;

    @BindView(R.id.tvResult)
    TextView tvResult;



    private SerialPortManager mSerialPortManager;
    private SerialPortUtil serialPortUtil;
    private OrderRequest orderRequestInstance;
    private String result = "";
    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_test;
    }

    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case ORDER_DATA_AND_RETURN:
                    //????????????????????????
                    String str = (String) msg.obj;
                    result = result + "\n" + str;
                    tvResult.setText(result);
//                    tvResult.setMovementMethod(ScrollingMovementMethod.getInstance());
                    break;
            }
            super.handleMessage(msg);

        }
    };

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
        getMyView();

    }


    public void getMyView(){
        etNumber0 = (EditText)findViewById(R.id.etNumber0);
        etNumber1 = (EditText)findViewById(R.id.etNumber1);
        etNumber2 =(EditText) findViewById(R.id.etNumber2);
        etNumber3 = (EditText)findViewById(R.id.etNumber3);
        etNumber4 = (EditText)findViewById(R.id.etNumber4);
        etNumber5 = findViewById(R.id.etNumber5);
        etNumber6 =(EditText) findViewById(R.id.etNumber6);

        number_lift = (Spinner)findViewById(R.id.number_lift);
        number_matchine = (Spinner)findViewById(R.id.number_matchine);
        cur_floor = (Spinner)findViewById(R.id.cur_floor);
        cur_matchine = (Spinner)findViewById(R.id.cur_matchine);
        tvResult =(TextView) findViewById(R.id.tvResult);

//        ArrayAdapter adapter = new ArrayAdapter<String>(DeviceTestActivity.this,
//                R.layout.activity_tipsprice_spinner, R.array.matchine_value);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        number_matchine.setAdapter(adapter);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.query_floor_numbers).setOnClickListener(this);
        findViewById(R.id.set_floor_numbers).setOnClickListener(this);
        findViewById(R.id.start_matchine).setOnClickListener(this);
        findViewById(R.id.test_all_matchine).setOnClickListener(this);
        findViewById(R.id.query_matchine).setOnClickListener(this);
        findViewById(R.id.tv_push_up).setOnClickListener(this);
        findViewById(R.id.tv_push_down).setOnClickListener(this);
        findViewById(R.id.query_door_status).setOnClickListener(this);
        findViewById(R.id.bt_query_pallet).setOnClickListener(this);
        findViewById(R.id.bt_query_protect).setOnClickListener(this);
        findViewById(R.id.query_all_status).setOnClickListener(this);
        findViewById(R.id.btClear).setOnClickListener(this);


        findViewById(R.id.start_lift).setOnClickListener(this);
        findViewById(R.id.query_lift_status).setOnClickListener(this);
        findViewById(R.id.get_goods).setOnClickListener(this);

    }

    @Override
    protected void initData() {

        mSerialPortManager = SerialPortManagerUtils.getInstance();
        orderRequestInstance = new OrderRequest(mHandler);

        mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File file) {
                Logger.e("?????????????????????" + file.getPath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result = result + "\n" + "??????????????????";
                        tvResult.setText(result);
                    }
                });
            }

            @Override
            public void onFail(File file, Status status) {
                Logger.e("?????????????????????" + file.getPath());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result = result + "\n" + "??????????????????";
                        tvResult.setText(result);
                    }
                });
            }
        });
        mSerialPortManager.openSerialPort(new File(MCU.PortMCU.MCU1.getPath()), MCU.PORT_RATE);

        OnSerialPortDataListener onSerialPortDataListener = new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
//                requestResult = null;
//                Logger.e(" ???????????? ?????????????????? onDataReceived");
                if (bytes == null) {
                    Logger.e("????????????  ?????????????????? ????????????bytes == null");
                } else if (bytes.length != 20) {
                    Logger.e("????????????  ?????????????????? ????????????????????????20 data =" + Utils.byteBufferToHexString(bytes));
                } else {
                    byte[] dataMachine = Utils.getBytes(bytes, 0, 18);
                    byte[] checks = CRC.crc16New(dataMachine);

                    byte[] checkMachine = Utils.getBytes(bytes, 18, 2);
                    boolean equalValue = byteEquals(checkMachine, checks);
                    if (!equalValue) {
                        Logger.e("????????????  ?????????????????? ????????????????????? ??????=" + checkMachine + ",??????=" + checks);
                    }
                    String result = "";
//                    isExit = true;
                    result = "\n ??????????????????" + Utils.byteBufferToHexString(bytes);
                    Logger.e("????????????  ??????????????????????????????:" + result);
                    mHandler.obtainMessage(ORDER_DATA_AND_RETURN, result).sendToTarget();
                }

            }

            @Override
            public void onDataSent(byte[] bytes) {
                String result = "";
                byte[] check = Utils.getBytes(bytes, 18, 2);
                result = result + "\n" + " ????????????:" + Utils.byteBufferToHexString(bytes) + "-----:" + Utils.byteBufferToHexString(check);
//                Logger.e("????????????  onDataSent" + result);
                mHandler.obtainMessage(ORDER_DATA_AND_RETURN, result).sendToTarget();
            }
        };

        SerialPortManager serialPortManager = SerialPortManagerUtils.getInstance();
        serialPortManager.setOnSerialPortDataListener(onSerialPortDataListener);
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

    int ip=-1;
//    @OnClick({R.id.back,R.id.query_floor_numbers,R.id.set_floor_numbers,R.id.start_matchine,R.id.test_all_matchine,R.id.query_matchine,
//            R.id.tv_push_up,R.id.tv_push_down,R.id.query_door_status,
//            R.id.bt_query_pallet,R.id.bt_query_protect,R.id.query_all_status,R.id.btClear,
//            R.id.start_lift,R.id.query_lift_status,R.id.get_goods
//    })
//    public void onViewClicked(View view) {
        @Override
        public void onClick(View view) {
        byte data;
        Runnable runnable;
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.query_floor_numbers:
                //??????
                queryLiftNum(true);
                break;
            case R.id.set_floor_numbers:
                //??????
                setLiftNum(true);
                break;


            case R.id.start_matchine:
                String str = number_matchine.getSelectedItem().toString();//.getText().toString();
                Logger.e("spinner=" + str);
                if (TextUtils.isEmpty(str)) {
                    Toast.makeText(BaseApplication.getContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                }
                try{
                    ip = Integer.valueOf(str).intValue();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    Toast.makeText(BaseApplication.getContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                }
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        byte[] nData = new byte[16];
                        nData[0] = (byte) ip;
                        nData[1] = (byte) 0x03;
                        for (int i = 2; i < nData.length; i++) {
                            nData[i] = 0x00;
                        }
                        OrderRequest orderRequest = new OrderRequest(mHandler);
                        orderRequest.requestCard(MCU.PortMCU.MCU5, (byte) 0x05, nData, ORDER_POWER_MACHINE);
                        try {
                            Thread.sleep(3000);//.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(runnable).start();
                break;
//

            case R.id.tv_push_up:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1, (byte) 0x14, (byte) 0x01));
                    }
                };
                new Thread(runnable).start();
                break;
            case R.id.tv_push_down:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1, (byte) 0x14, (byte) 0x00));
                    }
                };
                new Thread(runnable).start();
                break;

            case R.id.query_door_status:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1, (byte) 0x15, (byte) 0x00));
                    }
                };
                new Thread(runnable).start();
                break;

            case R.id.bt_query_pallet:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        queryIsHasGoodStatus();
                    }
                };
                new Thread(runnable).start();
                break;


            case R.id.bt_query_protect:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        queryIsProtectHand();
                    }
                };
                new Thread(runnable).start();
                break;

            case R.id.query_all_status:
                queryAllLiftStatus("test");
                break;


            case R.id.start_lift:
                String liftValue = number_lift.getSelectedItem().toString();
                Logger.e("spinner=" + liftValue);
                if(liftValue.equals("0")){
                    data = 0x00;
                }else if(liftValue.equals("1")){
                    data = 0x01;
                }else if(liftValue.equals("2")){
                    data = 0x02;
                }else if(liftValue.equals("3")){
                    data = 0x03;
                }else if(liftValue.equals("4")){
                    data = 0x04;
                }else if(liftValue.equals("5")){
                    data = 0x05;
                }else if(liftValue.equals("6")){
                    data = 0x06;
                }else if(liftValue.equals("?????????")){
                    data = 0x07;
                }else{
                    data = 0x07;
                }
                liftMove(data);
                break;
            case R.id.query_lift_status:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mSerialPortManager.sendBytes(DataProtocol.packSendData(MCU.PortMCU.MCU1, (byte) 0x13, (byte) 0x00));
                    }
                };
                new Thread(runnable).start();
                break;

            case R.id.query_matchine:
                queryMachineStatus();
                break;

            //???????????????
            case R.id.get_goods:
                int curMatchine= -1;
                int curFloor = -1;
                String strTest = cur_floor.getSelectedItem().toString();//.getText().toString();
                Logger.e("spinner=" + strTest);
                if (TextUtils.isEmpty(strTest)) {
                    Toast.makeText(BaseApplication.getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                    break;
                }
                try{
                    curFloor = Integer.valueOf(strTest).intValue();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    Toast.makeText(BaseApplication.getContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                }
                strTest = cur_matchine.getSelectedItem().toString();//.getText().toString();
                Logger.e("spinner=" + strTest);
                if (TextUtils.isEmpty(strTest)) {
                    Toast.makeText(BaseApplication.getContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                }
                try{
                    curMatchine = Integer.valueOf(strTest).intValue();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    Toast.makeText(BaseApplication.getContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(curMatchine<0||curFloor<0){
                    Toast.makeText(BaseApplication.getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                GoodInfoModel goodsModel = new GoodInfoModel();
                goodsModel.setContainerFloor(curFloor);
                goodsModel.setContainerNum(curMatchine);
                List<GoodInfoModel> goodsList = new ArrayList<GoodInfoModel>();
                goodsList.add(goodsModel);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        TxErrorModel txError = getGoods(goodsList);
                        MessageEvent event = new MessageEvent(4);
                        event.setType(4);
                        event.setTag(txError);
                        EventBus.getDefault().post(event);
                    }
                };
                new Thread(runnable).start();
                break;

            case R.id.test_all_matchine://??????????????????
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        for (int ip = 0; ip < 50; ip++) {
                            byte[] nData = new byte[16];
                            nData[0] = (byte) ip;
                            nData[1] = (byte) 0x03;
                            for (int i = 2; i < nData.length; i++) {
                                nData[i] = 0x00;
                            }
                            OrderRequest orderRequest = new OrderRequest(mHandler);
                            orderRequest.requestCard(MCU.PortMCU.MCU5, (byte) 0x05, nData, ORDER_POWER_MACHINE);
                            try {
                                Thread.sleep(3000);//.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                new Thread(runnable).start();
                break;

            case R.id.btClear:
                result = "";
                tvResult.setText("");
                break;
        }
    }


    public String getErrorMsg(int type) {
        String data = "";
        switch (type) {
            case TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME:
                data = "????????????????????????7?????? ??????";
                break;
            case TYPE_ERROR_RESET_TO_7_OVERTIME:
                data = "??????????????????7??? ??????";
                break;
            case TYPE_ERROR_RESET_TO_7_FAIL:
                data = "??????????????????7??? ?????????????????????";
                break;
            case TYPE_ERROR_MOVE_TARGET_FAIL:
                data = "???????????????????????????????????? ??????";
                break;
            case TYPE_ERROR_MOVE_TARGET_CHECK_FAIL:
                data = "???????????????????????????????????? ????????????????????? ????????????????????????";
                break;
            case TYPE_ERROR_START_MACHINE:
                data = "?????????????????? ????????????";
                break;
            case TYPE_ERROR_MACHINE_RUN:
                data = "????????????????????? ??????????????????";
                break;
            case TYPE_ERROR_OPEN_RESET_TO_7_FAIL:
                data = "????????? ??????????????? ?????????????????????";
                break;
            case TYPE_ERROR_LIFT_EMPTY:
                data = "???????????? ??????????????????????????????";
                break;
            case TYPE_ERROR_OPEN_DOOR_FAIL:
                data = "??????????????????????????????";
                break;
            case TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY:
                data = "??????????????????????????????";
                break;
            case TYPE_ERROR_CANNOT_CLOSE_CASE:
                data = "???????????????????????????";
                break;
            case TYPE_ERROR_CLOSE_DOOR_FAIL:
                data = "????????????????????????";
                break;
        }
        return data;
    }


    /**
     * ???????????????
     *
     * @param data ??????
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
                orderRequestInstance = new OrderRequest(mHandler);
                byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x12, nData, ORDER_POWER_MACHINE);
                if (responseBytes == null) {
                    Logger.e("??????:responseBytes?????????");
                } else {
                    Logger.e("??????:responseBytes:" + Utils.byteBufferToHexString(responseBytes));
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * ???????????????
     *
     * @param data ??????
     */
    public int liftMoveNew(byte data) {
        byte[] nData = new byte[16];
        nData[0] = (byte) data;
        for (int i = 1; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x12, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //?????????
            Logger.e("??????????????? ?????????");
        } else if (resultInt == 1) {
            //?????????
            Logger.e("??????????????? ????????????");
        } else if (resultInt == 2) {
            //?????????
            Logger.e("??????????????? ????????????");
        } else if (resultInt == 3) {
            //?????????
            Logger.e("??????????????? ????????????");
        } else {
            //?????????
            Logger.e("??????????????? ????????????");
        }
        return resultInt;
    }

    /**
     * ????????????
     *
     * @return
     */
    public int startMachine(int index) {
        byte[] nData = new byte[16];
        nData[0] = (byte) index;//???????????????
        nData[1] = (byte) 0x03;//????????????
        for (int i = 2; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU5, (byte) 0x05, nData, ORDER_POWER_MACHINE);
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
     * ??????????????????????????? 13
     *
     * @return
     */
    public List<Integer> queryLiftStatusTX() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x13, nData, ORDER_POWER_MACHINE);
        //????????????
        int orderRunStatus = 100;
        int currentFloorStatus = 100;
        int orderRunResult = 100;
        if (responseBytes != null) {
            orderRunStatus = Utils.byteToInt(responseBytes[2]);
            currentFloorStatus = Utils.byteToInt(responseBytes[3]);
            orderRunResult = Utils.byteToInt(responseBytes[4]);
        }
        Logger.e("???????????????????????????:orderRunStatus=" + orderRunStatus + ",currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * ??????????????????????????? 1d
     *
     * @return ??????:true,?????????false ???????????????false
     */
    public boolean queryIsHasGoods() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x1C, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //?????????
            return false;
        } else if (resultInt == 1) {
            //???????????????
            return true;
        }
        return false;
    }

    /**
     * ???????????????????????? 1c
     *
     * @return
     */
    public int queryIsHasGoodStatus() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x1C, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        if (resultInt == 0) {
            //?????????
            return resultInt;
        } else if (resultInt == 1) {
            //???????????????
            return resultInt;
        }
        return resultInt;
    }

    /**
     * ????????????run????????????
     *
     * @return
     */
    public List<Integer> queryMachineStatus() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
//        nData[0]=(byte)index;//???????????????
//        nData[1]=(byte)0x03;//????????????
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU5, (byte) 0x03, nData, ORDER_POWER_MACHINE);
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
        Logger.e("??????????????????????????????:orderRunStatus=" + orderRunStatus + ",currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * ?????? isOpen ??????????????? false:??????
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
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x14, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }

        Logger.e("????????????????????????:isOpen = " + isOpen + " ,orderRunStatus=" + resultInt);
        return resultInt;
    }


    /**
     * byte????????????int???????????????????????????(???????????????????????????)???????????????intToBytes2??????????????????
     */
    public static int bytesToInt2(byte[] src, int offset) {
//        byte high, byte low
        return (((0x000000ff & src[0]) << 8) & 0x0000ff00) | (0x000000ff & src[1]);
    }

    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[2];
//        src[0] = (byte) ((value>>24) & 0xFF);
//        src[1] = (byte) ((value>>16)& 0xFF);
        src[0] = (byte) ((value>>8)&0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }


    /**
     * ????????????????????????(Set Coded Value)?????? 10H?????????????????????
     * @param isOpen
     * @return
     */
    public byte[] setLiftNum(boolean isOpen) {
        byte[] nData = new byte[16];
        nData[0] = (byte) 0x00;

        byte[] value = null;
        value = intToBytes2(Integer.valueOf(etNumber0.getText().toString()));
        if(value != null && value.length==2){
            nData[1] =value[0];
            nData[2] =value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber1.getText().toString()));
        if(value != null && value.length==2){
            nData[3] =value[0];
            nData[4] =value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber2.getText().toString()));
        if(value != null && value.length==2){
            nData[5] =value[0];
            nData[6] =value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber3.getText().toString()));
        if(value != null && value.length==2){
            nData[7] =value[0];
            nData[8] =value[1];
        }
        value = intToBytes2(Integer.valueOf(etNumber4.getText().toString()));
        if(value != null && value.length==2){
            nData[9] =value[0];
            nData[10] =value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber5.getText().toString()));
        if(value != null && value.length==2){
            nData[11] =value[0];
            nData[12] =value[1];
        }

        value = intToBytes2(Integer.valueOf(etNumber6.getText().toString()));
        if(value != null && value.length==2){
            nData[13] =value[0];
            nData[14] =value[1];
        }

        for (int i = 15; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        Logger.e("??????????????????1:isOpen = " + isOpen + " ,responseBytes=" + Utils.byteBufferToHexString(nData));
        orderRequestInstance = new OrderRequest(mHandler);
//        byte[] responseBytes = null;
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x11, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;

        if (responseBytes != null) {
            Logger.e("??????????????????2:isOpen = " + isOpen + " ,responseBytes=" + Utils.byteBufferToHexString(responseBytes));
            //0
            resultInt =responseBytes[2];
        }


        return responseBytes;
    }


    /**
     * ????????????????????????(Set Coded Value)?????? 10H?????????????????????
     * @param isOpen
     * @return
     */
    public byte[] queryLiftNum(boolean isOpen) {
        byte[] nData = new byte[16];
        nData[0] = (byte) 0x00;
        for (int i = 1; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x10, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        int value = 0;
        if (responseBytes != null) {
            //0
            byte[] checkMachine = Utils.getBytes(responseBytes, 3, 2);
            value = bytesToInt2(checkMachine,0);
            etNumber0.setText(""+value);
            //1
            checkMachine = Utils.getBytes(responseBytes, 5, 2);
            value = bytesToInt2(checkMachine,0);
            etNumber1.setText(""+value);
            //2
            checkMachine = Utils.getBytes(responseBytes, 7, 2);
            value = bytesToInt2(checkMachine,0);
            etNumber2.setText(""+value);
            //3
            checkMachine = Utils.getBytes(responseBytes, 9, 2);
            value = bytesToInt2(checkMachine,0);
            etNumber3.setText(""+value);
            //4
            checkMachine = Utils.getBytes(responseBytes, 11, 2);
            value = bytesToInt2(checkMachine,0);
            etNumber4.setText(""+value);
            //5
            checkMachine = Utils.getBytes(responseBytes, 13, 2);
            value = bytesToInt2(checkMachine,0);
            etNumber5.setText(""+value);
            //6
            checkMachine = Utils.getBytes(responseBytes, 15, 2);
            value = bytesToInt2(checkMachine,0);
            etNumber6.setText(""+value);
            //7
//            checkMachine = Utils.getBytes(responseBytes, 17, 2);
//            value = bytesToInt2(checkMachine,0);
//            etNumber7.setText(""+value);
        }



//001000049A04860473044B042504250425009575
        if(responseBytes!=null)
            Logger.e("??????????????????:isOpen = " + isOpen + " ,responseBytes=" + Utils.byteBufferToHexString(responseBytes));
        return responseBytes;
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public List<Integer> queryDoorStatus() {
        List<Integer> resultList = new ArrayList<Integer>();
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x15, nData, ORDER_POWER_MACHINE);
        //????????????
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
        Logger.e("??????????????????????????????:orderRunStatus=" + orderRunStatus + " ,currentFloorStatus=" + currentFloorStatus + ",orderRunResult=" + orderRunResult);
        resultList.add(orderRunStatus);
        resultList.add(currentFloorStatus);
        resultList.add(orderRunResult);
        return resultList;
    }

    /**
     * ???????????????
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
                case TYPE_OPERATE_PROTECT_HAND:
                    //???????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(true, true, 1, 0, 0, 0);
                    }
                    txErrorModel = queryProtectHandTask();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_PALLET_STATUS://TYPE_QUERY_PALLET_STATUS
                    //????????????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryPalletStatusForISReset();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_DOOR_STATUS_OPEN:
                    //???????????????????????? ??????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask(true);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_DOOR_CLOSE_CASE:
                    //???????????????????????? ??????????????????????????????
                    if (beforeTxErrorModel.getStamp() != 0 && ((System.currentTimeMillis() - beforeTxErrorModel.getStamp()) > 5000)) {
                        return new TxErrorModel(true, true, 0);
                    }
                    beforeTxErrorModel = queryCloseDoorCase(beforeTxErrorModel);
                    break;

                case TYPE_QUERY_DOOR_STATUS_CLOSE:
                    //???????????????????????? ??????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryDoorStatusTask(false);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_QUERY_PALLET_STATUS_IS_POSITION://TYPE_QUERY_PALLET_STATUS
                    //?????????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME, 0, 0, 0);
                    }
                    txErrorModel = queryPalletStatusISPosition(position);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;
                case TYPE_QUERY_MACHINE_STATUS_IS_POSITION://TYPE_QUERY_PALLET_STATUS
                    //??????????????????
                    if ((beforeTime + overtime) < System.currentTimeMillis()) {
                        return new TxErrorModel(false, false, 1, 0, 0, 0);
                    }
                    txErrorModel = queryMachineStatusFor();
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        return txErrorModel;
                    }
                    break;

                case TYPE_OPERATE_PALLET:
                    //???????????????????????????
                    //???????????????????????? 3??????????????????????????????????????????
                    Logger.e("???????????????resetLiftTask TYPE_OPERATE_PALLET :" + startTime + "," + repeatTimes);
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, 3, 0, 0, 0);
                    }
                    txErrorModel = resetLiftTask(beforeTxErrorModel, position);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isSuccess()) {
                            Logger.e("???????????????resetLiftTask  ??????????????????");
                            //??????????????????
                            return txErrorModel;
                        } else {
                            if (txErrorModel.getZ1() == 0 || txErrorModel.getZ1() == 2) {
                                Logger.e("???????????????resetLiftTask  ??????startTime+1");
                                startTime = startTime + 1;
                            } else {
                                Logger.e("???????????????resetLiftTask  ?????? startTime");
                                startTime = 0;
                            }
                        }
                    } else {
                        Logger.e("???????????????resetLiftTask  not sucess");
                    }
                    break;

                case TYPE_OPERATE_PALLET_TO_7:
                    //???????????????????????? 3??????????????????????????????????????????
                    Logger.e("???????????????resetLiftTask TYPE_OPERATE_PALLET_TO_7 :" + startTime + "," + repeatTimes);
                    if (startTime > repeatTimes) {
                        return new TxErrorModel(false, false, TYPE_ERROR_RESET_TO_7_OVERTIME, 0, 0, 0);
                    }
                    txErrorModel = resetLiftTask(beforeTxErrorModel, 7);
                    if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
                        if (txErrorModel.isSuccess()) {
                            //??????????????????
                            return txErrorModel;
                        } else {
                            //????????????
                            if (txErrorModel.getZ1() == 0) {
                                startTime = startTime + 1;
                            } else {
                                startTime = 0;
                            }
                        }
                    }
                    break;

                case TYPE_OPEN_DOOR:
                    //???????????????????????? 10????????????????????????
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
                    //????????????????????????
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
                    //?????????????????? ?????????
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
     * ??????????????????????????????
     *
     * @param
     * @return
     */
    public TxErrorModel queryProtectHandTask() {
        long before = System.currentTimeMillis();
        int resultHand = queryIsProtectHand();
        if (resultHand != 0) {
            return new TxErrorModel(false, true, 21);
        }
        sleepWait(before, 100);
        return null;//????????????
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    public TxErrorModel queryCloseDoorTask() {
        long before = System.currentTimeMillis();

        int value = queryIsProtectHand();// 1:????????? 0?????????
        if (value == 1) {
            Logger.e("????????? ??????????????????????????? ?????????");
            return new TxErrorModel(false, true, 22);
        }

        List<Integer> list = queryDoorStatus();
        if (list.size() == 3) {
            if (list.get(0) == 0 || list.get(0) == 2) {
                if (list.get(1) == 0) {
                    //????????????????????????
                    Logger.e("????????? ?????????????????????????????? ?????????");
                    return new TxErrorModel(true, true, 21);
                }
            }
        }

        sleepWait(before, 50);
        return null;
    }

    /**
     * ??????????????????????????????
     *
     * @param beforeTx
     * @return
     */
    public TxErrorModel queryCloseDoorCase(TxErrorModel beforeTx) {
        long before = System.currentTimeMillis();
        //???????????? ????????? ??????00???
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
            //?????????
            beforeTx.setStamp(System.currentTimeMillis());
        }
        sleepWait(before, 100);
        return beforeTx;
    }

    /**
     * ????????????????????????????????????
     *
     * @param isOpen
     * @return
     */
    public TxErrorModel queryDoorStatusTask(boolean isOpen) {
        long before = System.currentTimeMillis();
        List<Integer> list = queryDoorStatus();
        if (list.size() == 3) {
            if (list.get(2) != 0) {
                //????????????
                return new TxErrorModel(false, false, 15);
            } else if (list.get(0) == 0 || list.get(0) == 2) {
                if (isOpen && list.get(1) == 1) {
                    //????????????????????????
                    return new TxErrorModel(false, true, 0);
                } else if (!isOpen && list.get(1) == 0) {
                    //????????????????????????
                    return new TxErrorModel(false, true, 0);
                } else {
                    sleepWait(before, 150);
                }
            } else {
                //????????????
                sleepWait(before, 150);
            }
        } else {
            sleepWait(before, 150);
        }
        return null;
    }

    /**
     * ???????????????????????? ????????????
     *
     * @param isOpen
     * @return
     */
    public TxErrorModel openDoorTask(boolean isOpen, long delayTime) {
        int result = openDoor(isOpen);
        if (result == 0) {
            //0 ????????????
            return new TxErrorModel(true, true, 0);
        } else {
            //3 ????????????
            //1 ????????????????????????????????????
            //2 ?????????????????????
            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * ??????????????? ?
     *
     * @return
     */
    public TxErrorModel resetLiftTask(TxErrorModel beforeTx, int line) {
        Logger.e("???????????????resetLiftTask");
        int value = liftMoveNew((byte) line);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //???????????? ??????????????????
        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(), 10000, 0, line);
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            int compare = 0;
            if (line < 5) {
                compare = 4 - line;
            } else {
                compare = line;
            }
            if (txErrorModel.getZ2() == compare) {
                //???????????????????????????
                Logger.e("???????????????resetLiftTask ????????????????????? curent:" + txErrorModel.getZ2() + ",want Position:" + line);
                return new TxErrorModel(true, true, 0);
            } else {
                //???????????????????????????
                Logger.e("???????????????resetLiftTask ??????????????????????????? curent:" + txErrorModel.getZ2() + ",want Position:" + line);
                return new TxErrorModel(false, true, 0, txErrorModel.getZ1(), txErrorModel.getZ2(), txErrorModel.getZ3(), 0);
            }
        } else {
            Logger.e("???????????????resetLiftTask ?????????????????????");
            return new TxErrorModel(false, false, 0);
        }
    }

    /**
     * ????????????
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
     * ???????????????????????? ???????????????????????? ?????????
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
                    //????????????????????????
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //???????????????
                    sleepWait(currentTimeMillis, 250);
                    isSleep = true;
                }
            }
        }
        return null;
    }

    /**
     * ???????????????????????? ?????????????????????????????????
     *
     * @return
     */
    public TxErrorModel queryMachineStatusFor() {
        long currentTimeMillis = System.currentTimeMillis();
        boolean isSleep = false;
        List<Integer> list = queryMachineStatus();
        if (list != null && list.size() > 0) {
            if (list.get(2) != 0) {
                //???????????? error
                sleepWait(currentTimeMillis, 150);
                isSleep = true;
            } else {
                if (list.get(0) == 0 || list.get(0) == 2) {
                    //????????????????????????
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //???????????????
                    sleepWait(currentTimeMillis, 150);
                    isSleep = true;
                }
            }
        }
        return null;
    }

    /**
     * ?????????????????????????????????
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
                //????????????????????????
                if (list.get(1) == position) {
                    return new TxErrorModel(true, true, 0, list.get(0), list.get(1), list.get(2));
                } else {
                    //
                    return new TxErrorModel(false, true, 0, list.get(0), list.get(1), list.get(2));
                }
            } else {
                //???????????????
                sleepWait(currentTimeMillis, 150);
                isSleep = true;
            }
        }
        return null;
    }

    /**
     * ????????????????????????????????????
     *
     * @return N
     */
    public int queryIsProtectHand() {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        byte[] responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x18, nData, ORDER_POWER_MACHINE);
        //????????????
        byte result = 0;
        int resultInt = 100;
        if (responseBytes != null) {
            result = responseBytes[2];
            resultInt = Utils.byteToInt(result);
        }
        Logger.e("???????????????????????? ??????:" + resultInt);
        return resultInt;
    }

    /**
     * ?????????????????????
     *
     * @param line
     * @param row
     * @return
     */
    public TxErrorModel getOneGood(int line, int row) {//??????????????? errorCode
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
            //????????????
            Logger.e("??????????????? ???????????? ");
            txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                int tempLine = (line + 2) % 5;
                liftMoveNew((byte) tempLine);
                txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
                if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                    Logger.e("??????????????? ?????????????????????");
                    //?????????????????????
                    txErrorModel.setSuccess(false);
                    txErrorModel.setErrorCode(TYPE_ERROR_RESET_TO_7_FAIL);
                    return txErrorModel;
                }
            }
        }
        //????????????????????????
        //???????????????????????? ????????????
        txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, 3, line);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("?????????????????????????????? ??????");
            //?????????????????????
            int tempLine = (line + 2) % 5;
            liftMoveNew((byte) tempLine);

            txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, 3, line);
            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_FAIL);
            }

        }
        Logger.e("??????????????? ???????????? ?????????????????????????????????");
        //?????????????????????????????????

        txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS, System.currentTimeMillis(), 10000, 0, line);
        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
            int compare = 0;
            if (txErrorModel.getZ2() < 5) {
                compare = 4 - txErrorModel.getZ2();
            } else {
                compare = txErrorModel.getZ2();
            }
            if (compare == line) {
                Logger.e("??????????????? ?????????????????????");
            } else {
                Logger.e("??????????????? ????????????????????????");
                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
            }
        } else {
            return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
        }
        //???????????????????????????
        Logger.e("???????????????????????????????????????????????????");
        //????????????
        int value = startMachine(row);
        if (value != 0) {
            //??????????????????
            return new TxErrorModel(false, false, TYPE_ERROR_START_MACHINE);
        }
        Logger.e("??????????????? ????????????????????????");
        txErrorModel = processByStatus(TYPE_QUERY_MACHINE_STATUS_IS_POSITION, System.currentTimeMillis(), 5000, 0, 0);
        if (!txErrorModel.isTaskSuccess()) {
            //??????
            Logger.e("??????????????? ??????????????????");
            return new TxErrorModel(false, false, TYPE_ERROR_MACHINE_RUN);
        }
        return new TxErrorModel(true, true, 0);
    }

    /**
     * ??????
     *
     * @param goodsList
     * @return
     */
    public TxErrorModel getGoods(List<GoodInfoModel> goodsList) {

        List<TxErrorModel> resultList = new ArrayList();
        for (GoodInfoModel goodsModel : goodsList) {
            TxErrorModel txErrorModel = getOneGood(goodsModel.getContainerFloor(), goodsModel.getContainerNum());
            if (!txErrorModel.isSuccess()) {
                return txErrorModel;
            }

//            MessageEvent event = new MessageEvent();
//            event.setType(5);
//            event.setTag(txErrorModel);
//            EventBus.getDefault().post(event);

            resultList.add(txErrorModel);
        }

        //??????????????? ??????????????????
        TxErrorModel txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("??????????????? ?????????????????????");
            //?????????????????????
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_RESET_TO_7_FAIL);
            return txErrorModel;
        }
        //??????????????????
        boolean isHasGoods = queryIsHasGoods();
        if (!isHasGoods) {
            Logger.e("???????????? ??????????????????????????????");
            return new TxErrorModel(false, false, TYPE_ERROR_LIFT_EMPTY);
        }
        return openDoorAfterGood();
    }

    public TxErrorModel openDoorAfterGood() {
        Logger.e("???????????? openDoorAfterGood");
        //????????????
        TxErrorModel txErrorModel = processByStatus(TYPE_OPEN_DOOR, 0, 0, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("??????????????????????????????");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_FAIL);
            return txErrorModel;
        }
        //?????????????????????????????? ??????2???
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        Logger.e("??????????????????????????????");
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_STATUS_OPEN, System.currentTimeMillis(), 30000, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("??????????????????????????????");
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY);
            return txErrorModel;
        }
        Logger.e("???????????????????????? ??????????????????");
//        //???????????????????????? ??????????????????
        txErrorModel = processByStatus(TYPE_QUERY_DOOR_CLOSE_CASE, 0, 0, 10, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            Logger.e("???????????????????????????");
            //?????????????????????
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CANNOT_CLOSE_CASE);
            return txErrorModel;
        }
        Logger.e("????????????????????????");
//        //??????
        txErrorModel = processByStatus(TYPE_CLOSE_DOOR, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
            //????????????????????????
            txErrorModel.setSuccess(false);
            txErrorModel.setErrorCode(TYPE_ERROR_CLOSE_DOOR_FAIL);
            return txErrorModel;
        }

        Logger.e("?????????????????????????????? ??? ?????????");
        txErrorModel = processByStatus(TYPE_QUERY_CLOSE_DOOR_STATUS, 0, 0, 5, 0);
        if (txErrorModel != null && !txErrorModel.isSuccess()) {
            Logger.e("??????????????? ?????? ??????????????? ?????????????????? ");
            //??????????????? ?????????????????????
            return openDoorAfterGood();
        } else {
            Logger.e("??????????????????");
            //??????????????????
            //???????????????3???
            txErrorModel = processByStatus(TYPE_OPERATE_PROTECT_HAND, System.currentTimeMillis(), 3000, 0, 0);
            if (txErrorModel != null && txErrorModel.isSuccess()) {
                //??????????????????
                txErrorModel.setSuccess(true);
                txErrorModel.setErrorCode(0);
                return txErrorModel;
            } else {
                //???????????????
                return openDoorAfterGood();
            }
        }
    }

    //?????????????????????????????????
    public int queryAllLiftStatus(String type) {
        byte[] nData = new byte[16];
        for (int i = 0; i < nData.length; i++) {
            nData[i] = 0x00;
        }
        orderRequestInstance = new OrderRequest(mHandler);
        //????????????
        byte result = 0;
        int resultInt = 100;
        int time = 1;
        byte[] responseBytes = null;
        while (time < ConstantUtils.SEND_ORDER_TIME_OUT_NUM) {
            responseBytes = orderRequestInstance.requestCard(MCU.PortMCU.MCU1, (byte) 0x1D, nData, ORDER_POWER_MACHINE);
            if (responseBytes != null) {
                result = responseBytes[2];
                resultInt = Utils.byteToInt(result);

                break;
            } else {
                time++;
            }
        }

        if(responseBytes!= null){
            Logger.d("????????????run ????????????????????????????????? ??????:type = " +type+",?????? data="+ Utils.byteBufferToHexString(responseBytes));
        }else{
            Logger.d("????????????run ????????????????????????????????? ??????:" );
        }

        return resultInt;
    }
}