package com.example.retailmachineclient.ui;


import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.zoloz.smile2pay.service.Zoloz;
import com.alipay.zoloz.smile2pay.service.ZolozCallback;
import com.example.retailmachineclient.R;
import com.example.retailmachineclient.alipay.api.AlipayCallBack;
import com.example.retailmachineclient.alipay.api.AlipayClient;
import com.example.retailmachineclient.alipay.api.AlipayResponse;
import com.example.retailmachineclient.alipay.api.DefaultAlipayClient;
import com.example.retailmachineclient.alipay.api.request.AlipayTradePayRequest;
import com.example.retailmachineclient.alipay.api.request.TradepayParam;
import com.example.retailmachineclient.alipay.api.request.ZolozAuthenticationCustomerSmilepayInitializeRequest;
import com.example.retailmachineclient.alipay.api.response.ZolozAuthenticationCustomerSmilepayInitializeResponse;
import com.example.retailmachineclient.base.BaseActivity;
import com.example.retailmachineclient.util.Logger;
import com.xuhao.didi.socket.common.interfaces.utils.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;

import static com.example.retailmachineclient.alipay.api.MerchantInfo.appId;
import static com.example.retailmachineclient.alipay.api.MerchantInfo.appKey;
import static com.example.retailmachineclient.alipay.api.MerchantInfo.mockInfo;

/**
 * Created by bruce on 2018/6/15.
 */
public class PayByFaceActivity extends BaseActivity {
    private static final String TAG = "smiletopay";
    @BindView(R.id.btn)
    Button mSmilePayButton;

    public static final String KEY_INIT_RESP_NAME = "zim.init.resp";
    private Zoloz zoloz;

    // 值为"1000"调用成功
    // 值为"1003"用户选择退出
    // 值为"1004"超时
    // 值为"1005"用户选用其他支付方式
    static final String CODE_SUCCESS = "1000";
    static final String CODE_EXIT = "1003";
    static final String CODE_TIMEOUT = "1004";
    static final String CODE_OTHER_PAY = "1005";

    static final String TXT_EXIT = "已退出刷脸支付";
    static final String TXT_TIMEOUT = "操作超时";
    static final String TXT_OTHER_PAY = "已退出刷脸支付";
    static final String TXT_OTHER = "抱歉未支付成功，请重新支付";

    //刷脸支付相关
    static final String SMILEPAY_CODE_SUCCESS = "10000";
    static final String SMILEPAY_SUBCODE_LIMIT = "ACQ.PRODUCT_AMOUNT_LIMIT_ERROR";
    static final String SMILEPAY_SUBCODE_BALANCE_NOT_ENOUGH = "ACQ.BUYER_BALANCE_NOT_ENOUGH";
    static final String SMILEPAY_SUBCODE_BANKCARD_BALANCE_NOT_ENOUGH = "ACQ.BUYER_BANKCARD_BALANCE_NOT_ENOUGH";

    static final String SMILEPAY_TXT_LIMIT = "刷脸支付超出限额，请选用其他支付方式";
    static final String SMILEPAY_TXT_EBALANCE_NOT_ENOUGH = "账户余额不足，支付失败";
    static final String SMILEPAY_TXT_BANKCARD_BALANCE_NOT_ENOUGH = "账户余额不足，支付失败";
    static final String SMILEPAY_TXT_FAIL = "抱歉未支付成功，请重新支付";
    static final String SMILEPAY_TXT_SUCCESS = "刷脸支付成功";

