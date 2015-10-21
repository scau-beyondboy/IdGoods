package com.scau.beyondboy.idgoods.fragment;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.scau.beyondboy.idgoods.MainActivity;
import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.consts.Consts;
import com.scau.beyondboy.idgoods.manager.ThreadManager;
import com.scau.beyondboy.idgoods.model.MediaBean;
import com.scau.beyondboy.idgoods.model.ResponseObject;
import com.scau.beyondboy.idgoods.model.UploadBean;
import com.scau.beyondboy.idgoods.utils.OkHttpNetWorkUtil;
import com.scau.beyondboy.idgoods.utils.ShareUtils;
import com.scau.beyondboy.idgoods.view.CircleProgressBar;
import com.squareup.okhttp.Request;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-18
 * Time: 02:05
 * 播放录音界面
 */
public class FragmentPlay extends Fragment
{
    private static final String TAG = FragmentPlay.class.getName();
    @Bind(R.id.seekbar)
    SeekBar mSeekBar;
    @Bind(R.id.current_date)
    TextView currentDate;
    @Bind(R.id.nickname)
    TextView nickName;
    @Bind(R.id.total_date)
    TextView totalDate;
    @Bind(R.id.play)
    Button play;
    private MediaBean mMediaBean;
    private AudioTrack mAudioTrack;
    private int mChannelOutMono;
    private File audioFile;
    private FileInputStream mInputStream;
    private byte[] buffer;
    private boolean isPlay=false;
    private boolean isFinish=true;
    private ExecutorService mExecutorService;
    private String date;
    private long totalTime;
    private PlayRuannble mPlayRuannble;
    private MainActivity mActivity;
    private String mToken;
    @Bind(R.id.progressbar)
    CircleProgressBar mProgressBar;
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mActivity=(MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.playrecord,container,false);
        ButterKnife.bind(this,view);
        mMediaBean=getArguments().getParcelable(Consts.MEDIA_BEAN);
        init();
        nickName.setText(ShareUtils.getAccount(mActivity));
        mMediaBean =getArguments().getParcelable(Consts.MEDIA_BEAN);
        totalDate.setText(mMediaBean.getDate());
        date=getResources().getString(R.string.play_date);
        String dateArr[]=mMediaBean.getDate().split(":");
        int minute=Integer.valueOf(dateArr[0]);
        int second=Integer.valueOf(dateArr[1]);
        totalTime=minute*60+second;
        buffer=new byte[mMediaBean.getBufferSize()];
        return view;
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
            if(!audioFile.exists())
            {
                displayToast("音频文件已删除");
                return;
            }
            //重复播放
            if(isFinish)
            {
                try
                {
                    mExecutorService=ThreadManager.createThreadPool();
                    isFinish=false;
                    mInputStream = new FileInputStream(audioFile);
                    mInputStream.skip(0x2c);
                    currentDate.setVisibility(View.VISIBLE);
                    mSeekBar.setProgress(0);
                    currentDate.setText(String.format(date, "00:00"));
                } catch (IOException e)
                {
                    e.printStackTrace();
                    displayToast("文件读入异常");
                }
            }
            play.setSelected(true);
            play.setText("暂停");
            isPlay=true;
            mAudioTrack.play();
            mExecutorService.submit(createPlayRuannble());
        }
    }

    @OnClick({R.id.delete,R.id.uploading})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.delete:
                if(audioFile.exists())
                {
                    audioFile.delete();
                    displayToast("删除成功");
                }
                else
                {
                    displayToast("该音频文件已删除");
                }
                break;
            case R.id.uploading:
                if(audioFile.exists())
                {
                    OkHttpNetWorkUtil.postAsyn(Consts.UPLOAD_TOKEN, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
                    {
                        @Override
                        public void onError(Request request, Exception e)
                        {
                            e.printStackTrace();
                            displayToast("上传文件失败");
                        }

                        @Override
                        public void onResponse(ResponseObject<Object> response)
                        {
                            UploadBean uploadBean=parseUpLoadingDateJson(response);
                            if(uploadBean!=null)
                            {
                                mToken=uploadBean.getToken();
                                mProgressBar.setVisibility(View.VISIBLE);
                                upLoading();
                            }
                        }
                    });
                }
                else
                {
                    displayToast("音频文件已删除不能上传");
                }
                break;
        }
    }
    private void init()
    {
        ThreadManager.scoolPoolSize=1;
        if(mMediaBean.getChannelInMono()== AudioFormat.CHANNEL_IN_MONO)
        {
            mChannelOutMono =AudioFormat.CHANNEL_OUT_MONO;
        }
        else
        {
            mChannelOutMono =AudioFormat.CHANNEL_OUT_STEREO;
        }
        mAudioTrack=new AudioTrack(AudioManager.STREAM_MUSIC,mMediaBean.getSamplerateinhz(), mChannelOutMono,mMediaBean.getAudioformat(),mMediaBean.getBufferSize(),AudioTrack.MODE_STREAM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mAudioTrack.setVolume(1.0f);
        }
        else
        {
            //noinspection deprecation
            mAudioTrack.setStereoVolume(1.0f,1.0f);
        }
        audioFile=new File(mMediaBean.getFilePath());
    }

    private void displayToast(String warnning)
    {
        Toast.makeText(mActivity, warnning, Toast.LENGTH_SHORT).show();
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
                        mActivity.runOnUiThread(new Runnable()
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
                mActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        currentDate.setText(String.format(date, mMediaBean.getDate()));
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

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ThreadManager.release();
        mAudioTrack.release();
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

    /**上传文件操作*/
    private void upLoading()
    {
        UploadManager manager=new UploadManager();
        manager.put(audioFile, mMediaBean.getFilePath(), mToken, new UpCompletionHandler()
        {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response)
            {
                mProgressBar.setVisibility(View.INVISIBLE);
                displayToast("上传成功");
                saveRadio();
            }
        },new UploadOptions(null, null, false, new UpProgressHandler()
        {
            @Override
            public void progress(String key, double percent)
            {
                int progress=(int)(percent*100);
                mProgressBar.setProgress(progress);
            }
        },null));
    }

    private void saveRadio()
    {
        ArrayMap<String,String> params=new ArrayMap<>();
        String fileName=mMediaBean.getFilePath();
        fileName=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
        params.put(Consts.RADIO_KEY,fileName);
        params.put(Consts.SERIALNUMBERVALUEKEY,ShareUtils.getSerialNumberValue(mActivity));
        params.put(Consts.CUSTOMERID_KEY,ShareUtils.getUserId(mActivity));
        OkHttpNetWorkUtil.postAsyn(Consts.SAVE_RADIO, new OkHttpNetWorkUtil.ResultCallback<ResponseObject<Object>>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                displayToast("网络异常");
                e.printStackTrace();
            }

            @Override
            public void onResponse(ResponseObject<Object> responseObject)
            {
                if (responseObject.getResult() == 1)
                {
                    displayToast("保存文件成功");
                } else
                {
                    displayToast("保存文件失败");
                }
            }
        },params);
    }
    /**解析json*/
    private UploadBean parseUpLoadingDateJson(ResponseObject<Object> responseObject)
    {
        Gson gson=new Gson();
        String data=gson.toJson(responseObject.getData());
        if(responseObject.getResult()==1)
        {
            return gson.fromJson(data,UploadBean.class);
        }
        else
        {
            displayToast(data);
            return null;
        }
    }
}
