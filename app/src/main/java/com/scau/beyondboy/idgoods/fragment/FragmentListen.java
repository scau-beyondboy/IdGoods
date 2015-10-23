package com.scau.beyondboy.idgoods.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.ListenActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-19
 * Time: 21:26
 * 收听界面
 */
public class FragmentListen extends Fragment
{
    @Bind(R.id.nickname)
    TextView nickName;
    private ScanCodeBean mScanCodeBean;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.listen, container, false);
        ButterKnife.bind(this,view);
        Bundle bundle=getArguments();
        mScanCodeBean = bundle.getParcelable(Consts.SCAN_CODE_BEAN);
        nickName.setText(mScanCodeBean.getAdversementName());
        return view;
    }

    @OnClick(R.id.listen)
    public void onClick()
    {
        Intent intent=new Intent(getActivity(),ListenActivity.class);
        intent.putExtra(Consts.SCAN_CODE_BEAN,mScanCodeBean);
         startActivity(intent);
    }
}
