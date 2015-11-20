package com.scau.beyondboy.idgoods.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-18
 * Time: 01:06
 * 存储音频信息
 */
@SuppressWarnings("ALL")
public class MediaBean implements Parcelable
{
    private short mChannelInMono;
    private int mAudioformat;
    private int mSamplerateinhz;
    private int bufferSize;
    private String date;
    private String filePath;

    public MediaBean()
    {

    }
    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public MediaBean(Parcel in)
    {
        mChannelInMono=(short)in.readInt();
        mAudioformat=in.readInt();
        mSamplerateinhz=in.readInt();
        bufferSize=in.readInt();
        date=in.readString();
        filePath=in.readString();
    }
    public short getChannelInMono()
    {
        return mChannelInMono;
    }

    public void setChannelInMono(short channelInMono)
    {
        mChannelInMono = channelInMono;
    }

    public int getAudioformat()
    {
        return mAudioformat;
    }

    public void setAudioformat(int audioformat)
    {
        mAudioformat = audioformat;
    }

    public int getSamplerateinhz()
    {
        return mSamplerateinhz;
    }

    public void setSamplerateinhz(int samplerateinhz)
    {
        mSamplerateinhz = samplerateinhz;
    }

    public int getBufferSize()
    {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator<MediaBean> CREATOR = new Creator<MediaBean>()
    {
        @Override
        public MediaBean[] newArray(int size)
        {
            return new MediaBean[size];
        }

        @Override
        public MediaBean createFromParcel(Parcel in)
        {
            return new MediaBean(in);
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mChannelInMono);
        dest.writeInt(mAudioformat);
        dest.writeInt(mSamplerateinhz);
        dest.writeInt(bufferSize);
        dest.writeString(date);
        dest.writeString(filePath);
    }
}
