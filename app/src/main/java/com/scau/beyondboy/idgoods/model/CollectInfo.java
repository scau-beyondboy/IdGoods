package com.scau.beyondboy.idgoods.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-21
 * Time: 08:48
 *  收藏列表详情信息
 */
@SuppressWarnings("ALL")
public class CollectInfo extends DataSupport implements Parcelable
{
    private String name;
    private String advertisementPhoto;
    private String advertisementName;
    private String serialNumber;
    private String time;
    private String radioAddress;

    public String getName()
    {
        return name;
    }

    public CollectInfo(Parcel in)
    {
        name=in.readString();
        advertisementPhoto=in.readString();
        advertisementName=in.readString();
        serialNumber=in.readString();
        time=in.readString();
        radioAddress=in.readString();
    }

    public CollectInfo()
    {

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

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getRadioAddress()
    {
        return radioAddress;
    }

    public void setRadioAddress(String radioAddress)
    {
        this.radioAddress = radioAddress;
    }

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
        dest.writeString(serialNumber);
        dest.writeString(time);
        dest.writeString(radioAddress);
    }

    public static final Parcelable.Creator<CollectInfo> CREATOR = new Creator<CollectInfo>()
    {
        @Override
        public CollectInfo[] newArray(int size)
        {
            return new CollectInfo[size];
        }

        @Override
        public CollectInfo createFromParcel(Parcel in)
        {
            return new CollectInfo(in);
        }
    };
}
