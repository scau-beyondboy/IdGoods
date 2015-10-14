package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-09
 * Time: 10:16
 * 支付界面
 */
public class PayActivity extends BaseActivity
{
    private static final String TAG = PayActivity.class.getName();
    /**0代表支付宝，1代表微信*/
    private int payWay=0;
    @Bind(R.id.alipay)
    RadioButton alipay;
    @Bind(R.id.wechatpay)
    RadioButton wechatpay;
    @Bind(R.id.account1)
    EditText inputAccount1;
    @Bind(R.id.account2)
    EditText inputAccount2;
    private String mSerialNumber;
    private ScanCodeBean mScanCodeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        alipay.setChecked(true);
        mSerialNumber = getIntent().getStringExtra(Consts.SERIALNUMBERVALUEKEY);
        mScanCodeBean = getIntent().getParcelableExtra(Consts.SCAN_CODE_BEAN);
    }

    @OnClick({R.id.alipay,R.id.wechatpay,R.id.pay_cancel,R.id.pay_back})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.alipay:
                payWay=0;
                break;
            case R.id.wechatpay:
                payWay=1;
                break;
            case R.id.pay_back:
                finish();
                break;
            case R.id.pay_cancel:
                inputAccount1.setText(null);
                inputAccount2.setText(null);
                alipay.setChecked(false);
                wechatpay.setChecked(false);
                break;
        }
    }

    @OnClick(R.id.nextstep)
    public void nextstep()
    {
       // Log.i(TAG,"account1: "+inputAccount1.toString()+"   account2:   "+inputAccount2.toString());
        if(StringUtils.isEmpty(inputAccount1.getText().toString())||StringUtils.isEmpty(inputAccount2.getText().toString()))
        {
            displayToast("支付宝和微信账号都不能为空");
        }
        else
        {
            if(inputAccount1.getText().toString().equals(inputAccount2.getText().toString()))
            {
                OkHttpNetWorkUtil.Param params[]=new OkHttpNetWorkUtil.Param[3];
                params[0]=new OkHttpNetWorkUtil.Param(Consts.CUSTOMERID_KEY, ShareUtils.getUserId(this));
                params[1]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,mSerialNumber);
                if(payWay==0)
                {
                    params[2]=new OkHttpNetWorkUtil.Param(Consts.ALIAPAY,inputAccount1.getText().toString());
                }
                else if(payWay==1)
                {
                    params[2]=new OkHttpNetWorkUtil.Param(Consts.WEBCHAT,inputAccount2.getText().toString());
                }
                OkHttpNetWorkUtil.postAsyn(Consts.GET_DIS_COUNT, new OkHttpNetWorkUtil.ResultCallback<String>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        displayToast("该药品还不可售");
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        ResponseObject<String> responseObject=parseDiscountJson(response);
                        if(responseObject.getResult()==1)
                        {
                            Intent intent=new Intent(PayActivity.this,FinishRegisterActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putString(Consts.SERIALNUMBERVALUEKEY,mSerialNumber);
                            bundle.putParcelable(Consts.SCAN_CODE_BEAN,mScanCodeBean);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            displayToast(responseObject.getData());
                        }
                    }
                },params);
            }
            else
            {
                displayToast("两次输入账号不匹配");
            }
        }
    }

    private void displayToast(String warnning)
    {
        Toast.makeText(this, warnning, Toast.LENGTH_SHORT).show();
    }

    /**
     * 解析数据
     */
    private ResponseObject<String> parseDiscountJson(String discountJson)
    {
        Gson gson=new Gson();
        Type type=new TypeToken<ResponseObject<String>>(){}.getType();
        gson.toJson(new ResponseObject<String>(), type);
        ResponseObject<String> responseObject=gson.fromJson(discountJson,type );
        return responseObject;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
