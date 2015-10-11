package com.scau.beyondboy.idgoods.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-03
 * Time: 00:49
 * 产品实体
 */
public class ProductBean extends DataSupport implements Parcelable
{
    private static final String TAG = ProductBean.class.getName();
    private TimeProductBean timeProductBean;
    private String name;
    private String advertisementPhoto;
    private String advertisementName;
    private String serialNumber;
    private String dateTime;

    public ProductBean()
    {

    }

    public ProductBean(Parcel in)
    {
        name=in.readString();
        advertisementPhoto=in.readString();
        advertisementName=in.readString();
        serialNumber=in.readString();
        dateTime=in.readString();
    }
    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
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

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString()
    {
        return String.format("{\"name\":\"%s\",\"advertisementPhoto\":\"%s\",\"advertisementName\":\"%s\",\"serialNumber\":\"%s\"}",name,advertisementPhoto,advertisementName,serialNumber);
    }

    public TimeProductBean getTimeProductBean()
    {
        return timeProductBean;
    }

    public void setTimeProductBean(TimeProductBean timeProductBean)
    {
        this.timeProductBean = timeProductBean;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductBean that = (ProductBean) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(serialNumber != null ? !serialNumber.equals(that.serialNumber) : that.serialNumber != null);

    }

    public static final Parcelable.Creator<ProductBean> CREATOR = new Creator<ProductBean>()
    {
        @Override
        public ProductBean[] newArray(int size)
        {
            return new ProductBean[size];
        }

        @Override
        public ProductBean createFromParcel(Parcel in)
        {
            return new ProductBean(in);
        }
    };

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (serialNumber != null ? serialNumber.hashCode() : 0);
        return result;
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
        dest.writeString(dateTime);
    }
}
