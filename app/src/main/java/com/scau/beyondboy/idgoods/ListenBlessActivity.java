package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
 * Date: 2015-10-19
 * Time: 21:26
 * 收听界面
 */
public class ListenBlessActivity extends AppCompatActivity
{
    @Bind(R.id.listen)
    Button mListen;
    private ScanCodeBean mScanCodeBean;
    private boolean isCollect=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listen);
        ButterKnife.bind(this);
        MyApplication.sActivityMap.put("ListenBlessActivity", this);
        isCollect=getIntent().getBooleanExtra(Consts.FRAGMENT_COLLECT,false);
        if(isCollect)
        {
            mListen.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, ListenBlessPopupWindow.class);
            CollectBean collectBean=getIntent().getParcelableExtra(Consts.COLLECT_BEAN);
            intent.putExtra(Consts.COLLECT_BEAN, collectBean);
            startActivity(intent);
        }
    }

    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.listen, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        mScanCodeBean = bundle.getParcelable(Consts.SCAN_CODE_BEAN);
        return view;
    }
*/
    @OnClick(R.id.listen)
    public void onClick()
    {
        Intent intent = new Intent(this, ListenBlessPopupWindow.class);
        mScanCodeBean=getIntent().getParcelableExtra(Consts.SCAN_CODE_BEAN);
        mListen.setVisibility(View.INVISIBLE);
        intent.putExtra(Consts.RECEIVEBLESS, true);
        intent.putExtra(Consts.SCAN_CODE_BEAN, mScanCodeBean);
        startActivity(intent);
    }

    @OnClick(R.id.listen_back)
    public void back()
    {
        MyApplication.sActivityMap.get("ListenBlessPopupWindow").finish();
        MyApplication.sActivityMap.remove("ListenBlessPopupWindow");
        finish();
    }
}
