package com.scau.beyondboy.idgoods.utils;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-11-01
 * Time: 10:12
 */
public class TimeUtils
{
    /**转换时间为mm:ss格式*/
    public static String converTommss(int second)
    {
        String[] time=new String[2];
        time[0]= second / 60 < 10 ? "0" + second / 60 : second / 60 + "";
        time[1] = second  % 60 < 10 ? "0" + second  % 60 : second  % 60 + "";
        return String.format("%s:%s",time[0], time[1]);
    }
}
