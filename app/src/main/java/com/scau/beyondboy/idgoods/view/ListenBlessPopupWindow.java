package com.scau.beyondboy.idgoods.view;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.MyApplication;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.CollectBean;
import com.scau.beyondboy.idgoods.model.CollectInfo;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.utils.NetWorkHandlerUtils;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.StorageUtils;
import com.scau.beyondboy.idgoods.utils.TimeUtils;
import com.scau.beyondboy.idgoods.utils.ToaskUtils;
import com.squareup.okhttp.internal.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-11-02
 * Time: 20:25
 */
public class ListenBlessPopupWindow extends AppCompatActivity
{
    private static final String TAG = ListenBlessPopupWindow.class.getName();
    @Bind(R.id.play)
    Button mPlay;
    @Bind(R.id.share)
    Button mShare;
    @Bind(R.id.save)
    Button mSave;
    @Bind(R.id.seekbar)
    SeekBar mSeekbar;
    @Bind(R.id.date)
    TextView mDate;
    private AudioTrack mAudioTrack;
    private String url;
    /**0代表准备播放，1代表正在播放，2代表暂停，3代表重新播放*/
    private AtomicInteger state=new AtomicInteger(-1);
    private Semaphore mSemaphore=new Semaphore(0);
    /**
     * 采样16位
     */
    private int mAudioformat = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * 采样率
     */
    private int mSamplerateinhz = 8000;
    /**
     * 双声道
     */
    private final short channelOutMono = AudioFormat.CHANNEL_OUT_MONO;
    private int bufferSize=2048;
    /**
     * 音频缓冲区
     */
    private byte[] buffer;
    private ScanCodeBean mScanCodeBean;
    private CollectBean mCollectBean;
    private InputStream mInputStream;
    private PlayRuannble mPlayRuannble;
    private File mPostCardVoice;
    private int totalTime;
    /**是否保存*/
    private boolean isSave=false;
    private String mFilePath;

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
        setContentView(R.layout.listen_bless_popupwindow);
        //点击会销毁窗口，并返回首页的标记
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        this.getWindow().setAttributes(lp);
        ButterKnife.bind(this);
        MyApplication.sActivityMap.put(Consts.BLESS_POPUP,this);
        init();
    }

    private void init()
    {
        OkHttpNetWorkUtil.setCache(StorageUtils.getOwnCacheDirectory(this, "poscard"), 100 * 1024 * 1024);
        if (bufferSize < AudioTrack.getMinBufferSize(mSamplerateinhz, channelOutMono, mAudioformat))
        {
            bufferSize = AudioTrack.getMinBufferSize(mSamplerateinhz, channelOutMono, mAudioformat);
        }
        if(mAudioTrack ==null)
        {
            mAudioTrack =new AudioTrack(AudioManager.STREAM_MUSIC,mSamplerateinhz, channelOutMono,mAudioformat,bufferSize,AudioTrack.MODE_STREAM);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                mAudioTrack.setVolume(1.0f);
            }
            else
            {
                //noinspection deprecation
                mAudioTrack.setStereoVolume(1.0f, 1.0f);
            }
            buffer=new byte[bufferSize];
        }
        //二维码扫描方式听明信片
        if(getIntent().getBooleanExtra(Consts.RECEIVEBLESS,false))
        {
            mScanCodeBean=getIntent().getParcelableExtra(Consts.SCAN_CODE_BEAN);
            url=mScanCodeBean.getAddress();
            Log.i(TAG,"在哪里啊");
            getRecordFile();
        }
        //收藏详情
        else
        {
            mCollectBean = getIntent().getParcelableExtra(Consts.COLLECT_BEAN);
            android.support.v4.util.ArrayMap<String,String> params=new android.support.v4.util.ArrayMap<>();
            params.put(Consts.SERIALNUMBERVALUEKEY, mCollectBean.getSerialNumberValue());
            Log.i(TAG,"这里了吗");
            NetWorkHandlerUtils.postAsynHandler(Consts.GET_COLLECT_INFO,params, null, null, new NetWorkHandlerUtils.PostCallback<CollectInfo>()
            {
                @Override
                public void success(CollectInfo result)
                {
                    url=result.getRadioAddress();
                    Log.i(TAG,"网址：  "+url);
                    getRecordFile();
                }
            }, CollectInfo.class);
        }
    }

    /**获取录音文件*/
    private void getRecordFile()
    {
        mFilePath = StorageUtils.getIndividualCacheDirectory(this, "postcard").getAbsolutePath()+"/"+ Util.md5Hex(url)+".0";
        mPostCardVoice=new File(mFilePath);
        if(mPostCardVoice.exists())
        {
            setInfo();
            isSave=true;
        }
        else
        {
            NetWorkHandlerUtils.downloadFileHandler(url, StorageUtils.getIndividualCacheDirectory(this, "postcard").getAbsolutePath(), new NetWorkHandlerUtils.PostCallback<String>()
            {
                @Override
                public void success(String result)
                {
                    mPostCardVoice = new File(result);
                    setInfo();
                }
            });
        }
    }

    private void setInfo()
    {
        totalTime = (int) mPostCardVoice.length() / mSamplerateinhz / 2;
        mDate.setText(TimeUtils.converTommss(totalTime));
        mSeekbar.setMax(totalTime);
        state.set(0);
    }

    private class PlayRuannble implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                int readByte;
                while (state.get()!=3&&(readByte= mInputStream.read(buffer, 0, buffer.length))>=0)
                {
                    int seconds=Math.round(mAudioTrack.getPlaybackHeadPosition() /mAudioTrack.getSampleRate( ));
                    mSeekbar.setProgress(seconds);
                    mAudioTrack.write(buffer, 0, readByte);
                    //当正在播放，且按暂停时
                    if(state.get()==2&&mAudioTrack.getPlayState()==AudioTrack.PLAYSTATE_PAUSED)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mPlay.setSelected(false);
                                mPlay.setText("播放录音");
                            }
                        });
                        //Log.i(TAG, "阻塞");
                        //阻塞当前线程
                        mSemaphore.acquire();
                        //Log.i(TAG,"解除阻塞");
                    }
                }
                //播放完时候
                mSeekbar.setProgress(totalTime);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mPlay.setSelected(false);
                        mPlay.setText("播放录音");
                    }
                });
                mAudioTrack.pause();
                mAudioTrack.flush();
                mAudioTrack.stop();
                state.set(3);
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

    @OnClick(R.id.play)
    public void play()
    {
        Log.i(TAG,"文件： "+mPostCardVoice);
        if(mPostCardVoice==null||!mPostCardVoice.exists())
        {
            Log.i(TAG,"删除");
            ToaskUtils.displayToast("音频文件还没下载完");
            return;
        }
        if(state.get()==1)
        {
            state.set(2);
            mAudioTrack.pause();
            mPlay.setSelected(false);
            mPlay.setText("播放录音");
            //Log.i(TAG,"暂停" );
        }
        else
        {
            mAudioTrack.play();
            //重新播放时候
            if(state.get()!=2)
            {
                try
                {
                    mInputStream=new FileInputStream(mPostCardVoice);
                    mInputStream.skip(0x2c);
                    mSeekbar.setProgress(0);
                    state.set(1);
                    ThreadManager.addSingalExecutorTask(createPlayRuannble());
                } catch (Exception e)
                {
                    e.printStackTrace();
                    ToaskUtils.displayToast("文件读入异常");
                }
            }
            mPlay.setText("暂停");
            mPlay.setSelected(true);
            if(state.get()==2)
            {
                //防止多次阻塞
                state.set(1);
                mSemaphore.release();
            }
            state.set(1);
        }
    }

    @OnClick(R.id.save)
    public void save()
    {
        isSave=true;
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(mAudioTrack!=null)
        {
            mAudioTrack.stop();
            mAudioTrack.release();
        }
        MyApplication.sActivityMap.get("ListenBlessActivity").finish();
        MyApplication.sActivityMap.remove("ListenBlessActivity");
        ThreadManager.release();
        if(!isSave&&mPostCardVoice!=null&&mPostCardVoice.exists())
        {
            String fileName=mPostCardVoice.getAbsolutePath();
            fileName=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length()-2);
            Log.i(TAG,"文件名：  "+fileName );
            OkHttpNetWorkUtil.removeCacheFile(fileName);
        }
        MyApplication.sActivityMap.clear();
    }
}
