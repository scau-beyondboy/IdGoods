package com.scau.beyondboy.idgoods.model;

import org.litepal.crud.DataSupport;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-06
 * Time: 15:55
 * 用户信息实体
 */
public class UserBean extends DataSupport
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

    public void setIsVerify(boolean isVerify)
    {
        this.isVerify = isVerify;
    }
}
