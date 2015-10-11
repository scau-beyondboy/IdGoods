package com.scau.beyondboy.idgoods.model;

import org.litepal.crud.DataSupport;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-03
 * Time: 08:42
 * 存储当前时间的所有产品
 * 一对多关联
 */
public class TimeProductBean extends DataSupport
{
    private Set<ProductBean> beanList;
    private String dateTime;

    public TimeProductBean()
    {
        if(beanList==null)
        {
            beanList=new LinkedHashSet<>();
        }
    }
    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public Set<ProductBean> getBeanList()
    {
        return beanList;
    }

    public void setBeanList(Set<ProductBean> beanList)
    {
        this.beanList = beanList;
    }

    @Override
    public String toString()
    {
        return String.format("{\"dateTime\":\"%s\",\"beanList\":\"%s\"}",dateTime,beanList);
    }

}
