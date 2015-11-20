package com.scau.beyondboy.idgoods.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-06
 * Time: 10:59
 * 扫描信息实体对象
 */
@SuppressWarnings("ALL")
public class ScanCodeBean implements Parcelable
{
    private int type;
    private String name;
    private String address;
    private String discount=null;
    private String adversementName;
    private String getAdversementPhoto;
    private boolean hasAdded=false;

    public ScanCodeBean(Parcel in)
    {
        type=in.readInt();
        name=in.readString();
        address=in.readString();
        discount=in.readString();
        adversementName=in.readString();
        getAdversementPhoto=in.readString();
        hasAdded=Boolean.valueOf(in.readString());
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getDiscount()
    {
        return discount;
    }

    public void setDiscount(String discount)
    {
        this.discount = discount;
    }

    public String getAdversementName()
    {
        return adversementName;
    }

    public void setAdversementName(String adversementName)
    {
        this.adversementName = adversementName;
    }

    public String getGetAdversementPhoto()
    {
        return getAdversementPhoto;
    }

    public void setGetAdversementPhoto(String getAdversementPhoto)
    {
        this.getAdversementPhoto = getAdversementPhoto;
    }

    public boolean isHasAdded()
    {
        return hasAdded;
    }

    public void setHasAdded(boolean hasAdded)
    {
        this.hasAdded = hasAdded;
    }

    public static final Parcelable.Creator<ScanCodeBean> CREATOR = new Creator<ScanCodeBean>()
    {
        @Override
        public ScanCodeBean[] newArray(int size)
        {
            return new ScanCodeBean[size];
        }

        @Override
        public ScanCodeBean createFromParcel(Parcel in)
        {
            return new ScanCodeBean(in);
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
        dest.writeInt(type);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(discount);
        dest.writeString(adversementName);
        dest.writeString(getAdversementPhoto);
        dest.writeString(hasAdded+"");
    }

    @Override
    public String toString()
    {
        return String.format("{\"type\":\"%s\",\"name\":\"%s\",\"address\":\"%s\",\"discount\":\"%s\",\"adversementName\":\"%s\",\"getAdversementPhotoe\":\"%s\",\"hasAdded\":\"%s\"}", type,name,address,discount,adversementName,getAdversementPhoto,hasAdded);
    }
}
