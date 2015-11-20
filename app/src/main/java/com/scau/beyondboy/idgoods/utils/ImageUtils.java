package com.scau.beyondboy.idgoods.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-02
 * Time: 13:34
 * 图片压缩和缩放工具类
 */
@SuppressWarnings("ALL")
public class ImageUtils
{
    private static final String TAG = ImageSize.class.getName();

    /**
     * 根据InputStream获取图片实际的宽度和高度
     * @param imageStream 图片流
     */
    public static ImageSize getImageSize(InputStream imageStream)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);
        return new ImageSize(options.outWidth, options.outHeight);
    }
    public static class ImageSize
    {
        int width;
        int height;
        public ImageSize()
        {
        }
        public ImageSize(int width, int height)
        {
            this.width = width;
            this.height = height;
        }
        @Override
        public String toString()
        {
            return "ImageSize{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

    /**
     * 压缩图片，返回压缩过的图片
     * @param imageView 显示图片组件
     * @param bitmapBytes 实际图片字节
     */
    public static Bitmap compressBitmap(final ImageView imageView,byte[] bitmapBytes)
    {
        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        ImageSize  targetSize=getImageViewSize(imageView);
        ImageSize srcSize=getBytesImageSize(bitmapBytes);
        int inSampleSize=calculateInSampleSize(srcSize,targetSize);
        BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inDensity=displayMetrics.densityDpi;
        ops.inTargetDensity=displayMetrics.densityDpi;
        ops.inJustDecodeBounds = false;
        ops.inSampleSize = inSampleSize;
        Log.i(TAG,"压缩比例：  "+inSampleSize);
        return BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length, ops);
    }

    /**通过字节获取图片实际尺寸*/
    private static ImageSize getBytesImageSize(byte[] bitmapBites)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =true;
        BitmapFactory.decodeByteArray(bitmapBites,0,bitmapBites.length,options);
        return new ImageSize(options.outWidth, options.outHeight);
    }
    /**
     * 计算图片压缩比例
     * @param srcSize 原图片尺寸
     * @param targetSize    目标尺寸
     */
    public static int calculateInSampleSize(ImageSize srcSize, ImageSize targetSize)
    {
        // 源图片的宽度
        int width = srcSize.width;
        int height = srcSize.height;
        int inSampleSize = 1;

        int reqWidth = targetSize.width;
        int reqHeight = targetSize.height;

        if (width > reqWidth && height > reqHeight)
        {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    /**
     * 计算合适的inSampleSize
     */
    public static int computeImageSampleSize(ImageSize srcSize, ImageSize targetSize, ImageView imageView)
    {
        final int srcWidth = srcSize.width;
        final int srcHeight = srcSize.height;
        final int targetWidth = targetSize.width;
        final int targetHeight = targetSize.height;
        int scale;
        if (imageView == null)
        {
            scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
        }
        else
        {
            switch (imageView.getScaleType())
            {
                case FIT_CENTER:
                case FIT_XY:
                case FIT_START:
                case FIT_END:
                case CENTER_INSIDE:
                    scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
                    break;
                case CENTER:
                case CENTER_CROP:
                case MATRIX:
                    scale = Math.min(srcWidth / targetWidth, srcHeight / targetHeight); // min
                    break;
                default:
                    scale = Math.max(srcWidth / targetWidth, srcHeight / targetHeight); // max
                    break;
            }
        }
        if (scale < 1)
        {
            scale = 1;
        }
        return scale;
    }

    /**
     * 根据ImageView获适当的压缩的宽和高
     * @param view 图片组件
     */
    public static ImageSize getImageViewSize(View view)
    {
        ImageSize imageSize = new ImageSize();
        imageSize.width = getExpectWidth(view);
        imageSize.height = getExpectHeight(view);
        return imageSize;
    }

    /**
     * 根据view获得期望的高度
     * @param view 图片组件
     */
    private static int getExpectHeight(View view)
    {
        int height = 0;
        if (view == null) return 0;
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        //如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
        if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT)
        {
            height = view.getWidth(); // 获得实际的宽度
        }
        if (height <= 0 && params != null)
        {
            height = params.height; // 获得布局文件中的声明的宽度
        }

        if (height <= 0)
        {
            height = getImageViewFieldValue(view, "mMaxHeight");// 获得设置的最大的宽度
        }

        //如果宽度还是没有获取到，憋大招，使用屏幕的宽度
        if (height <= 0)
        {
            DisplayMetrics displayMetrics = view.getContext().getResources()
                    .getDisplayMetrics();
            height = displayMetrics.heightPixels;
        }
        Log.i(TAG,"组件高度："+height );
        return height;
    }

    public static ImageSize getBitmapSize(Bitmap bitmap)
    {
        ImageSize bitmapSize=new ImageSize();
        bitmapSize.height=bitmap.getHeight();
        bitmapSize.width=bitmap.getWidth();
        return  bitmapSize;
    }
    /**
     * 根据view获得期望的宽度
     * @param view 图片组件
     */
    private static int getExpectWidth(View view)
    {
        int width = 0;
        if (view == null) return 0;

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        //如果是WRAP_CONTENT，此时图片还没加载，getWidth根本无效
        if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT)
        {
            width = view.getWidth(); // 获得实际的宽度
        }
        if (width <= 0 && params != null)
        {
            width = params.width; // 获得布局文件中的声明的宽度
        }

        if (width <= 0)

        {
            width = getImageViewFieldValue(view, "mMaxWidth");// 获得设置的最大的宽度
        }
        //如果宽度还是没有获取到，憋大招，使用屏幕的宽度
        if (width <= 0)

        {
            DisplayMetrics displayMetrics = view.getContext().getResources()
                    .getDisplayMetrics();
            width = displayMetrics.widthPixels;
        }
        Log.i(TAG,"组件宽度："+width );
        return width;
    }

    /**
     * 通过反射获取imageview的某个属性值
     * @param object 对象
     * @param fieldName 变量属性
     */
    private static int getImageViewFieldValue(Object object, String fieldName)
    {
        int value = 0;
        try
        {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
            {
                value = fieldValue;
            }
        }
        catch (Exception ignored)
        {
        }
        return value;
    }

    /**获取输出流字节*/
    public static byte[] readStream(InputStream inStream) throws Exception
    {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1)
        {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }
}
