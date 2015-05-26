package com.hesc.csdnblog.actionbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
     * ActionBar右边按钮1视图
     */
    @InjectView(R.id.actionbar_right_button1)
    protected ImageView mRightButton1View;
    /**
     * ActionBar右边按钮2视图
     */
    @InjectView(R.id.actionbar_right_button2)
    protected ImageView mRightButton2View;
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
     * 右边按钮1图标
     */
    protected Drawable mRightButton1Drawable;
    /**
     * 右边按钮2图标
     */
    protected Drawable mRightButton2Drawable;
    /**
     * 左边按钮点击回调事件
     */
    private View.OnClickListener mLeftButtonClickListener;
    /**
     * 右边按钮1点击回调事件
     */
    private View.OnClickListener mRightButton1ClickListener;
    /**
     * 右边按钮2点击回调事件
     */
    private View.OnClickListener mRightButton2ClickListener;
    /**
     * 菜单资源布局ID
     */
    private int mMenuLayoutResId;
    /**
     * 菜单项点击事件回调
     */
    private MenuItem.OnMenuItemClickListener mMenuItemClickListener;

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
    public Drawable getRightButton1Drawable() {
        return mRightButton1Drawable;
    }

    @Override
    public IActionBar setRightButton1Drawable(Drawable drawable) {
        mRightButton1Drawable = drawable;
        mRightButton1View.setImageDrawable(drawable);
        return this;
    }

    @Override
    public IActionBar setRightButton1ImageResource(int resID) {
        return setRightButton1Drawable(mContext.getResources().getDrawable(resID));
    }

    @Override
    public Drawable getRightButton2Drawable() {
        return mRightButton2Drawable;
    }

    @Override
    public IActionBar setRightButton2Drawable(Drawable drawable) {
        mRightButton2Drawable = drawable;
        mRightButton2View.setImageDrawable(drawable);
        return this;
    }

    @Override
    public IActionBar setRightButton2ImageResource(int resID) {
        return setRightButton2Drawable(mContext.getResources().getDrawable(resID));
    }

    @Override
    public IActionBar setOnLeftButtonClickListener(View.OnClickListener listener) {
        mLeftButtonClickListener = listener;
        return this;
    }

    @Override
    public IActionBar setOnRightButton1ClickListener(View.OnClickListener listener) {
        mRightButton1ClickListener = listener;
        return this;
    }

    @Override
    public IActionBar setOnRightButton2ClickListener(View.OnClickListener listener) {
        mRightButton2ClickListener = listener;
        return this;
    }

    @Override
    public View getActionView() {
        return mActionView;
    }

    @Override
    public int getMenuLayout() {
        return mMenuLayoutResId;
    }

    @Override
    public IActionBar setMenuLayout(int menuLayoutResId) {
        this.mMenuLayoutResId = menuLayoutResId;
        return this;
    }

    @Override
    public MenuItem.OnMenuItemClickListener getMenuItemClickListener() {
        return mMenuItemClickListener;
    }

    @Override
    public IActionBar setMenuItemClickListener(MenuItem.OnMenuItemClickListener listener) {
        this.mMenuItemClickListener = listener;
        return this;
    }

    @OnClick(R.id.actionbar_left_button)
    protected void onLeftButtonClick(View view){
        notifyLeftButtonClick();
    }

    @OnClick(R.id.actionbar_right_button1)
    protected void onRightButton1Click(View view){
        notifyRightButton1Click();
    }

    @OnClick(R.id.actionbar_right_button2)
    protected void onRightButton2Click(View view){
        notifyRightButton2Click();
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
     * 右边按钮1点击时通知外部回调事件
     */
    protected void notifyRightButton1Click(){
        if(mRightButton1ClickListener != null){
            mRightButton1ClickListener.onClick(mRightButton1View);
        }
    }

    /**
     * 右边按钮2点击时通知外部回调事件
     */
    protected void notifyRightButton2Click(){
        if(mRightButton2ClickListener != null){
            mRightButton2ClickListener.onClick(mRightButton2View);
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

    @Override
    public IActionBar setRightButton1Visible(boolean visible) {
        mRightButton1View.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置右边按钮的可见性
     * @param visible
     * @return
     */
    public IActionBar setRightButton2Visible(boolean visible){
        mRightButton2View.setVisibility(visible ? View.VISIBLE : View.GONE);
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
