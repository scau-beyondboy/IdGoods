package com.scau.beyondboy.idgoods;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.utils.StorageUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-16
 * Time: 16:11
 * 录音界面
 */
public class RecordActivity extends AppCompatActivity
{
    private static final String TAG = RecordActivity.class.getName();
    /**
     * 存储波形图的数据
     */
    ArrayBlockingQueue<short[]> inBuf = new ArrayBlockingQueue<>(10);
    /**
     * 输出音频内容到文件的时间间隔
     */
    private static final int TIMER_INTERVAL = 30;
    /**
     * 双声道
     */
    private short mChannelInMono = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 采样率数值
     */
    private static int[] mSampleRates = new int[]{8000,
            11025,
            22050,
            44100};
    /**
     * 采样16位
     */
    private int mAudioformat = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * 记录采样二进制位数
     */
    private short bSamples;
    /**
     * 采样率
     */
    private int mSamplerateinhz = 8000;
    private RandomAccessFile randomAccessWriter;
    /**
     * 记录录音状态，0代表已经创建实例,1代表正在录音，2代表录音错误,3代表录完音，4代表回放录音，5代表暂停回放录音，6代表回放录音结束，7代表重新录音
     */
    private int state = -1;
    /**
     * 双声道的标记
     */
    private short mChannels = 1;
    /**
     * 文件路劲
     */
    private String filePath;
    /**
     * 每次输出到文件的音频帧数
     */
    private int framePeriod;
    /**
     * 音频缓冲区
     */
    private byte[] buffer;
    /**
     * 记录数据的加载字节
     */
    private int paylaodSize;
    /**
     * 缓冲区大小
     */
    private int bufferSize;
    private ExecutorService mExecutorService;
    private AudioRecord audioRecorder;
    private final short RATEX = 4;
    private int oldX;
    private int oldY;
    private short baseLine;
    private int countDate = 0;
    private String second;
    private String minute;
    private MyHandler mHandler;
    TextView date;
    /**
     * 录音计时器
     */
    private Timer mTimer;
    private Paint mPaint;
    private RecordRunable mRecordRunable;
    private RecordPopupWindow mRecordPopupWindow;
    @Bind(R.id.record_back)
    TextView recordBack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        init();
        ButterKnife.bind(this);
    }


    public int getState()
    {
        return state;
    }

    @OnClick(R.id.record_back)
    public void back()
    {
        //finish();
        mRecordPopupWindow=new RecordPopupWindow(this);
        //显示窗口
        mRecordPopupWindow.showAtLocation(recordBack, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0); //设置layout在PopupWindow中显示的位置
        mRecordPopupWindow.addCallback();
    }

    /**
     * 初始化操作
     */
    private void init()
    {
        mHandler = new MyHandler(this);
        mTimer = new Timer();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(2);
        ThreadManager.scoolPoolSize = 2;
        mExecutorService = ThreadManager.createThreadPool();
        audioRecorder = findAudioRecord();
        if (mChannelInMono == AudioFormat.CHANNEL_IN_MONO)
        {
            mChannels = 1;
        } else
        {
            mChannels = 2;
        }
        framePeriod = mSamplerateinhz * TIMER_INTERVAL / 1000;
        bufferSize = framePeriod * 2 * bSamples * mChannels / 8;
        if (bufferSize < AudioRecord.getMinBufferSize(mSamplerateinhz, mChannelInMono, mAudioformat))
        {
            bufferSize = AudioRecord.getMinBufferSize(mSamplerateinhz, mChannelInMono, mAudioformat);
            framePeriod = bufferSize / (2 * bSamples * mChannels / 8);
        }
        //准备录音
        state = 0;
        //设置音频输出帧数
        audioRecorder.setPositionNotificationPeriod(framePeriod);
        audioRecorder.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener()
        {
            @Override
            public void onMarkerReached(AudioRecord recorder)
            {

            }

            @Override
            public void onPeriodicNotification(AudioRecord recorder)
            {
                mExecutorService.submit(createRecordRunable());
            }
        });
       /* mRecordPopupWindow=new RecordPopupWindow(this);
        //显示窗口
        mRecordPopupWindow.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置*/
    }

    /**
     * wav文件格式：
     * 偏移地址 字节数 类型 内容
     * 00H~03H 4 字符 资源交换文件标志（RIFF）
     * 04H~07H 4 长整数 从下个地址开始到文件尾的总字节数
     * 08H~0BH 4 字符 WAV文件标志（WAVE）
     * 0CH~0FH 4 字符 波形格式标志（FMT）
     * 10H~13H 4 整数 过滤字节（一般为00000010H）
     * 14H~15H 2 整数 格式种类（值为1时，表示数据为线性PCM编码）
     * 16H~17H 2 整数 通道数，单声道为1，双声音为2
     * 18H~1BH 4 长整数 采样频率
     * 1CH~1FH 4 长整数 波形数据传输速率（每秒平均字节数）
     * 20H~21H 2 整数 数据的调整数（按字节计算）
     * 22H~23H 2 整数 样本数据位数
     * 24H~27H 4 字符 数据标志符（data）
     * 28H~2BH 4 长整型 采样数据总数
     * 2CH...
     * ...
     * 采样数据
     * 为了能使PCM转换wav格式音频文件需做下面的准备工作
     */
    private void prepare()
    {
        try
        {
            randomAccessWriter = new RandomAccessFile(filePath, "rw");
            randomAccessWriter.setLength(0);
            randomAccessWriter.writeBytes("RIFF");
            //文件总长度不知道暂时设置为0
            randomAccessWriter.writeInt(0);
            randomAccessWriter.writeBytes("WAVE");
            randomAccessWriter.writeBytes("fmt ");
            randomAccessWriter.writeInt(Integer.reverseBytes(16));
            randomAccessWriter.writeShort(Short.reverseBytes((short) 1));
            //设置双声道
            randomAccessWriter.writeShort(Short.reverseBytes(mChannels));
            randomAccessWriter.writeInt(Integer.reverseBytes(mSamplerateinhz));
            randomAccessWriter.writeInt(Integer.reverseBytes(mSamplerateinhz * bSamples * mChannels / 8));
            randomAccessWriter.writeShort(Short.reverseBytes((short) (mChannels * bSamples / 8)));
            randomAccessWriter.writeShort(Short.reverseBytes(bSamples));
            randomAccessWriter.writeBytes("data");
            randomAccessWriter.writeInt(0);
            buffer = new byte[framePeriod * bSamples / 8 * mChannels];
        } catch (IOException e)
        {
            e.printStackTrace();
            ToaskUtils.displayToast("文件写入异常");
        }
    }

    private class RecordRunable implements Runnable
    {
        @Override
        public void run()
        {
            if (audioRecorder != null)
            {
                try
                {
                    audioRecorder.read(buffer, 0, buffer.length);
                    randomAccessWriter.write(buffer, 0, buffer.length);
                    paylaodSize += buffer.length;
                    short[] temBuf = new short[buffer.length / (2 * RATEX)];
                    for (int i = 0; i < buffer.length / (2 * RATEX); i++)
                    {
                        //音频振幅
                        short cursample = getShort(buffer[i * 2 * RATEX], buffer[i * 2 * RATEX + 1]);
                        short RATEY = 40;
                        temBuf[i] = (short) (cursample / RATEY + baseLine);
                    }
                    //存储振幅
                    inBuf.offer(temBuf);
                } catch (IOException e)
                {
                    state=2;
                    ToaskUtils.displayToast("文件写入异常");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 字节转换成Short
     */
    private short getShort(byte argB1, byte argB2)
    {
        return (short) (argB1 | (argB2 << 8));
    }


    /**
     * 录音结束
     */
    private void stop()
    {
        try
        {
            if (audioRecorder != null)
            {
                audioRecorder.stop();
                audioRecorder.release();
            }
            if (randomAccessWriter != null)
            {
                randomAccessWriter.seek(4); //写到RIFF头部
                randomAccessWriter.writeInt(Integer.reverseBytes(36 + paylaodSize));
                randomAccessWriter.seek(40); // 写到Subchunk2Size区域
                randomAccessWriter.writeInt(Integer.reverseBytes(paylaodSize));
                randomAccessWriter.close();
            }
            state = 3;
        } catch (IOException e)
        {
            state=2;
            e.printStackTrace();
            ToaskUtils.displayToast("文件写入异常");
        }
    }

    /**
     * 释放音频资源
     */
    private void release()
    {
        if (audioRecorder != null)
        {
            audioRecorder.release();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ThreadManager.release();
        //取消定时器
        mTimer.cancel();
    }

    private void start()
    {
        state = 1;
        audioRecorder.startRecording();
        mExecutorService.submit(createRecordRunable());
    }

    /**
     * 单利创建录音线程
     */
    private RecordRunable createRecordRunable()
    {
        if (mRecordRunable == null)
        {
            synchronized (this)
            {
                if (mRecordRunable == null)
                {
                    mRecordRunable = new RecordRunable();
                }
            }
        }
        return mRecordRunable;
    }



    /**
     * 解决Hanlder内存泄露问题
     */
    private static class MyHandler extends Handler
    {
        private final WeakReference<RecordActivity> mRecordActivityWeakReference;

        public MyHandler(RecordActivity recordActivity)
        {
            mRecordActivityWeakReference = new WeakReference<>(recordActivity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            RecordActivity recordActivity = mRecordActivityWeakReference.get();
            if (recordActivity != null)
            {
                if (msg.what == 0x123)
                {
                    recordActivity.countDate++;
                    recordActivity.minute = recordActivity.countDate / 60 < 10 ? "0" + recordActivity.countDate / 60 : recordActivity.countDate / 60 + "";
                    recordActivity.second = recordActivity.countDate % 60 < 10 ? "0" + recordActivity.countDate % 60 : recordActivity.countDate % 60 + "";
                    recordActivity.date.setText(String.format("%s:%s", recordActivity.minute, recordActivity.second));
                }
            }
        }
    }

    /**
     * 由于不同设备，硬件也不同，所以需要根据多种参数情况来创建其录音实例
     */
    private AudioRecord findAudioRecord()
    {
        for (int rate : mSampleRates)
        {
            for (short audioFormat : new short[]{AudioFormat.ENCODING_PCM_8BIT,
                    AudioFormat.ENCODING_PCM_16BIT})
            {
                for (short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.CHANNEL_IN_STEREO})
                {
                    try
                    {
                        Log.i(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE)
                        {
                            // 检查创建实例是否成功
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                            {
                                if (this.mAudioformat == AudioFormat.ENCODING_PCM_16BIT)
                                {
                                    this.bSamples = 16;
                                } else
                                {
                                    this.bSamples = 8;
                                }
                                mSamplerateinhz = rate;
                                this.mChannelInMono = channelConfig;
                                this.mAudioformat = audioFormat;
                                return recorder;
                            }
                        }
                    } catch (Exception e)
                    {
                        Log.e(TAG, rate + "Exception, keep trying.", e);
                    }
                }
            }
        }
        return null;
    }

    class RecordPopupWindow extends PopupWindow
    {
        @Bind(R.id.voice_blessing)
        Button voiceBlessing;
        @Bind(R.id.oscillograph)
        SurfaceView oscillograph;
        private DrawRunnable mDrawRunnable;

        public RecordPopupWindow(AppCompatActivity context)
        {
            super(context);
            View recordPopupView = LayoutInflater.from(context).inflate(R.layout.record_popupwindow, null, false);
            ButterKnife.bind(this, recordPopupView);
            oscillograph.getHolder().addCallback(new SurfaceHolder.Callback()
            {
                @Override
                public void surfaceCreated(SurfaceHolder holder)
                {
                    Log.i(TAG, "画图");
                    baseLine = (short) (oscillograph.getHeight() / 2);
                    //设置surfaceView初始背景为白色
                    draw();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
                {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder)
                {

                }
            });
            setContentView(recordPopupView);
                     //noinspection deprecation

            //oscillograph = (SurfaceView) recordPopupView.findViewById(R.id.oscillograph);
            date = (TextView) recordPopupView.findViewById(R.id.date);
            this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            //设置SelectPicPopupWindow弹出窗体的高
            this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置SelectPicPopupWindow弹出窗体可点击
            this.setFocusable(true);
            //设置SelectPicPopupWindow弹出窗体动画效果
            this.setAnimationStyle(R.style.AnimBottom);
            //实例化一个ColorDrawable颜色为半透明
            this.setBackgroundDrawable((ColorDrawable) getResources().getDrawable(R.drawable.popupwindow));
        }

        @OnTouch(R.id.voice_blessing)
        public boolean record(MotionEvent event)
        {
            //正在录音的时候
            if(state==1)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        stop();
                        state = 4;
                        dismiss();
                        break;
                }
                return true;
            }
            return false;
        }

        @OnLongClick(R.id.voice_blessing)
        public boolean recordStart()
        {
            try
            {
                //重新录音
                if (state == 7)
                {
                    randomAccessWriter.seek(0);
                }
                Date date = new Date();
                File file = StorageUtils.getIndividualCacheDirectory(RecordActivity.this, "music");
                //文件目录
                filePath = String.format("%s/%d.wav", file.getAbsolutePath(), date.getTime());
                prepare();
                //准备画图
                mExecutorService.submit(createDrawRunnable());
                //计录音时间
                mTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        mHandler.obtainMessage(0x123).sendToTarget();
                    }
                }, 0, 1000);
                voiceBlessing.setText("松开停止");
                start();
                state = 1;
            } catch (IOException e)
            {
                state=2;
                e.printStackTrace();
            }
            return true;
        }

        /**
         * 绘画波形图
         */
        private class DrawRunnable implements Runnable
        {
            private int X_index = 0;

            @Override
            public void run()
            {
                while (state==1)
                {
                    try
                    {
                        short[] temBuf = inBuf.take();
                        SimpleDraw(X_index, temBuf, RATEX, baseLine);
                        X_index += temBuf.length;
                        if (X_index > oscillograph.getWidth())
                        {
                            X_index = 0;
                        }
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }

        /**
         * 绘制指定区域
         *
         * @param start X轴开始的位置(全屏)
         * @param buffer 缓冲区
         * @param rate Y轴数据缩小的比例
         * @param baseLine Y轴基线
         */
        private void SimpleDraw(int start, short[] buffer, int rate, int baseLine)
        {
            if (start == 0)
                oldX = 0;
            Canvas canvas = oscillograph.getHolder().lockCanvas(new Rect(start, 0, start + buffer.length, oscillograph.getHeight()));// 关键:获取画布
            canvas.drawColor(Color.WHITE);// 清除背景
            int y = 0;
            for (int i = 0; i < buffer.length; i++)
            {
                // 有多少画多少
                int x = i + start;
                y = buffer[i] / rate + baseLine;// 调节缩小比例，调节基准线
                canvas.drawLine(oldX, oldY, x, y, mPaint);
                oldX = x;
                oldY = y;
            }
            oscillograph.getHolder().unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
        }

        private DrawRunnable createDrawRunnable()
        {
            if (mDrawRunnable == null)
            {
                synchronized (this)
                {
                    if (mDrawRunnable == null)
                    {
                        mDrawRunnable = new DrawRunnable();
                    }
                }
            }
            return mDrawRunnable;
        }

        /**
         * 用来绘制图形的方法
         */
        private void draw()
        {
            Log.i(TAG,"空值吗？"+oscillograph.getHolder().lockCanvas());
            Canvas canvas = oscillograph.getHolder().lockCanvas(); // 锁定canvas
            if (canvas != null)
            {
                Log.i(TAG,"到这了吗？");
                canvas.drawColor(Color.WHITE); // 底色
                canvas.drawLine(0, baseLine, oscillograph.getWidth(), baseLine, mPaint);
                oscillograph.getHolder().unlockCanvasAndPost(canvas); // 解锁canvas
            }
        }

        private void addCallback()
        {
            //noinspection deprecation
            oscillograph.getHolder().addCallback(new SurfaceHolder.Callback()
            {
                @Override
                public void surfaceCreated(SurfaceHolder holder)
                {
                    Log.i(TAG,"画图");
                    baseLine = (short) (oscillograph.getHeight() / 2);
                    //设置surfaceView初始背景为白色
                    draw();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
                {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder)
                {

                }
            });
        }
    }
}
