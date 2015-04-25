package com.hesc.csdnblog.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hesc.csdnblog.R;

/**
 * Created by hesc on 15/4/23.
 */
public abstract class MaskImageView extends ImageView {
    //遮盖形状画笔
    private Paint mPaint;
    //遮盖边框画笔
    private Paint mBorderPaint;
    //画图矩阵
    private Matrix mMatrix;
    //图片宽度
    private int mDrawableWidth;
    //图片高度
    private int mDrawableHeight;
    //是否显示遮盖边框
    private boolean showMaskBorder = false;
    //遮盖边框宽度
    private int maskBorderWidth = 10;
    //遮盖边框颜色
    private int maskBorderColor = Color.WHITE;
    private boolean loadOnce = false;

    public MaskImageView(Context context){
        super(context);
        initialize();
    }

    public MaskImageView(Context context, AttributeSet attrs){
        super(context, attrs, 0);
        initialize();
        initAttrs(attrs);
    }

    public MaskImageView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        initialize();
        initAttrs(attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(!loadOnce){
            //计算drawable的高度和宽度
            calcDrawableSize();
            //计算drawable的缩放矩阵
            calcDrawableMatrix();
            //设置画笔的渲染器
            mPaint.setShader(createBitmapShader());
            //设置遮盖边框画笔的颜色和宽度
            mBorderPaint.setColor(maskBorderColor);
            mBorderPaint.setStrokeWidth(maskBorderWidth);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            loadOnce = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() == null) return;
        drawMask(canvas, mPaint);
        if(showMaskBorder)
            drawMaskBorder(canvas, mBorderPaint);
    }

    /**
     * 获取是否显示遮盖边框的值
     * @return
     */
    public boolean isShowMaskBorder() {
        return showMaskBorder;
    }

    /**
     * 设置是否显示遮盖边框
     * @param showMaskBorder
     */
    public void setShowMaskBorder(boolean showMaskBorder) {
        this.showMaskBorder = showMaskBorder;
    }

    /**
     * 获取遮盖边框宽度的值
     * @return
     */
    public int getMaskBorderWidth() {
        return maskBorderWidth;
    }

    /**
     * 设置遮盖边框的宽度
     * @param maskBorderWidth
     */
    public void setMaskBorderWidth(int maskBorderWidth) {
        this.maskBorderWidth = maskBorderWidth;
    }

    /**
     * 获取遮盖边框颜色的值
     * @return
     */
    public int getMaskBorderColor() {
        return maskBorderColor;
    }

    /**
     * 设置遮盖边框的颜色
     * @param maskBorderColor
     */
    public void setMaskBorderColor(int maskBorderColor) {
        this.maskBorderColor = maskBorderColor;
    }

    /**
     * 画遮盖形状
     * @param canvas
     * @param paint
     */
    protected abstract  void drawMask(Canvas canvas, Paint paint);

    /**
     * 画遮盖边框
     * @param canvas
     */
    protected abstract  void drawMaskBorder(Canvas canvas, Paint paint);

    /**
     * 进行初始化操作
     */
    private void initialize(){
        //初始化遮盖形状画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        //初始化遮盖边框画笔
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setDither(true);
        //初始化画图矩阵
        mMatrix = new Matrix();

    }

    /**
     * 初始化属性
     * @param attrs
     */
    private void initAttrs(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskImageView);
        //是否显示遮盖边框
        showMaskBorder = a.getBoolean(R.styleable.MaskImageView_showMaskBorder, false);
        //遮盖边框宽度
        maskBorderWidth = a.getDimensionPixelSize(R.styleable.MaskImageView_maskBorderWidth, 10);
        //遮盖边框颜色
        maskBorderColor = a.getColor(R.styleable.MaskImageView_maskBorderColor, Color.WHITE);

        a.recycle();
    }

    /**
     * 创建位图渲染器
     * @return
     */
    private BitmapShader createBitmapShader(){
        Bitmap bitmap = drawableToBitmap();
        if(bitmap == null) return null;
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        shader.setLocalMatrix(mMatrix);
        return shader;
    }

    /**
     * 获取图片控件宽度(去掉Padding)
     * @return
     */
    protected int getRealWidth(){
        return getWidth() - getPaddingLeft() - getPaddingRight() - (showMaskBorder?getMaskBorderWidth()*2:0);
    }

    /**
     * 获取图片控件高度(去掉Padding)
     * @return
     */
    protected int getRealHeight(){
        return getHeight() - getPaddingTop() - getPaddingBottom()-(showMaskBorder?getMaskBorderWidth()*2:0);
    }

    /**
     * 计算drawable的缩放矩阵
     */
    private void calcDrawableMatrix(){
        if(mDrawableWidth < 0 || mDrawableHeight < 0) return;

        float wScale = getWidth() * 1.0f / mDrawableWidth;
        float hScale = getHeight() * 1.0f / mDrawableHeight;
        //按照宽和高最大比例缩放
        float scale = Math.max(wScale, hScale);
        mMatrix.setScale(scale, scale);
    }

    /**
     * 计算drawable的高度和宽度
     */
    private void calcDrawableSize(){
        if(getDrawable() == null){
            mDrawableWidth = -1;
            mDrawableWidth = -1;
        } else {
            //获取drawable的宽度和高度
            mDrawableWidth = getDrawable().getIntrinsicWidth();
            mDrawableHeight = getDrawable().getIntrinsicHeight();
            //如果drawable是ColorDrawable，则getIntrinsicWidth()和getIntrinsicHeight()
            //返回值为-1，则直接去控件的高度和宽度
            if (mDrawableWidth < 0)
                mDrawableWidth = getRealWidth();
            if (mDrawableHeight < 0)
                mDrawableHeight = getRealHeight();
        }
    }

    /**
     * 把drawable转成bitmap
     * @return
     */
    private Bitmap drawableToBitmap(){
        Drawable drawable = getDrawable();
        if(drawable == null) return null;
        if(drawable instanceof BitmapDrawable){
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            return bitmapDrawable.getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(mDrawableWidth, mDrawableWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

}
