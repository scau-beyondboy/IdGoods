package com.scau.beyondboy.idgoods.utils;
/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-08-24
 * Time: 10:07
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.manager.ThreadManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
/**
 * 下载图片，如果内存缓存有图片，则从内存拉取图片
 * 如果内存没有缓存该图片，看SD卡上是否缓存有图片
 * 如果有，直接从SD卡上拉取图片，否则异步的从网络
 * 上拉取图片，并缓存到SD卡和内存上
 * 内存缓存机采用LRU机制策略
 */
@SuppressWarnings("ALL")
public class LoadImageUtils
{
    private static final String TAG =LoadImageUtils.class.getName() ;
    /**内存利用该集合的LRU机制缓存图片*/
    private Map<String,Bitmap> mCacheMap;
    /**最大缓存内存*/
    private final long mCahceMemoryMax=Runtime.getRuntime().maxMemory()/10;
    /**单利对象*/
    private static LoadImageUtils sImageUtils;
    /**下载失败显示的图片*/
    private Bitmap mFailBitmap;
    /**建立线程缓存池，最大线程是五个，并且线程空闲状态超过60s将会自动删除其线程*//*
    public static ExecutorService mExecutorService= MyApplication.getInstance().sThreadManager.createThreadPool(3);*/
    private LoadImageHandle mImageHandle;
    private LoadImageUtils()
    {
        if(mCacheMap==null)
        {
            mCacheMap= Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(100, 0.75f, true)
            {
                long cacheImageMemorySize=0;
                @Override
                public Bitmap put(String key, Bitmap value)
                {
                    if(value!=null)
                    {
                        cacheImageMemorySize+=value.getRowBytes()*value.getHeight();
                        return super.put(key, value);
                    }
                    else
                    {
                        return null;
                    }
                }
                //删除最少使用的元素
                @Override
                protected boolean removeEldestEntry(Entry<String, Bitmap> eldest)
                {
                    if(cacheImageMemorySize>mCahceMemoryMax)
                    {
                        cacheImageMemorySize-=eldest.getValue().getRowBytes()*eldest.getValue().getHeight();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
    public LoadImageUtils setImageHandle(LoadImageHandle imageHandle)
    {
        mImageHandle = imageHandle;
        return this;
    }
    /**
     * 返回LoadImageUtils单例对象
     */
    public static LoadImageUtils getInstance()
    {
        if(sImageUtils==null)
        {
            sImageUtils=new LoadImageUtils();
        }
        return sImageUtils;
    }
    /**
     * 下载图片，如果内存缓存有图片，则从内存拉取图片
     * 如果内存没有缓存该图片，看SD卡上是否缓存有图片
     * 如果有，直接从SD卡上拉取图片，否则异步的从网络
     * 上拉取图片，并缓存到SD卡和内存上
     * 内存缓存机制采用LRU策略
     */
    @SuppressWarnings("UnnecessaryReturnStatement")
    public void loadImage(final ImageView ImageView,final  String ImageUrl,Context context)
    {
        //建立线程缓存池，最大线程是五个，并且线程空闲状态超过60s将会自动删除其线程
        ExecutorService mExecutorService= ThreadManager.createThreadPool();
        Log.i(TAG,"图片地址： "+ImageUrl);
        if(mImageHandle!=null)
            mImageHandle.start(ImageView);
        final String key=StringUtils.md5(ImageUrl);
        if(mCacheMap.containsKey(key))
        {
            Bitmap bitmap=mCacheMap.get(key);
            if(bitmap!=null)
            {
                ImageView.setImageBitmap(bitmap);
                if(mImageHandle!=null)
                    mImageHandle.success();
                Log.i(TAG, "内存命中");
                return;
            }
        }
        final File cacheDirectory=StorageUtils.getCacheDirectory(context);
        final File file=new File(cacheDirectory,key);
        if(file.exists()&&file.length()>0)
        {
            try
            {
                FileInputStream is=new FileInputStream(file);
                Bitmap bitmap1= BitmapFactory.decodeStream(is);
           //     Log.i(TAG,"长度：  "+bitmap1.getHeight()+"    宽度：  "+bitmap1.getWidth() );
                //放入内存缓存
                mCacheMap.put(key, bitmap1);
                ImageView.setImageBitmap(bitmap1);
                if(mImageHandle!=null)
                    mImageHandle.success();
                Log.i(TAG,"Sdcard命中");
                return;
            } catch (FileNotFoundException e)
            {
                if(mImageHandle!=null)
                    mImageHandle.failure();
                e.printStackTrace();
            }
        }
        //从网络拉取
        mExecutorService.execute(new Runnable()
        {
            @SuppressWarnings("TryFinallyCanBeTryWithResources")
            @Override
            public void run()
            {
                Log.i(TAG, "网络拉取");
                try
                {
                    final byte[] imageBytes = ImageUtils.readStream(OkHttpNetWorkUtil.post(ImageUrl).body().byteStream());
                    if (imageBytes != null)
                    {
                        final Bitmap bitmap =ImageUtils.compressBitmap(ImageView,imageBytes);
                        //缓存到内存
                        mCacheMap.put(key, bitmap);
                        File file = new File(cacheDirectory, key);
                        //将图片缓存到SD卡中
                        FileOutputStream out = new FileOutputStream(file);
                        try
                        {
                            out.write(imageBytes);
                        } finally
                        {
                            out.close();
                        }
                        ImageView.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ImageView.setImageBitmap(bitmap);
                                if(mImageHandle!=null)
                                    mImageHandle.success();
                            }
                        });
                    }
                } catch (IOException e)
                {
                    ImageView.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (mFailBitmap != null)
                                ImageView.setImageBitmap(mFailBitmap);
                            else
                            {
                                ImageView.setImageResource(R.drawable.download_default_image);
                            }
                        }
                    });
                    if (mImageHandle != null)
                        mImageHandle.failure();
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 修改下载失败的图片
     * @param failBitmap 设置下载失败的图片
     */
    public void setFailBitmap(Bitmap failBitmap)
    {
        this.mFailBitmap = failBitmap;
    }

    /**
     * 图片下载回调接口
     */
    public interface LoadImageHandle
    {
        public abstract void failure();
        public abstract void success();
        public void start(ImageView imageView);
    }
}
