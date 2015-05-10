package com.hesc.csdnblog.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.hesc.csdnblog.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by hesc on 15/4/25.
 */
public class RefreshableView extends ListView implements AbsListView.OnScrollListener {
    //列表头刷新状态
    private HeadRefreshState mHeadState;
    //列表脚刷新状态
    private FootRefreshState mFootState;
    //列表头视图
    private RefreshHeadView mHeadView;
    //列表脚视图
    private RefreshFootView mFootView;
    //上一次触摸点的Y坐标
    private float mTouchPointY = 0;
    //数据刷新侦听器
    private OnRefreshListener mRefreshListener;
    //滚动控制类
    private Scroller mScroller;
    private Handler mHandler;
    //滚动监听
    private OnScrollListener mOnScrollListener;
    //是否启用表头下拉视图
    private boolean mHeaderViewEnabled = false;
    //是否启用表尾加载更多视图
    private boolean mFooterViewEnabled = false;
    //是否正在刷新数据
    private boolean isRefreshing = false;
    //是否正在加载更多
    private boolean isloadingMore = false;

    public RefreshableView(Context context){
        super(context);
        initialize();
    }

    public RefreshableView(Context context, AttributeSet attrs){
        super(context, attrs, 0);
        initialize();
        initAttrs(attrs);
    }

