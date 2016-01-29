package com.scau.beyondboy.idgoods;

import android.support.test.espresso.IdlingResource;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-11-21
 * Time: 20:07
 */
public class IntentServiceIdlingResource implements IdlingResource
{
    private static final String TAG = IntentServiceIdlingResource.class.getName();
    private final long startTime;
    private final long waitingTime;
    private ResourceCallback resourceCallback;
    public  IntentServiceIdlingResource(long waitingTime)
    {
        this.startTime = System.currentTimeMillis();
        this.waitingTime = waitingTime;
    }
    @Override
    public String getName()
    {
        return IntentServiceIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow()
    {
        long elapsed = System.currentTimeMillis() - startTime;
        boolean idle = (elapsed >= waitingTime);
        if (idle) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;

    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback)
    {
        this.resourceCallback=callback;
        System.out.println("打印");
    }
}
