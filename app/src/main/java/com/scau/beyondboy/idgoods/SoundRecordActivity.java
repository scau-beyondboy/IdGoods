package com.scau.beyondboy.idgoods;


import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.scau.beyondboy.idgoods.manager.AudioManager;
import com.scau.beyondboy.idgoods.manager.MediaManager;

/**
 * 录音主界面
 */

public class SoundRecordActivity extends AppCompatActivity implements MediaManager.SoundFinishListener {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_CHECKE = 3;
    private static final int STATE_PAUSE = 4;
    private int mCurState = STATE_NORMAL;

    private TextView tv_time1;
    private TextView tv_time2;
    private Button btn_record;
    private Button btn_cancel;
    private Button btn_post;
    private SeekBar bar_progress;

    private AudioManager mAudioManager;
    private MediaManager mMediaManager;


    private boolean hasPause;
    private boolean isRecording;
    private int second1 = 0;
    private int minute = 0;
    private int position = 0;
    private int second2 = 0;



    private Runnable mGetVoiceLevelRunnable, mGetBarProgress;

    private Handler mhandle;
    private static final int MSG_TIME_CHANGE = 5;
    private static final int MSG_BAR_CHANGE = 6;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_record);

        String dir = Environment.getExternalStorageDirectory() + "/test";
        mAudioManager = new AudioManager(dir);
        mMediaManager = new MediaManager();

        tv_time1 = (TextView) findViewById(R.id.time1);
        tv_time2 = (TextView) findViewById(R.id.time2);
        btn_record = (Button) findViewById(R.id.record);
        btn_cancel = (Button) findViewById(R.id.cancel);
        btn_post = (Button) findViewById(R.id.post);
        bar_progress = (SeekBar) findViewById(R.id.progress);
        mMediaManager.setOnSoundFinishListener(this);
        bar_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mhandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_TIME_CHANGE:
                        //mAudioManager.getVoiceLevel()
                        tv_time1.setText(String.valueOf("正在播放" + " " + getTime(minute) + ":" + getTime(second1)));
                        second1++;
                        break;
                    case MSG_BAR_CHANGE:
                        bar_progress.setProgress(position);
                        tv_time1.setText(String.valueOf("正在播放" + " " + getTime(minute) + ":" + getTime(second2)));
                        break;

                }
                super.handleMessage(msg);

            }
        };
        mGetVoiceLevelRunnable = new Runnable() {
            @Override
            public void run() {
                mhandle.sendEmptyMessage(MSG_TIME_CHANGE);
                mhandle.postDelayed(this,1000);
            }
        };
        mGetBarProgress = new Runnable() {
            @Override
            public void run() {
                while (mMediaManager.playing()) {
                    position = mMediaManager.getPosition();
                    second2 = (int)Math.floor(position/1000);
                    addTime(second2);
                    mhandle.sendEmptyMessage(MSG_BAR_CHANGE);
                }

            }
        };

        btn_record.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mCurState == STATE_RECORDING) {
                    isRecording = true;
                    mAudioManager.prepareAudio();
                    new Thread(mGetVoiceLevelRunnable).start();
                }
                return false;
            }
        });

        btn_record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        switch (mCurState) {
                            case STATE_NORMAL:
                                ChangeState(STATE_RECORDING);
                                break;

                            case STATE_CHECKE:
                                if (hasPause == false) {
                                    ChangeState(STATE_PAUSE);
                                    mMediaManager.playSound(mAudioManager.getmCurrentFilePath());
                                    bar_progress.setMax(mMediaManager.getTime());
                                    new Thread(mGetBarProgress).start();

                                } else {
                                    mMediaManager.resume();
                                    ChangeState(STATE_PAUSE);
                                    new Thread(mGetBarProgress).start();
                                }
                                break;

                            case STATE_PAUSE:
                                hasPause = true;
                                ChangeState(STATE_CHECKE);
                                mMediaManager.pause();
                                break;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mCurState == STATE_RECORDING) {
                            ChangeState(STATE_CHECKE);
                            if (isRecording == false) {
                                reset();
                            } else {
                                mhandle.removeCallbacks(mGetVoiceLevelRunnable);
                                mAudioManager.release();
                                tv_time2.setText(getTime(minute) + ":" + getTime(second1));
                                tv_time1.setText("");
                                second1 = 0;
                                minute = 0;
                                bar_progress.setVisibility(View.VISIBLE);
                                btn_cancel.setVisibility(View.VISIBLE);
                                btn_post.setVisibility(View.VISIBLE);
                            }
                        }

                        break;
                }

                return false;
            }


        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaManager.release();
                mAudioManager.cancel();
                btn_cancel.setVisibility(View.INVISIBLE);
                btn_post.setVisibility(View.INVISIBLE);
                bar_progress.setVisibility(View.INVISIBLE);
                ChangeState(STATE_NORMAL);
                isRecording = false;
                hasPause = false;
                second1 = 0;
                second2 = 0;
                minute = 0;
                position = 0;
                tv_time1.setText("");
                tv_time2.setText("");
                bar_progress.setProgress(0);

            }
        });

    }

    private void addTime(int second) {
        if (second > 60) {
            minute = second / 60;
            minute = second % 60;
        }

    }

    private String getTime(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }


    private void reset() {
        ChangeState(STATE_NORMAL);
    }

    private void ChangeState(int state) {
        mCurState = state;
        switch (state) {
            case STATE_NORMAL:
                btn_record.setText(R.string.state_normal);
                //btn_record.setBackgroundResource(R.id.);
                break;
            case STATE_RECORDING:
                btn_record.setText(R.string.state_recording);
                //LineManger.Recording();
                break;
            case STATE_CHECKE:
                btn_record.setText(R.string.state_check);
                //LinManger.Checking();
                break;
            case STATE_PAUSE:
                btn_record.setText(R.string.state_stop);
                //LinManger.Stopping();
                break;


        }
    }
    @Override
    public void onFinish() {
        bar_progress.setProgress(0);
        hasPause = false;
        ChangeState(STATE_CHECKE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaManager.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaManager.release();
    }


}
