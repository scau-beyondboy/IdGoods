package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.UserBean;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-08
 * Time: 11:08
 */
public class SignupActivity extends AppCompatActivity
{
    @Bind(R.id.et_name)
    EditText name;
    @Bind(R.id.et_password)
    EditText password;
    @Bind(R.id.et_passworde2)
    EditText passwordagin;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_signup)
    public void onClick()
    {
        if(StringUtils.isEmpty(name.getText().toString())||StringUtils.isEmpty(password.getText().toString())||StringUtils.isEmpty(passwordagin.getText().toString()))
        {
            ToaskUtils.displayToast("都不能为空");
        }
        else
        {
            if(password.getText().toString().equals(passwordagin.getText().toString()))
            {
                ArrayMap<String,String> params=new ArrayMap<>(2);
                params.put(Consts.ACCOUNT_KEY,name.getText().toString());
                params.put(Consts.PASSWORD_KEY,password.getText().toString());
                NetWorkHandlerUtils.postAsynHandler(Consts.USERS_SIGNUP, params, "注册成功", "注册失败", new NetWorkHandlerUtils.PostCallback<UserBean>()
                {
                    @Override
                    public void success(final UserBean result)
                    {
                        ShareUtils.clearTempDate();
                        ShareUtils.putUserInfo(result, password.getText().toString());
                        ThreadManager.addSingalExecutorTask(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                //添加数据库中
                                if(DataSupport.where("account=?", result.getAccount()).find(UserBean.class).size()==0)
                                {
                                    result.save();
                                }
                            }
                        });
                        Intent intent=new Intent(SignupActivity.this,MainActivity.class);
                        intent.putExtra(Consts.USERS_SIGNUP,true);
                        startActivity(intent);
                    }
                },UserBean.class);
            }
            else
            {
                ToaskUtils.displayToast("两个密码不匹配");
            }
        }
    }

    @OnClick(R.id.signup_back)
    public void back()
    {
        finish();
    }

}
