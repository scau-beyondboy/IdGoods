package com.scau.beyondboy.idgoods.view;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

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
    @Bind(R.id.seekbar)
    SeekBar mSeekbar;
    @Bind(R.id.date)
    TextView mDate;
   // private AudioTrack mAudioTrack;
    private String url;
    /**0代表准备播放，1代表正在播放，2代表暂停，3代表重新播放*/
    private AtomicInteger state=new AtomicInteger(-1);
    private MediaPlayer player;
    private PlayRuannble mPlayRuannble;
    private File mPostCardVoice;
    private int totalTime;
    /**是否保存*/
    private boolean isSave=false;
    private final ThreadLocal<OnekeyShare> mOks = new ThreadLocal<>();

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
        ThreadManager.scoolPoolSize=1;
        OkHttpNetWorkUtil.setCache(StorageUtils.getOwnCacheDirectory(this, "poscard"), 100 * 1024 * 1024);
        //二维码扫描方式听明信片
        if(getIntent().getBooleanExtra(Consts.RECEIVEBLESS,false))
        {
            ScanCodeBean scanCodeBean = getIntent().getParcelableExtra(Consts.SCAN_CODE_BEAN);
            url= scanCodeBean.getAddress();
            Log.i(TAG,"在哪里啊");
            getRecordFile();
        }
        //收藏详情
        else
        {
            CollectBean collectBean = getIntent().getParcelableExtra(Consts.COLLECT_BEAN);
            android.support.v4.util.ArrayMap<String,String> params=new android.support.v4.util.ArrayMap<>();
            params.put(Consts.SERIALNUMBERVALUEKEY, collectBean.getSerialNumberValue());
            Log.i(TAG,"这里了吗");
            NetWorkHandlerUtils.postAsynHandler(Consts.GET_COLLECT_INFO,params, null, null, new NetWorkHandlerUtils.PostSuccessCallback<CollectInfo>()
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
        String filePath = StorageUtils.getIndividualCacheDirectory(this, "postcard").getAbsolutePath() + "/" + Util.md5Hex(url) + ".0";
        mPostCardVoice=new File(filePath);
        if(mPostCardVoice.exists())
        {
            setInfo();
            isSave=true;
        }
        else
        {
            NetWorkHandlerUtils.downloadFileHandler(url, StorageUtils.getIndividualCacheDirectory(this, "postcard").getAbsolutePath(), new NetWorkHandlerUtils.PostSuccessCallback<String>()
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
        player=new MediaPlayer();
        try
        {
            player.setDataSource(mPostCardVoice.toString());
            player.prepare();
            totalTime=player.getDuration()/1000;
            mDate.setText(TimeUtils.converTommss(totalTime));
            mSeekbar.setMax(totalTime);
            state.set(0);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    try
                    {
                        state.set(3);
                        player.stop();
                        //播放完时候
                        mPlay.setSelected(false);
                        mPlay.setText("播放录音");
                        player.prepare();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            ThreadManager.addTask(new Runnable()
            {
                Handler mHandler=new Handler(getMainLooper())
                {
                    @Override
                    public void handleMessage(Message msg)
                    {
                        try
                        {
                            if(msg.what== 9 &&state.get()==1&&player!=null)
                            {
                                mSeekbar.setProgress(player.getCurrentPosition()/1000);
                            }
                            else  if(state.get()==3)
                            {
                                mSeekbar.setProgress(totalTime);
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                @Override
                public void run()
                {
                    //noinspection InfiniteLoopStatement
                    while(true)
                    {
                        try
                        {
                            Thread.sleep(1000);
                            mHandler.sendEmptyMessage(9);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class PlayRuannble implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                if(state.get()!=3)
                {
                    player.start();
                }
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
            player.pause();
            mPlay.setSelected(false);
            mPlay.setText("播放录音");
        }
        else
        {
            //重新播放时候
            if(state.get()!=2)
            {
                try
                {
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
            player.start();
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
        MyApplication.sActivityMap.get(Consts.BLESS_ACTIVITY).finish();
        MyApplication.sActivityMap.remove(Consts.BLESS_ACTIVITY);
        ThreadManager.release();
        if(player!=null)
        {
            player.pause();
            player.stop();
            player.release();
            player=null;
        }
        if(!isSave&&mPostCardVoice!=null&&mPostCardVoice.exists())
        {
            String fileName=mPostCardVoice.getAbsolutePath();
            fileName=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length()-2);
            Log.i(TAG, "文件名：  " + fileName);
            OkHttpNetWorkUtil.removeCacheFile(fileName);
        }
        Log.i(TAG,"被销毁在这里" );
        MyApplication.sActivityMap.clear();
    }

    @OnClick(R.id.share)
    public void share()
    {
        showShare();
    }

    private void showShare()
    {
        ShareSDK.initSDK(this);
        mOks.set(new OnekeyShare());
        //关闭sso授权
        mOks.get().disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        mOks.get().setTitle("idgoods留言");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        mOks.get().setText("idgoods分享了明信片留言给你，明信片留言地址：" + url);
        mOks.get().setTitleUrl("idgoods分享了明信片留言给你" + url);
        mOks.get().setAddress(url);
        // 启动分享GUI
        mOks.get().show(this);
    }
}
