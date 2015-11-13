package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.content.Intent;
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

import com.scau.beyondboy.idgoods.CheckCodeActivity;
import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.UserBean;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;

import org.litepal.crud.DataSupport;

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
 * Time: 11:18
 * 登陆界面
 */
public class FragmentLogin extends Fragment
{
    //private static final String TAG = FragmentLogin.class.getName();
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
        login();
    }

    /**登录验证*/
    private void login()
    {
        if(StringUtils.isEmpty(name.getText().toString())&&StringUtils.isEmpty(inviteNumber.getText().toString()))
        {
            ToaskUtils.displayToast("账号错误，请输入手机号码");
        }
        else
        {
            Map<String,String> params=new LinkedHashMap<>();
            params.put(Consts.ACCOUNT_KEY,name.getText().toString());
            params.put(Consts.PASSWORD_KEY, password.getText().toString());
            if(!StringUtils.isEmpty(inviteNumber.getText().toString()))
            {
                params.put(Consts.INVITECODEVALUE_KEY,inviteNumber.getText().toString());
            }
            NetWorkHandlerUtils.postAsynHandler(Consts.USER_LOGIN, params, "登陆成功", "登陆不匹配或登陆失败", new NetWorkHandlerUtils.PostSuccessCallback<UserBean>()
            {
                @Override
                public void success(final UserBean result)
                {
                    ShareUtils.clearTempDate();
                    ShareUtils.putUserInfo(result, password.getText().toString());
                    mActivity.changeFragment(new FragmentHome(), true,"home");
                    mActivity.setNickName(result.getNickname());
                    mActivity.setChangeSetting("设置");
                    ThreadManager.addSingalExecutorTask(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //添加数据库中
                            if (DataSupport.where("account=?", result.getAccount()).find(UserBean.class).size() == 0)
                            {
                                result.save();
                            }
                        }
                    });
                }
            }, UserBean.class);
        }
    }

    @OnEditorAction({R.id.et_password,R.id.et_invitenumber})
    public boolean onEditorAction(TextView content,int actionId,KeyEvent event)
    {
        if(actionId== EditorInfo.IME_ACTION_SEND||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
        {
            login();
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
    @OnClick(R.id.btn_signup)
    public void signUp()
    {
        startActivity(new Intent(getActivity(), CheckCodeActivity.class));
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
