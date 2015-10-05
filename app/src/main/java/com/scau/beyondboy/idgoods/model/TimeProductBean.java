package com.scau.beyondboy.idgoods.model;

import java.util.List;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-03
 * Time: 08:42
 * 存储当前时间的所有产品
 */
public class TimeProductBean
{
    public List<ProductBean> beanList;
    public String dateTime;

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public List<ProductBean> getBeanList()
    {
        return beanList;
    }

    public void setBeanList(List<ProductBean> beanList)
    {
        this.beanList = beanList;
    }

    @Override
    public String toString()
    {
        return String.format("{\"dateTime\":\"%s\",\"beanList\":\"%s\"}",dateTime,beanList);
    }
}
