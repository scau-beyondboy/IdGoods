package com.scau.beyondboy.idgoods.utils;

import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-27
 * Time: 23:12
 * 请求响应处理
 */
public final class NetWorkHandlerUtils
{
    /**post异步处理*/
    public static void postAsynHandler(String url, Map<String, String> params, final String successMessage)
    {
        if(!NetworkUtils.isNetworkReachable())
        {
            ToaskUtils.displayToast("没有网络");
            return;
        }
        OkHttpNetWorkUtil.postAsyn(url, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                e.printStackTrace();
                ToaskUtils.displayToast("网络异常");
            }

            @Override
            public void onResponse(ResponseObject<Object> response)
            {
               ParseJsonUtils.parseDataJson(response,successMessage);
            }
        }, params);
    }
}
