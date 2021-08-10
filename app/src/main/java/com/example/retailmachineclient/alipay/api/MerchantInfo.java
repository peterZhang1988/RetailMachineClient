package com.example.retailmachineclient.alipay.api;

import com.example.retailmachineclient.util.ConstantUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bruce on 2018/6/15.
 */

public class MerchantInfo {
    //这里三个值请填写自己真实的值
    //应用的签名私钥
//    public final static String appKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCb4D5KD8mYAdUkvO/Jh+c2yLNXTn91O6SxXtAcLAE1NzNJnvGZpu54Fx+nP4T8mNTUfDzbrZPTzCjSzCAfyDugJ9cMvciKA2oecxNp4oOC7yhEqxqDyQebvtF6tPvS+7bJWhjTwFKioe5wRj92kHMGgg6Qoi2w4XMPnLItJyVRGQi0EKuldxcRNo2/ODei5RPqjdKnkyPDiJXU7QXAjzWMIsMJgEHLZ0x4jRypFO5C1J9Hv37W6lAJM1w56E2MxWC+AqBH2oQg371MpuypvNZxdAzQXpzT1xqn2ZsRjDaR1nze28u/p9E3KG9vxHjDfhko0Kpt0RSYsW+/hLJazhQRAgMBAAECggEAcrgp/8X4v1fx73TiIXdVErvJSeMq6TP5NNUr4t/8D5dNtsw00bnK3jVehW+5R1KPqMa6346zMniaPvIeXmW2hFtt6SGv+usiacCVtCMSWQX8o/UtsXn9kIFymJWklJXO5AfcJ9PuvWTiIF2DDZaoIU0MvSOu7vQuA3p2o8agrzrOu+IOzMZd7t7rEo134kEARRjl3uOErAQhXZ8J/MKkAB5SVL93kSSev7RocsDJd5NShfYf/X6NFxdKSbVPGtDr5ZbBqSZ6ysrG70c1lPDLjHkJK4x7qC3ERCzhv1m1HM7TAlBfeGz773CDdaz561jtwJmEV3doD7464dy1Y0oZ8QKBgQDeIGtb+SPtYxCWCyBO3nDwQwtEaFRsQjvV9DuT9YhPX+6t6+OKmAMnJJPUYK6YScn+XS0voQ+KX1Zi9z0C7LfTrKHCj3fmEMTsHmxVW+k/9NYX3IDTBvsCYrk+2fqDWbgB2jrRV5c/f7SsAsvFOg+Z+M80r/52pgfKxDsxNOW+XQKBgQCzpXg8WAd2ZmFRL8XXXuN9QvJSl1JxK/BwMPECkZck6iuWhkM5GE+chLT/6cqD2XqAQd9OfDmKfcgjsyN+NtB2Ysxf+UvKNbdrHERtPERfAAITTw1f9rqaB6B20T9VsnV4+q+LE3RP/ESMOzM5h6+ttfu2TuP7BUkPLiV8aeqJRQKBgHDqZAj+DlBncpADmend0+We/0RIENpzlP0/SWX+g8ttxeQtrJ7QhZHJW0iz2S1bHU6ryQOUSVUa/8wRSLeK5Cu7bwN7cQTH84LgOEvwDBNR/99jXS1pOAJPc0HLBzjsS8jNSecPMri5z3s9dJ6O1E1+GL781Es2PXHK7Rgnfr6VAoGAfAPM3wt+C1Rd9ifekfElZkGi+zUPaqUElM6UckuDZa5qbL0/BtkrjEdolAoXDbJCiOvwdkP3jQ2L7mkqJWU9v5wwrkhw86TlfAHFkws5v8NHq4C1IPw4kUCWm6+T41sREXUtXfsOHnFt+MidciejNW4d2BrZ814Qs3QN2Ldde2kCgYA43qWosvd1OOTFWEPOf4p0oV4ubFrLVDo1sU82GB+RAFWRPFJ6bwzOVQJ52zEyQW7TfDZnwdXYAPvh4lQrc0rQxKOwRZATtfNdX2WAVVnYKqv8aahHIRk8meRP7rmAHm5ukwqaw7AzqojXdSE0/8+WGn1B/Lyhj38260ncrP9atA==";
//    //商户id
//    public final static String partnerId = "2021032314113490";
//    //应用的appId
//    public final static String appId = "2019110769005215";

    //应用的签名私钥
    public  static String appKey = "";
    //商户id
    public  static String partnerId = "";
    //应用的appId
    public  static String appId = "";

    /**
     * mock数据，真实商户请填写真实信息.
     */
    public static Map mockInfo() {
        Map merchantInfo = new HashMap();
        //以下信息请根据真实情况填写
        //商户id
        appKey = ConstantUtils.ALI_PAY_PRIVATE_KEY_Id;
        partnerId = "2021032314113490";
        appId =ConstantUtils.ALI_PAY_APP_Id;

        merchantInfo.put("partnerId", partnerId);
        merchantInfo.put("merchantId", partnerId);
        //开放平台注册的appid
        merchantInfo.put("appId", appId);
        //机具编号，便于关联商家管理的机具
        merchantInfo.put("deviceNum", "202103231102888832");
        //真实店铺号
        merchantInfo.put("storeCode", "TEST");
        //口碑店铺号
        merchantInfo.put("alipayStoreCode", "TEST");

        return merchantInfo;
    }
}
