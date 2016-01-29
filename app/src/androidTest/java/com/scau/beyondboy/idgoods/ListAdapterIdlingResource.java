package com.scau.beyondboy.idgoods;

import android.support.test.espresso.IdlingResource;

import com.scau.beyondboy.idgoods.view.SlideListView;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-12-16
 * Time: 13:34
 * 实现ListView异步空闲处理类
 */
public class ListAdapterIdlingResource implements  IdlingResource
{
    private SlideListView mSlideListView;
    private IdlingResource.ResourceCallback mCallback;
    private final long startTime;
    private final long waitingTime;
    public  ListAdapterIdlingResource(long waitingTime,SlideListView slideListView)
    {
        this.startTime = System.currentTimeMillis();
        this.waitingTime = waitingTime;
        this.mSlideListView=slideListView;
    }

    @Override
    public String getName()
    {
        return "listadapterIdlingResource";
    }

    @Override
    public boolean isIdleNow()
    {
        //当网络数据加载完，才设置适配器，故可以通过适配器是否为空值来判断其异步数据加载是否完成
        if(mSlideListView.getAdapter()!=null)
        {
            mCallback.onTransitionToIdle();
            System.out.println("打印");
            return true;
        }
        return false;
        //通过时间来控制
        /*long elapsed = System.currentTimeMillis() - startTime;
        boolean idle = (elapsed >= waitingTime);
        if (idle) {
            System.out.println("打印");
            mCallback.onTransitionToIdle();
        }
        return idle;*/
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback)
    {
        this.mCallback=callback;
    }
}
