package com.scau.beyondboy.idgoods;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jakewharton.disklrucache.DiskLruCache;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.CollectBean;
import com.scau.beyondboy.idgoods.model.CollectInfo;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.ScanCodeBean;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.StorageUtils;
import com.scau.beyondboy.idgoods.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-19
 * Time: 21:40
 * 收听录音界面
 */
public class ListenActivity extends AppCompatActivity
{
    private static final String TAG = ListenActivity.class.getName();
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
    private Handler mHandler=new Handler();
    private byte[] buffer;
    private int bufferSize=2048;
    private boolean isPlay=false;
    private boolean isFinish=true;
    private boolean isFirstPlay=false;
    private ExecutorService mExecutorService;
    private boolean isFistLoad=false;
    private String date;
    private long totalTime;
    private String totalTimeStr;
    private static DiskLruCache sDiskLruCache;
    private InputStream mInputStream;
    private PlayRuannble mPlayRuannble;
  //  private static ReentrantLock sReentrantLock = new ReentrantLock();
    //private static Condition sCondition=sReentrantLock.newCondition();
    private MediaPlayer player;
    //private Timer mTimer;
    Uri uri;
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

    private ScanCodeBean mScanCodeBean;
    private CollectBean mCollectBean;

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        ButterKnife.bind(this);
        date=getResources().getString(R.string.play_date);
        mScanCodeBean = getIntent().getParcelableExtra(Consts.SCAN_CODE_BEAN);
        Log.i(TAG,"空吗："+mScanCodeBean);
        if(mScanCodeBean!=null)
        {
            nickName.setText(mScanCodeBean.getAdversementName());
            url=mScanCodeBean.getAddress();
            uri=Uri.parse(url);
        }
        else
        {
            mCollectBean = getIntent().getParcelableExtra(Consts.COLLECT_BEAN);
            nickName.setText(mCollectBean.getAdvertisementName());
            /*OkHttpNetWorkUtil.postAsyn(Consts.GET_COLLECT_INFO, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
            {
                @Override
                public void onError(Request request, Exception e)
                {
                    e.printStackTrace();
                    displayToast("错误");
                }

                @Override
                public void onResponse(ResponseObject<Object> response)
                {
                    try
                    {
                        Log.i(TAG, "返回内容:   " + response);
                        CollectInfo collectInfo = parseCollectInfoDataJson(response);
                        if (collectInfo != null)
                        {
                            Log.i(TAG, "这里吗");
                            url = collectInfo.getRadioAddress();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });*/
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Gson gson=new Gson();
                    try
                    {
                        ResponseObject responseObject=gson.fromJson(OkHttpNetWorkUtil.postString(Consts.GET_COLLECT_INFO, new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY, mCollectBean.getSerialNumberValue())), ResponseObject.class);
                        Log.i(TAG, "返回内容:   " + responseObject);
                        CollectInfo collectInfo = parseCollectInfoDataJson(responseObject);
                        if (collectInfo != null)
                        {
                            Log.i(TAG, "这里吗");
                            url = collectInfo.getRadioAddress();
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
            //return;
        }
        while (url==null);
        uri=Uri.parse(url);
        init();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                //播放完时候
                mSeekBar.setProgress(100);
                play.setSelected(false);
                play.setText("回放录音");
                isFinish = true;
                isPlay = false;
                currentDate.setText(totalTimeStr);
            }
        });
    }

    @Override
    public void finishAffinity()
    {
        super.finishAffinity();
    }

    @OnClick(R.id.play)
    public void play()
    {
        /*if(!isFistLoad)
        {
            OkHttpNetWorkUtil.postAsyn(Consts.GET_COLLECT_INFO, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
            {
                @Override
                public void onError(Request request, Exception e)
                {
                    e.printStackTrace();
                    displayToast("错误");
                }

                @Override
                public void onResponse(ResponseObject<Object> response)
                {
                    try
                    {
                        Log.i(TAG, "返回内容:   " + response);
                        CollectInfo collectInfo = parseCollectInfoDataJson(response);
                        if (collectInfo != null)
                        {
                            Log.i(TAG, "这里吗");
                            url = collectInfo.getRadioAddress();
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    // sCondition.notifyAll();
                }
            }, new OkHttpNetWorkUtil.Param(Consts.SERIALNUMBERVALUEKEY, mCollectBean.getSerialNumberValue()));
            while (url==null);
            uri=Uri.parse(url);
            init();
            isFistLoad=true;
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    //播放完时候
                    mSeekBar.setProgress(100);
                    play.setSelected(false);
                    play.setText("回放录音");
                    isFinish = true;
                    isPlay = false;
                    currentDate.setText(totalTimeStr);
                }
            });
        }*/
        if(isPlay)
        {
            player.pause();
            isPlay=false;
            play.setSelected(false);
            play.setText("回放录音");
        }
        else
        {
            //重复播放
            if(isFinish)
            {
                try
                {
                    DiskLruCache.Snapshot snapshot=sDiskLruCache.get(StringUtils.md5(url));
                    if(snapshot!=null&&!isFirstPlay)
                    {
                        mInputStream=snapshot.getInputStream(0);
                        isFirstPlay=true;
                        Log.i(TAG,"缓存拉取");
                        File tempFile=File.createTempFile("tempfile",".wav",getDir("postcardtmp",0));
                        FileOutputStream out=new FileOutputStream(tempFile);
                        while ( mInputStream.read(buffer)>0 )
                        {
                            out.write(buffer,0,buffer.length);
                        }
                        out.close();
                        player.reset();
                        player.setDataSource(new FileInputStream(tempFile).getFD());
                        player.prepare();
                    }
                    else
                    {
                        player.seekTo(0);
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                    displayToast("网络异常");
                    return;
                }
            }
            isFinish=false;
            play.setSelected(true);
            play.setText("暂停");
            isPlay=true;
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(!isFinish&&isPlay&&player!=null)
                    {
                        float seconds=player.getCurrentPosition()/1000;
                        final String minute = seconds / 60 < 10 ? "0" + (int) seconds / 60 : (int) seconds / 60 + "";
                        final String second = seconds % 60 < 10 ? "0" + (int) seconds % 60 : (int) seconds % 60 + "";
                        currentDate.setText(String.format(date, String.format("%s:%s", minute, second)));
                        int progressValue=(int)(seconds/totalTime*100);
                        mSeekBar.setProgress(progressValue);
                        mHandler.postDelayed(this, 1000);
                    }
                }
            });
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
        OkHttpNetWorkUtil.downloadAsyn(url, StorageUtils.getCacheDirectory(this).getAbsolutePath(), new OkHttpNetWorkUtil.ResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                e.printStackTrace();
                displayToast("出错");
            }

            @Override
            public void onResponse(String response)
            {
                try
                {
                    //缓存中文件中
                    DiskLruCache.Editor editor = sDiskLruCache.edit(StringUtils.md5(url));
                    Log.i(TAG, "数据:" + response);
                    mInputStream =new FileInputStream(new File(response));
                    if (downloadUrlToStream(mInputStream, editor.newOutputStream(0)))
                    {
                        editor.commit();
                    } else
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
        player=MediaPlayer.create(this, uri);
        buffer=new byte[bufferSize];
        int  seconds=player.getDuration()/1000;
        totalTime=player.getDuration()/1000;
        final String minute = seconds / 60 < 10 ? "0" + (int)seconds / 60 : (int)seconds / 60 + "";
        final String second = seconds % 60 < 10 ? "0" + (int)seconds % 60 : (int) seconds % 60 + "";
        total_date.setText(minute+":"+second);
        totalTimeStr=minute+":"+second;
        ThreadManager.scoolPoolSize=1;
        mExecutorService=ThreadManager.createThreadPool();
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
            player.start();
        }
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        player.stop();
        player.release();
        player=null;
    }

    /**解析json*/
    private CollectInfo parseCollectInfoDataJson(ResponseObject<Object> responseObject)
    {
        try
        {
            Gson gson=new Gson();
            String data=gson.toJson(responseObject.getData());
            if(responseObject.getResult()==1)
            {
                return gson.fromJson(data,CollectInfo.class);
            }
            else
            {
                displayToast(data);
                return null;
            }
        } catch (JsonSyntaxException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
