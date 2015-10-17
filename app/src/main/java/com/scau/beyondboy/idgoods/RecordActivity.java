package com.scau.beyondboy.idgoods;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.utils.StorageUtils;

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
public class RecordActivity extends BaseActivity
{
    private static final String TAG = RecordActivity.class.getName();
    /**存储波形图的数据*/
    ArrayBlockingQueue<short[]> inBuf=new ArrayBlockingQueue<>(10);
    /**
     * 输出音频内容到文件的时间间隔
     */
    private static final int TIMER_INTERVAL =30;
    /**
     * 双声道
     */
    private  short mChannelInMono = AudioFormat.CHANNEL_IN_MONO;
    /**采样率数值*/
    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
    /**
     * 采样16位
     */
    private int mAudioformat = AudioFormat.ENCODING_PCM_16BIT;
    /**记录采样二进制位数*/
    private short bSamples;
    /**
     * 采样率
     */
    private int mSamplerateinhz = 8000;
    /**
     * 音频源来自麦克风
     */
    private int mAudiosource = MediaRecorder.AudioSource.MIC;
    private RandomAccessFile randomAccessWriter;
    /**
     * 记录录音状态，0代表已经创建实例，1代表读取音频内容，2代表正在录音，3代表录音错误
     */
    private static int state = 0;
    /**双声道的标记*/
    private short mChannels =1;
    /**
     * 文件路劲
     */
    private String filePath;
    /**每次输出到文件的音频帧数*/
    private int framePeriod;
    /**音频缓冲区*/
    private byte[] buffer;
    /**记录数据的加载字节*/
    private int paylaodSize;
    /**缓冲区大小*/
    private int bufferSize;
    private ExecutorService mExecutorService;
    private AudioRecord audioRecorder;
    private final short RATEX =4;
    private final short RATEY=40;
    private boolean isRecording=false;
    private int oldX;
    private int oldY;
    private short baseLine;
    private int countDate=0;
    private String second;
    private String minute;
    private MyHandler mHandler;
    /**录音计时器*/
    private Timer mTimer;
    private Paint mPaint;
    private RecordRunable mRecordRunable;
    private DrawRunnable mDrawRunnable;
    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.oscillograph)
    SurfaceView oscillograph;
    @Bind(R.id.voice_blessing)
    Button voiceBlessing;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        init();
        ButterKnife.bind(this);
        oscillograph.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                baseLine = (short)(oscillograph.getHeight() / 2);
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

    @OnTouch(R.id.voice_blessing)
    public boolean record(MotionEvent event)
    {
        if(isRecording==false)
        {
            return false;
        }
        else
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG,"action_move");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG,"actong_up");
                    stop();
                    isRecording=false;
                    break;
            }
            return true;
        }
    }

    @OnLongClick(R.id.voice_blessing)
    public boolean recordStart()
    {
        Log.i(TAG,"长按");
        isRecording=true;
        Date date=new Date();
        File file= StorageUtils.getIndividualCacheDirectory(this,"music");
        filePath= String.format("%s/%d.wav", file.getAbsolutePath(), date.getTime());
        prepare();
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
        return true;
    }

    @OnClick(R.id.record_back)
    public void back()
    {
        finish();
    }
    /**初始化操作*/
    private void init()
    {
        mHandler=new MyHandler(this);
        mTimer=new Timer();
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(2);
        ThreadManager.scoolPoolSize=2;
        mExecutorService=ThreadManager.createThreadPool();
        audioRecorder = findAudioRecord();
        if(mChannelInMono==AudioFormat.CHANNEL_IN_MONO)
        {
            mChannels=1;
        }
        else
        {
            mChannels=2;
        }
        framePeriod = mSamplerateinhz * TIMER_INTERVAL / 1000;
        bufferSize = framePeriod * 2 *bSamples* mChannels / 8;
        if (bufferSize < AudioRecord.getMinBufferSize(mSamplerateinhz, mChannelInMono, mAudioformat))
        {
            bufferSize = AudioRecord.getMinBufferSize(mSamplerateinhz, mChannelInMono, mAudioformat);
            framePeriod = bufferSize / (2 * bSamples * mChannels / 8);
        }
        Log.i(TAG,"帧数：  "+framePeriod);
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
            Log.i(TAG,"已创建文件夹实例" );
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
            buffer = new byte[framePeriod *bSamples/ 8*mChannels];
        } catch (IOException e)
        {
            e.printStackTrace();
            displayToast("文件写入异常");
        }
    }

    private void displayToast(String warnning)
    {
        Toast.makeText(this, warnning, Toast.LENGTH_SHORT).show();
    }

    private class RecordRunable implements Runnable
    {
        @Override
        public void run()
        {
            if(audioRecorder!=null)
            {
                try
                {
                    audioRecorder.read(buffer,0,buffer.length);
                    randomAccessWriter.write(buffer);
                    paylaodSize+=buffer.length;
                    short[] temBuf=new short[buffer.length/(2*RATEX)];
                   // Log.i(TAG,"数据长度： "+buffer.length);
                    for(int i=0;i<buffer.length/(2*RATEX);i++)
                    {
                        //音频振幅
                        short cursample=getShort(buffer[i*2*RATEX],buffer[i*2*RATEX+1]);
                        temBuf[i]=(short)(cursample/RATEY+baseLine);
                    }
                    //存储振幅
                    inBuf.offer(temBuf);
                } catch (IOException e)
                {
                    displayToast("文件写入异常");
                    e.printStackTrace();
                }
            }
        }
    }

    /**绘画波形图*/
    private class DrawRunnable implements Runnable
    {
        private int X_index=0;
        @Override
        public void run()
        {
            while(isRecording)
            {
                try
                {
                    short[] temBuf=inBuf.take();
                    SimpleDraw(X_index,temBuf,RATEX, baseLine);
                    X_index+=temBuf.length;
                    if(X_index>oscillograph.getWidth())
                    {
                        X_index=0;
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
    /**字节转换成Short*/
    private short getShort(byte argB1, byte argB2)
    {
        return (short) (argB1 | (argB2 << 8));
    }

    /**
     * 绘制指定区域
     *
     * @param start
     *            X轴开始的位置(全屏)
     * @param buffer
     *            缓冲区
     * @param rate
     *            Y轴数据缩小的比例
     * @param baseLine
     *            Y轴基线
     */
    private void SimpleDraw(int start, short[] buffer, int rate, int baseLine)
    {
        if (start == 0)
            oldX = 0;
        Canvas canvas =oscillograph.getHolder().lockCanvas(new Rect(start,0,start+buffer.length,oscillograph.getHeight()));// 关键:获取画布
        canvas.drawColor(Color.WHITE);// 清除背景
        int y=0;
        for (int i = 0; i < buffer.length; i++)
        {
            // 有多少画多少
            int x = i + start;
            y = buffer[i] /rate + baseLine;// 调节缩小比例，调节基准线
            canvas.drawLine(oldX,oldY,x,y, mPaint);
            oldX = x;
            oldY = y;
        }
        oscillograph.getHolder().unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
    }

    /**停止录音*/
    private void stop()
    {
        try
        {
            if(audioRecorder!=null)
                audioRecorder.stop();
            if(randomAccessWriter!=null)
            {
                randomAccessWriter.seek(4); //写到RIFF头部
                randomAccessWriter.writeInt(Integer.reverseBytes(36 + paylaodSize));
                randomAccessWriter.seek(40); // 写到Subchunk2Size区域
                randomAccessWriter.writeInt(Integer.reverseBytes(paylaodSize));
                randomAccessWriter.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            displayToast("文件写入异常");
        }
    }

    /**释放音频资源*/
    private void release()
    {
        if(audioRecorder!=null)
        {
            audioRecorder.release();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        release();
        ThreadManager.release();
        mTimer.cancel();
    }

    private void start()
    {
        Log.i(TAG, "录音状态：" + audioRecorder.getState());
        audioRecorder.startRecording();
        mExecutorService.submit(createRecordRunable());
    }
    /**单利创建录音线程*/
    private RecordRunable createRecordRunable()
    {
        if(mRecordRunable==null)
        {
            synchronized (this)
            {
                if(mRecordRunable ==null)
                {
                    mRecordRunable =new RecordRunable();
                }
            }
        }
        return mRecordRunable;
    }

    private DrawRunnable createDrawRunnable()
    {
        if(mDrawRunnable==null)
        {
            synchronized (this)
            {
                if(mDrawRunnable==null)
                {
                    mDrawRunnable=new DrawRunnable();
                }
            }
        }
        return mDrawRunnable;
    }

    /**解决Hanlder内存泄露问题*/
    private static class  MyHandler extends  Handler
    {
        private final WeakReference<RecordActivity> mRecordActivityWeakReference;
        public MyHandler(RecordActivity recordActivity)
        {
            mRecordActivityWeakReference=new WeakReference<RecordActivity>(recordActivity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            RecordActivity recordActivity=mRecordActivityWeakReference.get();
            if(recordActivity!=null)
            {
                if(msg.what==0x123)
                {
                    recordActivity.countDate++;
                    recordActivity.minute = recordActivity.countDate / 60 < 10 ? "0" + recordActivity.countDate / 60 : recordActivity.countDate / 60 + "";
                    recordActivity.second = recordActivity.countDate % 60 < 10 ? "0" + recordActivity.countDate % 60 : recordActivity.countDate % 60 + "";
                    recordActivity.date.setText(String.format("%s:%s", recordActivity.minute, recordActivity.second));
                }
            }
        }
    }

    /**由于不同设备，硬件也不同，所以需要根据多种参数情况来创建其录音实例*/
    private  AudioRecord findAudioRecord()
    {
        for (int rate : mSampleRates)
        {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT })
            {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO })
                {
                    try
                    {
                        Log.i(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "+ channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE)
                        {
                            // 检查创建实例是否成功
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                            {
                                if(this.mAudioformat==AudioFormat.ENCODING_PCM_16BIT)
                                {
                                    this.bSamples=16;
                                }
                                else
                                {
                                    this.bSamples=8;
                                }
                                mSamplerateinhz=rate;
                                this.mChannelInMono=channelConfig;
                                this.mAudioformat=audioFormat;
                                return recorder;
                            }
                        }
                    } catch (Exception e)
                    {
                        Log.e(TAG, rate + "Exception, keep trying.",e);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 用来绘制图形的方法
     */
    private void draw()
    {
        Canvas canvas = oscillograph.getHolder().lockCanvas(); // 锁定canvas
        if(canvas!=null)
        {
            canvas.drawColor(Color.WHITE); // 底色
            canvas.drawLine(0,baseLine,oscillograph.getWidth(),baseLine, mPaint);
            oscillograph.getHolder().unlockCanvasAndPost(canvas); // 解锁canvas
        }
    }
}
