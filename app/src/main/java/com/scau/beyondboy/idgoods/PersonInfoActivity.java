package com.scau.beyondboy.idgoods;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.fragment.FragmentBirthday;
import com.scau.beyondboy.idgoods.fragment.FragmentSex;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.UserBean;
import com.scau.beyondboy.idgoods.utils.NetworkUtils;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ParseJsonUtils;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;
import com.squareup.okhttp.Request;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-11
 * Time: 23:41
 * 设置个人信息
 */
public class PersonInfoActivity extends AppCompatActivity
{
    //private static final String TAG = PersonInfoActivity.class.getName();
    @Bind(R.id.nickname)
    TextView nickName;
    @Bind(R.id.email)
    TextView email;
    @Bind(R.id.birthday)
    TextView birthday;
    @Bind(R.id.sex)
    TextView sex;
    @Bind(R.id.address)
    TextView address;
    private UserBean mUserBean;
    private FragmentManager mManager;
    private Date mDate;
    private int whichsex;
    private boolean isFinished=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        ButterKnife.bind(this);
        mManager=getSupportFragmentManager();
        ThreadManager.scoolPoolSize=1;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @MainThread
    private void setInfo(UserBean userBean)
    {
        mUserBean=userBean;
        nickName.setText(mUserBean.getNickname());
        email.setText(mUserBean.getEmail());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
        mDate = new Date();
        mDate.setTime(mUserBean.getBirthday());
        birthday.setText(simpleDateFormat.format(mDate));
        if(mUserBean.getSex()==0)
            sex.setText("未知性别");
        else if(mUserBean.getSex()==1)
            sex.setText("男性");
        else
            sex.setText("女性");
        address.setText(mUserBean.getAddress());
        whichsex=mUserBean.getSex();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        //开启线程防止阻塞主界面
        ThreadManager.addTask(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final List<UserBean> userBeans = DataSupport.where("account=?", ShareUtils.getAccount()).find(UserBean.class);
                    if (userBeans.size() != 0)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                setInfo(userBeans.get(0));
                            }
                        });
                        isFinished = true;
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.personinfo_back,R.id.nickname_layout,R.id.email_layout,R.id.sex_layout,R.id.birthday_layout,R.id.address_layout})
    public void skipSetting(View view)
    {
        if(!isFinished)
        {
            ToaskUtils.displayToast("个人加载还没完成");
            return;
        }
        Intent intent=new Intent();
        switch (view.getId())
        {
            case R.id.nickname_layout:
                intent.setClass(this,ChangeNickNameActivity.class);
                intent.putExtra(Consts.NICKNAME_KEY,mUserBean.getNickname());
                startActivity(intent);
                break;
            case R.id.email_layout:
                intent.setClass(this, ChangeEmailActivity.class);
                intent.putExtra(Consts.EMAIL_KEY,mUserBean.getEmail());
                startActivity(intent);
                break;
            case R.id.address_layout:
                intent.setClass(this,ChangeAddressActivity.class);
                intent.putExtra(Consts.ADDRESS_KEY,mUserBean.getAddress());
                startActivity(intent);
                break;
            case R.id.birthday_layout:
                FragmentBirthday datePicker= FragmentBirthday.newInstance(mDate);
                datePicker.show(mManager, Consts.DATE);
                break;
            case R.id.sex_layout:
                FragmentSex sex=FragmentSex.newInstance(whichsex);
                sex.show(mManager,Consts.SEX_KEY);
                break;
            case R.id.personinfo_back:
                finish();
                break;
        }
    }


    public void setSex(String chooseSex)
    {
        sex.setText(chooseSex);
    }

    public void setDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        birthday.setText(simpleDateFormat.format(date));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ThreadManager.release();
    }

    public static  void changeInfo(final ContentValues values, final String changeKey, final String changeValue, final AppCompatActivity activity)
    {
        if(!NetworkUtils.isNetworkReachable())
        {
            ToaskUtils.displayToast("请设置网络");
        }
        else if(StringUtils.isEmpty(changeValue))
        {
            ToaskUtils.displayToast("不能为空");
        }
        else
        {
            OkHttpNetWorkUtil.postAsyn(Consts.UPDATE_INFO, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
            {
                @Override
                public void onError(Request request, Exception e)
                {
                    e.printStackTrace();
                    ToaskUtils.displayToast("网络异常");
                }

                @Override
                public void onResponse(final ResponseObject<Object> response)
                {
                    ThreadManager.addTask(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //更新数据库
                            DataSupport.updateAll(UserBean.class, values, "account=?", ShareUtils.getAccount());
                            if(activity!=null)
                                activity.finish();
                        }
                    });
                    ParseJsonUtils.parseDataJson(response, "修改成功");
                }
            }, new OkHttpNetWorkUtil.Param(Consts.USERID_KEY, ShareUtils.getUserId()), new OkHttpNetWorkUtil.Param(changeKey,changeValue));
        }
    }

}
