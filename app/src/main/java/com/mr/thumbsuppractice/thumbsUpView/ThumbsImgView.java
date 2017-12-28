package com.mr.thumbsuppractice.thumbsUpView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.mr.thumbsuppractice.CalculateUtil;
import com.mr.thumbsuppractice.R;


/**
 * 图标部分
 * Author： by MR on 2017/12/22.
 */

public class ThumbsImgView extends View {


    private boolean hasBackGround;
    private boolean isThumbUp;

    private Bitmap unSelectBitmap;
    private Bitmap selectBitmap;
    private Bitmap shiningBitmap;
    private int sideLength;
    private int colorBg;

    private Paint paint;
    private Paint circlePaint;
    private Matrix matrix;

    private float selectX;
    private float selectY;
    private float unSelectX;
    private float unSelectY;
    private float shiningX;
    private float shiningY;
    private float circleX;
    private float circleY;
    private float circleRadius;

    private float faction = 1f;
    private int colorFaction;

    private int mClickCount;// 偶数是未点赞，奇数是已点赞
    private long lastStartTime;
    private float radiusFaction;
    private int mEndCount;
    private ThumbsUpResultListener mThumbsUpResultListener;
    private AnimatorSet mThumbUpAnim;
    private boolean isFastAnim;

    {
        shiningBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_thumbs_up_selected_shining);
    }

    public ThumbsImgView(Context context) {
        this(context, null);
    }

    public ThumbsImgView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbsImgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.thumbs_img_view);
        hasBackGround = typedArray.getBoolean(R.styleable.thumbs_img_view_has_background, true);
        BitmapDrawable drawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.thumbs_img_view_selected_img);
        BitmapDrawable unDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.thumbs_img_view_unselected_img);
        String color = typedArray.getString(R.styleable.thumbs_img_view_color_value);
        typedArray.recycle();


        if (drawable == null) {
            selectBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_thumbs_up_selected);
        } else {
            selectBitmap = drawable.getBitmap();
        }

        if (unDrawable == null) {
            unSelectBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_thumbs_up_unselected);
        } else {
            unSelectBitmap = unDrawable.getBitmap();
        }

        if (TextUtils.isEmpty(color)) {
            colorBg = Color.parseColor("#e24d3d");
        } else {
            colorBg = Color.parseColor(color);
        }

        init();

    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(colorBg);
        circlePaint.setStrokeWidth(2);
        circlePaint.setStyle(Paint.Style.STROKE);

        matrix = new Matrix();


        int with = Math.max(selectBitmap.getWidth(), unSelectBitmap.getWidth());
        int height = Math.max(selectBitmap.getHeight(), unSelectBitmap.getHeight());
        sideLength = Math.max(with, height + (shiningBitmap.getHeight() - 16) * 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(CalculateUtil.getDefaultSize(widthMeasureSpec, sideLength / 2 + selectBitmap.getHeight() / 2 + getPaddingLeft() + getPaddingRight()),
                CalculateUtil.getDefaultSize(heightMeasureSpec, sideLength + getPaddingBottom() + getPaddingTop()));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        calculateLocation();
    }

    private void calculateLocation() {

        selectX = (getWidth() - getPaddingLeft() - getPaddingRight() - selectBitmap.getWidth()) / 2;
        selectY = (getHeight() - getPaddingTop() - getPaddingBottom() - selectBitmap.getHeight()) / 2;

        unSelectX = (getWidth() - getPaddingLeft() - getPaddingRight() - unSelectBitmap.getWidth()) / 2;
        unSelectY = (getHeight() - getPaddingTop() - getPaddingBottom() - unSelectBitmap.getHeight()) / 2;

        shiningX = selectX + 4;
        shiningY = 0;

        circleRadius = (sideLength / 2 + selectBitmap.getHeight() / 2) / 2 - 4;
        circleX = getPaddingLeft() + circleRadius + 4;
        circleY = getPaddingTop() + circleRadius + 4;
        circlePaint.setMaskFilter(new BlurMaskFilter(circleRadius + 2, BlurMaskFilter.Blur.NORMAL));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isThumbUp || isFastAnim) {// 点赞

            // 画点赞
            canvas.save();
            matrix.reset();
            matrix.postScale(faction, faction, selectX + selectBitmap.getWidth() / 2, selectY + selectBitmap.getHeight() / 2);
            canvas.concat(matrix);
            canvas.drawBitmap(selectBitmap, selectX, selectY, paint);
            canvas.restore();

            if (hasBackGround) {

                // 画顶部发光点
                canvas.save();
                matrix.reset();
                matrix.postScale(faction, faction, shiningX + shiningBitmap.getWidth() / 2, shiningY + shiningBitmap.getHeight() / 2);
                canvas.concat(matrix);
                canvas.drawBitmap(shiningBitmap, shiningX, shiningY, paint);
                canvas.restore();


                // 画背景
//                circlePaint.setAlpha(colorFaction);
//                canvas.drawCircle(circleX, circleY, circleRadius * radiusFaction, circlePaint);
            }

        } else {// 取消点赞
            // 画点赞
            canvas.save();
            matrix.reset();
            matrix.postScale(faction, faction, unSelectX + unSelectBitmap.getWidth() / 2, unSelectY + unSelectBitmap.getHeight() / 2);
            canvas.concat(matrix);
            canvas.drawBitmap(unSelectBitmap, unSelectX, unSelectY, paint);
            canvas.restore();


        }

    }

    public void startAnim() {
        mClickCount++;
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastStartTime < 300) {
            isFastAnim = true;
        }

        lastStartTime = currentTimeMillis;

        if (isThumbUp) {// 如果已点赞，则进行取消点赞的操作
            if (isFastAnim) {
                startFastAnim();
                return;
            }
            isThumbUp = false;
            startThumbDownAnim();
            mClickCount = 0;

        } else {// 如果未点赞，则进行点赞的操作
            if (isFastAnim) {
                startFastAnim();
                return;
            }
            if (mThumbUpAnim != null) {
                mThumbUpAnim.end();
                mThumbUpAnim = null;
            }
            isThumbUp = true;
            startThumbUpAnim();
            mClickCount = 1;
        }

        mEndCount = mClickCount;
    }

    private void startThumbUpAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "faction", 1f, 1.4f, 1f);
        ObjectAnimator animatorColor = ObjectAnimator.ofInt(this, "colorFaction", 0, 27, 0);
        ObjectAnimator animatorRadius = ObjectAnimator.ofFloat(this, "radiusFaction", 0, 1f, 0);

        mThumbUpAnim = new AnimatorSet();
        mThumbUpAnim.playTogether(animator, animatorColor, animatorRadius);
        mThumbUpAnim.setDuration(250);
        mThumbUpAnim.setInterpolator(new OvershootInterpolator());
        mThumbUpAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mThumbsUpResultListener != null) {
                    mThumbsUpResultListener.result(true);
                }
            }
        });
        mThumbUpAnim.start();
    }

    private void startThumbDownAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "faction", 1f, 1.2f, 1f);
        animator.setDuration(250);
        animator.setInterpolator(new OvershootInterpolator());

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 监听事件
                if (mThumbsUpResultListener != null) {
                    mThumbsUpResultListener.result(false);
                }
            }
        });
        animator.start();
    }

    private void startFastAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "faction", 1f, 1.2f, 1f);
        ObjectAnimator animatorColor = ObjectAnimator.ofInt(this, "colorFaction", 0, 27, 0);
        ObjectAnimator animatorRadius = ObjectAnimator.ofFloat(this, "radiusFaction", 0f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, animatorColor, animatorRadius);
        animatorSet.setDuration(250);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mEndCount++;
                if (mClickCount != mEndCount)
                    return;

                if (mClickCount % 2 == 0) {
                    isThumbUp = false;
                    isFastAnim = false;
                    startThumbDownAnim();
                    mEndCount = mClickCount = 0;
                } else {
                    isThumbUp = true;
                    isFastAnim = false;
                    startThumbUpAnim();
                    mEndCount = mClickCount = 1;
                }

            }
        });
        animatorSet.start();
    }


    public void setIsThumbsUp(boolean isThumbUp) {
        if (this.isThumbUp != isThumbUp) {
            this.isThumbUp = isThumbUp;
            if (isThumbUp) {
                mClickCount = 1;
                startThumbUpAnim();
            } else {
                startThumbDownAnim();
                mClickCount = 0;
            }
        }
    }

    @Keep
    public void setFaction(float faction) {
        this.faction = faction;
        postInvalidate();
    }

    @Keep
    public void setColorFaction(int colorFaction) {
        this.colorFaction = colorFaction;
    }

    @Keep
    public void setRadiusFaction(float radiusFaction) {
        this.radiusFaction = radiusFaction;
    }

    public interface ThumbsUpResultListener {
        void result(boolean isThumbsUp);
    }

    public void setThumbsUpResultListener(ThumbsUpResultListener mThumbsUpResultListener) {
        this.mThumbsUpResultListener = mThumbsUpResultListener;
    }
}
