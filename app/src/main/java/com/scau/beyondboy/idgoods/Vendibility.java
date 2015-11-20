package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.consts.Consts;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-09
 * Time: 09:43
 * 产品可销售界面
 */
public class Vendibility extends AppCompatActivity
{
    @Bind(R.id.adverse_serialnumber)
    TextView serialNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendibility);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        serialNumberTextView.setText(intent.getStringExtra(Consts.SERIALNUMBERVALUEKEY));
    }

    @OnClick(R.id.finishbn)
    public void onClick()
    {
        finish();
    }

}
