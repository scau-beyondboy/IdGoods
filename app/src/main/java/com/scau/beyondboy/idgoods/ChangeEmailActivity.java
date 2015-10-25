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
 *修改email
 */
public class ChangeEmailActivity extends BaseActivity
{

    @Bind(R.id.email)
    ClearEditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        ButterKnife.bind(this);
        email.setText(getIntent().getStringExtra(Consts.EMAIL_KEY));
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
                changeEmail();
                break;
        }
    }

    private void changeEmail()
    {
        ContentValues values = new ContentValues();
        values.put(Consts.EMAIL_KEY,email.getText().toString());
        PersonInfoActivity.changeInfo(values,Consts.EMAIL_KEY, email.getText().toString());
    }
}
