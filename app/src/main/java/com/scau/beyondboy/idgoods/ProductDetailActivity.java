package com.scau.beyondboy.idgoods;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ProductBean;
import com.scau.beyondboy.idgoods.model.ProductInfo;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.utils.LoadImageUtils;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.squareup.okhttp.Request;

import java.lang.reflect.Type;

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
    private static final String TAG = ProductDetailActivity.class.getName();
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
        OkHttpNetWorkUtil.postAsyn(Consts.GET_PRODUCT_INFO, new OkHttpNetWorkUtil.ResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                LoadImageUtils.getInstance().loadImage(headerImage, mProductBean.getAdvertisementPhoto(), ProductDetailActivity.this);
                productName.setText(mProductBean.getName());
                adverseSerialNumber.setText(mProductBean.getSerialNumber());
                discount.setText("优惠??元");
                date.setText(mProductBean.getDateTime());
                e.printStackTrace();
                Toast.makeText(ProductDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response)
            {
                Log.i(TAG, "response:  " + response);
                ProductInfo productInfo =parseProductDataJson(response);
                LoadImageUtils.getInstance().loadImage(headerImage, mProductBean.getAdvertisementPhoto(), ProductDetailActivity.this);
                productName.setText(productInfo.getName());
                adverseSerialNumber.setText(productInfo.getSerialNumber());
                discount.setText("优惠"+productInfo.getDiscount()+"元");
                date.setText(productInfo.getTime());
            }
        }, new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,mProductBean.getSerialNumber()));
    }
    @OnClick(R.id.product_detail_back)
    public void onClick()
    {
        finish();
    }

    /**解析json*/
    private ProductInfo parseProductDataJson(String productDataJson)
    {
        Gson gson=new Gson();
        Type type=new TypeToken<ResponseObject<ProductInfo>>(){}.getType();
        gson.toJson(new ResponseObject<ProductInfo>(), type);
        ResponseObject<ProductInfo> responseObject=gson.fromJson(productDataJson,type );
        return responseObject.getData();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
