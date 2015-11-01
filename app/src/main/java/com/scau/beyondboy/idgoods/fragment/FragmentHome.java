package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.scau.beyondboy.idgoods.CustomScannerActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.handler.FinshBarCodeHandler;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;


/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-09-20
 * Time: 10:38
 * 首页界面
 */
public class FragmentHome extends Fragment
{
    @Bind(R.id.input_barcode)
    EditText inputTdcodeText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.home,container,false);
        ButterKnife.bind(this,view);
        inputTdcodeText.setFocusable(true);
        inputTdcodeText.setFocusableInTouchMode(true);
        inputTdcodeText.requestFocus();
        return view;
    }

    //开始扫描
    @OnClick(R.id.scan_barcode)
    public void onClick()
    {
        scanFromFragment();
    }

    @OnEditorAction(R.id.input_barcode)
    public boolean onEditorAction(TextView content,int actionId,KeyEvent event)
    {
        final String serialNumber=content.getText().toString().trim();
        if(actionId== EditorInfo.IME_ACTION_SEND||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
        {
            if(StringUtils.isEmpty(serialNumber))
            {
                ToaskUtils.displayToast("请输入二维码序列号");
            }
            else
            {
                ShareUtils.putSerialNumberValue(serialNumber);
                FinshBarCodeHandler.init(serialNumber,getActivity(),0);
                //二维码扫描处理
                FinshBarCodeHandler.finishScanHandler();
            }
             /*隐藏软键盘*/
            InputMethodManager imm = (InputMethodManager)content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive())
            {
                  imm.hideSoftInputFromWindow(content.getApplicationWindowToken(), 0);
            }
            return true;
        }
        return false;
    }
    /**二维码扫描*/
    private void scanFromFragment()
    {
        IntentIntegrator integrator =IntentIntegrator.forSupportFragment(this);
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.setCameraId(0);
        integrator.initiateScan();
    }

}

