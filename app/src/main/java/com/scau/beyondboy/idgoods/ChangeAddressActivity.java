package com.scau.beyondboy.idgoods;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.view.ClearEditText;

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
        ContentValues values = new ContentValues();
        values.put(Consts.ADDRESS_KEY,address.getText().toString());
        PersonInfoActivity.changeInfo(values,Consts.ADDRESS_KEY,address.getText().toString());
    }
}
