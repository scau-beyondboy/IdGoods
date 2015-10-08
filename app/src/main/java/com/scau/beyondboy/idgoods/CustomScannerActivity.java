package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.squareup.okhttp.Request;

import java.lang.reflect.Type;
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
public class CustomScannerActivity extends AppCompatActivity implements CompoundBarcodeView.TorchListener
{
    private static final String TAG = CustomScannerActivity.class.getName();
    /**登录身份标记，0代表用户登录，1代表销售员登录，2代表游客登录*/
    private int identity=0;
    private CompoundBarcodeView barcodeScannerView;
    private Button switchFlashlightButton;
    private BarcodeCallback callback = new BarcodeCallback()
    {
        @Override
        public void barcodeResult(BarcodeResult result)
        {
            if (result.getText() != null)
            {
                barcodeScannerView.setStatusText(result.getText());
            }
            Toast.makeText(CustomScannerActivity.this, "显示二维码：" + result.getText(), Toast.LENGTH_SHORT).show();
        }
        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints)
        {
            Log.i(TAG, "坐标： " + resultPoints);
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

    /**扫完二维码处理*/
    private void finishScanHandler()
    {
        //用户登录
        if(ShareUtils.getUserId(this)!=null&&ShareUtils.getInviteCodeValue(this)==null)
        {

        }
        //销售员登录
        else if(ShareUtils.getUserId(this)!=null&&ShareUtils.getInviteCodeValue(this)!=null)
        {

        }
        else if(ShareUtils.getUserId(this)==null)
        {

        }
    }

    /**二维码序列号认证处理*/
    private void barCodeVerifyHandler(final String serialNumberValue, final int identity)
    {
        String url=null;
        OkHttpNetWorkUtil.Param param[]=new OkHttpNetWorkUtil.Param[2];
        OkHttpNetWorkUtil.ResultCallback<String> resultCallback;
        switch (identity)
        {
            case 0:
                url= Consts.CUSTOMER_SCAN;
                param[0]=new OkHttpNetWorkUtil.Param(Consts.CUSTOMERID_KEY,ShareUtils.getUserId(this));
                param[1]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
                break;
            case 1:
                url=Consts.SELLER_SCAN;
                param[0]=new OkHttpNetWorkUtil.Param(Consts.SELLERID_KEY,ShareUtils.getUserId(this));
                param[1]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
                break;
            case 2:
                url=Consts.TOURIST_SCAN;
                param[0]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
        }
        OkHttpNetWorkUtil.postAsyn(url, new OkHttpNetWorkUtil.ResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e(TAG,"程序有异常抛出",e);
            }

            @Override
            public void onResponse(String response)
            {
                skipHandler(serialNumberValue,identity,response);
            }
        },param);
    }

    /**
     * 扫描跳转处理
     * @param identity 身份信息
     * @param response 响应实体
     */
    private void skipHandler(final String serialNumberValue,int identity,String response)
    {
        ScanCodeBean scanCodeBean=parsescanCodeJson(response,identity);
        switch (identity)
        {
            case 0:
                if (scanCodeBean != null)
                {
                    Intent intent=null;
                    if (scanCodeBean.getType() == 0||scanCodeBean.getType()==2)
                    {
                        if (scanCodeBean.isHasAdded() == false)
                        {
                            //todo 第一次扫描药品,第一次扫描其他
                            intent=new Intent(CustomScannerActivity.this,GetCashActivity.class);
                        } else if (scanCodeBean.isHasAdded() == true)
                        {
                            // TODO: 2015/10/7 第二次扫描药品,第二次扫描其他
                            intent=new Intent(CustomScannerActivity.this,FinishRegisterActivity.class);
                        }
                    }
                    else if (scanCodeBean.getType() == 1)
                    {
                        if (scanCodeBean.isHasAdded() == false)
                        {
                            //todo 第一次扫描明信片
                        } else if (scanCodeBean.isHasAdded() == true)
                        {
                            // TODO: 2015/10/7 第二次扫描明信片
                        }
                    }
                    /*else if (scanCodeBean.getType() == 2)
                    {
                        if (scanCodeBean.isHasAdded() == false)
                        {
                            //todo 第一次扫描其他
                        } else if (scanCodeBean.isHasAdded() == true)
                        {
                            // TODO: 2015/10/7 第二次扫描其他
                        }
                    }*/
                    Bundle bundle=new Bundle();
                    bundle.putString(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
                    bundle.putParcelable(Consts.SCAN_CODE_BEAN,scanCodeBean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case 2:
                if (scanCodeBean != null)
                {
                   //todo 跳转界面
                }

        }
    }

    /**
     * 若返回数据出错，则返回空,并显示信息提示用户，否则返回{@link ScanCodeBean}
     */
    private ScanCodeBean parsescanCodeJson(String scanCodeJson,int identity)
    {
        Gson gson=new Gson();
        try
        {
            Type type=new TypeToken<ResponseObject<ScanCodeBean>>(){}.getType();
            gson.toJson(new ResponseObject<ScanCodeBean>(), type);
            ResponseObject<ScanCodeBean> responseObject=gson.fromJson(scanCodeJson,type );
            return responseObject.getData();
        } catch (JsonSyntaxException e)
        {
            e.printStackTrace();
            Type type=new TypeToken<ResponseObject<String>>(){}.getType();
            gson.toJson(new ResponseObject<String>(), type);
            ResponseObject<String> responseObject=gson.fromJson(scanCodeJson,type );
            resultExceptionHandler(identity,responseObject.getData());
            return null;
        }
    }

    /**
     * 返回数据异常处理
     * @param identity 登录身份
     * @param errorInfo 异常信息
     */
    private void resultExceptionHandler(int identity,String errorInfo)
    {
       /* switch (identity)
        {
            *//*case 0:
                displayToast("如果该二维码作用于药品\n请先让销售员先扫描！");
                break;
            case 1:
            case 2:*//*

                break;
        }*/
        if(identity==1&&errorInfo.equals("OK"))
        {
            //// TODO: 2015/10/7 销售员扫描药品成功
            return;
        }
        displayToast(errorInfo);
    }

    private void displayToast(String warnning)
    {
        Toast.makeText(this,warnning,Toast.LENGTH_SHORT).show();
    }
}
