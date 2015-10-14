package com.scau.beyondboy.idgoods;

import android.content.ContentValues;
import android.os.Bundle;
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
 * 修改地址
 */

public class ChangeAddressActivity extends BaseActivity
{
    @Bind(R.id.address)
    ClearEditText address;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);
        ButterKnife.bind(this);
        address.setText(getIntent().getStringExtra(Consts.ADDRESS_KEY));
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
        if(StringUtils.isEmpty(address.getText().toString()))
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
                    values.put(Consts.ADDRESS_KEY,address.getText().toString());
                    //更新数据库
                    DataSupport.updateAll(UserBean.class, values, "account=?", ShareUtils.getAccount(ChangeAddressActivity.this));
                }
            },new OkHttpNetWorkUtil.Param(Consts.USERID_KEY, ShareUtils.getUserId(this)),new OkHttpNetWorkUtil.Param(Consts.ADDRESS_KEY,address.getText().toString()));
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
