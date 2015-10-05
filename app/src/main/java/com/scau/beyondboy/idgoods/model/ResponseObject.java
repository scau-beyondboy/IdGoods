package com.scau.beyondboy.idgoods.model;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-03
 * Time: 00:42
 * 响应实体
 */
public class ResponseObject<T>
{
    private int result;
    private T data;

    public int getResult()
    {
        return result;
    }

    public void setResult(int result)
    {
        this.result = result;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return String.format("{\"result\":\"%s\",\"data\":\"%s\"}",result,data.toString());
    }
}
