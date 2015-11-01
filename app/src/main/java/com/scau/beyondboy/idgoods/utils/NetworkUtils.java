package com.scau.beyondboy.idgoods.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.scau.beyondboy.idgoods.MyApplication;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-10
 * Time: 19:39
 * 检查手机是否有网络
 */
public class NetworkUtils
{
    private static final String TAG = "NetworkUtils";
    /**
     * 检查手机是否有网络
     * 若有则返回true，没有返回false
     */
    public static boolean isNetworkReachable()
    {
        ConnectivityManager manager=(ConnectivityManager)MyApplication.getContext().getSystemService(MyApplication.getContext().CONNECTIVITY_SERVICE);
        NetworkInfo current=manager.getActiveNetworkInfo();
        if(current==null)
        {
            Log.i(TAG, "没有网络");
            return false;
        }
        if(current.getType()==ConnectivityManager.TYPE_WIFI)
        {
            Log.i(TAG,"有wifi网络");
        }
        else if(current.getType()==ConnectivityManager.TYPE_MOBILE)
        {
            Log.i(TAG,"有移动网络");
        }
        else
        {
            Log.i(TAG,"其他网络");
        }
        return true;
    }
}
