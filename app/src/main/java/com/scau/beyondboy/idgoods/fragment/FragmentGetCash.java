package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.PayActivity;
import com.scau.beyondboy.idgoods.R;
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
 * Date: 2015-10-03
 * Time: 12:27
 * 领取返现界面
 */
public class FragmentGetCash extends Fragment
{
    //private static final String TAG = FragmentGetCash.class.getName();
    @Bind(R.id.header_image)
    ImageView headerImage;
    @Bind(R.id.adverse_serialnumber)
    TextView adverseSerialNumber;
    @Bind(R.id.product_name)
    TextView productName;
    private String serialNumberValue;
    private ScanCodeBean scanCodeBean;
    private MainActivity mActivity;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        ThreadManager.scoolPoolSize=1;
        mActivity=(MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.getcash, container, false);
        ButterKnife.bind(this,view);
        Bundle bundle=getArguments();
        serialNumberValue=bundle.getString(Consts.SERIALNUMBERVALUEKEY);
        scanCodeBean=bundle.getParcelable(Consts.SCAN_CODE_BEAN);
        if (scanCodeBean != null)
        {
            LoadImageUtils.getInstance().loadImage(headerImage,scanCodeBean.getGetAdversementPhoto(),mActivity);
            adverseSerialNumber.setText(serialNumberValue);
            productName.setText(scanCodeBean.getName());
        }
        return view;
    }

    @OnClick(R.id.getcashbn)
    public void onClick()
    {
        //跳转支付界面
        Intent intent=new Intent(mActivity,PayActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(Consts.SERIALNUMBERVALUEKEY, serialNumberValue);
        bundle.putParcelable(Consts.SCAN_CODE_BEAN, scanCodeBean);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
        //释放线程资源
        ThreadManager.release();
    }
}
