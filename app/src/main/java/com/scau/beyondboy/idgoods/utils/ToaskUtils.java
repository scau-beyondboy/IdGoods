package com.scau.beyondboy.idgoods.utils;

import android.widget.Toast;

import com.scau.beyondboy.idgoods.MyApplication;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-25
 * Time: 00:23
 */
public class ToaskUtils
{
    public static void displayToast(String warnning)
    {
        Toast.makeText(MyApplication.getInstance(),warnning,Toast.LENGTH_SHORT).show();
    }
}
