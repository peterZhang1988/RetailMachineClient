package com.example.retailmachineclient.http;

/**
 * desc :配置默认url
 */
public interface Api {
     String APP_DEFAULT_DOMAIN="http://192.168.0.95:8081/";         //默认的URL
//     String APP_UPDATE_DOMAIN="http://129.204.18.252:8000/AndroidClient/";
     String APP_UPDATE_DOMAIN="http://101.37.124.215:88/AndroidClient/RetailMachineFile/";//升级app的下载url
     String SERVER_UPDATE_DOMAIN_NAME="serverUpdate";
     String APP_QUERY_DOMAIN_NAME="versionQuery";
     String APP_UPDATE_DOMAIN_NAME="appUpdate";
//     String APP_LOGIN_DOMAIN="http://192.168.1.91/";//ip
     String APP_LOGIN_DOMAIN_NAME="appLogin";//服务器名称

//     String APP_UPDATE_DOMAIN_NAME="appUpdate";
//     String APP_LOGIN_DOMAIN="http://47.114.1.99:88";//ip
//     String ImageHost = APP_LOGIN_DOMAIN;




}
