package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.R;

import butterknife.ButterKnife;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-09-20
 * Time: 15:10
 * 我的产品界面
 */
public class FragmentProduct extends Fragment
{
    private MainActivity mMainActivity;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mMainActivity=(MainActivity)context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.myproduct,container,false);
        ButterKnife.bind(this, view);
        mMainActivity.mSearchView.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mMainActivity.mSearchView.setVisibility(View.GONE);
    }
}
