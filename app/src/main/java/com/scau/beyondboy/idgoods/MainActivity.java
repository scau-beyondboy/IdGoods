package com.scau.beyondboy.idgoods;

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

import com.scau.beyondboy.idgoods.fragment.FragmentHome;
import com.scau.beyondboy.idgoods.fragment.FragmentProduct;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity
{
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.menu_toggle)
    ImageView toggleImageView;
    @Bind(R.id.menu_search)
    public SearchView mSearchView;
    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle mDrawerToggle;
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
    }
    @OnClick(R.id.menu_toggle)
    public void menuToggle()
    {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }
    @OnClick({R.id.home,R.id.myproduct,R.id.mycollect,R.id.setting})
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
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
    private void changeFragment(Fragment fragment,boolean isAddToStack)
    {
        //开启事务
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        if (!isAddToStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
