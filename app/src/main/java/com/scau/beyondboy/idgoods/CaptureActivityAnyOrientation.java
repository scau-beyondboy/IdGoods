package com.scau.beyondboy.idgoods;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureActivity;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-01
 * Time: 21:41
 * 二维码扫面类
 */
public class CaptureActivityAnyOrientation extends CaptureActivity
{
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        cancel=(Button)findViewById(R.id.scan_barcode_cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}
