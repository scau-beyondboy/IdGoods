package com.scau.beyondboy.idgoods.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scau.beyondboy.idgoods.BlessingActivity;
import com.scau.beyondboy.idgoods.FinishRegisterActivity;
import com.scau.beyondboy.idgoods.fragment.FragmentGetCash;
import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.Vendibility;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.fragment.FragmentListen;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.squareup.okhttp.Request;

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
            Log.i(TAG,"游客登陆");
        }
        barCodeVerifyHandler(identity);
    }

    /**二维码序列号认证处理*/
    private static void barCodeVerifyHandler(final int identity)
    {
        String url=null;
        OkHttpNetWorkUtil.Param params[]=null;
        switch (identity)
        {
            case 0:
                params=new OkHttpNetWorkUtil.Param[2];
                url= Consts.CUSTOMER_SCAN;
                params[0]=new OkHttpNetWorkUtil.Param(Consts.CUSTOMERID_KEY,ShareUtils.getUserId(mContext));
                params[1]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
                break;
            case 1:
                params=new OkHttpNetWorkUtil.Param[2];
                url=Consts.SELLER_SCAN;
                params[0]=new OkHttpNetWorkUtil.Param(Consts.SELLERID_KEY,ShareUtils.getUserId(mContext));
                params[1]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
                break;
            case 2:
                params=new OkHttpNetWorkUtil.Param[1];
                url=Consts.TOURIST_SCAN;
                params[0]=new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
        }
        OkHttpNetWorkUtil.postAsyn(url, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e(TAG, "程序有异常抛出", e);
                displayToast("网络异常");
            }

            @Override
            public void onResponse(ResponseObject<Object> response)
            {
                skipHandler(identity, response);
            }
        }, params);
    }

    /**
     * 扫描跳转处理
     * @param identity 身份信息
     * @param response 响应实体
     */
    public static void skipHandler(int identity,ResponseObject<Object> response)
    {
        ScanCodeBean scanCodeBean=null;
        scanCodeBean=parsescanCodeJson(response,identity);
        if(scanCodeBean!=null)
        {
            Bundle bundle = new Bundle();
            //bundle.putBoolean(Consts.GET_DIS_COUNT, true);
            bundle.putString(Consts.SERIALNUMBERVALUEKEY, serialNumberValue);
            bundle.putParcelable(Consts.SCAN_CODE_BEAN, scanCodeBean);
            Intent intent = null;
            switch (identity)
            {
                //普通用户
                case 0:
                    if (scanCodeBean.getType() == 0 || scanCodeBean.getType() == 2)
                    {
                        if (scanCodeBean.isHasAdded() == false)
                        {
                            //todo 第一次扫描药品,第一次扫描其他
                            if(InputBarCodeWay==0)
                            {
                                Log.i(TAG, "到这里吗？");
                                FragmentGetCash fragmentGetCash=new FragmentGetCash();
                                fragmentGetCash.setArguments(bundle);
                                ((MainActivity) mContext).changeFragment(fragmentGetCash, true);
                                return;
                            }
                            else
                            {
                                bundle.putBoolean(Consts.GET_DIS_COUNT, true);
                                intent = new Intent(mContext, MainActivity.class);
                            }
                        } else if (scanCodeBean.isHasAdded() == true)
                        {
                            // TODO: 2015/10/7 第二次扫描药品,第二次扫描其他
                            intent = new Intent(mContext, FinishRegisterActivity.class);
                        }
                    }
                    else if (scanCodeBean.getType() == 1)
                    {
                        if (scanCodeBean.getAddress() ==null)
                        {
                            //todo 第一次扫描明信片
                            intent=new Intent(mContext, BlessingActivity.class);
                        }
                        else
                        {
                            //第二次扫描明信片,没加入收藏列表时
                            if(scanCodeBean.isHasAdded()==false)
                            {
                                ArrayMap<String,String> params=new ArrayMap<>(2);
                                params.put(Consts.CUSTOMERID_KEY,ShareUtils.getUserId(mContext));
                                params.put(Consts.SERIALNUMBERVALUEKEY,serialNumberValue);
                                OkHttpNetWorkUtil.postAsyn(Consts.ADD_COLLECT, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
                                {
                                    @Override
                                    public void onError(Request request, Exception e)
                                    {
                                        e.printStackTrace();
                                        displayToast("网络异常");
                                    }

                                    @Override
                                    public void onResponse(ResponseObject<Object> response)
                                    {
                                        Gson gson = new Gson();
                                        String data = gson.toJson(response.getData());
                                        if (response.getResult() == 1)
                                        {
                                            displayToast("收藏成功");
                                        } else
                                        {
                                            displayToast(data);
                                        }
                                    }
                                },params);
                            }
                            secondScanPostCard(intent,bundle);
                            return;
                            // TODO: 2015/10/7
                        }
                    }
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    break;
                //游客
                case 2:
                    //todo 跳转界面
                    secondScanPostCard(intent, bundle);
                    return;
            }
            // 二维码扫描时，要销毁该Activity实例
            if(InputBarCodeWay==1)
            {
                ((AppCompatActivity)mContext).finish();
            }
        }
    }

    /**
     * 若返回数据出错，则返回空,并显示信息提示用户，否则返回{@link ScanCodeBean}
     */
    private static ScanCodeBean parsescanCodeJson(ResponseObject<Object> responseObject,int identity)
    {
        Gson gson=new Gson();
        String data=gson.toJson(responseObject.getData());
        //销售员扫描成功或其他身份用户扫描不成功
        if(responseObject.getResult()!=1||(responseObject.getResult()==1&&identity==1))
        {
            if(responseObject.getResult()==1)
            {
                Log.i(TAG,"销售员扫描成功");
                Intent intent=new Intent(mContext,Vendibility.class);
                Bundle bundle=new Bundle();
                bundle.putString(Consts.SERIALNUMBERVALUEKEY, serialNumberValue);
                mContext.startActivity(intent);
                // 二维码扫描时，要销毁该Activity实例
                if(InputBarCodeWay==1)
                {
                    ((AppCompatActivity)mContext).finish();
                }
               // ((AppCompatActivity)mContext).finish();
            }
            else
            {
                displayToast(data);
            }
            return null;
        }
        else
        {
            return gson.fromJson(data,ScanCodeBean.class);
        }
    }

    private static void displayToast(String warnning)
    {
        Toast.makeText(mContext, warnning, Toast.LENGTH_SHORT).show();
    }

    /**第二次扫描跳转处理*/
    private static void secondScanPostCard(Intent intent, Bundle bundle)
    {
        if(InputBarCodeWay==0)
        {
            FragmentListen fragmentListen=new FragmentListen();
            fragmentListen.setArguments(bundle);
            ((MainActivity) mContext).changeFragment(fragmentListen, true);
            return;
        }
        intent=new Intent(mContext,MainActivity.class);
        intent.putExtra(Consts.FRAGMENT_LISTEN,true);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
}
