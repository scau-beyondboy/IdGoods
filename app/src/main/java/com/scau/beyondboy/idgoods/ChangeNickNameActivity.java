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
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-12
 * Time: 11:05
 * 修改昵称
 */
public class ChangeNickNameActivity extends BaseActivity
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
                changeNickname();
                break;
        }
    }

    private void changeNickname()
    {
        ContentValues values = new ContentValues();
        values.put(Consts.NICKNAME_KEY,nickname.getText().toString());
        PersonInfoActivity.changeInfo(values,Consts.NICKNAME_KEY,nickname.getText().toString());
    }
}
