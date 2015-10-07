package com.scau.beyondboy.idgoods.manager;

import android.media.*;
import android.media.AudioManager;
import android.widget.Toast;

import java.io.IOException;

/**
 *播放录音类
 */
public class MediaManager {
    private  MediaPlayer mMediaPlayer;
    private  boolean isPause;

    //回调接口
    public interface SoundFinishListener {
        void onFinish();
    }
    public SoundFinishListener mListener;
    public void setOnSoundFinishListener(SoundFinishListener listener){
              mListener = listener;
    }

    public int  getPosition(){
        return mMediaPlayer.getCurrentPosition();
    }
    public int getTime(){return  mMediaPlayer.getDuration();}
    public boolean playing(){return mMediaPlayer.isPlaying();}


    public void playSound(String filePath){
        if(mMediaPlayer==null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                        mListener.onFinish();
                }
            });
        }

        else{
            mMediaPlayer.reset();
        }

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }
    public  void pause(){
        //如果没有mMediaPlayer!=null，空指针异常
        //如果没有mMediaPlayer.isPlaying()，没播放却暂停报错
        if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            isPause = true;
        }
    }
    public  void resume(){
        if(mMediaPlayer!=null&&isPause) {
          mMediaPlayer.start();
            isPause = false;
        }
        }
    public  void release(){
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    }

