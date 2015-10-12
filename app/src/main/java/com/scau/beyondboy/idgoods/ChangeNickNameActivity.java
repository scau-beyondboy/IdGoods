package com.scau.beyondboy.idgoods;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.UserBean;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.scau.beyondboy.idgoods.view.ClearEditText;
import com.squareup.okhttp.Request;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-12
 * Time: 11:05
 * 修改昵称
 */
public class ChangeNickNameActivity extends AppCompatActivity
{
    @Bind(R.id.nickname)
    ClearEditText nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);
        ButterKnife.bind(this);
        nickname.setText(getIntent().getStringExtra(Consts.NICKNAME_KEY));
    }

    @OnClick({R.id.change_back,R.id.save})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.change_back:
                finish();
                break;
            case R.id.save:
                changeAddress();
                break;
        }
    }

    private void changeAddress()
    {
        if(StringUtils.isEmpty(nickname.getText().toString()))
        {
            displayToast("不能为空");
        }
        else
        {
            OkHttpNetWorkUtil.postAsyn(Consts.UPDATE_INFO, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
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
                    parseAddressDataJson(response);
                    ContentValues values = new ContentValues();
                    values.put(Consts.NICKNAME_KEY, nickname.getText().toString());
                    //更新数据库
                    DataSupport.updateAll(UserBean.class, values, "account=?", ShareUtils.getAccount(ChangeNickNameActivity.this));
                }
            }, new OkHttpNetWorkUtil.Param(Consts.USERID_KEY, ShareUtils.getUserId(this)), new OkHttpNetWorkUtil.Param(Consts.NICKNAME_KEY, nickname.getText().toString()));
        }
    }

    private void displayToast(String warnning)
    {
        Toast.makeText(this, warnning, Toast.LENGTH_SHORT).show();
    }

    /**解析json*/
    private void parseAddressDataJson(ResponseObject<Object> responseObject)
    {
        Gson gson=new Gson();
        String data=gson.toJson(responseObject.getData());
        if(responseObject.getResult()==1)
        {
            displayToast("修改成功");
        }
        else
        {
            displayToast(data);
        }
    }
}
