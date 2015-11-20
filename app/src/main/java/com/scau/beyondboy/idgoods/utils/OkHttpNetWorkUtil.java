package com.scau.beyondboy.idgoods.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntegerRes;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.jakewharton.disklrucache.DiskLruCache;
import com.scau.beyondboy.idgoods.MyApplication;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-02
 * Time: 12:40
 * 网络工具类
 * 说明：大部分代码来自https://github.com/hongyangAndroid/okhttp-utils，其使用文档也在其此处链接中
 */
@SuppressWarnings("ALL")
public class OkHttpNetWorkUtil
{
    private static final String TAG = OkHttpNetWorkUtil.class.getName();
    private static OkHttpNetWorkUtil mInstance;
    private static OkHttpClient mOkHttpClient;
    private static DiskLruCache sDiskLruCache;
    private Handler mDelivery;
    private Gson mGson;

    private OkHttpNetWorkUtil()
    {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(1000, TimeUnit.MILLISECONDS);
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    //多线程安全返回单例
    public static OkHttpNetWorkUtil getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpNetWorkUtil.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpNetWorkUtil();
                }
            }
        }
        return mInstance;
    }

    //*************对外公布的方法************

    /**
     * @see #_get(String)
     */
    public static Response get(String url) throws IOException
    {
        return getInstance()._get(url);
    }

    /**
     * @see #_getString(String)
     */
    public static String getString(String url) throws IOException
    {
        return getInstance()._getString(url);
    }

    /**
     * @see #_getAsyn(String, ResultCallback)
     */
    public static void getAsyn(String url, ResultCallback callback)
    {
        getInstance()._getAsyn(url, callback);
    }

    /**
     * @see #_post(String, Param...)
     */
    public static Response post(String url, Param... params) throws IOException
    {
        return getInstance()._post(url, params);
    }

    /**
     * @see #_postString(String, Param...)
     */
    public static String postString(String url, Param... params) throws IOException
    {
        return getInstance()._postString(url, params);
    }

    /**
     * @see #_postAsyn(String, ResultCallback, Param...)
     */
    public static void postAsyn(String url, final ResultCallback callback, Param... params)
    {
        getInstance()._postAsyn(url, callback, params);
    }

    /**
     * @see #_postAsyn(String, ResultCallback, Map)
     */
    public static void postAsyn(String url, final ResultCallback callback, Map<String, String> params)
    {
        getInstance()._postAsyn(url, callback, params);
    }

    /**
     * @see #_post(String, File[], String[], Param...)
     */
    public static Response post(String url, File[] files, String[] fileKeys, Param... params) throws IOException
    {
        return getInstance()._post(url, files, fileKeys, params);
    }

    /**
     * @see #_post(String, File, String)
     */
    public static Response post(String url, File file, String fileKey) throws IOException
    {
        return getInstance()._post(url, file, fileKey);
    }

    /**
     * @see #_post(String, File, String, Param...)
     */
    public static Response post(String url, File file, String fileKey, Param... params) throws IOException
    {
        return getInstance()._post(url, file, fileKey, params);
    }

    /**
     * @see #_postAsyn(String, ResultCallback, File[], String[], Param...)
     */
    public static void postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException
    {
        getInstance()._postAsyn(url, callback, files, fileKeys, params);
    }

    /**
     * @see #_postAsyn(String, ResultCallback, File, String)
     */
    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey) throws IOException
    {
        getInstance()._postAsyn(url, callback, file, fileKey);
    }

    /**
     * @see #_postAsyn(String, ResultCallback, File, String, Param...)
     */
    public static void postAsyn(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException
    {
        getInstance()._postAsyn(url, callback, file, fileKey, params);
    }

    /**
     * @see #_displayImage(ImageView, String, int)
     */
    public static void displayImage(final ImageView view, String url, int errorResId) throws IOException
    {
        getInstance()._displayImage(view, url, errorResId);
    }

    /**
     * @see #_displayImage(ImageView, String, int)
     */
    public static void displayImage(final ImageView view, String url)
    {
        getInstance()._displayImage(view, url, -1);
    }

    /**设置缓存*/
    public static void setCache(final  File destFileDir,long cacheSize)
    {
        try
        {
            sDiskLruCache=DiskLruCache.open(StorageUtils.getIndividualCacheDirectory(MyApplication.getInstance(), "postcard"), 1, 1, 100 * 1024 * 1024);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**取消缓存*/
    public static void cancelCache()
    {
        sDiskLruCache=null;
    }

    /**手动删除缓存文件*/
    public static void removeCacheFile(String fileName)
    {
        try
        {
            if(sDiskLruCache!=null)
                sDiskLruCache.remove(fileName);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * @see #_downloadAsyn(String, String, ResultCallback)
     */
    public static void downloadAsyn(String url, String destDir, ResultCallback callback)
    {
        getInstance()._downloadAsyn(url, destDir, callback);
    }

    /**
     * 同步的Get请求，返回响应实体
     * @param url 网络地址
     */
    private Response _get(String url) throws IOException
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response execute = call.execute();
        return execute;
    }

    /**
     * 同步的Get请求，返回响应字符内容
     * @param url 网络地址
     */
    private String _getString(String url) throws IOException
    {
        Response execute = _get(url);
        return execute.body().string();
    }

    /**
     * 异步的get请求
     * @param url 网络地址
     * @param callback 响应结果回调
     */
    private void _getAsyn(String url, final ResultCallback callback)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * 同步的Post请求，返回响应实体
     *
     * @param url 网络地址
     * @param params post的参数
     */
    private Response _post(String url, Param... params) throws IOException
    {
        Request request = buildPostRequest(url, params);
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }
    /**
     * 同步的Post请求，返回字符内容
     * @param url 网络地址
     * @param params post的参数
     */
    private String _postString(String url, Param... params) throws IOException
    {
        Response response = _post(url, params);
        return response.body().string();
    }
    /**
     * 异步的post请求
     *
     * @param url   网络地址
     * @param callback 响应结果回调
     * @param params 请求参数
     */
    private void _postAsyn(String url, final ResultCallback callback, Param... params)
    {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步的post请求
     * @param url   网络地址
     * @param callback 响应结果回调
     * @param params 请求参数
     */
    private void _postAsyn(String url, final ResultCallback callback, Map<String, String> params)
    {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request);
    }

    /**
     * 同步基于post的文件上传，返回响应体
     * @param  url 网络地址
     * @param files 文件数组
     * @param fileKeys 文件对应属性名
     * @param params 请求参数
     */
    private Response _post(String url, File[] files, String[] fileKeys, Param... params) throws IOException
    {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的文件上传，返回响应体
     * @param  url 网络地址
     * @param file 文件
     * @param fileKey 文件对应属性名
     */
    private Response _post(String url, File file, String fileKey) throws IOException
    {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步基于post的文件上传，返回响应体
     * @param  url 网络地址
     * @param file 文件
     * @param fileKey 文件对应属性名
     * @param params 请求参数
     */
    private Response _post(String url, File file, String fileKey, Param... params) throws IOException
    {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步文件上传
     * @param url 网络地址
     * @param callback 回调接口
     * @param files 文件数组
     * @param fileKeys 文件对应属性名
     * @param params 请求参数
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException
    {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步文件上传
     * @param url 网络地址
     * @param callback 回调接口
     * @param file 文件
     * @param fileKey 文件对应属性名
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey) throws IOException
    {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步文件上传
     * @param url 网络地址
     * @param callback 回调接口
     * @param file 文件
     * @param fileKey 文件对应属性名
     * @param params 请求参数
     * @throws IOException
     */
    private void _postAsyn(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException
    {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }
    /**
     * 异步下载文件,并缓存到磁盘当中
     * @param url 网络地址
     * @param destFileDir 本地文件存储的文件夹
     * @param callback 响应结果回调
     */
    private void _downloadAsyn(final String url, final String destFileDir, final ResultCallback callback)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(final Request request, final IOException e)
            {
                sendFailedStringCallback(request, e, callback);
            }
            @Override
            public void onResponse(Response response)
            {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try
                {
                    is = response.body().byteStream();
                    //缓存中文件中
                    DiskLruCache.Editor editor = sDiskLruCache.edit(Util.md5Hex(url));
                   // new FileOutputStream(new File(destFileDir + "/" + Util.md5Hex(url)+".0")
                    if (downloadUrlToStream(is,editor.newOutputStream(0),buf))
                    {
                        editor.commit();
                        //如果下载文件成功，第一个参数为文件的绝对路径
                        sendSuccessResultCallback(destFileDir + "/" + Util.md5Hex(url)+".0", callback);
                    } else
                    {
                        editor.abort();
                    }
                } catch (IOException e)
                {
                    sendFailedStringCallback(response.request(), e, callback);
                } finally
                {
                    try
                    {
                        if (is != null) is.close();
                    } catch (IOException e)
                    {
                    }
                    try
                    {
                        if (fos != null) fos.close();
                    } catch (IOException e)
                    {
                    }
                }

            }
        });
    }

    /**
     * 加载图片，并显示图片
     * @param view ImageView 组件
     * @param url 图片地址
     * @param errorResId 错误图片资源Id
     */
    private void _displayImage(final ImageView view, final String url, @IntegerRes final int errorResId)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                setErrorResId(view, errorResId);
            }
            @Override
            public void onResponse(Response response)
            {
                InputStream is = null;
                try
                {
                    is = response.body().byteStream();
                    ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
                    ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(view);
                    //计算图片压缩比例
                    int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
                    try
                    {
                        is.reset();
                    } catch (IOException e)
                    {
                        response = _get(url);
                        is = response.body().byteStream();
                    }
                    BitmapFactory.Options ops = new BitmapFactory.Options();
                    ops.inJustDecodeBounds = false;
                    ops.inSampleSize = inSampleSize;
                    final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
                    mDelivery.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            view.setImageBitmap(bm);
                        }
                    });
                } catch (Exception e)
                {
                    setErrorResId(view, errorResId);

                } finally
                {
                    if (is != null) try
                    {
                        is.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**设置错误图片Id*/
 private void setErrorResId(final ImageView view, final int errorResId)
    {
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                view.setImageResource(errorResId);
            }
        });
    }
    /**
     * 获取从网络拉取的文件名
     * @param path 网络地址
     */
    private static String getFileName(String path)
    {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**添加文件请求参数*/
    private Request buildMultipartFormRequest(String url, File[] files,String[] fileKeys, Param[] params)
    {
        params = validateParam(params);
        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);
        for (Param param : params)
        {
            //添加用户信息
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null)
        {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++)
            {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                                "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    /**添加请求参数*/
    private Request buildPostRequest(String url, Param[] params)
    {
        if (params == null)
        {
            params = new Param[0];
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params)
        {
            //Log.i(TAG,"空指针吗：  "+param);
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }
    /**处理异步请求的结果*/
    private void deliveryResult(final ResultCallback callback, Request request)
    {
        mOkHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(final Request request, final IOException e)
            {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(final Response response)
            {
                try
                {
                    final String string = response.body().string();
                    Log.i(TAG,"响应内容：  "+string );
                    if (callback.mType == String.class)
                    {
                        sendSuccessResultCallback(string, callback);
                    } else
                    {
                        Object o = mGson.fromJson(string, callback.mType);
                        sendSuccessResultCallback(o, callback);
                    }


                } catch (IOException e)
                {
                    sendFailedStringCallback(response.request(), e, callback);
                } catch (com.google.gson.JsonParseException e)//Json解析的错误
                {
                    sendFailedStringCallback(response.request(), e, callback);
                }

            }
        });
    }

    /**响应回调处理类*/
    public static abstract class ResultCallback<T>
    {
        Type mType;

        public ResultCallback()
        {
            mType = getSuperclassTypeParameter(getClass());
        }

        /**
         * 获取泛型参数类型
         * @param subclass 该类对象
         */
        static Type getSuperclassTypeParameter(Class<?> subclass)
        {
            //获取泛型父类
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class)
            {
                throw new RuntimeException("Missing type parameter.");
            }
            //获取泛型参数类型
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }
        //响应失败的方法
        public abstract void onError(Request request, Exception e);
        //响应成功的回调方法
        public abstract void onResponse(T response);
    }

    /**响应失败的回调方法*/
    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback)
    {
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    /**响应成功的回调方法*/
    private void sendSuccessResultCallback(final Object object, final ResultCallback callback)
    {
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onResponse(object);
                }
            }
        });
    }

    /**
     * 请求参数实体
     */
    public static class Param
    {
        public Param()
        {
        }

        public Param(String key, String value)
        {
            this.key = key;
            this.value = value;
        }
        String key;
        String value;

        @Override
        public String toString()
        {
            return String.format("{\"key\":\"%s\",\"value\":\"%s\"}",key, value);
        }
    }

    /**检验请求参数的有效性*/
    private Param[] validateParam(Param[] params)
    {
        if (params == null)
            return new Param[0];
        else return params;
    }

    /**获取其文件类型*/
    private String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**转换map集合为param数组对象*/
    private Param[] map2Params(Map<String, String> params)
    {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries)
        {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    /**保存流到缓存文件中*/
    private static boolean downloadUrlToStream(InputStream inputStream,OutputStream outputStream,byte[] buffer)
    {
        try
        {
            int readByte=0;
            while ((readByte=inputStream.read(buffer))>=0)
            {
                outputStream.write(buffer,0,readByte);
            }
            outputStream.flush();
            return true;
        } catch (IOException e)
        {
            e.printStackTrace();
            return  false;
        }
        finally
        {
            try
            {
                if(inputStream!=null)
                    inputStream.close();
                if(outputStream!=null)
                    outputStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
