package com.scau.beyondboy.idgoods.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-06
 * Time: 15:55
 * 可序列化的用户信息实体
 */
public class UserBean extends DataSupport implements Parcelable
{
    private int id;
    private String userId;
    private String account;
    private int sex;
    private Long birthday;
    private String avater;
    private String email;
    private String address;
    private String nickname;
    private String inviteCodeValue;
    private boolean isVerify;

    public UserBean()
    {
        super();
    }

    public UserBean(Parcel in)
    {
        userId=in.readString();
        account=in.readString();
        sex=in.readInt();
        birthday=in.readLong();
        avater=in.readString();
        email=in.readString();
        address=in.readString();
        nickname=in.readString();
        inviteCodeValue=in.readString();
        isVerify=Boolean.valueOf(in.readString());
    }
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getAccount()
    {
        return account;
    }

    public void setAccount(String account)
    {
        this.account = account;
    }

    public int getSex()
    {
        return sex;
    }

    public void setSex(int sex)
    {
        this.sex = sex;
    }

    public Long getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Long birthday)
    {
        this.birthday = birthday;
    }

    public String getAvater()
    {
        return avater;
    }

    public void setAvater(String avater)
    {
        this.avater = avater;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getInviteCodeValue()
    {
        return inviteCodeValue;
    }

    public void setInviteCodeValue(String inviteCodeValue)
    {
        this.inviteCodeValue = inviteCodeValue;
    }

    public boolean isVerify()
    {
        return isVerify;
    }

    public void setVerify(boolean isVerify)
    {
        this.isVerify = isVerify;
    }

    public static final Parcelable.Creator<UserBean> CREATOR = new Creator<UserBean>()
    {
        @Override
        public UserBean[] newArray(int size)
        {
            return new UserBean[size];
        }

        @Override
        public UserBean createFromParcel(Parcel in)
        {
            return new UserBean(in);
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(userId);
        dest.writeString(account);
        dest.writeInt(sex);
        dest.writeLong(birthday);
        dest.writeString(avater);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(nickname);
        dest.writeString(inviteCodeValue);
        dest.writeString(isVerify+"");
    }
}
