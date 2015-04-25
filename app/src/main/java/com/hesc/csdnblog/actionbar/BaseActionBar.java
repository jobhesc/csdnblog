package com.hesc.csdnblog.actionbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hesc.csdnblog.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by hesc on 15/4/22.
 *
 */
class BaseActionBar implements IActionBar{
    /**
     * ActionBar视图
     */
    protected View mActionView;
    /**
     * ActionBar左边按钮视图
     */
    @InjectView(R.id.actionbar_left_button)
    protected ImageView mLeftButtonView;
    /**
     * ActionBar右边按钮视图
     */
    @InjectView(R.id.actionbar_right_button)
    protected ImageView mRightButtonView;
    /**
     * ActionBar分割线视图
     */
    @InjectView(R.id.actionbar_dividing)
    protected View mDividingView;
    /**
     * ActionBar标题栏视图
     */
    @InjectView(R.id.actionbar_title)
    protected TextView mTitleView;
    /**
     * android上下文
     */
    protected Context mContext;
    /**
     * 标题文本
     */
    protected String mTitle;
    /**
     * 左边按钮图标
     */
    protected Drawable mLeftButtonDrawable;
    /**
     * 右边按钮图标
     */
    protected Drawable mRightButtonDrawable;
    /**
     * 左边按钮点击回调事件
     */
    private View.OnClickListener mLeftButtonClickListener;
    /**
     * 右边按钮点击回调事件
     */
    private View.OnClickListener mRightButtonClickListener;

    public BaseActionBar(Context context){
        mContext = context;
        //加载布局文件
        mActionView = LayoutInflater.from(context).inflate(R.layout.actionbar_main, null);
        //使用butterKnife注入框架解析视图
        ButterKnife.inject(this, mActionView);
    }

    public Context getContext(){
        return mContext;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public IActionBar setTitle(String title) {
        mTitle = title;
        mTitleView.setText(title);
        return this;
    }

    @Override
    public Drawable getLeftButtonDrawable() {
        return mLeftButtonDrawable;
    }

    @Override
    public IActionBar setLeftButtonDrawable(Drawable drawable) {
        mLeftButtonDrawable = drawable;
        mLeftButtonView.setImageDrawable(drawable);
        return this;
    }

    @Override
    public IActionBar setLeftButtonImageResource(int resID) {
        return setLeftButtonDrawable(mContext.getResources().getDrawable(resID));
    }

    @Override
    public Drawable getRightButtonDrawable() {
        return mRightButtonDrawable;
    }

    @Override
    public IActionBar setRightButtonDrawable(Drawable drawable) {
        mRightButtonDrawable = drawable;
        mRightButtonView.setImageDrawable(drawable);
        return this;
    }

    @Override
    public IActionBar setRightButtonImageResource(int resID) {
        return setRightButtonDrawable(mContext.getResources().getDrawable(resID));
    }

    @Override
    public IActionBar setOnLeftButtonClickListener(View.OnClickListener listener) {
        mLeftButtonClickListener = listener;
        return this;
    }

    @Override
    public IActionBar setOnRightButtonClickListener(View.OnClickListener listener) {
        mRightButtonClickListener = listener;
        return this;
    }

    @Override
    public View getActionView() {
        return mActionView;
    }

    @OnClick(R.id.actionbar_left_button)
    protected void onLeftButtonClick(View view){
        notifyLeftButtonClick();
    }

    @OnClick(R.id.actionbar_right_button)
    protected void onRightButtonClick(View view){
        notifyRightButtonClick();
    }

    /**
     * 左边按钮点击时通知外部回调事件
     */
    protected void notifyLeftButtonClick(){
        if(mLeftButtonClickListener != null){
            mLeftButtonClickListener.onClick(mLeftButtonView);
        }
    }

    /**
     * 右边按钮点击时通知外部回调事件
     */
    protected void notifyRightButtonClick(){
        if(mRightButtonClickListener != null){
            mRightButtonClickListener.onClick(mRightButtonView);
        }
    }

    /**
     * 设置左边按钮的可见性
     * @param visible
     * @return
     */
    public IActionBar setLeftButtonVisible(boolean visible){
        mLeftButtonView.setVisibility(visible?View.VISIBLE:View.GONE);
        setDividingViewVisible(); //设置分割线的可见性
        return this;
    }

    /**
     * 设置右边按钮的可见性
     * @param visible
     * @return
     */
    public IActionBar setRightButtonVisible(boolean visible){
        mRightButtonView.setVisibility(visible?View.VISIBLE:View.GONE);
        return this;
    }

    /**
     * 设置标题栏的可见性
     * @param visible
     * @return
     */
    public IActionBar setTitleViewVisible(boolean visible){
        mTitleView.setVisibility(visible?View.VISIBLE:View.GONE);
        setDividingViewVisible(); //设置分割线的可见性
        return this;
    }

    /**
     * 设置分割线的可见性
     */
    private void setDividingViewVisible(){
        //只有左边按钮和标题栏视图都可见时，分割线才可见
        boolean visible = mLeftButtonView.getVisibility() == View.VISIBLE &&
                mTitleView.getVisibility() == View.VISIBLE;
        mDividingView.setVisibility(visible?View.VISIBLE:View.GONE);
    }
}
