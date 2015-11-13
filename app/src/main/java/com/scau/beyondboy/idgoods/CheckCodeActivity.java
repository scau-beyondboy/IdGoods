package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-11
 * Time: 14:02
 * 短信验证
 */
public class CheckCodeActivity extends BaseActivity
{
    @Bind(R.id.btn_getCheckCode)
    Button checkCodeBn;
    @Bind(R.id.et_phone)
    EditText phone;
    @Bind(R.id.et_checkcode)
    EditText checkCode;
    private CountTimer mCountTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getcheck_code);
        ButterKnife.bind(this);
        mCountTimer=new CountTimer(60000,1000);
    }

    //每隔一分钟可点击一次验证码
    public class CountTimer extends CountDownTimer
    {
        /**
         * @param millisInFuture 时间间隔是多长的时间
         * @param countDownInterval 回调onTick方法，没隔多久执行一次
         */
        public CountTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }
        //间隔时间结束的时候调用的方法
        @Override
        public void onFinish()
        {
            //更新页面的组件
            checkCodeBn.setText(R.string.register_get_check_num);
            checkCodeBn.setBackgroundResource(R.drawable.my_register_get_check_pass);
            checkCodeBn.setClickable(true);
        }
        //间隔时间内执行的操作
        @Override
        public void onTick(long millisUntilFinished)
        {
            //更新页面的组件
            checkCodeBn.setText(String.format("%d秒后重新发送", millisUntilFinished / 1000));
            checkCodeBn.setBackgroundResource(R.drawable.btn_light_press);
            checkCodeBn.setClickable(false);
        }
    }

    @OnClick({R.id.btn_getCheckCode,R.id.confirm,R.id.checkcode_back})
    public void onClick(View view)
    {
           switch (view.getId())
           {
               case R.id.btn_getCheckCode:
                   mCountTimer.start();
                   getCheckCode();
                   break;
               case R.id.confirm:
                   sendCheckCode();
                   break;
               case R.id.checkcode_back:
                   finish();
                   break;
           }
    }

    /**获取短信验证*/
    private void getCheckCode()
    {
        if(StringUtils.isEmpty(phone.getText().toString()))
        {
            ToaskUtils.displayToast("手机号不能为空");
        }
        else
        {
            ArrayMap<String,String> params=new ArrayMap<>(1);
            params.put(Consts.ACCOUNT_KEY,phone.getText().toString());
            NetWorkHandlerUtils.postAsynHandler(Consts.SEND_CODE, params, "获取验证码成功");
        }
    }

    /**发送验证码验证*/
    private void sendCheckCode()
    {
        if(StringUtils.isEmpty(checkCode.getText().toString()))
        {
            ToaskUtils.displayToast("验证码不能为空");
        }
        else
        {
            ArrayMap<String,String> params=new ArrayMap<>(2);
            params.put(Consts.ACCOUNT_KEY,phone.getText().toString());
            params.put(Consts.SMS_CODE,checkCode.getText().toString());
            NetWorkHandlerUtils.postAsynHandler(Consts.VERIFY_CODE, params, null, new NetWorkHandlerUtils.PostSuccessCallback<Object>()
            {
                @Override
                public void success(Object result)
                {
                    Intent intent=new Intent(CheckCodeActivity.this,SignupActivity.class);
                    ShareUtils.putAccount(phone.getText().toString());
                    startActivity(intent);
                }
            });
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
