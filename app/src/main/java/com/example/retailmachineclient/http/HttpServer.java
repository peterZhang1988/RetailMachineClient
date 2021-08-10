package com.example.retailmachineclient.http;

import com.example.retailmachineclient.model.CreateOrderRspModel;
import com.example.retailmachineclient.model.GetGoodsInfoRspModel;
import com.example.retailmachineclient.model.LoginRspModel;
import com.example.retailmachineclient.model.QueryOrderRspModel;
import com.example.retailmachineclient.model.rsp.TradeSuccessRspModel;
import com.example.retailmachineclient.model.rsp.VersionRspModel;


import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import static com.example.retailmachineclient.http.Api.APP_LOGIN_DOMAIN_NAME;
import static com.example.retailmachineclient.http.Api.APP_UPDATE_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

/**
 * desc:请求接口
 * time:2020/7/1
 */

public interface HttpServer {

    //下载apk
//    @Headers({DOMAIN_NAME_HEADER+APP_UPDATE_DOMAIN_NAME})
//    @Streaming
//    @GET("/")
//    Observable<ResponseBody>downloadApk();
    //下载apk
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @Streaming
//    @GET("APK/20210731/1de05bb89bb4e4a0e7c4181b5a9d628b.apk")
        @GET
    Observable<ResponseBody>downloadApk(@Url String url);



    //与服务端网络登录 测试http协议
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/login")
    Observable<ResponseBody>loginTest(@Body RequestBody requestBody);

    //与服务端网络登录
//    @FormUrlEncoded
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/login")
    Observable<LoginRspModel>login(@Body RequestBody requestBody);


    //获取商品信息
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/goods_info")
//    @HTTP("HTTP/1.1")
    Observable<GetGoodsInfoRspModel>getGoodsInfo(@Body RequestBody requestBody);


    //创建订单
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/creat_order")
    Observable<CreateOrderRspModel>createOrder(@Body RequestBody requestBody);

    //创建订单
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/creat_order_plural_goods")
    Observable<CreateOrderRspModel>createOrderNew(@Body RequestBody requestBody);

    //查询订单状态
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/query_order")
    Observable<QueryOrderRspModel>queryOrder(@Body RequestBody requestBody);

    //支付成功确认
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/trade_success")
    Observable<TradeSuccessRspModel>tradeSuccessOrder(@Body RequestBody requestBody);


    //出货完成接口
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/close_order")
    Observable<TradeSuccessRspModel>closeOrder(@Body RequestBody requestBody);

    //错误上报接口
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/error_report")
    Observable<TradeSuccessRspModel>errorReportOrder(@Body RequestBody requestBody);

    //版本
    @Headers({DOMAIN_NAME_HEADER+APP_LOGIN_DOMAIN_NAME})
    @POST("robot/get_APK")
    Observable<VersionRspModel>getVersion(@Body RequestBody requestBody);

}
