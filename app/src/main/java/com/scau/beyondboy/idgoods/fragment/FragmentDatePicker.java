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
import android.widget.DatePicker;

import com.scau.beyondboy.idgoods.PersonInfoActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-12
 * Time: 20:15
 * 设置时间
 */
public class FragmentDatePicker extends DialogFragment
{
    //private static final String TAG = FragmentDatePicker.class.getName();
    private static FragmentDatePicker sDatePicker;
    private Date mDate;
    @Bind(R.id.datePicker)
    DatePicker datePicker;
    private PersonInfoActivity mActivity;

    public static FragmentDatePicker newInstance(Date date)
    {
        Bundle args = new Bundle();
        args.putSerializable(Consts.DATE, date);
        if(sDatePicker==null)
        {
            sDatePicker=new FragmentDatePicker();
        }
        sDatePicker.setArguments(args);
        return sDatePicker;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mActivity=(PersonInfoActivity)activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        mDate = (Date) getArguments().getSerializable(Consts.DATE);
        Calendar calendar = Calendar.getInstance();
        if (mDate != null)
        {
            calendar.setTime(mDate);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("InflateParams")
        View dialogView= LayoutInflater.from(mActivity).inflate(R.layout.dialog_date,null);
        ButterKnife.bind(this, dialogView);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener()
        {
            public void onDateChanged(DatePicker view, int year, int month, int day)
            {
                mDate = new GregorianCalendar(year, month, day).getTime();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle);
        builder.setView(dialogView);
        return builder.create();
    }

    @OnClick({R.id.comfirm,R.id.cancel})
    public void changeDate(View view)
    {
        switch (view.getId())
        {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.comfirm:
               mActivity.setDate(mDate);
                final ContentValues values = new ContentValues();
                values.put(Consts.BIRTHDAY_KEY, mDate.getTime());
                PersonInfoActivity.changeInfo(values, Consts.BIRTHDAY_KEY, String.valueOf(mDate.getTime()),null);
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
