package com.scau.beyondboy.idgoods.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.scau.beyondboy.idgoods.PersonInfoActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-12
 * Time: 16:55
 * 性别选择dialog
 */
public class FragmentSex extends DialogFragment
{
    private static FragmentSex sFragmentSex;
    private String[] sexs=new String[]{"其他","男","女"};
    private int whichSex=0;
    private PersonInfoActivity mActivity;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mActivity=(PersonInfoActivity)activity;
    }

    public static FragmentSex newInstance(int chooseSex)
    {
        Bundle bundle=new Bundle();
        bundle.putInt(Consts.SEX_KEY, chooseSex);
        if(sFragmentSex==null)
        {
            sFragmentSex=new FragmentSex();
        }
        sFragmentSex.setArguments(bundle);
        return sFragmentSex;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        whichSex=getArguments().getInt(Consts.SEX_KEY);
        @SuppressLint("InflateParams")
        View dialog= LayoutInflater.from(mActivity).inflate(R.layout.dialog_sex,null);
        ButterKnife.bind(this, dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setView(dialog);
        return builder.create();
    }

    @OnClick({R.id.man,R.id.female,R.id.comfirm,R.id.cancel,R.id.other})
    public void changeSex(View view)
    {
        switch (view.getId())
        {
            case R.id.man:
                whichSex=1;
                break;
            case R.id.female:
                whichSex=2;
                break;
            case R.id.other:
                whichSex=0;
                break;
            case R.id.comfirm:
                mActivity.setSex(sexs[whichSex]);
                final ContentValues values = new ContentValues();
                values.put(Consts.SEX_KEY, whichSex);
                PersonInfoActivity.changeInfo(values, Consts.SEX_KEY, String.valueOf(whichSex),null);
                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }
}
