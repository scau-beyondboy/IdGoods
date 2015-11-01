package com.scau.beyondboy.idgoods.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.scau.beyondboy.idgoods.R;
import com.scau.beyondboy.idgoods.RecordActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-28
 * Time: 09:25
 * 播放弹出菜单
 */
public class PlayPopupWindow extends PopupWindow
{
    @Bind(R.id.delete)
    Button delete;
    @Bind(R.id.uploading)
    Button uploading;
    @Bind(R.id.play)
    Button play;
    private PlayCallaBack mPlayCallaBack;
    private RecordActivity mActivity;
    public PlayPopupWindow(AppCompatActivity context)
    {
        super(context);
        mActivity=(RecordActivity)context;
    }

    public void init(AppCompatActivity context)
    {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View playPopupView =inflater.inflate(R.layout.play_popupwindow,null,false);
        setContentView(playPopupView);
        ButterKnife.bind(this, playPopupView);
    }

    public void setPlayCallaBack(PlayCallaBack playCallaBack)
    {
        this.mPlayCallaBack=playCallaBack;
    }

    /**播放录音回调接口*/
    interface PlayCallaBack
    {
        void play();
        void delete();
        void upload();
    }

    @OnClick({R.id.play,R.id.delete,R.id.uploading})
    public void onClick(View view)
    {
        if(mPlayCallaBack==null)
            return;
        switch (view.getId())
        {
            case R.id.play:
                mPlayCallaBack.play();
                if(mActivity.getState()==4)
                {
                    delete.setClickable(true);
                    uploading.setClickable(true);
                }
                break;
            case R.id.delete:
                mPlayCallaBack.delete();
                if(mActivity.getState()==5)
                {
                    delete.setClickable(false);
                    uploading.setClickable(false);
                }
                break;
            case R.id.uploading:
                mPlayCallaBack.upload();
                break;
        }
    }
}
