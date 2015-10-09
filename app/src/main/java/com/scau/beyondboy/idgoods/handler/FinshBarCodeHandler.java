package com.scau.beyondboy.idgoods.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.scau.beyondboy.idgoods.FinishRegisterActivity;
import com.scau.beyondboy.idgoods.GetCashActivity;
import com.scau.beyondboy.idgoods.Vendibility;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.squareup.okhttp.Request;

import java.lang.reflect.Type;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-09
 * Time: 15:14
 * 二维码序列号处理
 */
final public class FinshBarCodeHandler
{
    private static final String TAG = FinshBarCodeHandler.class.getName();
    private static String serialNumberValue;
    private static Context mContext;
    private static int identity=0;
    /**二维码扫描输入方式，0代表手动输入，1代表二维码扫描输入*/
    private static int InputBarCodeWay=0;

    /**初始化值*/
    public static void init(String serialNumber, Context context,int InputBarWay)
    {
        serialNumberValue = serialNumber;
        mContext = context;
        InputBarCodeWay=InputBarWay;
    }

    /**扫完二维码处理*/
    public static void finishScanHandler()
    {
        Log.i(TAG,"邀请码： "+ShareUtils.getInviteCodeValue(mContext));
        //用户登录
        if(ShareUtils.getUserId(mContext)!=null&&ShareUtils.getInviteCodeValue(mContext)==null)
        {
            Log.i(TAG,"用户登录"+ShareUtils.getInviteCodeValue(mContext));
            identity=0;
        }
        //销售员登录
        else if(ShareUtils.getUserId(mContext)!=null&&ShareUtils.getInviteCodeValue(mContext)!=null)
        {
            identity=1;
            Log.i(TAG,"销售员登录");
        }
        else if(ShareUtils.getUserId(mContext)==null)
        {
            identity=2;
        }
        barCodeVerifyHandler(identity);
    }

    /**二维码序列号认证处理*/
    private static void barCodeVerifyHandler(final int identity)
    {
        String url=null;
        OkHttpNetWorkUtil.Param params[]=new OkHttpNetWorkUtil.Param[2];
        switch (identity)
        {
            case 0:
                url= Consts.CUSTOMER_SCAN;
                params[0]=new OkHttpNetWorkUtil.Param(Consts.CUSTOMERID_KEY,ShareUtils.getUserId(mContext));
                params[1]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
                break;
            case 1:
                url=Consts.SELLER_SCAN;
                params[0]=new OkHttpNetWorkUtil.Param(Consts.SELLERID_KEY,ShareUtils.getUserId(mContext));
                params[1]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
                break;
            case 2:
                url=Consts.TOURIST_SCAN;
                params[0]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
        }
        OkHttpNetWorkUtil.postAsyn(url, new OkHttpNetWorkUtil.ResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e(TAG, "程序有异常抛出", e);
                displayToast("网络异常");
            }

            @Override
            public void onResponse(String response)
            {
                skipHandler(identity,response);
            }
        },params);
    }

    /**
     * 扫描跳转处理
     * @param identity 身份信息
     * @param response 响应实体
     */
    public static void skipHandler(int identity,String response)
    {
        ScanCodeBean scanCodeBean=null;
        if(identity!=1)
        {
            scanCodeBean=parsescanCodeJson(response,identity);
        }
        else if(identity==1)
        {
            Gson gson=new Gson();
            Type type=new TypeToken<ResponseObject<String>>(){}.getType();
            gson.toJson(new ResponseObject<String>(), type);
            ResponseObject<String> responseObject=gson.fromJson(response,type );
            // TODO: 2015/10/7 销售员扫描药品成功
            if(responseObject.getResult()==1)
            {
                Log.i(TAG,"销售员成功扫描");
                Intent intent=new Intent(mContext,Vendibility.class);
                Bundle bundle=new Bundle();
                bundle.putString(Consts.SERIALNUMBERVALUEKEY, serialNumberValue);
                mContext.startActivity(intent);
                ((AppCompatActivity)mContext).finish();
            }
            else
            {
                displayToast(responseObject.getData());
            }
        }
        if(scanCodeBean!=null)
        {
            switch (identity)
            {
                //销售员
                case 0:
                    Intent intent = null;
                    if (scanCodeBean.getType() == 0 || scanCodeBean.getType() == 2)
                    {
                        if (scanCodeBean.isHasAdded() == false)
                        {
                            //todo 第一次扫描药品,第一次扫描其他
                            intent = new Intent(mContext, GetCashActivity.class);
                        } else if (scanCodeBean.isHasAdded() == true)
                        {
                            // TODO: 2015/10/7 第二次扫描药品,第二次扫描其他
                            intent = new Intent(mContext, FinishRegisterActivity.class);
                        }
                    } else if (scanCodeBean.getType() == 1)
                    {
                        if (scanCodeBean.isHasAdded() == false)
                        {
                            //todo 第一次扫描明信片
                        } else if (scanCodeBean.isHasAdded() == true)
                        {
                            // TODO: 2015/10/7 第二次扫描明信片
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.SERIALNUMBERVALUEKEY, serialNumberValue);
                    Log.i(TAG,"浏览扫描成功数据： "+scanCodeBean);
                    bundle.putParcelable(Consts.SCAN_CODE_BEAN, scanCodeBean);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    break;
                //游客
                case 2:
                    //todo 跳转界面
                    break;
            }
            if(InputBarCodeWay==1)
            {
                ((AppCompatActivity)mContext).finish();
            }
        }
    }

    /**
     * 若返回数据出错，则返回空,并显示信息提示用户，否则返回{@link ScanCodeBean}
     */
    private static ScanCodeBean parsescanCodeJson(String scanCodeJson,int identity)
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
    private static void resultExceptionHandler(int identity,String errorInfo)
    {
        displayToast(errorInfo);
    }

    private static void displayToast(String warnning)
    {
        Toast.makeText(mContext, warnning, Toast.LENGTH_SHORT).show();
    }
}
