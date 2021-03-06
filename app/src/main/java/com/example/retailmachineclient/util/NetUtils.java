package com.example.retailmachineclient.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.retailmachineclient.model.CretaeOrderDataModel;
import com.alibaba.fastjson.JSON;
import com.example.retailmachineclient.base.BaseApplication;
import com.example.retailmachineclient.http.HttpManager;
import com.example.retailmachineclient.model.CreateOderReqModel;
import com.example.retailmachineclient.model.CreateOrderRspModel;
import com.example.retailmachineclient.model.req.VersionReqModel;

import com.example.retailmachineclient.model.GetGoodsInfoRspModel;
import com.example.retailmachineclient.model.GoodInfoModel;
import com.example.retailmachineclient.model.LoginRspModel;
import com.example.retailmachineclient.model.MessageEvent;
import com.example.retailmachineclient.model.QueryOrderRspModel;
import com.example.retailmachineclient.model.TxErrorModel;
import com.example.retailmachineclient.model.req.CloseOrderReqModel;
import com.example.retailmachineclient.model.req.ErrorReportReqModel;
import com.example.retailmachineclient.model.req.TradeSuccessReqModel;
import com.example.retailmachineclient.model.rsp.TradeSuccessRspModel;
import com.example.retailmachineclient.protocobuf.dispatcher.BaseMessageDispatcher;
import com.example.retailmachineclient.socket.TcpAiClient;
import com.example.retailmachineclient.socket.TcpClient;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import com.example.retailmachineclient.model.CreateOrderModel;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import org.litepal.LitePal;

//import static com.example.retailmachineclient.http.Api.APP_LOGIN_DOMAIN;
import static com.example.retailmachineclient.http.Api.APP_LOGIN_DOMAIN_NAME;
import static com.example.retailmachineclient.model.MessageEvent.EventType_CreateOrder_Fail;
import static com.example.retailmachineclient.model.MessageEvent.EventType_CreateOrder_Success;
import static com.example.retailmachineclient.model.MessageEvent.EventType_QueryOrder_Fail;
import static com.example.retailmachineclient.model.MessageEvent.EventType_QueryOrder_Success;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_CANNOT_CLOSE_CASE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_CLOSE_DOOR_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_GOOD_PUSH_MATCHINE_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_LIFT_EMPTY;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_LIFT_MATCHINE_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MOVE_TARGET_CHECK_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_MOVE_TARGET_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_DOOR_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_DOOR_SUCCESSFULLY;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_OPEN_RESET_TO_7_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_LIFT_POSITION_7_OVERTIME;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL_CLOSE;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_RESET_TO_7_FAIL;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_RESET_TO_7_OVERTIME;
import static com.example.retailmachineclient.util.ConstantUtils.TYPE_ERROR_START_MACHINE;

import com.example.retailmachineclient.model.rsp.VersionRspModel;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.StringBuffer;
import java.lang.StringBuffer;
import java.io.IOException;
import java.lang.InterruptedException;
import java.io.InterruptedIOException;//.InterruptedException;

//InputStream
//        BufferedReader
//        InputStreamReader
//        StringBuffer
public class NetUtils {
    public static NetUtils netUtils;

    public static NetUtils getInstance() {
        if (netUtils == null) {
            synchronized (NetUtils.class) {
                if (netUtils == null) {
                    Logger.d("?????? NetUtils:?????????");
                    netUtils = new NetUtils();
                }
            }
        }
        return netUtils;
    }

