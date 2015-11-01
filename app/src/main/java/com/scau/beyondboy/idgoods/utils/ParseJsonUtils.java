package com.scau.beyondboy.idgoods.utils;

import com.google.gson.Gson;
import com.scau.beyondboy.idgoods.model.ResponseObject;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-25
 * Time: 00:36
 * 解析json工具类
 */
public class ParseJsonUtils
{
  public static <T> void parseDataJson(ResponseObject<T> responseObject,String successMessage)
  {
      Gson gson=new Gson();
      String data=gson.toJson(responseObject.getData());
      if(responseObject.getResult()==1)
      {
          ToaskUtils.displayToast(successMessage);
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
}
