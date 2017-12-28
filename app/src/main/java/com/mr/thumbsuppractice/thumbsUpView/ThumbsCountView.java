package com.mr.thumbsuppractice.thumbsUpView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mr.thumbsuppractice.CalculateUtil;
import com.mr.thumbsuppractice.R;

/**
 * 计数部分的View
 * Author： by MR on 2017/12/22.
 */

public class ThumbsCountView extends View {

    private final int DEFAULT_TEXT_SIZE = 32;
    private final int DEFAULT_TEXT_COLOR = Color.parseColor("#999999");
    private final int DEFAULT_DRAWABLE_PADDING = 4;

    private int textSize;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int count;

    private String[] mText = new String[3];
    private TextPoint[] mTextPoint = new TextPoint[3];
    private boolean mToBigger;
    private float mOldOffY;
    private float mNewOffY;

    private Paint paint;
    private Rect rect;
    private OnLoadCompleteListener mOnLoadCompleteListener;

    public ThumbsCountView(Context context) {
        this(context, null);
    }

    public ThumbsCountView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbsCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.thumbs_count_view);
        count = typedArray.getInteger(R.styleable.thumbs_count_view_count, 0);
        textSize = typedArray.getInteger(R.styleable.thumbs_count_view_text_size, DEFAULT_TEXT_SIZE);
        String color = typedArray.getString(R.styleable.thumbs_count_view_text_color);
        typedArray.recycle();

        if (!TextUtils.isEmpty(color)) {
            textColor = Color.parseColor(color);
        }

        initView();

    }

    private void initView() {
        rect = new Rect();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.getTextBounds(String.valueOf(count), 0, String.valueOf(count).length(), rect);

        mTextPoint[0] = new TextPoint();
        mTextPoint[1] = new TextPoint();
        mTextPoint[2] = new TextPoint();

        calculateChangeNum(0);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("ThumbsCountView", "width:" + getRealWidth() + ";height:" + (rect.height() * 3 + getPaddingBottom() + getPaddingTop()));
        setMeasuredDimension(CalculateUtil.getDefaultSize(widthMeasureSpec, getRealWidth() + (int) Math.ceil(paint.measureText("9"))),
                CalculateUtil.getDefaultSize(heightMeasureSpec, rect.height() * 3 + getPaddingBottom() + getPaddingTop()));


    }

    private int getRealWidth() {
        return getPaddingLeft() == 0 ? rect.width() + DEFAULT_DRAWABLE_PADDING + getPaddingRight() : rect.width() + getPaddingLeft() + getPaddingRight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        calculateLocation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制不变的部分
        canvas.drawText(mText[0], mTextPoint[0].x, mTextPoint[0].y, paint);

        // 绘制改变的部分
        canvas.drawText(mText[1], mTextPoint[1].x, mTextPoint[1].y, paint);

        // 绘制新字符串部分
        canvas.drawText(mText[2], mTextPoint[2].x, mTextPoint[2].y, paint);
    }

    /**
     * 计算数字中不变的部分、变化的部分、新的部分的字符串
     *
     * @param change
     */
    public void calculateChangeNum(int change) {
        if (change == 0) {
            mText[0] = String.valueOf(count);
            mText[1] = "";
            mText[2] = "";
            return;
        }

        if (count == 0 && change < 0)
            return;

        String oldCount = String.valueOf(count);
        String newCount = String.valueOf(count + change);

        for (int i = 0; i < oldCount.length(); i++) {
            if (oldCount.charAt(i) != newCount.charAt(i)) {
                mText[0] = i == 0 ? "" : newCount.substring(0, i);
                mText[1] = oldCount.substring(i);
                mText[2] = newCount.substring(i);
                break;
            }
        }

        count += change;
        mToBigger = change > 0;
        startAnim();

    }

    /**
     * 计算三部分Text的位置
     */
    private void calculateLocation() {
        String countStr = String.valueOf(count);
        float avgWidth = paint.measureText(countStr) / countStr.length();

        float y = rect.height() * 2 + getPaddingTop();

        mTextPoint[0].x = getPaddingLeft();
        mTextPoint[1].x = mTextPoint[0].x + mText[0].length() * avgWidth;
        mTextPoint[2].x = mTextPoint[1].x;
        mTextPoint[0].y = y;
        mTextPoint[1].y = y + mOldOffY;
        mTextPoint[2].y = y + mNewOffY;
    }

    private void startAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "textOffSetY", 0, mToBigger ? -rect.height() * 2 : rect.height() * 2);
        animator.setDuration(250);
        animator.start();
    }

    @Keep
    public void setTextOffSetY(float textOffSetY) {
        if (textOffSetY < 0) { // 点赞，mCount变大
            mOldOffY = textOffSetY - getPaddingTop() - 6;
            mNewOffY = rect.height() * 2 + textOffSetY;
        } else { // 取消点赞，mCount变小
            mOldOffY = textOffSetY + getPaddingBottom() + 6;
            mNewOffY = textOffSetY - rect.height() * 2;
        }

        calculateLocation();
        postInvalidate();
    }


    //region 设置属性

    public void setCount(int mCount) {
        this.count = mCount;

        paint.getTextBounds(String.valueOf(count), 0, String.valueOf(count).length(), rect);
        calculateChangeNum(0);
        requestLayout();
        postInvalidate();
    }

    //endregion 设置属性

    interface OnLoadCompleteListener {
        void complete();
    }

}
