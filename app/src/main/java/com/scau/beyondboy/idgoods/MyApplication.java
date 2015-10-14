package com.scau.beyondboy.idgoods;

import com.scau.beyondboy.idgoods.manager.ThreadManager;

import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-06
 * Time: 16:19
 */
public class MyApplication extends LitePalApplication
{
    private static final String TAG = MyApplication.class.getName();
    private static MyApplication sMyApplication;
    public static ThreadManager sThreadManager=new ThreadManager();
    //多线程安全返回单例
    public static MyApplication getInstance()
    {
        if (sMyApplication == null)
        {
            synchronized (MyApplication.class)
            {
                if (sMyApplication == null)
                {
                    sMyApplication = new MyApplication();
                }
            }
        }
        return sMyApplication;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sMyApplication=this;
        Connector.getDatabase();
    }
}
