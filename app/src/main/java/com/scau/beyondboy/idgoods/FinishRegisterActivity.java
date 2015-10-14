package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.utils.LoadImageUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-07
 * Time: 21:12
 * 登记完成界面
 */
public class FinishRegisterActivity extends BaseActivity
{
    @Bind(R.id.header_image)
    ImageView headerImage;
    @Bind(R.id.adverse_serialnumber)
    TextView adverseSerialNumber;
    @Bind(R.id.product_name)
    TextView productName;
    @Bind(R.id.discount)
    TextView discount;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ThreadManager.scoolPoolSize=1;
        setContentView(R.layout.activity_finish_register);
        ButterKnife.bind(this);
        String serialNumber=getIntent().getStringExtra(Consts.SERIALNUMBERVALUEKEY);
        ScanCodeBean scanCodeBean=getIntent().getParcelableExtra(Consts.SCAN_CODE_BEAN);
        LoadImageUtils.getInstance().loadImage(headerImage, scanCodeBean.getGetAdversementPhoto(), this);
        adverseSerialNumber.setText(serialNumber);
        productName.setText(scanCodeBean.getName());
        discount.setText(scanCodeBean.getDiscount());
    }

    //跳转到首页
    @OnClick(R.id.finishbn)
    public void onClick()
    {
        Intent intent=new Intent(FinishRegisterActivity.this,MainActivity.class);
        intent.putExtra(Consts.FINISHREGISTER,true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ThreadManager.release();
    }
}
