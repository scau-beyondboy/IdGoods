package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.CollectBean;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.view.ListenBlessPopupWindow;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-11-18
 * Time: 09:32
 */
public class ListenBlessActivity extends AppCompatActivity

{

    private static final String TAG = ListenBlessPopupWindow.class.getName();

    @Bind(R.id.listen)

    Button mListen;



    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState)

    {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.listen);

        ButterKnife.bind(this);

        MyApplication.sActivityMap.put(Consts.BLESS_ACTIVITY, this);

        boolean isCollect = getIntent().getBooleanExtra(Consts.FRAGMENT_COLLECT, false);

        if(isCollect)

        {

            Log.i(TAG,"这里消失");

            mListen.setVisibility(View.GONE);

            Intent intent = new Intent(this, ListenBlessPopupWindow.class);

            CollectBean collectBean=getIntent().getParcelableExtra(Consts.COLLECT_BEAN);

            intent.putExtra(Consts.COLLECT_BEAN, collectBean);

            startActivity(intent);

        }

    }



    @OnClick(R.id.listen)

    public void onClick()

    {

        Intent intent = new Intent(this, ListenBlessPopupWindow.class);

        ScanCodeBean scanCodeBean = getIntent().getParcelableExtra(Consts.SCAN_CODE_BEAN);

        mListen.setVisibility(View.INVISIBLE);

        intent.putExtra(Consts.RECEIVEBLESS, true);

        intent.putExtra(Consts.SCAN_CODE_BEAN, scanCodeBean);

        startActivity(intent);

    }





    @OnClick(R.id.listen_back)

    public void back()

    {

        MyApplication.sActivityMap.get(Consts.BLESS_POPUP).finish();

        MyApplication.sActivityMap.remove(Consts.BLESS_POPUP);

        finish();

    }



    @Override

    protected void onDestroy()

    {

        super.onDestroy();

        Log.i(TAG, "被销毁吗");

    }

}