    /**
     * 发起刷脸支付请求，先zolozGetMetaInfo获取本地app信息，然后调用服务端获取刷脸付协议.
     */
    private void smilePay() {
        zoloz.zolozGetMetaInfo(mockInfo(), new ZolozCallback() {
            @Override
            public void response(Map smileToPayResponse) {
                if (smileToPayResponse == null) {
//                    Log.e(TAG, "response is null");
                    promptText(TXT_OTHER +"22");
                    return;
                }

                String code = (String)smileToPayResponse.get("code");
                String metaInfo = (String)smileToPayResponse.get("metainfo");

                //获取metainfo成功
                if (CODE_SUCCESS.equalsIgnoreCase(code) && metaInfo != null) {
//                    Log.i(TAG, "metanfo is:" + metaInfo);

                    //正式接入请上传metaInfo到服务端，不要忘记UrlEncode，使用服务端使用的sdk从服务端访问openapi获取zimId和zimInitClientData；
                    AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                            appId,
                            appKey,
                            "json",
                            "utf-8",
                            null,
                            "RSA2");
                    ZolozAuthenticationCustomerSmilepayInitializeRequest request
                            = new ZolozAuthenticationCustomerSmilepayInitializeRequest();
                    request.setBizContent(metaInfo);

                    //初始化
                    //起一个异步线程发起网络请求
                    alipayClient.execute(request,
                            new AlipayCallBack() {
                                @Override
                                public AlipayResponse onResponse(AlipayResponse response) {
                                    Logger.e(" response =="+response.toString());
                                    if (response != null && SMILEPAY_CODE_SUCCESS.equals(response.getCode())) {
                                        try {
                                            ZolozAuthenticationCustomerSmilepayInitializeResponse zolozResponse;
                                            zolozResponse = (ZolozAuthenticationCustomerSmilepayInitializeResponse)response;

                                            String result = zolozResponse.getResult();
                                            JSONObject resultJson = JSON.parseObject(result);
                                            String zimId = resultJson.getString("zimId");
                                            String zimInitClientData = resultJson.getString("zimInitClientData");
                                            //人脸调用
                                            smile(zimId, zimInitClientData);
                                        } catch (Exception e) {
                                            promptText(TXT_OTHER+"33");
                                        }
                                    } else {
                                        promptText(TXT_OTHER+"44");
                                    }
                                    return null;
                                }
                            });
                } else {
                    promptText(TXT_OTHER+"66");
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_by_face;
    }

    @Override
    protected void initView() {
        zoloz = Zoloz.getInstance(getApplicationContext());

        mSmilePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smilePay();
            }

        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zoloz.zolozUninstall();
    }

    /**
     * 发起刷脸支付请求.
     * @param zimId 刷脸付token，从服务端获取，不要mock传入
     * @param protocal 刷脸付协议，从服务端获取，不要mock传入
     */
    private void smile(String zimId, String protocal) {
        Map params = new HashMap();
        params.put(KEY_INIT_RESP_NAME, protocal);
        zoloz.zolozVerify(zimId, params, new ZolozCallback() {
            @Override
            public void response(final Map smileToPayResponse) {
                if (smileToPayResponse == null) {
                    promptText(TXT_OTHER +"77");
                    return;
                }

                String code = (String)smileToPayResponse.get("code");
                String fToken = (String)smileToPayResponse.get("ftoken");
                String subCode = (String)smileToPayResponse.get("subCode");
                String msg = (String)smileToPayResponse.get("msg");
//                Log.d(TAG, "ftoken is:" + fToken);

                //刷脸成功
                if (CODE_SUCCESS.equalsIgnoreCase(code) && fToken != null) {
                    //promptText("刷脸成功，返回ftoken为:" + fToken);
                    //这里在Main线程，网络等耗时请求请放在异步线程中
                    //后续这里可以发起支付请求
                    //https://docs.open.alipay.com/api_1/alipay.trade.pay
                    //需要修改两个参数
                    //scene固定为security_code
                    //auth_code为这里获取到的fToken值
                    //支付一分钱，支付需要在服务端发起，这里只是模拟
                    try {
                        pay(fToken, "0.01");
                    } catch (Exception e) {
                        promptText(SMILEPAY_TXT_FAIL);
                    }
                } else if (CODE_EXIT.equalsIgnoreCase(code)) {
                    promptText(TXT_EXIT);
                } else if (CODE_TIMEOUT.equalsIgnoreCase(code)) {
                    promptText(TXT_TIMEOUT);
                } else if (CODE_OTHER_PAY.equalsIgnoreCase(code)) {
                    promptText(TXT_OTHER_PAY);
                } else {
                    String txt = TXT_OTHER+"88";
                    if (!TextUtils.isEmpty(subCode)) {
                        txt = txt + "(" + subCode + ")";
                    }
                    promptText(txt);
                }
            }

        });
    }



    /**
     * 发起刷脸支付请求.
     * @param txt toast文案
     */
    void promptText(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 发起刷脸支付请求.
     * @param ftoken 刷脸返回的token
     * @param amount 支付金额
     */
    private void pay(String ftoken, String amount) throws Exception {
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                appId,
                appKey,
                "json",
                "utf-8",
                null,
                "RSA2");
        AlipayTradePayRequest alipayTradePayRequest = new AlipayTradePayRequest();
        TradepayParam tradepayParam = new TradepayParam();
        tradepayParam.setOut_trade_no(UUID.randomUUID().toString());

        //auth_code和scene填写需要注意
        tradepayParam.setAuth_code(ftoken);
        tradepayParam.setScene("security_code");
        tradepayParam.setSubject("smilepay");
        tradepayParam.setStore_id("smilepay test");
        tradepayParam.setTimeout_express("5m");
        tradepayParam.setTotal_amount(amount);
        alipayTradePayRequest.setBizContent(JSON.toJSONString(tradepayParam));
        alipayClient.execute(alipayTradePayRequest,
                new AlipayCallBack() {

                    @Override
                    public AlipayResponse onResponse(AlipayResponse response) {
                        if (response != null && SMILEPAY_CODE_SUCCESS.equals(response.getCode())) {
                            promptText(SMILEPAY_TXT_SUCCESS);
                        } else {
                            if (response != null) {
                                String subCode = response.getSubCode();
                                if (SMILEPAY_SUBCODE_LIMIT.equalsIgnoreCase(subCode)) {
                                    promptText(SMILEPAY_TXT_LIMIT);
                                } else if(SMILEPAY_SUBCODE_BALANCE_NOT_ENOUGH.equalsIgnoreCase(subCode)) {
                                    promptText(SMILEPAY_TXT_EBALANCE_NOT_ENOUGH);
                                } else if(SMILEPAY_SUBCODE_BANKCARD_BALANCE_NOT_ENOUGH.equalsIgnoreCase(subCode)) {
                                    promptText(SMILEPAY_TXT_BANKCARD_BALANCE_NOT_ENOUGH);
                                } else {
                                    promptText(SMILEPAY_TXT_FAIL);
                                }
                            } else {
                                promptText(SMILEPAY_TXT_FAIL);
                            }
                        }
                        return null;
                    }
                });
        return;
    }
}
