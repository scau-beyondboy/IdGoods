package com.scau.beyondboy.idgoods;

import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-03
 * Time: 00:59
 */
@RunWith(JUnit4.class)
public class JavaTest
{
    @Test
    public void testPost() throws Exception
    {
       // System.out.println(OkHttpNetWorkUtil.postString(Consts.USER_LOGIN,new OkHttpNetWorkUtil.Param("account","13560418206"),new OkHttpNetWorkUtil.Param("password","chenwenhao"));
        System.out.println(OkHttpNetWorkUtil.postString(Consts.USER_LOGIN,new OkHttpNetWorkUtil.Param("account","13560418206"),new OkHttpNetWorkUtil.Param("password","chenwenhao")));
    }
}
