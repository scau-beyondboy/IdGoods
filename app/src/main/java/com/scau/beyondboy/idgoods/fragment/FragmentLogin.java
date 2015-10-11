package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scau.beyondboy.idgoods.CheckCodeActivity;
import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.UserBean;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.squareup.okhttp.Request;

import org.litepal.crud.DataSupport;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-11
 * Time: 11:18
 * 登陆界面
 */
public class FragmentLogin extends Fragment
{
    private static final String TAG = FragmentLogin.class.getName();
    @Bind(R.id.et_password)
    EditText password;
    @Bind(R.id.et_name)
    EditText name;
    @Bind(R.id.et_invitenumber)
    EditText inviteNumber;
    private MainActivity mActivity;
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mActivity=(MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.login,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @OnClick(R.id.btn_login)
    public void onClick()
    {
        if(StringUtils.isEmpty(name.getText().toString())||StringUtils.isEmpty(password.getText().toString()))
        {
            displayToast("两者都不能为空");
        }
        else
        {
            Map<String,String> params=new LinkedHashMap<>();
            params.put(Consts.ACCOUNT_KEY,name.getText().toString());
            params.put(Consts.PASSWORD_KEY,password.getText().toString());
            if(!StringUtils.isEmpty(inviteNumber.getText().toString()))
            {
                params.put(Consts.INVITECODEVALUE_KEY,inviteNumber.getText().toString());
            }
            OkHttpNetWorkUtil.postAsyn(Consts.USER_LOGIN, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
            {
                @Override
                public void onError(Request request, Exception e)
                {
                    displayToast("密码不匹配或登陆失败");
                }

                @Override
                public void onResponse(ResponseObject<Object> response)
                {
                    Log.i(TAG, "登陆数据"+response);
                    UserBean userBean=parseLoginDataJson(response);
                    if(userBean!=null)
                    {
                        ShareUtils.clearTempDate(getActivity());
                        ShareUtils.putUserInfo(getActivity(), userBean, password.getText().toString());
                        mActivity.changeFragment(new FragmentHome(), true);
                        mActivity.setChangeSetting("设置");
                        Log.i(TAG,"userBean: "+userBean);
                        //添加数据库中
                        if(DataSupport.where("account=?", userBean.getAccount()).find(UserBean.class).size()==0)
                        {
                            userBean.save();
                        }
                    }
                }
            },params);
        }
    }

    @OnClick(R.id.btn_signup)
    public void signUp()
    {
        startActivity(new Intent(getActivity(), CheckCodeActivity.class));
    }
    /**解析json*/
    private UserBean parseLoginDataJson(ResponseObject<Object> responseObject)
    {
        Gson gson=new Gson();
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
        Toast.makeText(getActivity(), warnning, Toast.LENGTH_SHORT).show();
    }
}
