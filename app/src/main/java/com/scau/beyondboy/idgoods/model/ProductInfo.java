package com.scau.beyondboy.idgoods.model;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-06
 * Time: 01:03
 * 用户详情界面
 */
@SuppressWarnings("ALL")
public class ProductInfo
{
    private String advertisementName;
    private String advertisementPhoto;
    private int discount;
    private String name;
    private String serialNumber;
    private String time;

    public int getDiscount()
    {
        return discount;
    }

    public void setDiscount(int discount)
    {
        this.discount = discount;
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

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

}
