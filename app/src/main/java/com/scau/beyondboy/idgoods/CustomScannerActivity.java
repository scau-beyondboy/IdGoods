package com.scau.beyondboy.idgoods;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.scau.beyondboy.idgoods.handler.FinshBarCodeHandler;
import com.scau.beyondboy.idgoods.utils.ShareUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-06
 * Time: 20:32
 * 自定义二维扫描框
 */
public class CustomScannerActivity extends BaseActivity implements CompoundBarcodeView.TorchListener
{
    private static final String TAG = CustomScannerActivity.class.getName();
    /**登录身份标记，0代表用户登录，1代表销售员登录，2代表游客登录*/
    private int identity=0;
    private CompoundBarcodeView barcodeScannerView;
    private String serialNumberValue;
    private Button switchFlashlightButton;
    private BarcodeCallback callback = new BarcodeCallback()
    {
        @Override
        public void barcodeResult(BarcodeResult result)
        {
            if (result.getText() != null)
            {
                //防止多次扫描
                barcodeScannerView.pause();
                barcodeScannerView.setStatusText(result.getText());
                serialNumberValue=result.getText().toString();
                ShareUtils.putSerialNumberValue(CustomScannerActivity.this,serialNumberValue);//// TODO: 2015/10/20
                FinshBarCodeHandler.init(serialNumberValue,CustomScannerActivity.this,1);
                FinshBarCodeHandler.finishScanHandler();
            }
            //Toast.makeText(CustomScannerActivity.this, "显示二维码：" + result.getText(), Toast.LENGTH_SHORT).show();
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints)
        {
           // Log.i(TAG, "坐标： " + resultPoints);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);
        ButterKnife.bind(this);
        barcodeScannerView = (CompoundBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.decodeContinuous(callback);
        barcodeScannerView.setTorchListener(this);
        switchFlashlightButton = (Button)findViewById(R.id.switch_flashlight);
        //如果有闪光灯则显示按钮，否则不显示
        if (!hasFlash())
        {
            switchFlashlightButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.scan_barcode_cancel)
    public void onClick(View v)
    {
        finish();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * 检查是否有闪关灯
     */
    private boolean hasFlash()
    {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**按钮监听*/
    public void switchFlashlight(View view)
    {
        if (getString(R.string.turn_on_flashlight).equals(switchFlashlightButton.getText()))
        {
            barcodeScannerView.setTorchOn();
        } else
        {
            barcodeScannerView.setTorchOff();
        }
    }

    @Override
    public void onTorchOn()
    {
        switchFlashlightButton.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff()
    {
        switchFlashlightButton.setText(R.string.turn_on_flashlight);
    }

}
