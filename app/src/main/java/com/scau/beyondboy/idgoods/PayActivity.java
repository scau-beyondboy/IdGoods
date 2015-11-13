package com.scau.beyondboy.idgoods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
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
 * Date: 2015-10-09
 * Time: 10:16
 * 支付界面
 */
public class PayActivity extends BaseActivity
{
    //private static final String TAG = PayActivity.class.getName();
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
        next();
    }

    private void next()
    {
        if(StringUtils.isEmpty(inputAccount1.getText().toString())||StringUtils.isEmpty(inputAccount2.getText().toString()))
        {
            ToaskUtils.displayToast("支付宝和微信账号都不能为空");
        }
        else
        {
            if(inputAccount1.getText().toString().equals(inputAccount2.getText().toString()))
            {
                ArrayMap<String,String> params=new ArrayMap<>(3);
                params.put(Consts.CUSTOMERID_KEY, ShareUtils.getUserId());
                params.put(Consts.SERIALNUMBERVALUEKEY,mSerialNumber);
                if(payWay==0)
                {
                    params.put(Consts.ALIAPAY,inputAccount1.getText().toString());
                }
                else if(payWay==1)
                {
                    params.put(Consts.WEBCHAT,inputAccount2.getText().toString());
                }

                NetWorkHandlerUtils.postAsynHandler(Consts.GET_DIS_COUNT, params, null, new NetWorkHandlerUtils.PostSuccessCallback<Object>()
                {
                    @Override
                    public void success(Object result)
                    {
                        Intent intent = new Intent(PayActivity.this, FinishRegisterActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Consts.SERIALNUMBERVALUEKEY, mSerialNumber);
                        bundle.putParcelable(Consts.SCAN_CODE_BEAN, mScanCodeBean);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else
            {
                ToaskUtils.displayToast("两次输入账号不匹配");
            }
        }
    }

    @OnEditorAction(R.id.account2)
    public boolean onEditorAction(TextView content,int actionId,KeyEvent event)
    {
        if(actionId== EditorInfo.IME_ACTION_SEND||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
        {
            next();
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
}
