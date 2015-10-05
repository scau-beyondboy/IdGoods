package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.squareup.okhttp.Request;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-03
 * Time: 10:53
 * 产品详细介绍界面
 */
public class ProductDetailActivity extends AppCompatActivity
{
    @Bind(R.id.header_image)
    ImageView headerImage;
    @Bind(R.id.product_name)
    TextView advertiseName;
    @Bind(R.id.adverse_serialnumber)
    TextView adverseSerialNumber;
    @Bind(R.id.discount)
    TextView discount;
    @Bind(R.id.date)
    TextView date;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        Intent intent=getIntent();
        loadData(intent.getStringExtra(Consts.SERIALNUMBERVALUEKEY));
        ButterKnife.bind(this);
    }

    private void loadData(String  serialNumberValue )
    {
        OkHttpNetWorkUtil.postAsyn(Consts.GET_PRODUCT_INFO, new OkHttpNetWorkUtil.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {

            }

            @Override
            public void onResponse(Object response)
            {

            }
        },new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue));
    }
    @OnClick(R.id.product_detail_back)
    public void onClick()
    {
        finish();
    }
}
