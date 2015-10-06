package com.scau.beyondboy.idgoods.model;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-06
 * Time: 10:59
 * 扫描信息实体对象
 */
public class ScanCodeBean
{
    private int type;
    private String name;
    private String address;
    private String discount;
    private String adversementName;
    private String getAdversementPhoto;
    private boolean hasAdded=false;

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
}
