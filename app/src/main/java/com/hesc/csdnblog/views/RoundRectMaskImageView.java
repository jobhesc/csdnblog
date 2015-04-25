package com.hesc.csdnblog.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.hesc.csdnblog.R;

/**
 * Created by hesc on 15/4/23.
 * 圆角图像View控件
 */
public class RoundRectMaskImageView extends MaskImageView {
    private RectF mBound = null;
    private boolean loadOnce = false;
    //圆角x方向的半径
    private int roundRectRadiusX=5;
    //圆角y方向的半径
    private int roundRectRadiusY=5;

    public RoundRectMaskImageView(Context context){
        super(context);
    }

    public RoundRectMaskImageView(Context context, AttributeSet attrs){
        super(context, attrs, 0);
        initAttrs(attrs);
    }

    public RoundRectMaskImageView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(!loadOnce){
            int rectLeft = getPaddingLeft() + (isShowMaskBorder()?getMaskBorderWidth():0);
            int rectTop = getPaddingTop() + (isShowMaskBorder()?getMaskBorderWidth():0);
            int rectRight = rectLeft + getRealWidth();
            int rectBottom = rectTop + getRealHeight();
            mBound = new RectF(rectLeft, rectTop, rectRight, rectBottom);
            loadOnce = true;
        }
    }

    private void initAttrs(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundRectMaskImageView);
        //圆角x方向的半径
        roundRectRadiusX = a.getDimensionPixelSize(R.styleable.RoundRectMaskImageView_roundRectRadiusX, 5);
        //圆角y方向的半径
        roundRectRadiusY = a.getDimensionPixelSize(R.styleable.RoundRectMaskImageView_roundRectRadiusY, 5);
        a.recycle();
    }

    /**
     * 获取圆角x方向的半径
     * @return
     */
    public int getRoundRectRadiusX() {
        return roundRectRadiusX;
    }

    /**
     * 设置圆角x方向的半径
     * @param roundRectRadiusX
     */
    public void setRoundRectRadiusX(int roundRectRadiusX) {
        this.roundRectRadiusX = roundRectRadiusX;
    }

    /**
     * 获取圆角y方向的半径
     * @return
     */
    public int getRoundRectRadiusY() {
        return roundRectRadiusY;
    }

    /**
     * 设置圆角y方向的半径
     * @param roundRectRadiusY
     */
    public void setRoundRectRadiusY(int roundRectRadiusY) {
        this.roundRectRadiusY = roundRectRadiusY;
    }

    @Override
    protected void drawMask(Canvas canvas, Paint paint) {
        canvas.drawRoundRect(mBound, roundRectRadiusX, roundRectRadiusY, paint);
    }

    @Override
    protected void drawMaskBorder(Canvas canvas, Paint paint) {
        drawMask(canvas, paint);
    }
}
