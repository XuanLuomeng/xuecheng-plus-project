package com.xuecheng.orders;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description TODO
 * @date 2024/3/24 19:59
 */
public class Test {
    public static void main(String[] args) throws AlipayApiException {
        String APP_ID = "9021000135656624";
        String APP_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCHsnHVLK7OZDSykJwR8LvXlSnYAFpWDWChIawhwofwu4rBZi3KjG/XLbumHbUIxzMmWfV3Jyr9srfnUjJjTNCqrNPBxE/Mnur1FytHXrGHh8gMLrqJDjudwulq7e3uAMEUT5ruAq47peGjKvOQHmoYFafLl3duh+FvoKD5q6J2vepWF7Hd8+COpz2ccOAYlts6AL7eXN6ZJB9U9mGGTohiPh6aEK+FA+uILlt2qFtT36FSyUpte4FAywjtLS+3Pc9izvv6L8RDrGpUfRyp4muyuWodWtp4Jc/8Bo/J5NaDeY9cDg4BhEmEKxZD273kuTyw93IuEdlsKYU1EyNDWq+9AgMBAAECggEAXACesVwUQRcrq43kYz2G/knCDxNeLE3xoA4GePU92fgtwaaXkWbaRVhai+xEW+lDQtNnXBwQR+YuTgYq56yjJbT79bo2KJPu1bGZxE/tqehjt+7OxjICNZp7S+Z5lQ6p2i2G66yhmQuFCm4KqUIVyLKWC7FsZmJ2dIrB2cWG514onOcllpwqfimdB7mXtkCX1XAhSXyZ+LDWjNRJbveLEI0wf3y9xPaGfIVCjUusEOqlDn9hi7/Arz31cV9lugdvZDb5eWWnv682yXGBgSqXNuQXSBwRLUfvIH9z/9itqszQCwZKoRTI9xKFIV9tEMzTKtEW5pcDx6htvySSHpuwQQKBgQDZIbHMmpkzOeN9sx6LAyl2RmbM3E0BHBrJliQ31Q8ysrnxE1qND0TG6xJ3iC5OQx3c27z7sBBOKnQ1Elt/hBRB7y3BpBz4bAfWCsucojUr1vSNDfJhk2QA/mSWPoz++llmOBEw/JsbGJ+SrYrZzQakDM+YOD6jiCd5jTdwv/p2tQKBgQCf/OrmO39ET665sWX4ZVvMdbo9Q9t2iX/WvnhvHAXQK+QWOTDp0OauPCKi8LX7/EPzfoAw8nwfuS+WuwvQYtYqghmnSvWCrU2Lp1BM9Rz1vGKYzGs4AF7fSvs3HTptYUQMkLDoXHPGtHyXR7Rx1QQwURNRz04cDYjdWTDauHwx6QKBgGA3VyN0amjRwSYlZmZxW0EnB0zGXnUccB5eIuR+zEJKAq16Rwj0+CQxbLh9Jw/VG/mPgdoB9ee9VktiPBtwes2Q4DTLtW676GkH1ZwuIOOxWCLdSEfG2Wy7TfVp+G7Qnyb1t2B+v3itW6DSuBG1kbjGWIN6gP9USTHKywNyftl1AoGAatEGVGls80LwOXLDzjB+Neh0S+s+X8o13D6XUje3eeGUC3JrwfCD09i6l2d3WaJ4C95t4EBFtziBGXYQ0TTsIL10O0nE2Vaz64XuCzOr/jWk7res7lrw/MynJYkSNW03bdw9ASaY9hYTCT4Kr+W1Qj0fVQO/9uLiekbOY3nBEeECgYBwzZEjhfhSqDnKV3aRwYGDH5pgXR253hIcZedXVc4Lrlc8T+LYEJD2gNELtDlBZ8DMyam+7s5L2im+dydSrnD0cYkPNxKK1IKPmfathfhI++0vae6CDM3I/uknx6aX1RS4JzsytvokurRsiBjlqu5BIQO6jMcHKAyJ7mvBwOyv5A==";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkjllLP5NR9bEh5gjwxPZ39PIiE8vMEZ5+87d8XCyxKZ9zE16adAQJuqSGjP2lfUC7PjJuNeavP5y3+nWRgCIi01A/oFr/CigyR4YZVslItwKaLDWU9lAYdhd7sNxfZ/p36ZknQcotJoxMZ10LjYgxA7JFHPk0MkLAVcE48d/NE9idpj8PGvbbY62fZCQUD4VYkZRoxvm5z1M3WUoD6TXwf0lOIgwtkIFrDrqUyJPGL0uiqw44kjbwGOk6Cph/D4pYW1EOaz5DxHPBVk4tWemEIwtkSMALhfj4g3atPtcSHd45egPHdbXMxv1Rr0C7VS42sz+dUYzsw4dfhCrWJv7vwIDAQAB";
        String BUYER_ID = "2088722032440790";
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi-sandbox.dl.alipaydev.com/gateway.do",APP_ID,APP_PRIVATE_KEY,"json","GBK",ALIPAY_PUBLIC_KEY,"RSA2");
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        request.setNotifyUrl("");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "20210817010101003X02");
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", "测试商品");
        bizContent.put("buyer_id", BUYER_ID);
        bizContent.put("timeout_express", "10m");
        bizContent.put("product_code", "JSAPI_PAY");
        request.setBizContent(bizContent.toString());
        AlipayTradeCreateResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
    }
}
