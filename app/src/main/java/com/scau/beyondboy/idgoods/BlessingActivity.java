package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.scau.beyondboy.idgoods.view.RecordPopupWindow;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-17
 * Time: 09:57
 * 祝福录音界面
 */
public class BlessingActivity extends AppCompatActivity
{
    @Bind(R.id.voice_blessing)
    Button voiceBlessing;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blessing);
        ButterKnife.bind(this);
        MyApplication.sActivityMap.put("BlessingActivity",this);
    }

    @OnClick({R.id.blessing_back,R.id.voice_blessing})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.blessing_back:
                MyApplication.sActivityMap.get("RecordPopupWindow").finish();
                MyApplication.sActivityMap.remove("RecordPopupWindow");
                finish();
                break;
            case R.id.voice_blessing:
                voiceBlessing.setVisibility(View.GONE);
                startActivity(new Intent(this, RecordPopupWindow.class));
                break;
        }
    }
}
