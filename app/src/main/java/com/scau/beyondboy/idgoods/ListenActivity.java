package com.scau.beyondboy.idgoods;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.disklrucache.DiskLruCache;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.StorageUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okio.Buffer;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-19
 * Time: 21:40
 * 收听录音界面
 */
public class ListenActivity extends AppCompatActivity
{
    public static final int CACHEMAXSIZE = 1024 * 1024 * 100;
    @Bind(R.id.seekbar)
    SeekBar mSeekBar;
    @Bind(R.id.play)
    Button play;
    @Bind(R.id.nickname)
    TextView nickName;
    @Bind(R.id.total_date)
    TextView total_date;
    @Bind(R.id.current_date)
    TextView currentDate;
    private String url;
    private AudioTrack mAudioTrack;
    private byte[] buffer;
    private int bufferSize=2048;
    private boolean isPlay=false;
    private boolean isFinish=true;
    private ExecutorService mExecutorService;
    private String date;
    private long totalTime;
    private static DiskLruCache sDiskLruCache;
    private InputStream mInputStream;
    private PlayRuannble mPlayRuannble;
    private Buffer okBuffer;
    /**
     * 采样16位
     */
    private int mAudioformat = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * 双声道
     */
    private  short mChannelInMono = AudioFormat.CHANNEL_OUT_MONO;
    /**采样率数值*/
    private static int mSampleRates = 8000;
    static
    {
        try
        {
            sDiskLruCache=DiskLruCache.open(StorageUtils.getOwnCacheDirectory(MyApplication.getInstance(), "poscard"), 1, 1, 100 * 1024 * 1024);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.play)
    public void play()
    {
        if(isPlay)
        {
            mAudioTrack.pause();
            isPlay=false;
            play.setSelected(false);
            play.setText("回放录音");
        }
        else
        {
           /* if(!audioFile.exists())
            {
                displayToast("音频文件已删除");
                return;
            }*/
            //重复播放
            if(isFinish)
            {
                try
                {
                    DiskLruCache.Snapshot snapshot=sDiskLruCache.get(StringUtils.md5(url));
                    if(snapshot!=null)
                    {
                        mInputStream=snapshot.getInputStream(0);
                    }
                    else
                    {
                        repeatPlay(url);
                        return;
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                    displayToast("网络异常");
                    return;
                }
            }
            play.setSelected(true);
            play.setText("暂停");
            isPlay=true;
            mAudioTrack.play();
            mExecutorService.submit(createPlayRuannble());
        }
    }

    @OnClick({R.id.save,R.id.share,R.id.listen_back})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.save:
                save();
                break;
            case R.id.listen_back:
                finish();
                break;
        }
    }

    private void save()
    {
        OkHttpNetWorkUtil.getAsyn(url, new OkHttpNetWorkUtil.ResultCallback<Response>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                e.printStackTrace();
                displayToast("网络异常");
            }

            @Override
            public void onResponse(Response response)
            {
                try
                {
                    //缓存中文件中
                    DiskLruCache.Editor editor=sDiskLruCache.edit(StringUtils.md5(url));
                    mInputStream=response.body().byteStream();
                    if(downloadUrlToStream(mInputStream,editor.newOutputStream(0)))
                    {
                        editor.commit();
                    }
                    else
                    {
                        editor.abort();
                    }
                } catch (IOException e)
                {
                    displayToast("读取异常");
                    e.printStackTrace();
                }
            }
        });
    }

    private void init()
    {
        bufferSize=AudioTrack.getMinBufferSize(mSampleRates,mChannelInMono,mAudioformat);
        buffer=new byte[bufferSize];
        mAudioTrack=new AudioTrack(AudioManager.STREAM_MUSIC,mSampleRates,mChannelInMono,mAudioformat,bufferSize,AudioTrack.MODE_STREAM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mAudioTrack.setVolume(1.0f);
        }
        else
        {
            //noinspection deprecation
            mAudioTrack.setStereoVolume(1.0f,1.0f);
        }
        okBuffer=new Buffer();
    }
    private void displayToast(String warnning)
    {
        Toast.makeText(this, warnning, Toast.LENGTH_SHORT).show();
    }

    private PlayRuannble createPlayRuannble()
    {
        if(mPlayRuannble ==null)
        {
            synchronized (this)
            {
                if(mPlayRuannble ==null)
                {
                    mPlayRuannble =new PlayRuannble();
                }
            }
        }
        return mPlayRuannble;
    }

    private class PlayRuannble implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                while (mInputStream.read(buffer)>=0)
                {
                    float seconds=mAudioTrack.getPlaybackHeadPosition() /mAudioTrack.getSampleRate( );
                    final String minute = seconds / 60 < 10 ? "0" + (int)seconds / 60 : (int)seconds / 60 + "";
                    final String second = seconds % 60 < 10 ? "0" + (int)seconds % 60 : (int) seconds % 60 + "";
                    mAudioTrack.write(buffer, 0, buffer.length);
                    //更新当前播放时间
                    currentDate.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            currentDate.setText(String.format(date, String.format("%s:%s", minute, second)));
                        }
                    });
                    float currentPosition=seconds/totalTime*100;
                    //更新进度条
                    mSeekBar.setProgress((int)currentPosition);
                    //当正在播放，且按暂停时
                    if(!isFinish&&mAudioTrack.getPlayState()==AudioTrack.PLAYSTATE_PAUSED)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                play.setSelected(false);
                                play.setText("回放录音");
                            }
                        });
                        isPlay=false;
                        return;
                    }
                }
                //播放完时候
                mSeekBar.setProgress(100);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // currentDate.setText(String.format(date, mMediaBean.getDate()));
                        play.setSelected(false);
                        play.setText("回放录音");
                    }
                });
                isFinish=true;
                isPlay=false;
                mInputStream.close();
                mAudioTrack.stop();
            } catch (Exception e)
            {
                e.printStackTrace();
                displayToast("读取异常");
            }
        }
    }

    /**获取音频流*/
    private void repeatPlay(String url)
    {
        OkHttpNetWorkUtil.getAsyn(url, new OkHttpNetWorkUtil.ResultCallback<Response>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                e.printStackTrace();
                displayToast("网络异常");
            }

            @Override
            public void onResponse(Response response)
            {
                try
                {
                    mInputStream=response.body().byteStream();
                    mInputStream.skip(0x2c);
                    mSeekBar.setProgress(0);
                    currentDate.setText(String.format(date, "00:00"));
                    play.setSelected(true);
                    play.setText("暂停");
                    isPlay=true;
                    mAudioTrack.play();
                    mExecutorService.submit(createPlayRuannble());
                } catch (IOException e)
                {
                    e.printStackTrace();
                    displayToast("读取异常");
                }
            }
        });
    }

    /**保存流到文件中*/
    private boolean downloadUrlToStream(InputStream inputStream,OutputStream outputStream)
    {
        try
        {
            while (inputStream.read(buffer)>=0)
            {
                outputStream.write(buffer,0,buffer.length);
            }
            displayToast("保存成功");
            return true;
        } catch (IOException e)
        {
            e.printStackTrace();
            return  false;
        }
        finally
        {
            try
            {
                if(inputStream!=null)
                    inputStream.close();
                if(outputStream!=null)
                    outputStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
