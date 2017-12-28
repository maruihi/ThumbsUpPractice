package com.mr.thumbsuppractice.thumbsUpView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.mr.thumbsuppractice.R;

/**
 * 点赞布局
 * Author： by MR on 2017/12/22.
 */

public class ThumbsUpLayout extends LinearLayout implements View.OnClickListener {

    private int mCount;                          // 计数的初始值
    private boolean isThumbUp;                   // 是否是选中状态

    private ThumbsImgView mThumbsImgView;
    private ThumbsCountView mThumbsCountView;
    private ThumbsImgView.ThumbsUpResultListener mThumbsUpResultListener;
    private boolean isSetCount = false;
    private boolean isSetIsThumbUp = false;


    public ThumbsUpLayout(Context context) {
        this(context, null);
    }

    public ThumbsUpLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbsUpLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.thumbs_up_view);
        isThumbUp = a.getBoolean(R.styleable.thumbs_up_view_is_thumb_up, false);

        a.recycle();


        setGravity(Gravity.CENTER_VERTICAL);

        init();
    }

    private void init() {

        setOnClickListener(this);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("ThumbsUpLayout", "onDraw");
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.e("ThumbsUpLayout", "dispatchDraw");
        mThumbsCountView = findViewById(R.id.thumbs_count_view);
        mThumbsImgView = findViewById(R.id.thumbs_img_view);

        if (mThumbsCountView != null && !isSetCount) {
            mThumbsCountView.setCount(mCount);
            isSetCount = true;
        }
        if (mThumbsImgView != null && !isSetIsThumbUp) {
            mThumbsImgView.setIsThumbsUp(isThumbUp);
            isSetIsThumbUp = true;
        }
        if (mThumbsUpResultListener != null) {
            mThumbsImgView.setThumbsUpResultListener(mThumbsUpResultListener);
        }
    }

    @Override
    public void onClick(View view) {
        if (mThumbsImgView != null && mThumbsCountView != null) {

            isThumbUp = !isThumbUp;
            if (isThumbUp) {
                mCount += 1;
                mThumbsCountView.calculateChangeNum(1);
            } else {
                if (mCount > 0) {
                    mCount -= 1;
                    mThumbsCountView.calculateChangeNum(-1);
                }
            }
            mThumbsImgView.startAnim();
        }
    }

    public boolean isThumbUp() {
        return isThumbUp;
    }

    public void setThumbUp(boolean thumbUp) {
        isThumbUp = thumbUp;
        isSetIsThumbUp = false;
        if (mThumbsImgView != null) {
            mThumbsImgView.setIsThumbsUp(isThumbUp);
        }
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
        isSetCount = false;
        if (mThumbsCountView != null) {
            mThumbsCountView.setCount(mCount);
        }
    }

    public void setThumbsUpResultListener(ThumbsImgView.ThumbsUpResultListener mThumbsUpResultListener) {
        this.mThumbsUpResultListener = mThumbsUpResultListener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle data = new Bundle();
        data.putParcelable("superData", super.onSaveInstanceState());
        data.putInt("count", mCount);
        data.putBoolean("isThumbUp", isThumbUp);
        return data;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle data = (Bundle) state;
        Parcelable superData = data.getParcelable("superData");
        super.onRestoreInstanceState(superData);
        mCount = data.getInt("count");
        isThumbUp = data.getBoolean("isThumbUp", false);
        init();
    }

}


