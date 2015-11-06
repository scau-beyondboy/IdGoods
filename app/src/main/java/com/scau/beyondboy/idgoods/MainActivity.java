package com.scau.beyondboy.idgoods;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.fragment.FragmentCollect;
import com.scau.beyondboy.idgoods.fragment.FragmentGetCash;
import com.scau.beyondboy.idgoods.fragment.FragmentHome;
import com.scau.beyondboy.idgoods.fragment.FragmentLogin;
import com.scau.beyondboy.idgoods.fragment.FragmentModifyPassword;
import com.scau.beyondboy.idgoods.fragment.FragmentPlay;
import com.scau.beyondboy.idgoods.fragment.FragmentProduct;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.UserBean;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity
{
   // private static final String TAG = MainActivity.class.getName();
    @Bind(R.id.title_content)
    TextView titleContent;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
//    @Bind(R.id.menu_toggle)
//    ImageView toggleImageView;
//    @Bind(R.id.menu_search)
//    public SearchView mSearchView;
    @Bind(R.id.changesetting)
    TextView changeSetting;
    private FragmentManager mFragmentManager;
    @Bind(R.id.user_name)
    TextView userName;
    private UserBean mUserBean;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mFragmentManager=getSupportFragmentManager();
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.open, R.string.close)
        {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                View content = mDrawerLayout.getChildAt(0);
                View menu = mDrawerLayout.getChildAt(1);
                float slideDistance = menu.getWidth() * slideOffset;
                //平移主界面内容布局
                content.setTranslationX(slideDistance);
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive() && (MainActivity.this.getCurrentFocus() != null))
                {
                    imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);
        changeFragment(new FragmentHome(), true,"home");
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(ShareUtils.getAccount()!=null&&ShareUtils.getPassword()!=null)
        {
            changeSetting.setText("设置");
        }
        //开启线程更改头像名
        ThreadManager.addTask(new Runnable()
        {
            @Override
            public void run()
            {
                if (ShareUtils.getAccount() != null)
                {
                    List<UserBean> userBeans = DataSupport.where("account=?", ShareUtils.getAccount()).find(UserBean.class);
                    if (userBeans.size() != 0)
                    {
                        mUserBean = userBeans.get(0);
                        userName.setText(mUserBean.getNickname());
                    } else
                    {
                        userName.setText("未登陆");
                    }
                } else
                {
                    userName.setText("未登陆");
                }
            }
        });
    }

    @Override
    public void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
    }

    @OnClick(R.id.menu_toggle)
    public void menuToggle()
    {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }
    @OnClick({R.id.home,R.id.myproduct,R.id.mycollect,R.id.setting,R.id.header_image})
    public void onClick(View view)
    {
        boolean isDrawerOpen=mDrawerLayout.isDrawerOpen(GravityCompat.START);
        if(isDrawerOpen)
            mDrawerLayout.closeDrawer(GravityCompat.START);
        switch (view.getId())
        {
            case R.id.home:
                changeFragment(new FragmentHome(),true,"home");
                break;
            case R.id.myproduct:
                if(ShareUtils.getAccount(this)==null&&ShareUtils.getPassword(this)==null)
                {
                    ToaskUtils.displayToast("请登录你的账号");
                }
                else
                {
                    changeFragment(new FragmentProduct(), true,"product");
                }
                break;
            case R.id.setting:
                if(changeSetting.getText().toString().equals("登陆"))
                {
                    changeFragment(new FragmentLogin(),true,"login");
                }
                else
                {
                    changeFragment(new FragmentModifyPassword(),true,"modifypassword");
                    titleContent.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.header_image:
                if(ShareUtils.getAccount(this)==null&&ShareUtils.getPassword(this)==null)
                {
                    ToaskUtils.displayToast("请登录你的账号");
                }
                else
                {
                    startActivity(new Intent(this,PersonInfoActivity.class));
                }
                break;
            case R.id.mycollect:
                if(ShareUtils.getAccount(this)==null&&ShareUtils.getPassword(this)==null)
                {
                    ToaskUtils.displayToast("请登录你的账号");
                }
                else
                {
                    changeFragment(new FragmentCollect(), true,"collect");
                }
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if(intent.getBooleanExtra(Consts.USERS_SIGNUP,false)||intent.getBooleanExtra(Consts.FINISHREGISTER,false))
        {
            changeSetting.setText("设置");
            changeFragment(new FragmentHome(),true,"home");
        }
        else if(intent.getBooleanExtra(Consts.GET_DIS_COUNT,false))
        {
            FragmentGetCash fragmentGetCash=new FragmentGetCash();
            fragmentGetCash.setArguments(intent.getExtras());
            changeFragment(fragmentGetCash,true,"getcash");
        }
        else if(intent.getBooleanExtra(Consts.FRAGMENT_PLAY,false))
        {
            FragmentPlay fragmentPlay=new FragmentPlay();
            fragmentPlay.setArguments(intent.getExtras());
            changeFragment(fragmentPlay,true,"play");
        }
        /*else if(intent.getBooleanExtra(Consts.FRAGMENT_LISTEN,false))
        {
           *//* ListenBlessActivity fragmentListen=new ListenBlessActivity();
            fragmentListen.setArguments(intent.getExtras());
            changeFragment(fragmentListen, true);*//*
        }*/
    }

    public void setChangeSetting(String content)
    {
        changeSetting.setText(content);
    }

    //切换不同的fragment
    public void changeFragment(Fragment fragment,boolean isAddToStack,String tag)
    {
        if(mFragmentManager.findFragmentByTag(tag)!=null)
        {
            return;
        }
        //开启事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment,tag);
        //加入后退栈
        if (!isAddToStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public TextView getTitleContent()
    {
        return titleContent;
    }

    public void setNickName(String nickName)
    {
        userName.setText(nickName);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
