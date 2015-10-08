package com.scau.beyondboy.idgoods.model;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-03
 * Time: 00:49
 * 产品实体
 */
public class ProductBean
{
    private String name;
    private String advertisementPhoto;
    private String advertisementName;
    private String serialNumber;
    private String dateTime;

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
}