    public RefreshableView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        initialize();
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RefreshableView);
        setHeaderViewEnabled(a.getBoolean(R.styleable.RefreshableView_headerViewEnabled, false));
        setFooterViewEnabled(a.getBoolean(R.styleable.RefreshableView_footerViewEnabled, false));
        a.recycle();
    }

    /**
     * 获取表头刷新视图是否已经启用
     * @return
     */
    public boolean isHeaderViewEnabled(){
        return mHeaderViewEnabled;
    }

    /**
     * 设置表头刷新视图是否启用
     * @param enabled
     */
    public void setHeaderViewEnabled(boolean enabled){
        if(mHeaderViewEnabled == enabled) return;
        if(enabled)
            addHeaderView(mHeadView);
        else
            removeHeaderView(mHeadView);
        mHeaderViewEnabled = enabled;
    }

    /**
     * 获取表尾加载更多视图是否已经启用
     * @return
     */
    public boolean isFooterViewEnabled(){
        return mFooterViewEnabled;
    }

    /**
     * 设置表尾加载更多视图是否启用
     * @param enabled
     */
    public void setFooterViewEnabled(boolean enabled){
        if(mFooterViewEnabled == enabled) return;

        if(enabled)
            addFooterView(mFootView);
        else
            removeFooterView(mFootView);
        mFooterViewEnabled = enabled;
    }

    /**
     * 执行初始化操作
     */
    private void initialize(){
        mHeadState = HeadRefreshState.INIT;
        mFootState = FootRefreshState.INIT;
        mHeadView = new RefreshHeadView(getContext());
        mFootView = new RefreshFootView(getContext());
        mHeadView.updateViews(mHeadState);
        mFootView.updateViews(mFootState);

        //创建滚动控制类实例，使用减速插值程序（动画从开始到结束,变化率是一个减速的过程）
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        //handler
        mHandler = new Handler();
        //侦听ListView的滚动
        super.setOnScrollListener(this);
        //关闭Over-Scroll模式
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    /**
     * 设置数据刷新侦听类
     * @param l
     */
    public void setOnRefreshListener(OnRefreshListener l){
        mRefreshListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mTouchPointY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //除以3增加下拉的难度
                float delta = (ev.getY()-mTouchPointY)/3;
                mTouchPointY = ev.getY();

                //没有启用表头刷新视图，不处理
                if(!mHeaderViewEnabled) break;
                //如果不是向下拉，不处理
                if(delta<=0) break;
                //如果第一个可见项索引不为0，不处理
                if(getFirstVisiblePosition() != 0) break;
                //修改表头的可见高度
                mHeadView.setVisibleHeight((int)(mHeadView.getVisibleHeight()+delta));

                if(!isRefreshing) {
                    if (mHeadView.isReachLoadHeight()) {  //如果超过需要装载数据的界限
                        mHeadState = HeadRefreshState.NEEDLOAD;
                    } else {
                        mHeadState = HeadRefreshState.NONLOAD;
                    }
                    mHeadView.updateViews(mHeadState);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(getFirstVisiblePosition() != 0) break;
                //没有启用表头刷新视图，不处理
                if(!mHeaderViewEnabled) break;

                if(mHeadState == HeadRefreshState.NONLOAD){  //不需要装载数据
                    resetHeaderHeight(false);
                    mHeadState = HeadRefreshState.INIT;
                } else if(mHeadState == HeadRefreshState.NEEDLOAD){  //需要装载数据
                    startRefresh();
                    //设置滚动回退到需要装载界限上
                    resetHeaderHeight(true);
                } else if(mHeadState == HeadRefreshState.LOADING) { //正在装载
                    //设置滚动回退到需要装载界限上
                    resetHeaderHeight(true);
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 停止刷新数据
     */
    public void stopRefresh(boolean isSuccessful){
        //没有启用表头刷新视图，不处理
        if(!mHeaderViewEnabled) return;
        if(!isRefreshing) return;
        if(isSuccessful)
            mHeadView.storeCurrUpdateTime();
        resetHeaderHeight(false);
        //设置界面为初始状态
        mHeadState = HeadRefreshState.INIT;
        mHeadView.updateViews(mHeadState);
        isRefreshing = false;
    }

    /**
     * 停止加载更多
     */
    public void stopLoadMore(boolean isSuccessful){
        //没有启用底部加载更多，不处理
        if(!mFooterViewEnabled) return;
        if(!isloadingMore) return;
        if(isSuccessful)
            mFootState = FootRefreshState.INIT;
        else
            mFootState = FootRefreshState.FAILURE;  //加载失败
        mFootView.updateViews(mFootState);
        isloadingMore = false;
    }

    /**
     * 开始进行刷新数据操作
     */
    public void startRefresh(){
        //没有启用表头刷新视图，不处理
        if(!mHeaderViewEnabled) return;
        //正在刷新数据，不处理
        if(isRefreshing) return;
        //设置界面为数据装载状态
        mHeadState = HeadRefreshState.LOADING;
        mHeadView.updateViews(mHeadState);

        isRefreshing=true;
        mHandler.postDelayed(() -> {
            //刷新数据成功，保存更新数据时间
            if (mRefreshListener != null){
                mRefreshListener.onRefresh();
            }

        },300);
    }

    /**
     * 装载更多
     */
    public void startLoadMore(){
        //没有启用底部加载更多，不处理
        if(!mFooterViewEnabled) return;
        //正在加载更多，不处理
        if(isloadingMore) return;

        isloadingMore=true;
        mFootState = FootRefreshState.LOADING;
        mFootView.updateViews(mFootState);
        mHandler.postDelayed(() -> {
            if (mRefreshListener != null) {
                mRefreshListener.onLoadMore();
            }
        }, 300);
    }

    /**
     * 使用Scroller且开始执行滚动后，使用scroller的值更新表头的可见高度
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset() && mHeaderViewEnabled) {
            mHeadView.setVisibleHeight(mScroller.getCurrY());
        }
        postInvalidate();
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * 更新表头高度
     */
    private void resetHeaderHeight(boolean needLoad){
        int finalHeight = 0;
        if(needLoad)
            finalHeight = mHeadView.getLoadHeight();

        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        int height = mHeadView.getVisibleHeight();
        mScroller.startScroll(0, height, 0, finalHeight - height);
        invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(mOnScrollListener != null){
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(mOnScrollListener != null){
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

        if(firstVisibleItem + visibleItemCount>=totalItemCount){
            startLoadMore();
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener((parent, view, position, id)->{
            if(getAdapter() instanceof HeaderViewListAdapter){
                if(view == mHeadView) return;
                if(view == mFootView) return;
            }
            if(listener != null){
                listener.onItemClick(parent, view, position, id);
            }
        });
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        super.setOnItemLongClickListener((parent, view, position, id)->{
            if(getAdapter() instanceof HeaderViewListAdapter){
                if(view == mHeadView) return true;
                if(view == mFootView) return true;
            }
            if(listener != null){
                return listener.onItemLongClick(parent, view, position, id);
            }
            return false;
        });
    }

    /**
     * 列表头刷新状态
     */
    enum HeadRefreshState{
        Empty,
        /**
         * 初始状态
         */
        INIT,
        /**
         * 未达到需要装载数据界限
         */
        NONLOAD,
        /**
         * 正在装载数据状态
         */
        LOADING,
        /**
         * 已达到需要装载数据界限
         */
        NEEDLOAD,
    }

    /**
     * 列表脚刷新状态
     */
    enum FootRefreshState{
        /**
         * 初始状态
         */
        INIT,
        /**
         * 正在装载数据状态
         */
        LOADING,
        // 加载失败
        FAILURE
    }

    /**
     * 数据刷新侦听接口
     */
    public interface OnRefreshListener{
        void onRefresh();
        void onLoadMore();
    }

    /**
     * 可刷新的列表头视图
     */
    class RefreshHeadView extends FrameLayout{
        private static final String REFRESH_PREF = "refreshable";
        private ImageView mArrowIV;
        private ProgressBar mProgressBar;
        private TextView mTitleTV;
        private TextView mUpdateTimeTV;
        private ViewGroup mContainView;
        //开始进行数据装载的高度
        private int mLoadHeight;
        //状态保存文件
        private SharedPreferences pref = null;
        //上次更新时间
        private String mLastUpdateTime;
        //向上翻转动画
        private Animation mRotateUpAnim;
        //刷新状态
        private HeadRefreshState mState;

        public RefreshHeadView(Context context){
            super(context);
            initLayout();
            initData();
        }

        /**
         * 初始化布局
         */
        private void initLayout() {
            mContainView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.refreshable_head, null);
            mArrowIV = (ImageView) mContainView.findViewById(R.id.refreshable_head_arrow);
            mProgressBar = (ProgressBar) mContainView.findViewById(R.id.refreshable_head_progress);
            mTitleTV = (TextView) mContainView.findViewById(R.id.refreshable_head_title);
            mUpdateTimeTV = (TextView) mContainView.findViewById(R.id.refreshable_head_update_time);
            addView(mContainView);
        }

        public int getLoadHeight(){
            return mLoadHeight;
        }

        /**
         * 初始化数据
         */
        private void initData(){
            //状态保存文件
            pref = getContext().getSharedPreferences(REFRESH_PREF, Context.MODE_PRIVATE);
            //获取上次更新时间
            mLastUpdateTime = getLastUpdateTime();
            //初始状态=空
            mState = HeadRefreshState.Empty;
            //向上翻转动画
            mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            mRotateUpAnim.setFillAfter(true);
            mRotateUpAnim.setDuration(200);

            mLoadHeight = (int)(getResources().getDisplayMetrics().density * 50 + 0.5f);
        }

        /**
         * 根据刷新状态更新界面状态
         * @param state
         */
        public void updateViews(HeadRefreshState state){
            if(mState == state) return;
            mState = state;
            if(state == HeadRefreshState.INIT){ //初始状态
                updateWithNonLoadState();
                setVisibleHeight(0);
            } else if(state == HeadRefreshState.NONLOAD){  //未达到需要装载数据界限状态
                updateWithNonLoadState();
            } else if(state == HeadRefreshState.NEEDLOAD){  //已达到需要装载数据界限状态
                updateWithNeedLoadState();
            } else if(state == HeadRefreshState.LOADING) {  //正在装载数据状态
                updateWithLoading();
            }
        }

        /**
         * 设置表头可见高度
         * @param height
         */
        public void setVisibleHeight(int height){
            ViewGroup.LayoutParams lp = mContainView.getLayoutParams();
            lp.height = height;
            mContainView.setLayoutParams(lp);
        }

        /**
         * 获取表头可见高度
         * @return
         */
        public int getVisibleHeight(){
            ViewGroup.LayoutParams lp = mContainView.getLayoutParams();
            return lp.height;
        }

        /**
         * 是否达到开始装载数据的高度
         * @return
         */
        public boolean isReachLoadHeight(){
            return getVisibleHeight() >= mLoadHeight;
        }

        /**
         * 当刷新状态为初始化状态或者未达到需要装载数据界限状态时，更新界面数据
         */
        private void updateWithNonLoadState(){
            mArrowIV.clearAnimation();
            mArrowIV.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            mTitleTV.setText(R.string.refreshable_head_title_pulling);
            mUpdateTimeTV.setText(mLastUpdateTime);
        }

        /**
         * 当刷新状态为已达到需要装载数据界限状态时，更新界面数据
         */
        private void updateWithNeedLoadState(){
            mArrowIV.clearAnimation();
            mArrowIV.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            mTitleTV.setText(R.string.refreshable_head_title_release);
            mUpdateTimeTV.setText(mLastUpdateTime);
            mArrowIV.startAnimation(mRotateUpAnim);
        }

        /**
         * 当刷新状态为正在装载数据时，更新界面数据
         */
        private void updateWithLoading(){
            mArrowIV.clearAnimation();
            mArrowIV.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mTitleTV.setText(R.string.refreshable_head_title_loading);
            mUpdateTimeTV.setText(mLastUpdateTime);
        }

        /**
         * 获取上次更新时间
         * @return
         */
        private String getLastUpdateTime(){
            String updateTime = pref.getString("update_time","");
            if(TextUtils.isEmpty(updateTime))
                return getResources().getString(R.string.refreshable_head_update_init);
            else {
                return getResources().getString(R.string.refreshable_head_update_time) + updateTime;
            }
        }

        /**
         * 保存当前的更新时间
         */
        private void storeCurrUpdateTime(){
            Date date = Calendar.getInstance(Locale.getDefault()).getTime();
            SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm");
            String updateTime = format.format(date);
            pref.edit().putString("update_time", updateTime).commit();

            mLastUpdateTime = getLastUpdateTime();
        }
    }

    /**
     * 可刷新的列表脚视图
     */
    class RefreshFootView extends FrameLayout {
        private View mProgressBarContainer;
        private TextView mTitleTV;

        public RefreshFootView(Context context){
            super(context);
            initLayout();
            initListener();
        }

        /**
         * 初始化布局
         */
        private void initLayout() {
            ViewGroup containerView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.refreshable_foot, null);
            mProgressBarContainer = containerView.findViewById(R.id.refreshable_foot_progress);
            mTitleTV = (TextView) containerView.findViewById(R.id.refreshable_foot_title);
            addView(containerView);
        }

        private void initListener(){
            mTitleTV.setOnClickListener(v->{
                startLoadMore();  //加载更多
            });
        }

        /**
         * 根据刷新状态更新界面状态
         * @param state
         */
        public void updateViews(FootRefreshState state){
            if(state == FootRefreshState.INIT){  //初始化状态或者下拉状态
                updateWithInitState();
            } else if(state == FootRefreshState.LOADING) {  //正在装载数据状态
                updateWithLoading();
            } else if(state == FootRefreshState.FAILURE){  //数据加载失败
                updateWithFailState();
            }
        }

        /**
         * 当刷新状态为初始化状态或者下拉状态时，更新界面数据
         */
        private void updateWithInitState(){
            mProgressBarContainer.setVisibility(View.INVISIBLE);
            mTitleTV.setVisibility(View.VISIBLE);
            mTitleTV.setText(R.string.refreshable_foot_title);
        }

        /**
         * 当刷新状态为正在装载数据时，更新界面数据
         */
        private void updateWithLoading(){
            mProgressBarContainer.setVisibility(View.VISIBLE);
            mTitleTV.setVisibility(View.INVISIBLE);
        }

        /**
         * 当刷新状态为刷新失败时，更新界面数据
         */
        private void updateWithFailState(){
            mProgressBarContainer.setVisibility(View.INVISIBLE);
            mTitleTV.setVisibility(View.VISIBLE);
            mTitleTV.setText(R.string.refreshable_foot_failure);
        }
    }
}
