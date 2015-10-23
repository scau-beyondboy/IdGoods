package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.fragment.FragmentCollect;
import com.scau.beyondboy.idgoods.fragment.FragmentGetCash;
import com.scau.beyondboy.idgoods.fragment.FragmentHome;
import com.scau.beyondboy.idgoods.fragment.FragmentListen;
import com.scau.beyondboy.idgoods.fragment.FragmentLogin;
import com.scau.beyondboy.idgoods.fragment.FragmentModifyPassword;
import com.scau.beyondboy.idgoods.fragment.FragmentPlay;
import com.scau.beyondboy.idgoods.fragment.FragmentProduct;
import com.scau.beyondboy.idgoods.model.UserBean;
import com.scau.beyondboy.idgoods.utils.ShareUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity
{
    private static final String TAG = MainActivity.class.getName();
    @Bind(R.id.title_content)
    TextView titleContent;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.menu_toggle)
    ImageView toggleImageView;
    @Bind(R.id.menu_search)
    public SearchView mSearchView;
    @Bind(R.id.changesetting)
    TextView changeSetting;
    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle mDrawerToggle;
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
        mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,null,R.string.open,R.string.close)
        {

            @Override
            public void onDrawerClosed(View drawerView)
            {
                toggleImageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                View content=mDrawerLayout.getChildAt(0);
                View menu=mDrawerLayout.getChildAt(1);
                float slideDistance=menu.getWidth()*slideOffset;
                //平移主界面内容布局
                content.setTranslationX(slideDistance);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        changeFragment(new FragmentHome(), true);
        if(ShareUtils.getAccount(this)!=null)
        {
            List<UserBean> userBeans= DataSupport.where("account=?", ShareUtils.getAccount(this)).find(UserBean.class);
            if(userBeans.size()!=0)
            {
                Log.i(TAG,"有Id");
                mUserBean =userBeans.get(0);
                userName.setText(mUserBean.getNickname());
            }
            else
            {
                userName.setText("未登陆");
            }
        }
        else
        {
            userName.setText("未登陆");
        }
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
               // Log.i(TAG,"数据：  "+ShareUtils.getAccount(this)+"      "+ShareUtils.getPassword(this));
                changeFragment(new FragmentHome(),true);
                break;
            case R.id.myproduct:
               // Log.i(TAG, "数据：  " + ShareUtils.getAccount(this) + "      " + ShareUtils.getPassword(this));
                if(ShareUtils.getAccount(this)==null&&ShareUtils.getPassword(this)==null)
                {
                    displayToast("请登录你的账号");
                }
                else
                {
                    changeFragment(new FragmentProduct(), true);
                }
                break;
            case R.id.setting:
                Log.i(TAG,"数据：  "+ShareUtils.getAccount(this)+"      "+ShareUtils.getPassword(this));
                if(changeSetting.getText().toString().equals("登陆"))
                {
                    changeFragment(new FragmentLogin(),true);
                }
                else
                {
                    changeFragment(new FragmentModifyPassword(),true);
                    titleContent.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.header_image:
                if(ShareUtils.getAccount(this)==null&&ShareUtils.getPassword(this)==null)
                {
                    displayToast("请登录你的账号");
                }
               // Log.i(TAG,"数据：  "+ShareUtils.getAccount(this)+"      "+ShareUtils.getPassword(this));
                else
                {
                    startActivity(new Intent(this,PersonInfoActivity.class));
                }
                break;
            case R.id.mycollect:
                if(ShareUtils.getAccount(this)==null&&ShareUtils.getPassword(this)==null)
                {
                    displayToast("请登录你的账号");
                }
                else
                {
                    changeFragment(new FragmentCollect(), true);
                }
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Log.i(TAG, "真假：  " + intent.getBooleanExtra(Consts.GET_DIS_COUNT, false));
        super.onNewIntent(intent);
        if(intent.getBooleanExtra(Consts.USERS_SIGNUP,false)||intent.getBooleanExtra(Consts.FINISHREGISTER,false))
        {
            changeSetting.setText("设置");
            changeFragment(new FragmentHome(),true);
        }
        else if(intent.getBooleanExtra(Consts.GET_DIS_COUNT,false))
        {
            FragmentGetCash fragmentGetCash=new FragmentGetCash();
            fragmentGetCash.setArguments(intent.getExtras());
            changeFragment(fragmentGetCash,true);
        }
        else if(intent.getBooleanExtra(Consts.FRAGMENT_PLAY,false))
        {
            FragmentPlay fragmentPlay=new FragmentPlay();
            fragmentPlay.setArguments(intent.getExtras());
            changeFragment(fragmentPlay,true);
        }
        else if(intent.getBooleanExtra(Consts.FRAGMENT_LISTEN,false))
        {
            FragmentListen fragmentListen=new FragmentListen();
            fragmentListen.setArguments(intent.getExtras());
            changeFragment(fragmentListen, true);
        }
    }

    public void setChangeSetting(String content)
    {
        changeSetting.setText(content);
    }

    //切换不同的fragment
    public void changeFragment(Fragment fragment,boolean isAddToStack)
    {
        //开启事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
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

    private void displayToast(String warnning)
    {
        Toast.makeText(this, warnning, Toast.LENGTH_SHORT).show();
    }
}
