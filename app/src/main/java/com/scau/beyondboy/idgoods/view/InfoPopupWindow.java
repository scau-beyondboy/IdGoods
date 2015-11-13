package com.scau.beyondboy.idgoods.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-11-07
 * Time: 16:08
 */
public class InfoPopupWindow extends AppCompatActivity
{
    @Bind(R.id.product_name)
    TextView mProductName;
    @Bind(R.id.adverse_serialnumber)
    TextView mAdverseSerialnumber;
    @Bind(R.id.discount)
    TextView mDiscount;
    @Bind(R.id.date)
    TextView mDate;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;//设置对话框置顶显示
        super.onCreate(savedInstanceState);
        win.setAttributes(lp);
        setContentView(R.layout.activity_inforpopup_window);
        //点击会销毁窗口，并返回首页的标记
        //lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        this.getWindow().setAttributes(lp);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        mProductName.setText(intent.getStringExtra(Consts.NICKNAME_KEY));
        mAdverseSerialnumber.setText(intent.getStringExtra(Consts.SERIALNUMBERVALUEKEY));
        mDiscount.setText(String.format("优惠%d元", intent.getIntExtra(Consts.GET_DIS_COUNT, 0)));
        mDate.setText(intent.getStringExtra(Consts.DATE));
    }



}
