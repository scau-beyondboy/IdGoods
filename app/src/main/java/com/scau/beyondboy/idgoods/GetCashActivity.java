package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.utils.LoadImageUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-03
 * Time: 12:27
 * 领取返现界面
 */
public class GetCashActivity extends AppCompatActivity
{
    private static final String TAG = GetCashActivity.class.getName();
    @Bind(R.id.header_image)
    ImageView headerImage;
    @Bind(R.id.adverse_serialnumber)
    TextView adverseSerialNumber;
    @Bind(R.id.product_name)
    TextView productName;
    private String serialNumberValue;
    private ScanCodeBean scanCodeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_cash);
        ButterKnife.bind(this);
        serialNumberValue=(String)getIntent().getStringExtra(Consts.SERIALNUMBERVALUEKEY);
        scanCodeBean=(ScanCodeBean)getIntent().getParcelableExtra(Consts.SCAN_CODE_BEAN);
        LoadImageUtils.getInstance().loadImage(headerImage,scanCodeBean.getGetAdversementPhoto(),this);
        adverseSerialNumber.setText(serialNumberValue);
        productName.setText(scanCodeBean.getName());
    }

    @OnClick(R.id.getcashbn)
    public void onClick()
    {
        //跳转支付界面
        Intent intent=new Intent(GetCashActivity.this,PayActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(Consts.SERIALNUMBERVALUEKEY, serialNumberValue);
        bundle.putParcelable(Consts.SCAN_CODE_BEAN, scanCodeBean);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
