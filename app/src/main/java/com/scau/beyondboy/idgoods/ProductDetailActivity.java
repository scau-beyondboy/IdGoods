package com.scau.beyondboy.idgoods;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ProductBean;
import com.scau.beyondboy.idgoods.model.ProductInfo;
import com.scau.beyondboy.idgoods.utils.LoadImageUtils;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;

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
public class ProductDetailActivity extends BaseActivity
{
    //private static final String TAG = ProductDetailActivity.class.getName();
    @Bind(R.id.header_image)
    ImageView headerImage;
    @Bind(R.id.product_name)
    TextView productName;
    @Bind(R.id.adverse_serialnumber)
    TextView adverseSerialNumber;
    @Bind(R.id.discount)
    TextView discount;
    @Bind(R.id.date)
    TextView date;
    private ProductBean mProductBean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        mProductBean=getIntent().getParcelableExtra(Consts.PRODUCT_BEAN);
        ButterKnife.bind(this);
        loadData();
    }

    private void loadData()
    {
        ArrayMap<String,String> params=new ArrayMap<>();
        params.put(Consts.SERIALNUMBERVALUEKEY, mProductBean.getSerialNumber());
        NetWorkHandlerUtils.postAsynHandler(Consts.GET_PRODUCT_INFO, params, null, "获取失败", new NetWorkHandlerUtils.PostCallback<ProductInfo>()
        {
            @Override
            public void success(ProductInfo result)
            {
                LoadImageUtils.getInstance().loadImage(headerImage, mProductBean.getAdvertisementPhoto(), ProductDetailActivity.this);
                productName.setText(result.getName());
                adverseSerialNumber.setText(result.getSerialNumber());
                discount.setText(String.format("优惠%d元", result.getDiscount()));
                date.setText(result.getTime());
            }
        }, ProductInfo.class);
    }
    @OnClick(R.id.product_detail_back)
    public void onClick()
    {
        finish();
    }
}