    /**
     * ???????????????
     *
     * @param salesIdValue
     * @param machineNameValue
     */
    public void loginBg(String salesIdValue, String machineNameValue) {
        //??????????????????
        RetrofitUrlManager.getInstance().putDomain(APP_LOGIN_DOMAIN_NAME, ConstantUtils.APP_LOGIN_DOMAIN + "");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String str = "{\"SalesID\":\"" + salesIdValue + "\",\"RobotName\":\"" + machineNameValue + "\"}";
        Logger.e("-------????????????  str = " + str);
        RequestBody body = RequestBody.create(JSON, str);
        HttpManager.getInstance().getHttpServer().login(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<LoginRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("----onSubscribe");
                    }

                    @Override
                    public void onNext(LoginRspModel responseBody) {
                        try {
                            if (responseBody != null) {
                                Logger.d("-------???????????? responseBody code = " + responseBody.getCode());
                                if (responseBody.getCode().equals("1")) {
                                    if (responseBody.getUserMsgModel() != null) {
                                        SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.ROBOT_Id, responseBody.getUserMsgModel().getRobotName());
                                        SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.SALES_Id, responseBody.getUserMsgModel().getSalesID());

                                        ConstantUtils.ALI_PAY_APP_Id = responseBody.getUserMsgModel().getAliPayAppID();
                                        ConstantUtils.ALI_PAY_PRIVATE_KEY_Id = responseBody.getUserMsgModel().getAliPayPrivateKey();
                                    }

                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_SUCCESS));
                                } else {
                                    Logger.d("-------???????????? ??????sp12");
                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_FAIL));
                                    SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.SALES_Id, "");
                                }
                            } else {
                                Logger.d("-------???????????? responseBody code = null");
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_FAIL_Error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_FAIL_Error));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:" + e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_FAIL_Error));
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete?????????????????????");
                    }
                });
    }


    public void loginBgAuto(String salesIdValue, String machineNameValue) {
        //??????????????????
        RetrofitUrlManager.getInstance().putDomain(APP_LOGIN_DOMAIN_NAME, ConstantUtils.APP_LOGIN_DOMAIN + "");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String str = "{\"SalesID\":\"" + salesIdValue + "\",\"RobotName\":\"" + machineNameValue + "\"}";
        Logger.e("-------????????????  str = " + str);
        RequestBody body = RequestBody.create(JSON, str);
        HttpManager.getInstance().getHttpServer().login(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<LoginRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("----onSubscribe");
                    }

                    @Override
                    public void onNext(LoginRspModel responseBody) {
                        try {
                            if (responseBody != null) {
                                Logger.d("-------???????????? responseBody code = " + responseBody.getCode());
                                if (responseBody.getCode().equals("1")) {
                                    if (responseBody.getUserMsgModel() != null) {
                                        SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.ROBOT_Id, responseBody.getUserMsgModel().getRobotName());
                                        SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.SALES_Id, responseBody.getUserMsgModel().getSalesID());

                                        ConstantUtils.ALI_PAY_APP_Id = responseBody.getUserMsgModel().getAliPayAppID();
                                        ConstantUtils.ALI_PAY_PRIVATE_KEY_Id = responseBody.getUserMsgModel().getAliPayPrivateKey();
                                    }

                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_SUCCESS_Auto));
                                } else {
                                    Logger.d("-------???????????? ??????sp1");
                                    SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.ROBOT_Id, "");
                                    SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.SALES_Id, "");
                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_FAIL_Auto));
                                }
                            } else {
                                Logger.d("-------???????????? responseBody code = null");
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_FAIL_Error_Auto));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_FAIL_Error_Auto));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:" + e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_LOGIN_FAIL_Error_Auto));
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete?????????????????????");
                    }
                });
    }

    /**
     * ??????????????????
     *
     * @param type          0 ?????? ???1 ????????? 2????????? 3??????
     * @param result
     * @param salesId
     * @param OutTradeNo
     * @param EndOutTradeNo
     */
    public void orderTradeSuccess(String type, String result, CreateOrderModel createOrderModel, String EndOutTradeNo) {
        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
        TradeSuccessReqModel tradeSuccessReqModel = new TradeSuccessReqModel();
        String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        tradeSuccessReqModel.setSalesID(salesId);

        if (createOrderModel != null) {
            tradeSuccessReqModel.setOutTradeNo(createOrderModel.getOutTradeNo());
        } else {
            //??????????????????
        }
        if (type.equals("1") || type.equals("0")) {
            //??????
            tradeSuccessReqModel.setEndOutTradeNo(tradeSuccessReqModel.getOutTradeNo());
        } else if (type.equals("2")) {
            //??????
            tradeSuccessReqModel.setEndOutTradeNo(EndOutTradeNo);
        } else if (type.equals("3")) {
            //??????
            tradeSuccessReqModel.setEndOutTradeNo(EndOutTradeNo);
        }
        tradeSuccessReqModel.setEndOrderResult(result);//"TRADE_SUCCESS"
        tradeSuccessReqModel.setEndStatus(type);
        String reqStr = JSON.toJSON(tradeSuccessReqModel).toString();
        reqStr = reqStr
                .replace("outTradeNo", "OutTradeNo")
                .replace("endOutTradeNo", "EndOutTradeNo")
                .replace("outTradeNo", "OutTradeNo")
                .replace("endStatus", "EndStatus")
                .replace("endOrderResult", "EndOrderResult")
                .replace("salesID", "SalesID");
        Logger.d("-------???????????????????????? reqStr = " + reqStr);
        RequestBody body = RequestBody.create(JSONType, reqStr);
        HttpManager.getInstance().getHttpServer().tradeSuccessOrder(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<TradeSuccessRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.d("----onSubscribe");
                    }

                    @Override
                    public void onNext(TradeSuccessRspModel responseBody) {
                        try {
                            if (responseBody != null) {
                                Logger.d("-------?????????????????? responseBody code = " + responseBody.getCode());
                            }
                            if (responseBody != null && responseBody.getCode().equals("1")) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_TradeSuccessOrder_Success));
                            } else {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_TradeSuccessOrder_Fail));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_TradeSuccessOrder_Fail));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:" + e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_TradeSuccessOrder_Fail));
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete?????????????????????");
                    }
                });
    }

    public void requestGoods(final boolean isDetail, Context mContext) {

        if (!checkOnlineState(mContext)) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_NET_ERROR));
            return;
        }
        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
        String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        String reqStr = "";
        if (isDetail) {
            reqStr = "{\"SalesID\":\"" + salesId + "\",\"GetDetails\":" + 1 + "}";
        } else {
            reqStr = "{\"SalesID\":\"" + salesId + "\",\"GetDetails\":" + 0 + "}";
        }
        Logger.d("-------?????? ????????????????????????   = " + reqStr);
        RequestBody body = RequestBody.create(JSONType, reqStr);
        HttpManager.getInstance().getHttpServer().getGoodsInfo(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<GetGoodsInfoRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.d("----onSubscribe");
                    }

                    @Override
                    public void onNext(GetGoodsInfoRspModel responseBody) {
                        try {
                            if (responseBody != null) {
                                Logger.d("-------?????? ?????????????????? responseBody code = " + responseBody.getCode());
                            }
                            if (responseBody.getCode().equals("1")) {
                                String phone = responseBody.getPhone();
                                SpUtil.getInstance(BaseApplication.getContext()).putString(SpUtil.PHONE, phone);
                                if (isDetail) {
                                    List<GoodInfoModel> requestDataList = new ArrayList<GoodInfoModel>();
                                    List<GoodInfoModel> requestDataListStart = new ArrayList<GoodInfoModel>();

                                    requestDataListStart = responseBody.getUserMsgModel();
                                    if (requestDataListStart != null) {
                                        for (GoodInfoModel goodInfo : requestDataListStart) {
                                            if (goodInfo.getGoodsInventory() != 0) {
                                                requestDataList.add(goodInfo);
                                            }
                                        }
                                        for (GoodInfoModel goodInfo : requestDataListStart) {
                                            if (goodInfo.getGoodsInventory() == 0) {
                                                requestDataList.add(goodInfo);
                                            }
                                        }
                                    }

                                    //????????????????????????
                                    LitePal.deleteAll(GoodInfoModel.class);
                                    LitePal.saveAll(requestDataList);
                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_GOODS_SUCCESS, requestDataList));
                                } else {
//                                    requestNumDataList = responseBody.getUserMsgModel();//??????
                                    //?????????????????????0???????????? ????????????
                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_GOODS_NUM_SUCCESS));
                                }
                            } else {
                                if (isDetail) {
                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_GOODS_FAIL));
                                } else {
                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_GOODS_NUM_FAIL));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (isDetail) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_GOODS_FAIL));
                            } else {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_GOODS_NUM_FAIL));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("-------onError:" + e.getMessage());
                        if (isDetail) {
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_GOODS_FAIL));
                        } else {
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_GOODS_NUM_FAIL));
                        }
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete?????????????????????");
                    }
                });
    }

    public void createOrderNew(float totalPrice, List<GoodInfoModel> shoppingCarDataList) {
        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
        CreateOderReqModel createOderReqModel = new CreateOderReqModel();
        String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        createOderReqModel.setSalesID(salesId);
        createOderReqModel.setPaymentAmount(totalPrice);
        String[] GoodsID = new String[shoppingCarDataList.size()];
        String[] ClassID = new String[shoppingCarDataList.size()];
        String[] ContainerNumList = new String[shoppingCarDataList.size()];
        for (int i = 0; i < shoppingCarDataList.size(); i++) {
            GoodsID[i] = shoppingCarDataList.get(i).getGoodsID();
            ClassID[i] = shoppingCarDataList.get(i).getClassID();
            ContainerNumList[i] = "" + shoppingCarDataList.get(i).getContainerNum();
        }
        createOderReqModel.setGoodsIDList(GoodsID);
        createOderReqModel.setClassIDList(ClassID);
        createOderReqModel.setContainerNumList(ContainerNumList);

        List<CretaeOrderDataModel> dataList = new ArrayList<CretaeOrderDataModel>();
        for (int i = 0; i < shoppingCarDataList.size(); i++) {
            CretaeOrderDataModel cretaeOrderDataModel = new CretaeOrderDataModel();
            cretaeOrderDataModel.setClassID(shoppingCarDataList.get(i).getClassID());
            cretaeOrderDataModel.setContainerNum("" + shoppingCarDataList.get(i).getContainerNum());
            cretaeOrderDataModel.setGoodsID(shoppingCarDataList.get(i).getGoodsID());
            cretaeOrderDataModel.setUnivalent(shoppingCarDataList.get(i).getPrice());
            dataList.add(cretaeOrderDataModel);
        }
        createOderReqModel.setData(dataList);

        String reqStr = JSON.toJSON(createOderReqModel).toString();
        //goodsID
        reqStr = reqStr
                .replace("containerNumList", "ContainerNumList")
                .replace("goodsIDList", "GoodsIDList")
                .replace("classIDList", "ClassIDList")
                .replace("paymentAmount", "PaymentAmount")

                .replace("data", "Data")
                .replace("classID", "ClassID")
                .replace("goodsID", "GoodsID")
                .replace("containerNum", "ContainerNum")
                .replace("univalent", "Univalent")
                .replace("salesID", "SalesID");
        Logger.e("-------???????????????????????? responseBody reqStr = " + reqStr + ",httrp==" + HttpManager.getInstance().getHttpServer().toString());
        RequestBody body = RequestBody.create(JSONType, reqStr);
        HttpManager.getInstance().getHttpServer().createOrderNew(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<CreateOrderRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("----onSubscribe");
                    }

                    @Override
                    public void onNext(CreateOrderRspModel responseBody) {
                        try {
                            if (responseBody != null && responseBody.getCode().equals("1")) {
                                Logger.e("-------???????????? responseBody code = " + responseBody.getCode());
//                                createOrderModel = responseBody.getData();
                                EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Success, responseBody.getData()));
                            } else {
                                Logger.e("-------???????????? responseBody error1");
                                EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Fail));
                            }
                        } catch (Exception e) {
                            Logger.e("-------???????????? responseBody error2");
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Fail));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:" + e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Fail));
                    }

                    @Override
                    public void onComplete() {
                        Logger.e("onComplete?????????????????????");
                    }
                });
    }

    public void createOrder(float totalPrice, List<GoodInfoModel> shoppingCarDataList) {
        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
        CreateOderReqModel createOderReqModel = new CreateOderReqModel();
        String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        createOderReqModel.setSalesID(salesId);
        createOderReqModel.setPaymentAmount(totalPrice);
        String[] GoodsID = new String[shoppingCarDataList.size()];
        String[] ClassID = new String[shoppingCarDataList.size()];
        String[] ContainerNumList = new String[shoppingCarDataList.size()];
        for (int i = 0; i < shoppingCarDataList.size(); i++) {
            GoodsID[i] = shoppingCarDataList.get(i).getGoodsID();
            ClassID[i] = shoppingCarDataList.get(i).getClassID();
            ContainerNumList[i] = "" + shoppingCarDataList.get(i).getContainerNum();
        }
        createOderReqModel.setGoodsIDList(GoodsID);
        createOderReqModel.setClassIDList(ClassID);
        createOderReqModel.setContainerNumList(ContainerNumList);
        String reqStr = JSON.toJSON(createOderReqModel).toString();
        reqStr = reqStr
                .replace("containerNumList", "ContainerNumList")
                .replace("goodsID", "GoodsIDList")
                .replace("classID", "ClassIDList")
                .replace("paymentAmount", "PaymentAmount")
                .replace("salesID", "SalesID");
        Logger.e("-------???????????????????????? responseBody reqStr = " + reqStr + ",httrp==" + HttpManager.getInstance().getHttpServer().toString());
        RequestBody body = RequestBody.create(JSONType, reqStr);
        HttpManager.getInstance().getHttpServer().createOrder(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<CreateOrderRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("----onSubscribe");
                    }

                    @Override
                    public void onNext(CreateOrderRspModel responseBody) {
                        try {
                            if (responseBody != null && responseBody.getCode().equals("1")) {
                                Logger.e("-------???????????? responseBody code = " + responseBody.getCode());
//                                createOrderModel = responseBody.getData();
                                EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Success, responseBody.getData()));
                            } else {
                                Logger.e("-------???????????? responseBody error1");
                                EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Fail));
                            }
                        } catch (Exception e) {
                            Logger.e("-------???????????? responseBody error2");
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Fail));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:" + e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Fail));
                    }

                    @Override
                    public void onComplete() {
                        Logger.e("onComplete?????????????????????");
                    }
                });
    }
