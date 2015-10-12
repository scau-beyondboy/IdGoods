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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.fragment.FragmentHome;
import com.scau.beyondboy.idgoods.fragment.FragmentLogin;
import com.scau.beyondboy.idgoods.fragment.FragmentModifyPassword;
import com.scau.beyondboy.idgoods.fragment.FragmentProduct;
import com.scau.beyondboy.idgoods.model.UserBean;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity
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
            public void onDrawerOpened(View drawerView)
            {
                toggleImageView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                toggleImageView.setVisibility(View.VISIBLE);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        changeFragment(new FragmentHome(), true);
       /* List<UserBean> userBeans= DataSupport.where("account=?", ShareUtils.getAccount(this)).find(UserBean.class);
        if(userBeans.size()!=0)
        {
            mUserBean =userBeans.get(0);
        }
        userName.setText(mUserBean.getNickname());*/
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
                changeFragment(new FragmentHome(),true);
                break;
            case R.id.myproduct:
                changeFragment(new FragmentProduct(), true);
                break;
            case R.id.setting:
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
                startActivity(new Intent(this,PersonInfoActivity.class));
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if(intent.getBooleanExtra(Consts.USERS_SIGNUP,false))
        {
            changeSetting.setText("设置");
            changeFragment(new FragmentHome(),true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void setChangeSetting(String content)
    {
        changeSetting.setText(content);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
