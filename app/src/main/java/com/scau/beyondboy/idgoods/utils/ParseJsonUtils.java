package com.scau.beyondboy.idgoods.utils;

import com.google.gson.Gson;
import com.scau.beyondboy.idgoods.model.ResponseObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-25
 * Time: 00:36
 * 解析json工具类
 */
public class ParseJsonUtils
{
    private static final String TAG = ParseJsonUtils.class.getName();

    public static <T> void parseDataJson(ResponseObject<T> responseObject,String successMessage)
  {
      Gson gson=new Gson();
      String data=gson.toJson(responseObject.getData());
      if(responseObject.getResult()==1&&successMessage!=null)
      {
          ToaskUtils.displayToast(successMessage);
      }
      else if(responseObject.getResult()==1&&successMessage==null)
      {
          return;
      }
      else
      {
          ToaskUtils.displayToast(data);
      }
  }

    public static <T> T parseDataJson(ResponseObject<Object> responseObject,Class<T> classOfT)
    {
        Gson gson=new Gson();
        String data=gson.toJson(responseObject.getData());
        if(responseObject.getResult()==1)
        {
            return  gson.fromJson(data,classOfT);
        }
        else
        {
            ToaskUtils.displayToast(data);
            return null;
        }
    }

    /**返回list集合*/
    public static <T> List<T> paresListDataJson(ResponseObject<Object> responseObject,Type type)
    {
        Gson gson=new Gson();
        String data=gson.toJson(responseObject.getData());
        if(responseObject.getResult()==1)
        {
            return  gson.fromJson(data,type);
        }
        else
        {
            ToaskUtils.displayToast(data);
            return null;
        }
    }
}
