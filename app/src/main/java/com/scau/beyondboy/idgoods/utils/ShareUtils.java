package com.scau.beyondboy.idgoods.utils;

import android.content.Context;

import com.scau.beyondboy.idgoods.model.UserBean;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-06
 * Time: 19:40
 * 轻量级存储类
 */
public class ShareUtils
{
    public static final String FILE_NAME = "tempuserinfo";
    public static final String USER_ID = "userId";
    public static final String PASSWORD = "password";
    public static final String INVITE_CODE_VALUE = "inviteCodeValue";
    public static final String ACCOUNT = "account";
    public static  void putUserId(Context context,String userId)
    {
        context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit().putString(USER_ID, userId).apply();
    }
    public static  void putAccount(Context context,String account)
    {
        context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit().putString(USER_ID, account).apply();
    }
    public static String getUserId(Context context)
    {
        return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getString(USER_ID,null);
    }
    public static  void putPassword(Context context,String password)
    {
        context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit().putString(PASSWORD,password).apply();
    }
    public static String getPassword(Context context)
    {
        return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getString(PASSWORD,null);
    }
    public static void putUserInfo(Context context,UserBean userBean,String password)
    {
        putUserId(context, userBean.getUserId());
        putPassword(context, userBean.getInviteCodeValue());
        putAccount(context, userBean.getAccount());
        putPassword(context,password);
    }
    public static String getAccount(Context context)
    {
        return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getString(ACCOUNT,null);
    }
    public static  void putInviteCodeValue(Context context,String inviteCodeValue)
    {
        context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit().putString(INVITE_CODE_VALUE,inviteCodeValue).apply();
    }
    public static String getInviteCodeValue(Context context)
    {
        return context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).getString(INVITE_CODE_VALUE,null);
    }
    public static void clearTempDate(Context context)
    {
        context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit().clear().apply();
    }
}
