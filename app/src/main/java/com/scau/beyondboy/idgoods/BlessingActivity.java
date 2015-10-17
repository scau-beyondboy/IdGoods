package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-17
 * Time: 09:57
 * 祝福录音界面
 */
public class BlessingActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blessing);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.blessing_back,R.id.voice_blessing})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.blessing_back:
                finish();
                break;
            case R.id.voice_blessing:
                startActivity(new Intent(this,RecordActivity.class));
                break;
        }
    }
}
