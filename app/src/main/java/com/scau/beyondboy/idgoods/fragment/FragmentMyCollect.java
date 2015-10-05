package com.scau.beyondboy.idgoods.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.scau.beyondboy.idgoods.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-04
 * Time: 16:06
 */
public class FragmentMyCollect extends Fragment
{
    private static final String TAG = FragmentMyCollect.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root=inflater.inflate(R.layout.test, container, false);
        ButterKnife.bind(this,root);
        return root;
    }

    @OnClick(R.id.btn)
    public void onClick()
    {
        Log.i(TAG, "RTB onclick");
    }

    @OnTouch(R.id.btn)
    public boolean onTouch()
    {
        Log.i(TAG,"RTB ontouch");
        return false;
    }

    @OnClick(R.id.myLayout)
    public void onclick1()
    {
        System.out.println("RTLayout clicked!");
    }
    @OnTouch(R.id.myLayout)
    public boolean onTouch1(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                System.out.println("RTLayout---onTouch---DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("RTLayout---onTouch---MOVE");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("RTLayout---onTouch---UP");
                break;
            default:
                break;
        }
        return false;
     }
}
