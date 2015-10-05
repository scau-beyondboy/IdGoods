package com.scau.beyondboy.idgoods.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

import com.scau.beyondboy.idgoods.utils.DisplayUtil;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-02
 * Time: 20:39
 * 自定义ListView左侧滑删除
 */
public class SlideListView extends ListView
{
    private static final String TAG = SlideListView.class.getName();
    /**
     * 当前滑动的ListView　position
     */
    private int slidePosition;
    /**
     * 手指按下X的坐标
     */
    private int downY;
    /**删除按钮是否正在显示*/
    private boolean isDeleteShown=false;

    /**

     * 手指按下Y的坐标
     */
    private int downX;
    /**
     * ListView的item
     */
    private View itemView;
    private int mDeleteBnWidth= DisplayUtil.dip2px(getContext(),100);
    /**
     * 滑动类
     */
    private Scroller scroller;
    private static final int SNAP_VELOCITY =1500;
     /**速度追踪对象*/
    private VelocityTracker velocityTracker;
    /**
     * 是否响应滑动，默认为不响应
     */
    private boolean isSlide = false;
    /**
     * 认为是用户滑动的最小距离
     */
    private int mTouchSlop;

    /**滑动距离*/
    private int mDeltaX=0;

    public SlideListView(Context context)
    {
        this(context, null);
    }

    public SlideListView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SlideListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 分发事件，主要做的是判断点击的是那个item, 以及通过postDelayed来设置响应左右滑动事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        Log.i(TAG, "dispatchTouch");
        addVelocityTracker(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                // 假如scroller滚动还没有结束或删除按钮显示，我们直接返回
                if (!scroller.isFinished()||isDeleteShown)
                {
                    return super.dispatchTouchEvent(event);
                }
                downX = (int) event.getX();
                downY = (int) event.getY();
                slidePosition = pointToPosition(downX, downY);
                // 无效的position, 不做任何处理
                if (slidePosition == AdapterView.INVALID_POSITION)
                {
                    return super.dispatchTouchEvent(event);
                }
                // 获取我们点击的item view
                itemView = getChildAt(slidePosition - getFirstVisiblePosition());
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                Log.i(TAG,"dispatch:move");
                if (Math.abs(downX-event.getX()) > mTouchSlop && Math.abs(event.getY() - downY) < mTouchSlop)
                {
                    isSlide = true;
                    performMove(event);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                //按钮显示时，使其恢复原位置
                if(isDeleteShown==true&&scroller.isFinished())
                {
                    itemView.scrollTo(0, 0);
                    invalidate();
                    isDeleteShown=false;
                }
                else
                {
                    //滑动太快时
                    if(-getScrollVelocity()>SNAP_VELOCITY&&itemView.getScrollX()>mTouchSlop)
                    {
                       // Log.i(TAG,"显示");
                        showDeleteBn();
                    }
                    else
                    {
                        scrollByDistanceX();
                    }
                }
                Log.i(TAG,"dispatch:up");
                // 手指离开的时候就不响应左滚动
                isSlide = false;
                recycleVelocityTracker();
                break;
        }
        return super.dispatchTouchEvent(event);
    }


    //正在移动时回调
    private void performMove(MotionEvent ev)
    {
        MotionEvent cancelEvent = MotionEvent.obtain(ev);
        cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
        onTouchEvent(cancelEvent);
        mDeltaX = downX -(int)ev.getX();
        // 手指拖动itemView滚动, deltaX大于0向左滚动,且滑动距离小于删除按钮宽度
        if(Math.abs(mDeltaX) >0&& Math.abs(mDeltaX) <=mDeleteBnWidth)
        {
            itemView.scrollTo(mDeltaX, 0);
            itemView.postInvalidate();
        }
    }

    // 根据滑动距离是否显示删除按钮
    private void scrollByDistanceX()
    {
        if(itemView.getScrollX()>=mDeleteBnWidth/2&&itemView.getScrollX()<=mDeleteBnWidth)
        {
            isDeleteShown=true;
            // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
            final int DELTA=(mDeleteBnWidth-itemView.getScrollX());
            scroller.startScroll(itemView.getScrollX(), 0, DELTA, 0, Math.abs(mDeltaX));
        }
        else
        {
            //滑动回原来的位置
            scroller.startScroll(itemView.getScrollX(),0,-itemView.getScrollX(),0,Math.abs(itemView.getScrollX()));
            isDeleteShown=false;
        }
        postInvalidate(); // 刷新itemView
    }

    //滑动太快时调用
    private void showDeleteBn()
    {
        final int DELTA;
        if(itemView.getScrollX()>mDeleteBnWidth)
        {
            DELTA=mDeleteBnWidth;
        }
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        else
        {
            DELTA=(mDeleteBnWidth-itemView.getScrollX());
        }
        scroller.startScroll(itemView.getScrollX(), 0, DELTA, 0, Math.abs(mDeltaX));
        postInvalidate();
        isDeleteShown=true;
    }
    /**
     * 添加用户的速度跟踪器
     */
    private void addVelocityTracker(MotionEvent event)
    {
        if (velocityTracker == null)
        {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    /**
     * 计算滑动距离，并根据滑动距离绘制itemView
     */
    @Override
    public void computeScroll()
    {
        if (scroller.computeScrollOffset())
        {
            // 让ListView item根据当前的滚动偏移量进行滚动
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            itemView.postInvalidate();
        }
        else
        {
            mDeltaX=0;
        }
    }


    /**
     * 移除用户速度跟踪器
     */
    private void recycleVelocityTracker()
    {
        if (velocityTracker != null)
        {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * 获取X方向的滑动速度,大于0向右滑动，反之向左
     */
    private int getScrollVelocity()
    {
        velocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) velocityTracker.getXVelocity();
        return velocity;
    }

    /**判断滑动是否结束*/
    public boolean isScrollFinished()
    {
        return scroller.isFinished();
    }

}
