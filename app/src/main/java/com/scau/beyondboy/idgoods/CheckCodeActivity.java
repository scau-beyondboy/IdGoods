package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.lang.reflect.Type;

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
public class CheckCodeActivity extends AppCompatActivity
{
    @Bind(R.id.btn_getCheckCode)
    Button checkCodeBn;
    @Bind(R.id.et_phone)
    EditText phone;
    @Bind(R.id.et_checkcode)
    EditText checkCode;
    private CountTimer mCountTimer;
    private boolean isVerifyCode=false;
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
            checkCodeBn.setText(millisUntilFinished / 1000 + "秒后发送");
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
            displayToast("手机号不能为空");
        }
        else
        {
            OkHttpNetWorkUtil.postAsyn(Consts.SEND_CODE, new OkHttpNetWorkUtil.ResultCallback<String>()
            {
                @Override
                public void onError(Request request, Exception e)
                {
                    displayToast("网络异常");
                }

                @Override
                public void onResponse(String response)
                {
                    parseCheckCodeDataJson(response);
                }
            },new OkHttpNetWorkUtil.Param(Consts.ACCOUNT_KEY,phone.getText().toString()));
        }
    }

    /**发送验证码验证*/
    private void sendCheckCode()
    {
        if(StringUtils.isEmpty(checkCode.getText().toString()))
        {
            displayToast("验证码不能为空");
        }
        else
        {
            OkHttpNetWorkUtil.postAsyn(Consts.VERIFY_CODE, new OkHttpNetWorkUtil.ResultCallback<String>()
            {
                @Override
                public void onError(Request request, Exception e)
                {
                    displayToast("网络异常");
                }

                @Override
                public void onResponse(String response)
                {
                    isVerifyCode=true;
                    parseCheckCodeDataJson(response);
                }
            },new OkHttpNetWorkUtil.Param(Consts.ACCOUNT_KEY,phone.getText().toString()),new OkHttpNetWorkUtil.Param(Consts.SMS_CODE,checkCode.getText().toString()));
        }
    }
    /**解析json*/
    private void parseCheckCodeDataJson(String checkCodeDataJson)
    {
        Gson gson=new Gson();
        Type type=new TypeToken<ResponseObject<Object>>(){}.getType();
        ResponseObject<Object> responseObject=gson.fromJson(checkCodeDataJson, type);
        String data=gson.toJson(responseObject.getData());
        if(responseObject.getResult()==1)
        {
            if(isVerifyCode==false)
            {
                displayToast("获取验证码成功");
            }
            else
            {
                Intent intent=new Intent(this,SignupActivity.class);
                startActivity(intent);
                isVerifyCode=false;
            }
        }
        else
        {
            displayToast(data);
        }
    }

    private void displayToast(String warnning)
    {
        Toast.makeText(this, warnning, Toast.LENGTH_SHORT).show();
    }
}
