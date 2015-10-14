package com.scau.beyondboy.idgoods;

import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.scau.beyondboy.idgoods.utils.ShareUtils;

import java.util.LinkedList;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-14
 * Time: 19:24
 * 所有Activity的父类
 */
public class BaseActivity extends AppCompatActivity
{
    private static final String TAG = BaseActivity.class.getName();
    public static LinkedList<AppCompatActivity> sActivities=new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sActivities.add(this);
        Log.i(TAG,"添加Activity: "+this.toString() );
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(sActivities.size()==1)
        {
            ShareUtils.clearTempDate(this);
            sActivities.remove(this);
            Log.i(TAG, "清除临时文件");
        }
        Log.i(TAG,"删除Activity: "+this.toString());
        sActivities.remove(this);
    }
}
