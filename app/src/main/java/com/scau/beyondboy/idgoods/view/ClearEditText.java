package com.scau.beyondboy.idgoods.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.scau.beyondboy.idgoods.R;


/**
 * Created by acer on 2015/7/22.
 * 自定义view 实现清除功能的edt
 */
public class ClearEditText extends EditText implements View.OnFocusChangeListener,TextWatcher{
    private static final String TAG = ClearEditText.class.getName();
    /**
     * 删除按钮的引用
     */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean hasFocus;

    public ClearEditText(Context context)
    {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){

        mClearDrawable = getCompoundDrawables()[2];
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        if (mClearDrawable == null)
        {
            //noinspection deprecation
            mClearDrawable = getResources().getDrawable(R.drawable.selector_ic_delete);
        }

        assert mClearDrawable != null;
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //默认设置隐藏图标
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }
    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    public void setClearIconVisible(boolean visible)
    {
        Drawable right = visible ? mClearDrawable:null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }
    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    public void onFocusChange(View v, boolean hasFocus)
    {
        this.hasFocus = hasFocus;
        if (hasFocus)
        {
            setClearIconVisible(getText().length()>0);
        }else
        {
            setClearIconVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void afterTextChanged(Editable s)
    {
        if (hasFocus)
        {
            setClearIconVisible(getText().length()>0);
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
    {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }
    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event)
    {

        if (event.getAction() == MotionEvent.ACTION_UP)
        {

            if (getCompoundDrawables()[2]!=null)
            {
                boolean touchable = event.getX()>(getWidth()-getTotalPaddingRight())
                        &&(event.getX()<((getWidth() - getPaddingBottom())));
                if (touchable)
                {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }
    public void setShakeAnimation()
    {
        this.setAnimation(shakeAnimation(5));
    }
    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     * @return
     */
    private Animation shakeAnimation(int counts)
    {
        Animation translateAnimation = new TranslateAnimation(0,10,0,0);

        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}
