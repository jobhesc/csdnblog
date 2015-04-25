package com.hesc.csdnblog.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by hesc on 15/4/23.
 * 圆形图像View控件
 */
public class CircleMaskImageView extends MaskImageView {

    public CircleMaskImageView(Context context){
        super(context);
    }

    public CircleMaskImageView(Context context, AttributeSet attrs){
        super(context, attrs, 0);
    }

    public CircleMaskImageView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawMask(Canvas canvas, Paint paint) {
        drawCircle(canvas, paint);
    }

    @Override
    protected void drawMaskBorder(Canvas canvas, Paint paint) {
        drawCircle(canvas, paint);
    }

    private void drawCircle(Canvas canvas, Paint paint){
        //取高度和宽度的最小值作为圆直径长度
        int w = getRealWidth()>getRealHeight()?getRealHeight():getRealWidth();
        float radius = w/2.0f;
        float cx = getPaddingLeft()+(isShowMaskBorder()?getMaskBorderWidth():0)+radius;
        float cy = getPaddingTop()+(isShowMaskBorder()?getMaskBorderWidth():0)+radius;
        canvas.drawCircle(cx, cy, radius, paint);
    }
}