//    public void createOrder(float totalPrice, List<GoodInfoModel> shoppingCarDataList) {
//        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
//        CreateOderReqModel createOderReqModel = new CreateOderReqModel();
//        String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
//        createOderReqModel.setSalesID(salesId);
//        createOderReqModel.setPaymentAmount(totalPrice);
//        String[] GoodsID = new String[shoppingCarDataList.size()];
//        String[] ClassID = new String[shoppingCarDataList.size()];
//        String[] ContainerNumList = new String[shoppingCarDataList.size()];
//        for (int i = 0; i < shoppingCarDataList.size(); i++) {
//            GoodsID[i] = shoppingCarDataList.get(i).getGoodsID();
//            ClassID[i] = shoppingCarDataList.get(i).getClassID();
//            ContainerNumList[i] = "" + shoppingCarDataList.get(i).getContainerNum();
//        }
//        createOderReqModel.setGoodsID(GoodsID);
//        createOderReqModel.setClassID(ClassID);
//        createOderReqModel.setContainerNumList(ContainerNumList);
//        String reqStr = JSON.toJSON(createOderReqModel).toString();
//        reqStr = reqStr
//                .replace("containerNumList", "ContainerNumList")
//                .replace("goodsID", "GoodsIDList")
//                .replace("classID", "ClassIDList")
//                .replace("paymentAmount", "PaymentAmount")
//                .replace("salesID", "SalesID");
//        Logger.e("-------???????????????????????? responseBody reqStr = " + reqStr + ",httrp==" + HttpManager.getInstance().getHttpServer().toString());
//        RequestBody body = RequestBody.create(JSONType, reqStr);
//        HttpManager.getInstance().getHttpServer().createOrder(body)
//                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
//                .subscribe(new Observer<CreateOrderRspModel>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Logger.e("----onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(CreateOrderRspModel responseBody) {
//                        try {
//                            if (responseBody != null && responseBody.getCode().equals("1")) {
//                                Logger.e("-------???????????? responseBody code = " + responseBody.getCode());
////                                createOrderModel = responseBody.getData();
//                                EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Success, responseBody.getData()));
//                            } else {
//                                Logger.e("-------???????????? responseBody error1");
//                                EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Fail));
//                            }
//                        } catch (Exception e) {
//                            Logger.e("-------???????????? responseBody error2");
//                            e.printStackTrace();
//                            EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Fail));
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Logger.e("-------onError:" + e.getMessage());
//                        EventBus.getDefault().post(new MessageEvent(EventType_CreateOrder_Fail));
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Logger.e("onComplete?????????????????????");
//                    }
//                });
//    }


    public void closeOrder(String type, CreateOrderModel createOrderModel) {
        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
        CloseOrderReqModel closeOrderReqModel = new CloseOrderReqModel();
        String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        closeOrderReqModel.setSalesID(salesId);
//        closeOrderReqModel.setOutTradeNo(createOrderModel.getOutTradeNo());
        if (createOrderModel != null) {
            closeOrderReqModel.setOutTradeNo(createOrderModel.getOutTradeNo());
        }
        if (type.equals("1")) {
            closeOrderReqModel.setEndRemarks("????????????");
            closeOrderReqModel.setEndType("1");
        } else if (type.equals("0")) {
            closeOrderReqModel.setEndRemarks("????????????");
            closeOrderReqModel.setEndType("0");
        } else if (type.equals("2")) {
            closeOrderReqModel.setEndRemarks("???????????? ????????????");
            closeOrderReqModel.setEndType("2");
        }
//        if (isSuccess) {
//            closeOrderReqModel.setEndRemarks("????????????");
//            closeOrderReqModel.setEndType("1");
//        } else {
//            closeOrderReqModel.setEndRemarks("????????????");
//            closeOrderReqModel.setEndType("0");
//        }

        String reqStr = JSON.toJSON(closeOrderReqModel).toString();//.toString(createOderReqModel);
        reqStr = reqStr
                .replace("outTradeNo", "OutTradeNo")
                .replace("endRemarks", "EndRemarks")
                .replace("endType", "EndType")
                .replace("salesID", "SalesID");
        Logger.e("-------?????????????????? ???????????? responseBody reqStr = " + reqStr);
        RequestBody body = RequestBody.create(JSONType, reqStr);
        HttpManager.getInstance().getHttpServer().closeOrder(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<TradeSuccessRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("----onSubscribe");
                    }

                    @Override
                    public void onNext(TradeSuccessRspModel responseBody) {
                        try {
                            Logger.e("-------?????????????????? responseBody code = " + responseBody.getCode());
                            if (responseBody.getCode().equals("1")) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_CloseOrder_Success));
                            } else {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_CloseOrder_Fail));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_CloseOrder_Fail));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:" + e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_CloseOrder_Fail));
                    }

                    @Override
                    public void onComplete() {
                        Logger.e("onComplete?????????????????????");
                    }
                });
    }


    public void queryOrder(CreateOrderModel createOrderModel) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        if (createOrderModel == null) {
            return;
        }
        String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        String reqStr = "{\"SalesID\":\"" + salesId + "\",\"OutTradeNo\":\"" + createOrderModel.getOutTradeNo() + "\"}";
        RequestBody body = RequestBody.create(JSON, reqStr);
