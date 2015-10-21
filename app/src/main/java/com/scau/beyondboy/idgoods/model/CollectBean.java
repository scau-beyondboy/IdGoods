package com.scau.beyondboy.idgoods.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-21
 * Time: 08:41
 * 收藏信息列表
 */
public class CollectBean extends DataSupport implements Parcelable
{
    private String name;
    private String advertisementPhoto;
    private String advertisementName;
    private String serialNumberValue;
    private String dateTime;

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public CollectBean()
    {

    }

    public CollectBean(Parcel in)
    {
        name=in.readString();
        advertisementPhoto=in.readString();
        advertisementName=in.readString();
        serialNumberValue=in.readString();
        dateTime=in.readString();
    }
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAdvertisementPhoto()
    {
        return advertisementPhoto;
    }

    public void setAdvertisementPhoto(String advertisementPhoto)
    {
        this.advertisementPhoto = advertisementPhoto;
    }

    public String getAdvertisementName()
    {
        return advertisementName;
    }

    public void setAdvertisementName(String advertisementName)
    {
        this.advertisementName = advertisementName;
    }

    public String getSerialNumberValue()
    {
        return serialNumberValue;
    }

    public void setSerialNumberValue(String serialNumberValue)
    {
        this.serialNumberValue = serialNumberValue;
    }

    public static final Parcelable.Creator<CollectBean> CREATOR = new Creator<CollectBean>()
    {
        @Override
        public CollectBean[] newArray(int size)
        {
            return new CollectBean[size];
        }

        @Override
        public CollectBean createFromParcel(Parcel in)
        {
            return new CollectBean(in);
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
        dest.writeString(name);
        dest.writeString(advertisementPhoto);
        dest.writeString(advertisementName);
        dest.writeString(serialNumberValue);
        dest.writeString(dateTime);
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (serialNumberValue != null ? serialNumberValue.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectBean that = (CollectBean) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(serialNumberValue != null ? !serialNumberValue.equals(that.serialNumberValue) : that.serialNumberValue != null);

    }
}
