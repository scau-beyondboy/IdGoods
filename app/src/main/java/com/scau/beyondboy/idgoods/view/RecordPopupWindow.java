package com.scau.beyondboy.idgoods.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.scau.beyondboy.idgoods.BlessingActivity;
import com.scau.beyondboy.idgoods.MyApplication;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.UploadBean;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.utils.StorageUtils;
import com.scau.beyondboy.idgoods.utils.TimeUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-29
 * Time: 18:36
 */
public class RecordPopupWindow extends AppCompatActivity
{

    private static final String TAG = RecordPopupWindow.class.getName();
    @Bind(R.id.uploading)
    Button uploading;
    @Bind(R.id.delete)
    Button delete;
    @Bind(R.id.seekbar)
    SeekBar seekbar;
    /**
     * 采样率
     */
    private int mSamplerateinhz = 8000;
    /**
     * 双声道
     */
    private final short mChannelInMono = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 记录采样二进制位数
     */
    private final short bSamples = 16;
    /**
     * 存储波形图的数据
     */

    ArrayBlockingQueue<short[]> inBuf = new ArrayBlockingQueue<>(10);
    /**
     * 输出音频内容到文件的时间间隔
     */
    private static final int TIMER_INTERVAL = 30;
    /**
     * 采样16位
     */
    private int mAudioformat = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * 双声道的标记
     */
    private final short mChannels = 1;
    /**
     * 记录录音状态，0代表准备录音,1代表正在录音，2代表录音错误,3代表录完音，4代表回放录音，5代表正在播放录音，6代表暂停回放录音，7代表回放录音结束，8代表重新录音
     */
    private AtomicInteger state =new AtomicInteger(-1);
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
    private AudioRecord audioRecorder;
    private final short RATEX = 6;
    private int oldX;
    private int oldY;
    private int countDate = 0;
    private MyHandler mHandler;
    private RandomAccessFile randomAccessWriter;
    private RecordRunable mRecordRunable;
    private PlayRuannble mPlayRuannble;
    private FileInputStream mInputStream;
    @Bind(R.id.date)
    TextView date;
    @Bind(R.id.oscillograph)
    SurfaceView oscillograph;
    @Bind(R.id.voice_blessing)
    Button voiceBlessing;
    private short baseLine;
    private Paint mPaint;
    private DrawRunnable mDrawRunnable;
    private AudioTrack mAudioTrack;
    private Semaphore mSemaphore=new Semaphore(0);
    /**
     * 录音计时器
     */
    private Timer mTimer;
    private File audioFile;
    private String mToken;
    private String fileName;
    private BlessingActivity mBlessingActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        super.onCreate(savedInstanceState);
        win.setAttributes(lp);
        setContentView(R.layout.record_popupwindow);
        //点击会销毁窗口，并返回首页的标记
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        this.getWindow().setAttributes(lp);
        init();
        ButterKnife.bind(this);
        //设置全透明
        oscillograph.getHolder().setFormat(PixelFormat.TRANSPARENT);
        oscillograph.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
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
        MyApplication.sActivityMap.put("RecordPopupWindow", this);
    }

    /**
     * 用来绘制图形的方法
     */
    private void draw()
    {
        Canvas canvas = oscillograph.getHolder().lockCanvas(); // 锁定canvas
        if (canvas != null)
        {
            //清屏
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            canvas.drawLine(0, baseLine, oscillograph.getWidth(), baseLine, mPaint);
            oscillograph.getHolder().unlockCanvasAndPost(canvas); // 解锁canvas
        }
    }

    //初始化
    private void init()
    {
        mHandler = new MyHandler(this);
        mBlessingActivity=(BlessingActivity)MyApplication.sActivityMap.get("BlessingActivity");
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(2);
        ThreadManager.scoolPoolSize = 2;
        framePeriod = mSamplerateinhz * TIMER_INTERVAL / 1000;
        bufferSize = framePeriod * 2 * bSamples * mChannels / 8;
        if (bufferSize < AudioRecord.getMinBufferSize(mSamplerateinhz, mChannelInMono, mAudioformat))
        {

            bufferSize = AudioRecord.getMinBufferSize(mSamplerateinhz, mChannelInMono, mAudioformat);
            framePeriod = bufferSize / (2 * bSamples * mChannels / 8);
        }
        //开启线程
        ThreadManager.addSingalExecutorTask(new Runnable()
        {
            @Override
            public void run()
            {
                prepareRecord();
            }
        });
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
                    state.set(2);
                    ToaskUtils.displayToast("文件写入异常");
                    e.printStackTrace();
                }
            }
        }
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
     * 字节转换成Short
     */
    private short getShort(byte argB1, byte argB2)
    {
        return (short) (argB1 | (argB2 << 8));
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
    private void prepareRecord()
    {
        try
        {
            if(audioRecorder==null)
            {
                audioRecorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSamplerateinhz, mChannelInMono, mAudioformat, bufferSize);
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
                        if (state.get() == 1&&audioRecorder!=null)
                            ThreadManager.addTask(createRecordRunable());
                    }
                });
            }
            Date date = new Date();
            File file = StorageUtils.getIndividualCacheDirectory(RecordPopupWindow.this, "music");
            //文件目录
            filePath = String.format("%s/%d.wav", file.getAbsolutePath(), date.getTime());
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
            //准备录音
            state.set(0);
        } catch (IOException e)
        {
            e.printStackTrace();
            ToaskUtils.displayToast("文件写入异常");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mBlessingActivity.finish();
        if(mAudioTrack!=null)
        {
            mAudioTrack.stop();
            mAudioTrack.release();
        }
        if(audioRecorder!=null)
        {
            audioRecorder.stop();
            audioRecorder.release();
        }
        ThreadManager.release();
        MyApplication.sActivityMap.clear();
    }

    /**
     * 解决Hanlder内存泄露问题
     */
    private static class MyHandler extends Handler
    {
        private final WeakReference<RecordPopupWindow> mRecordActivityWeakReference;

        public MyHandler(RecordPopupWindow recordPopupWindow)
        {
            mRecordActivityWeakReference = new WeakReference<>(recordPopupWindow);
        }

        @Override
        public void handleMessage(Message msg)
        {
            RecordPopupWindow recordPopupWindow = mRecordActivityWeakReference.get();
            if (recordPopupWindow != null)
            {
                if (msg.what == 0x123)
                {
                    recordPopupWindow.countDate++;
                    recordPopupWindow.date.setText(TimeUtils.converTommss(recordPopupWindow.countDate));
                }
            }
        }
    }

    private void start()
    {
        state.set(1);
        audioRecorder.startRecording();
        //准备画图
        ThreadManager.addTask(createDrawRunnable());
        ThreadManager.addTask(createRecordRunable());
    }


    private class DrawRunnable implements Runnable
    {
        private int X_index = 0;

        @Override
        public void run()
        {
            while (state.get() == 1)
            {
                try
                {
                    short[] temBuf = inBuf.poll(100, TimeUnit.MILLISECONDS);
                    if(temBuf!=null&&temBuf.length!=0)
                    {
                        SimpleDraw(X_index, temBuf, RATEX, baseLine);
                        X_index += temBuf.length;
                    }
                    if (X_index > oscillograph.getWidth())
                    {
                        X_index = 0;
                    }
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                    Log.i(TAG,"中断退出线程");
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
        canvas.drawColor(Color.TRANSPARENT);// 清除背景
        canvas.drawColor(Color.TRANSPARENT);
        //清屏
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        int y;
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
                audioRecorder=null;
                oscillograph.destroyDrawingCache();
                //暂停掉画图线程
                ThreadManager.stopFuture(createRecordRunable().toString());
            }
            if (randomAccessWriter != null)
            {
                randomAccessWriter.seek(4); //写到RIFF头部
                randomAccessWriter.writeInt(Integer.reverseBytes(36 + paylaodSize));
                randomAccessWriter.seek(40); // 写到Subchunk2Size区域
                randomAccessWriter.writeInt(Integer.reverseBytes(paylaodSize));
                randomAccessWriter.close();
            }
            state.set(3);
        } catch (IOException e)
        {
            state.set(2);
            e.printStackTrace();
            ToaskUtils.displayToast("文件写入异常");
        }
    }

    /**
     * 绘画波形图
     */
    @OnLongClick(R.id.voice_blessing)
    public boolean recordStart()
    {
        if(state.get()!=0)
        {
            if(state.get()==8||state.get()==-1)
                ToaskUtils.displayToast("录音准备正在初始化");
            return false;
        }
        //当状态为0时
        else
        {
            try
            {
                //录音
                randomAccessWriter.seek(0xc);
                //计录音时间
                mTimer = new Timer();
                mTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        mHandler.obtainMessage(0x123).sendToTarget();
                    }
                }, 0, 1000);
                voiceBlessing.setSelected(true);
                voiceBlessing.setText("松开停止");
                start();
            } catch (IOException e)
            {
                state.set(2);
                e.printStackTrace();
            }
            return true;
        }
    }

    @OnTouch(R.id.voice_blessing)
    public boolean record(MotionEvent event)
    {
        //录完音时
        if (state.get()==1)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_UP:
                    stop();
                    state.set(4);
                    preparePlay();
                    Log.i(TAG,"录完音");
                    break;
            }
            return true;
        }
        return false;
    }

    @OnClick(R.id.voice_blessing)
    public void play()
    {
        if(state.get()==5)
        {
            state.set(6);
            mAudioTrack.pause();
            voiceBlessing.setSelected(false);
            voiceBlessing.setText("回放录音");
        }
        else if(state.get()==4||state.get()==6||state.get()==7)
        {
            if(!audioFile.exists())
            {
                ToaskUtils.displayToast("音频文件已删除");
                return;
            }
            mAudioTrack.play();
            //重复播放
            if(state.get()==4||state.get()==7)
            {
                try
                {
                    mInputStream = new FileInputStream(audioFile);
                    seekbar.setProgress(0);
                    ThreadManager.addSingalExecutorTask(createPlayRuannble());
                } catch (Exception e)
                {
                    e.printStackTrace();
                    ToaskUtils.displayToast("文件读入异常");
                }
            }
            voiceBlessing.setSelected(true);
            voiceBlessing.setText("暂停");
            if(state.get()==6)
            {
                //防止多次阻塞
                state.set(5);
                mSemaphore.release();
            }
            state.set(5);
        }
    }
    /**
     * 准备播放
     */
    private void preparePlay()
    {
        mTimer.cancel();
        mTimer.purge();
        delete.setVisibility(View.VISIBLE);
        uploading.setVisibility(View.VISIBLE);
        delete.setEnabled(true);
        uploading.setEnabled(true);
        draw();
        oscillograph.setVisibility(View.INVISIBLE);
        seekbar.setVisibility(View.VISIBLE);
        if(mAudioTrack==null)
        {
            int channelOutMono = AudioFormat.CHANNEL_OUT_MONO;
            mAudioTrack=new AudioTrack(AudioManager.STREAM_MUSIC,mSamplerateinhz, channelOutMono,mAudioformat,bufferSize,AudioTrack.MODE_STREAM);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                mAudioTrack.setVolume(1.0f);
            }
            else
            {
                //noinspection deprecation
                mAudioTrack.setStereoVolume(1.0f,1.0f);
            }
        }
        voiceBlessing.setText("回放录音");
        voiceBlessing.setSelected(false);
        audioFile = new File(filePath);
        seekbar.setMax(countDate);
        seekbar.setProgress(0);
        date.setText(TimeUtils.converTommss(countDate));
    }
    private class PlayRuannble implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                int readByte;
                while (state.get()!=8&&(readByte=mInputStream.read(buffer,0,buffer.length))>=0)
                {
                    int seconds=Math.round(mAudioTrack.getPlaybackHeadPosition() /mAudioTrack.getSampleRate( ));
                    seekbar.setProgress(seconds);
                    mAudioTrack.write(buffer, 0, readByte);
                    //当正在播放，且按暂停时
                    if(state.get()==6&&mAudioTrack.getPlayState()==AudioTrack.PLAYSTATE_PAUSED)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                voiceBlessing.setSelected(false);
                                voiceBlessing.setText("回放录音");
                            }
                        });
                        //阻塞当前线程
                        mSemaphore.acquire();
                    }
                }
                //播放完时候
                seekbar.setProgress(countDate);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        voiceBlessing.setSelected(false);
                        voiceBlessing.setText("回放录音");
                    }
                });
                mInputStream.close();
                mAudioTrack.pause();
                mAudioTrack.flush();
                mAudioTrack.stop();
                state.set(7);
                //Log.i(TAG,"播放结束");
            } catch (Exception e)
            {
                e.printStackTrace();
                ToaskUtils.displayToast("读取异常");
            }
        }
    }

    private PlayRuannble createPlayRuannble()
    {
        if(mPlayRuannble==null)
        {
            synchronized (this)
            {
                if(mPlayRuannble==null)
                {
                    mPlayRuannble=new PlayRuannble();
                }
            }
        }
        return mPlayRuannble;
    }

    @OnClick({R.id.delete,R.id.uploading})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.delete:
                if(state.get()==6)
                    mSemaphore.release();
                ThreadManager.stopFuture(createPlayRuannble().toString());
                if(state.get()!=7)
                {
                    mAudioTrack.pause();
                    mAudioTrack.flush();
                    mAudioTrack.stop();
                }
                //noinspection ResultOfMethodCallIgnored
                audioFile.delete();
                state.set(8);
                voiceBlessing.setSelected(false);
                voiceBlessing.setText("长按说话");
                seekbar.setVisibility(View.INVISIBLE);
                oscillograph.setVisibility(View.VISIBLE);
                uploading.setEnabled(false);
                delete.setEnabled(false);
                date.setText(TimeUtils.converTommss(0));
                countDate=0;
                ThreadManager.addSingalExecutorTask(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        prepareRecord();
                    }
                });
                break;
            case R.id.uploading:
                getToken();
                break;
        }
    }

    private void getToken()
    {
        mBlessingActivity = (BlessingActivity) MyApplication.sActivityMap.get("BlessingActivity");
        NetWorkHandlerUtils.postAsynHandler(Consts.UPLOAD_TOKEN, null, null,"已上传过该文件",new NetWorkHandlerUtils.PostCallback()
        {
            @Override
            public void success(Object result)
            {
                if(result instanceof UploadBean)
                {
                    UploadBean uploadBean=(UploadBean)result;
                    mToken =uploadBean.getToken();
                    fileName =uploadBean.getFileName();
                    //Log.i(TAG,"数据：  "+uploadBean.getToken());
                    fileName +=".wav";
                    mBlessingActivity.mProgressbar.setVisibility(View.VISIBLE);
                    upLoading();
                }
            }
        }, UploadBean.class);
    }

    /**上传文件操作*/
    private void upLoading()
    {
        UploadManager manager=new UploadManager();
        manager.put(audioFile, fileName, mToken, new UpCompletionHandler()
        {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response)
            {
                mBlessingActivity.mProgressbar.setVisibility(View.INVISIBLE);
                saveRadio();
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler()
        {
            @Override
            public void progress(String key, double percent)
            {
                int progress = (int) (percent * 100);
                mBlessingActivity.mProgressbar.setProgress(progress);
            }
        }, null));
    }

    private void saveRadio()
    {
        ArrayMap<String,String> params=new ArrayMap<>();
        fileName=Consts.QINIU+fileName;
        params.put(Consts.RADIO_KEY,fileName);
        params.put(Consts.SERIALNUMBERVALUEKEY, ShareUtils.getSerialNumberValue());
        params.put(Consts.CUSTOMERID_KEY, ShareUtils.getUserId());
        NetWorkHandlerUtils.postAsynHandler(Consts.SAVE_RADIO, params, "上传成功");
    }
}
