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
    private static final String TAG = NetWorkHandlerUtils.class.getName();

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

    public interface PostCallback<T>
    {
        void success(T result);
    }

    /**post异步处理*/
    public static<T> void postAsynHandler(String url, Map<String, String> params, final String successMessage,final String failMessage,final PostCallback postCallback, final Class<T> tClass)
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
                if(response.getResult()==1)
                {
                    T result=ParseJsonUtils.parseDataJson(response,tClass);
                    if(postCallback!=null)
                        postCallback.success(result);
                }
                else
                {
                    ToaskUtils.displayToast(failMessage);
                }
            }
        }, params);
    }

    /**文件下载处理*/
    public static void downloadFileHandler(final String url, final String destFileDir,final PostCallback postCallback)
    {
        if(!NetworkUtils.isNetworkReachable())
        {
            ToaskUtils.displayToast("没有网络");
            return;
        }
        OkHttpNetWorkUtil.downloadAsyn(url, destFileDir, new OkHttpNetWorkUtil.ResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                ToaskUtils.displayToast("下载音频文件出错");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String filePath)
            {
                if(postCallback!=null)
                    postCallback.success(filePath);
            }
        });
    }
}