//        Logger.e("-------???????????????????????? ???????????? = " + reqStr);
        HttpManager.getInstance().getHttpServer().queryOrder(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<QueryOrderRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("----onSubscribe");
                    }

                    @Override
                    public void onNext(QueryOrderRspModel responseBody) {
                        try {
//                            Logger.e("-------???????????????????????? responseBody code = " + responseBody.getCode());
//                            queryOrderModel = responseBody.getData();
                            EventBus.getDefault().post(new MessageEvent(EventType_QueryOrder_Success, responseBody.getData()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(EventType_QueryOrder_Fail));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:" + e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(EventType_QueryOrder_Fail));
                    }

                    @Override
                    public void onComplete() {
                        Logger.e("onComplete?????????????????????");
                    }
                });
    }

    public void errorReportOrder(TxErrorModel txErrorModel) {
        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
        ErrorReportReqModel errorReportReqModel = new ErrorReportReqModel();
        String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        errorReportReqModel.setSalesID(salesId);
        errorReportReqModel.setLogMsg(getErrorMsg(txErrorModel.getErrorCode()));
        errorReportReqModel.setErrorCode("" + txErrorModel.getErrorCode());

        String reqStr = JSON.toJSON(errorReportReqModel).toString();//.toString(createOderReqModel);
        reqStr = reqStr
                .replace("logMsg", "LogMsg")
                .replace("errorCode", "ErrorCode")
                .replace("salesID", "SalesID");
        Logger.e("-------?????????????????? ???????????? responseBody reqStr = " + reqStr);
        RequestBody body = RequestBody.create(JSONType, reqStr);
        HttpManager.getInstance().getHttpServer().errorReportOrder(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<TradeSuccessRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("----onSubscribe");
                    }

                    @Override
                    public void onNext(TradeSuccessRspModel responseBody) {
                        try {
                            Logger.e("-------?????????????????? responseBody code = " + responseBody.getCode());
                            if (responseBody.getCode().equals("1")) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_ErrorReportOrder_Success));
                            } else {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_ErrorReportOrder_Fail));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_ErrorReportOrder_Fail));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:" + e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_ErrorReportOrder_Fail));
                    }

                    @Override
                    public void onComplete() {
                        Logger.e("onComplete?????????????????????");
                    }
                });
    }

    public String getErrorMsg(int errorCode) {
        //????????????
        String data = "";
        switch (errorCode) {
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
            case TYPE_ERROR_LIFT_MATCHINE_FAIL:
                data = "?????????????????????";
                break;
            case TYPE_ERROR_GOOD_PUSH_MATCHINE_FAIL:
                data = "????????????????????????";
                break;
            case TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL:
                data = "?????????????????????????????????";
                break;
            case TYPE_ERROR_QUERY_PUSH_MATCHINE_FAIL_CLOSE:
                data = "?????????????????????????????????";
                break;

            default:
                data = "??????????????????";
                break;
        }
        return data;
    }

    public void getVersion(int version) {
        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
        VersionReqModel versionReqModel = new VersionReqModel();
        String salesId = SpUtil.getInstance(BaseApplication.getContext()).getString(SpUtil.SALES_Id);
        versionReqModel.setSalesID(salesId);
        versionReqModel.setVersion(version);

        String reqStr = JSON.toJSON(versionReqModel).toString();//
        reqStr = reqStr
                .replace("version", "VersionCode")
                .replace("salesID", "SalesID");
        Logger.e("-------?????????????????? ???????????? responseBody reqStr = " + reqStr);
        RequestBody body = RequestBody.create(JSONType, reqStr);
        HttpManager.getInstance().getHttpServer().getVersion(body)
                .subscribeOn(Schedulers.io())        //?????????????????????IO??????????????????
                .subscribe(new Observer<VersionRspModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("----onSubscribe");
                    }

                    @Override
                    public void onNext(VersionRspModel responseBody) {
                        try {
                            Logger.e("-------???????????? responseBody code = " + responseBody.getCode());
                            if (responseBody.getCode().equals("1")) {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GET_VERSIONH_SUCCESS, responseBody));
                            } else {
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GET_VERSIONH_FAIL));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GET_VERSIONH_ERROR));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e("-------onError:" + e.getMessage());
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_GET_VERSIONH_ERROR));
                    }

                    @Override
                    public void onComplete() {
                        Logger.e("onComplete?????????????????????");
                    }
                });
    }


    public boolean checkOnlineState(Context mContext) {
        //test
        ConnectivityManager CManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NInfo = CManager.getActiveNetworkInfo();
        try {
            if (NInfo != null && NInfo.isConnectedOrConnecting()) {
                if (ping("www.baidu.com")) {//InetAddress.getByName("www.163.com").isReachable(1000)
                    Logger.e("?????? reach");
                    // host reachable
                    return true;
                } else {
                    // host not reachable
                    Logger.e("?????? not reach");
                    return false;
                }
            } else {
                Logger.e("?????? not connect");
                return false;
            }
        } catch (Exception e) {
            Logger.e("?????? ????????????");
            e.printStackTrace();
        }
        return false;
    }


    public boolean ping(String urld) {

        boolean isCan = false;
        String result = null;
        try {
            String ip = urld;// ???????????????????????????????????????????????????(????????????????????????????????????????????????)
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 1 " + ip);// ping3???
            // PING?????????
            int status = p.waitFor();
            for (int i = 0; i < 5; i++) {
                p = Runtime.getRuntime().exec("ping -c 1 -w 1 " + ip);// ping3???
                status = p.waitFor();
                if (status == 0) {
                    result = "successful~";
                    isCan = true;
                    break;
                } else {
                    result = "fail~";
                    isCan = false;
                }
            }
        } catch (IOException e) {
            result = "failed~ IOException";
        } catch (InterruptedException e) {
            result = "failed~ InterruptedException";
        } finally {
            Logger.e("result = " + result);
        }
        return isCan;
    }


    public static final boolean isNodeReachable(String hostname) {
        try {
            return 0 == Runtime.getRuntime().exec("ping -c 1 " + hostname).waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
