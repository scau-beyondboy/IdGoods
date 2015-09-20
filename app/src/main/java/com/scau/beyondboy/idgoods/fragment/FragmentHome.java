package com.scau.beyondboy.idgoods.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.scau.beyondboy.idgoods.R;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-09-20
 * Time: 10:38
 * 首页界面
 */
public class FragmentHome extends Fragment
{
    @Bind(R.id.input_barcode)
    EditText inputTdcodeText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.home,container,false);
        ButterKnife.bind(this,view);
        inputTdcodeText.setFocusable(true);
        inputTdcodeText.setFocusableInTouchMode(true);
        inputTdcodeText.requestFocus();
        return view;
    }
}
