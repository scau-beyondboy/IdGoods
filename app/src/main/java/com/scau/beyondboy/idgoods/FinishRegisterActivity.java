package com.scau.beyondboy.idgoods;

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
 * Date: 2015-10-07
 * Time: 21:12
 * 登记完成界面
 */
public class FinishRegisterActivity extends AppCompatActivity
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
        setContentView(R.layout.finish_register);
        ButterKnife.bind(this);
        String serialNumber=(String)getIntent().getStringExtra(Consts.SERIALNUMBERVALUEKEY);
        ScanCodeBean scanCodeBean=(ScanCodeBean)getIntent().getSerializableExtra(Consts.SCAN_CODE_BEAN);
        LoadImageUtils.getInstance().loadImage(headerImage, scanCodeBean.getGetAdversementPhoto(), this);
        adverseSerialNumber.setText(serialNumber);
        productName.setText(scanCodeBean.getName());
    }

    @OnClick(R.id.finishbn)
    //跳转到首页
    public void onClick()
    {
        finish();
    }
}
