package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-11
 * Time: 16:46
 */
public class FragmentModifyPassword extends Fragment
{
    @Bind(R.id.origin_password)
    EditText originPassword;
    @Bind(R.id.new_password)
    EditText newPassword;
    @Bind(R.id.new_passwordsecond)
    EditText newPasswordSecond;
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
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.modify_password,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @OnClick({R.id.logout,R.id.modify})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.modify:
                modifyPassword();
                break;
            case R.id.logout:
                mActivity.changeFragment(new FragmentLogin(), true);
                mActivity.setChangeSetting("登陆");
                break;
        }
    }

    private void modifyPassword()
    {
        if(StringUtils.isEmpty(originPassword.getText().toString())||StringUtils.isEmpty(newPassword.getText().toString())||StringUtils.isEmpty(newPasswordSecond.getText().toString()))
        {
            displayToast("都不能为空");
        }
        else
        {
            if(newPassword.getText().toString().equals(newPasswordSecond.getText().toString()))
            {
                Map<String,String> params=new LinkedHashMap<>();
                params.put(Consts.USERID_KEY, ShareUtils.getUserId(mActivity));
                params.put(Consts.ORIPASSWORD_KEY, originPassword.getText().toString());
                params.put(Consts.NEWPASSWORD_KEY,newPassword.getText().toString());
                OkHttpNetWorkUtil.postAsyn(Consts.UPDATE_PASSWORD, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        e.printStackTrace();
                        displayToast("网络异常");
                    }

                    @Override
                    public void onResponse(ResponseObject<Object> response)
                    {
                        parseUdPdDataJson(response);
                    }
                },params);
            }
            else
            {
                displayToast("两次输入的新密码不匹配");
            }
        }
    }

    /**解析json*/
    private void parseUdPdDataJson(ResponseObject<Object> responseObject)
    {
        Gson gson=new Gson();
        String data=gson.toJson(responseObject.getData());
        if(responseObject.getResult()==1)
        {
            displayToast("更改密码成功");
        }
        else
        {
            displayToast(data);
        }
    }

    private void displayToast(String warnning)
    {
        Toast.makeText(getActivity(), warnning, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mActivity.getTitleContent().setVisibility(View.GONE);
    }
}
