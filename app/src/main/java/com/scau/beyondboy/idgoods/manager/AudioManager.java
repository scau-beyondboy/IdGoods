package com.scau.beyondboy.idgoods.manager;

import android.media.MediaRecorder;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 录音类
 */
public class AudioManager {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private  String mCurrentFilePath;

    public AudioManager(String dir){
        mDir = dir;
    }


    public void prepareAudio(){
        try {

            File dir = new File(mDir);
        if(!dir.exists())
            dir.mkdirs();
        String fileName = generateFileName();
        File file = new File(dir,fileName);
            mCurrentFilePath = file.getAbsolutePath();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateFileName() {
        return UUID.randomUUID().toString()+".wma";
    }

    public int getVoiceLevel(){
        try{
            return mMediaRecorder.getMaxAmplitude();
        }catch (Exception e)
            {
            }
        return 1;
    }
    public void release(){
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }
    public void cancel(){
        if(mCurrentFilePath!=null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }

    }

    public  String getmCurrentFilePath() {
        return mCurrentFilePath;
    }
}
