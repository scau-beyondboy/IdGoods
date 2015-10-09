package com.scau.beyondboy.idgoods;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 支付方式
 */
public class SelectePayActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_ali;
    private Button btn_wechat;
    private EditText ed1;
    private EditText ed2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ed1 = (EditText) findViewById(R.id.account1);
        ed2 = (EditText) findViewById(R.id.account2);
        btn_ali = (Button) findViewById(R.id.alipay);
        btn_wechat = (Button) findViewById(R.id.wechatpay);
        btn_ali.setOnClickListener(this);
        btn_wechat.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.alipay:
                 ed1.setHint("请输入支付宝账号");
                 ed2.setHint("请再次输入支付宝账号");
                break;
            case R.id.wechatpay:
                ed1.setHint("请输入微信账号");
                ed2.setHint("请再次输入微信账号");
                break;
        }
    }
}
