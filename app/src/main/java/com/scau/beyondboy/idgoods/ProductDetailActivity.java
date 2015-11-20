package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ProductBean;
import com.scau.beyondboy.idgoods.model.ProductInfo;
import com.scau.beyondboy.idgoods.utils.LoadImageUtils;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
import com.scau.beyondboy.idgoods.view.InfoPopupWindow;

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
    //private static final String TAG = ProductDetailActivity.class.getName();
    @Bind(R.id.header_image)
    ImageView headerImage;
    private ProductBean mProductBean;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        mProductBean=getIntent().getParcelableExtra(Consts.PRODUCT_BEAN);
        ButterKnife.bind(this);
        mIntent = new Intent(this, InfoPopupWindow.class);
        loadData();
    }

    private void loadData()
    {
        ArrayMap<String,String> params=new ArrayMap<>();
        params.put(Consts.SERIALNUMBERVALUEKEY, mProductBean.getSerialNumber());
        NetWorkHandlerUtils.postAsynHandler(Consts.GET_PRODUCT_INFO, params, null, "获取失败", new NetWorkHandlerUtils.PostSuccessCallback<ProductInfo>()
        {
            @Override
            public void success(ProductInfo result)
            {
                LoadImageUtils.getInstance().loadImage(headerImage, mProductBean.getAdvertisementPhoto(), ProductDetailActivity.this);
                //productName.setText(result.getName());
                Bundle bundle=new Bundle();
                bundle.putString(Consts.NICKNAME_KEY, result.getName());
                bundle.putString(Consts.SERIALNUMBERVALUEKEY, result.getSerialNumber());
                bundle.putInt(Consts.GET_DIS_COUNT, result.getDiscount());
                bundle.putString(Consts.DATE, result.getTime());
                mIntent.putExtras(bundle);
            }
        }, ProductInfo.class);
    }

    @OnClick(R.id.header_image)
    public void display()
    {
        startActivity(mIntent);
    }
    @OnClick(R.id.product_detail_back)
    public void onClick()
    {
        finish();
    }
}
