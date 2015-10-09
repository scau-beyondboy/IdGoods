package com.scau.beyondboy.idgoods.utils;
/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-08-24
 * Time: 10:09
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
/**可以提供应用文件路径来创建缓存目录，或默认目录下创建缓存目录*/
public  final class StorageUtils
{
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String INDIVIDUAL_DIR_NAME = "uil-images";
    private static final String TAG =StorageUtils.class.getName() ;
    private StorageUtils()
    {

    }
    /**
     * @see #getCacheDirectory(Context, boolean)
     */
    public static File getCacheDirectory(Context context)
    {
        return getCacheDirectory(context, true);
    }
    /**
     * 如果有内存卡，将在SD卡上创建缓存目录，否则直接在根目录上创建缓存目录
     * @param context 上下文
     * @param preferExternal 决定是否要在SD卡上创建缓存目录
     * @return /Android/data/[app_package_name]/cache或者data/data/[app_package_name]/cache
     */
    public static File getCacheDirectory(Context context,boolean preferExternal)
    {
        File appCacheDir=null;
        String externalStorageState;
        externalStorageState= Environment.getExternalStorageState();
        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)&&preferExternal&&hasExternalStoragePermisson(context))
        {
            appCacheDir=getExternalCacheDir(context);
        }
        if(appCacheDir==null)
        {
            appCacheDir=context.getCacheDir();
        }
        if(appCacheDir==null)
        {
            String cacheDirPath="/data/data/"+context.getPackageName()+"/cache";
            Log.i(TAG,String.format("Can't define system cache directory! '%s' will be used.", cacheDirPath));
            appCacheDir=new File(cacheDirPath);
        }
        return appCacheDir;
    }
    /**
     * @see #getIndividualCacheDirectory(Context, String)
     */
    public static File getIndividualCacheDirectory(Context context)
    {
        return getIndividualCacheDirectory(context, INDIVIDUAL_DIR_NAME);
    }
    /**
     * 再缓存目录下创建名为cacheDir文件夹
     * @param context 上下文
     * @param cacheDir 文件夹的名字
     * @return 返回创建的文件名
     */
    public static File getIndividualCacheDirectory(Context context,String cacheDir)
    {
        File appCacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if (!individualCacheDir.exists())
        {
            if (!individualCacheDir.mkdir())
            {
                individualCacheDir = appCacheDir;
            }
        }
        return individualCacheDir;
    }
    /**
     * 返回应用的指定缓存文件夹
     * @param context 上下文
     * @param cacheDir 缓存目录的路劲（e.g: "AppCacheDir","AppDir/cache/images"）
     * @return Cache {@link File directory}
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir)
    {
        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermisson(context))
        {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs()))
        {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }
    /**
     * 返回应用的指定缓存文件夹
     * @param context 上下文
     * @param cacheDir 缓存目录的路劲（e.g: "AppCacheDir","AppDir/cache/images"）
     * @param preferExternal 决定是否从SDK返回缓存目录文件夹
     * @return Cache {@link File directory}
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir, boolean preferExternal)
    {
        File appCacheDir = null;
        if (preferExternal &&Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermisson(context))
        {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs()))
        {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }
    /**
     * 在SD卡上创建缓存目录
     * @param context
     * @return  mnt/sdcard/Android/data/cache
     */
    private static File getExternalCacheDir(Context context)
    {
        File dataDir=new File(new File(Environment.getExternalStorageDirectory(),"Android"),"data");
        File appCacheDir=new File(dataDir,"cache");
        if(!appCacheDir.exists())
        {
            if(!appCacheDir.mkdirs())
            {
                Log.w(TAG,"Unable to create external cache directory");
                return null;
            }
            try
            {
                new File(appCacheDir,".nomedia").createNewFile();
            } catch (IOException e)
            {
                Log.i(TAG,"Can't create \".nomedia\" file in application external cache directory");
                Toast.makeText(context, "存储空间不够", Toast.LENGTH_SHORT);
            }
        }
        return appCacheDir;
    }
    private static boolean hasExternalStoragePermisson(Context context)
    {
        int perm=context.checkCallingPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm== PackageManager.PERMISSION_GRANTED;
    }
}