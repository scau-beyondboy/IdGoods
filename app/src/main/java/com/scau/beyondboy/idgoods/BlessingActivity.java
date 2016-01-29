package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.view.CircleProgressBar;
import com.scau.beyondboy.idgoods.view.RecordPlayPopupWindow;

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
    @Bind(R.id.record_play)
    Button voiceBlessing;
    @Bind(R.id.progressbar)
    public CircleProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blessing);
        ButterKnife.bind(this);
        MyApplication.sActivityMap.put(Consts.BLESSING_ACTIVITY, this);
    }

    @OnClick({R.id.blessing_back,R.id.record_play})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.blessing_back:
                if(MyApplication.sActivityMap.get(Consts.RECORD_POPUP)!=null)
                {
                    MyApplication.sActivityMap.get(Consts.RECORD_POPUP).finish();
                    MyApplication.sActivityMap.remove(Consts.RECORD_POPUP);
                }
                finish();
                break;
            case R.id.record_play:
                voiceBlessing.setVisibility(View.GONE);
                startActivity(new Intent(this, RecordPlayPopupWindow.class));
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        MyApplication.sActivityMap.clear();
    }
}
