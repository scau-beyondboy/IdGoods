package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.UserBean;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.squareup.okhttp.Request;

import org.litepal.crud.DataSupport;

import java.lang.reflect.Type;

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
            displayToast("都不能为空");
        }
        else
        {
            if(password.getText().toString().equals(passwordagin.getText().toString()))
            {
                OkHttpNetWorkUtil.postAsyn(Consts.USERS_SIGNUP, new OkHttpNetWorkUtil.ResultCallback<String>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {

                    }

                    @Override
                    public void onResponse(String response)
                    {
                        UserBean userBean=parseRegisterDataJson(response);
                        if(userBean!=null)
                        {
                            ShareUtils.clearTempDate(SignupActivity.this);
                            ShareUtils.putUserInfo(SignupActivity.this, userBean, password.getText().toString());
                            //添加数据库中
                            if(DataSupport.where("account=?", userBean.getAccount()).find(UserBean.class).size()==0)
                            {
                                userBean.save();
                            }
                            Intent intent=new Intent(SignupActivity.this,MainActivity.class);
                            intent.putExtra(Consts.USERS_SIGNUP,true);
                            startActivity(intent);
                        }
                    }
                },new OkHttpNetWorkUtil.Param(Consts.ACCOUNT_KEY,name.getText().toString()),new OkHttpNetWorkUtil.Param(Consts.PASSWORD_KEY,password.getText().toString()));
            }
            else
            {
                displayToast("两个密码不匹配");
            }
        }
    }

    @OnClick(R.id.signup_back)
    public void back()
    {
        finish();
    }
    /**解析json*/
    private UserBean parseRegisterDataJson(String registerDataJson)
    {
        Gson gson=new Gson();
        Type type=new TypeToken<ResponseObject<Object>>(){}.getType();
        ResponseObject<Object> responseObject=gson.fromJson(registerDataJson, type);
        String data=gson.toJson(responseObject.getData());
        if(responseObject.getResult()==1)
        {
            return gson.fromJson(data,UserBean.class);
        }
        else
        {
            displayToast(data);
            return null;
        }
    }

    private void displayToast(String warnning)
    {
        Toast.makeText(this, warnning, Toast.LENGTH_SHORT).show();
    }
}
