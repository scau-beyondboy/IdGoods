package com.scau.beyondboy.idgoods.model;

import org.litepal.crud.DataSupport;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-21
 * Time: 09:43
 * 收藏列表
 */
@SuppressWarnings("ALL")
public class TimeCollectBean extends DataSupport
{
    Long currentDayStartTime;
    Long getCurrentDayEndTime;
    private String dateTime;
    private Set<CollectBean> beanList;

    public TimeCollectBean()
    {
        if(beanList==null)
        {
            beanList=new LinkedHashSet<>();
        }
    }
    public Long getCurrentDayStartTime()
    {
        return currentDayStartTime;
    }

    public void setCurrentDayStartTime(Long currentDayStartTime)
    {
        this.currentDayStartTime = currentDayStartTime;
    }

    public Long getGetCurrentDayEndTime()
    {
        return getCurrentDayEndTime;
    }

    public void setGetCurrentDayEndTime(Long getCurrentDayEndTime)
    {
        this.getCurrentDayEndTime = getCurrentDayEndTime;
    }

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public Set<CollectBean> getBeanList()
    {
        return beanList;
    }

    public void setBeanList(Set<CollectBean> beanList)
    {
        this.beanList = beanList;
    }
}
