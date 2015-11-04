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

import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

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
                ShareUtils.clearTempDate(mActivity);
                mActivity.changeFragment(new FragmentLogin(), true);
                mActivity.setChangeSetting("登陆");
                break;
        }
    }

    private void modifyPassword()
    {
        if(StringUtils.isEmpty(originPassword.getText().toString())||StringUtils.isEmpty(newPassword.getText().toString())||StringUtils.isEmpty(newPasswordSecond.getText().toString()))
        {
            ToaskUtils.displayToast("都不能为空");
        }
        else
        {
            if(newPassword.getText().toString().equals(newPasswordSecond.getText().toString()))
            {
                Map<String,String> params=new LinkedHashMap<>();
                params.put(Consts.USERID_KEY, ShareUtils.getUserId(mActivity));
                params.put(Consts.ORIPASSWORD_KEY, originPassword.getText().toString());
                params.put(Consts.NEWPASSWORD_KEY,newPassword.getText().toString());
                NetWorkHandlerUtils.postAsynHandler(Consts.UPDATE_PASSWORD,params,"更改密码成功");
            }
            else
            {
                ToaskUtils.displayToast("两次输入的新密码不匹配");
            }
        }
    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mActivity.getTitleContent().setVisibility(View.GONE);
    }

    @OnEditorAction({R.id.new_passwordsecond})
    public boolean onEditorAction(TextView content,int actionId,KeyEvent event)
    {
        if(actionId== EditorInfo.IME_ACTION_SEND||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
        {
            modifyPassword();
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
