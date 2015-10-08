package com.scau.beyondboy.idgoods.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-04
 * Time: 16:00
 */
public class RTButton extends Button
{
    private static final String TAG = RTButton.class.getName();

    public RTButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                System.out.println("RTButton---dispatchTouchEvent---DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("RTButton---dispatchTouchEvent---MOVE");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("RTButton---dispatchTouchEvent---UP");
                break;
            default:
                break;
        }
      //  Log.i(TAG, "suepr.dispatch:  " + super.dispatchTouchEvent(event));
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                System.out.println("RTButton---onTouchEvent---DOWN");
                //Log.i(TAG,super.onTouchEvent(event)+"" );
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("RTButton---onTouchEvent---MOVE");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("RTButton---onTouchEvent---UP");
                break;
            default:
                break;
        }
        //super.onTouchEvent(event);
        return false;
    }
}
