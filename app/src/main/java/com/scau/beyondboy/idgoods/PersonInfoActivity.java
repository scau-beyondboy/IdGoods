package com.scau.beyondboy.idgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.fragment.FragmentDatePicker;
import com.scau.beyondboy.idgoods.fragment.FragmentSex;
import com.scau.beyondboy.idgoods.model.UserBean;
import com.scau.beyondboy.idgoods.utils.ShareUtils;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        List<UserBean> userBeans=DataSupport.where("account=?", ShareUtils.getAccount(this)).find(UserBean.class);
        if(userBeans.size()!=0)
        {
            mUserBean=userBeans.get(0);
        }
        ButterKnife.bind(this);
        nickName.setText(mUserBean.getNickname());
        email.setText(mUserBean.getEmail());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
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
        mManager=getSupportFragmentManager();
        whichsex=mUserBean.getSex();
    }

    @OnClick({R.id.personinfo_back,R.id.nickname_layout,R.id.email_layout,R.id.sex_layout,R.id.birthday_layout,R.id.address_layout})
    public void skipSetting(View view)
    {
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
                FragmentDatePicker datePicker=FragmentDatePicker.newInstance(mDate);
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

    private void displayToast(String warnning)
    {
        Toast.makeText(this, warnning, Toast.LENGTH_SHORT).show();
    }

    public void setSex(String chooseSex)
    {
        sex.setText(chooseSex);
    }

    public void setDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        birthday.setText(simpleDateFormat.format(date));
    }
}
