package com.hesc.csdnblog.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hesc.csdnblog.R;

/**
 * Created by hesc on 15/4/25.
 */
public class RefreshableView extends ListView{
    /**
     * 列表头刷新状态
     */
    private HeadRefreshState mHeadState = HeadRefreshState.INIT;

    public RefreshableView(Context context){
        super(context);
    }

    public RefreshableView(Context context, AttributeSet attrs){
        super(context, attrs, 0);
    }

    public RefreshableView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    /**
     * 列表头刷新状态
     */
    enum HeadRefreshState{
        /**
         * 初始状态
         */
        INIT,
        /**
         * 正在拖拽状态(未达到需要装载数据界限)
         */
        PULLING_NONLOAD,
        /**
         * 正在拖拽状态(已达到需要装载数据界限)
         */
        PULLING_FORLOAD,
        /**
         * 正在装载数据状态
         */
        LOADING,
        /**
         * 释放后需要装载数据状态
         */
        RELEASE_FORLOAD,
        /**
         * 释放后不需要装载数据状态
         */
        RELEASE_NONLOAD;
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
        //箭头旋转开始角度
        private float mArrowFromDegree=0;
        //箭头旋转结束角度
        private float mArrowToDegree=0;
        //状态保存文件
        private SharedPreferences pref = null;

        public RefreshHeadView(Context context){
            super(context);
            initLayout();
            initData();
        }

        public RefreshHeadView(Context context, AttributeSet attrs){
            super(context, attrs, 0);
            initLayout();
            initData();
        }

        public RefreshHeadView(Context context, AttributeSet attrs, int defStyleAttr){
            super(context, attrs, defStyleAttr);
            initLayout();
            initData();
        }

        /**
         * 初始化布局
         */
        private void initLayout() {
            ViewGroup containerView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.refreshable_head, null);
            mArrowIV = (ImageView) containerView.findViewById(R.id.refreshable_head_arrow);
            mProgressBar = (ProgressBar) containerView.findViewById(R.id.refreshable_head_progress);
            mTitleTV = (TextView) containerView.findViewById(R.id.refreshable_head_title);
            mUpdateTimeTV = (TextView) containerView.findViewById(R.id.refreshable_head_update_time);
            addView(containerView);
        }

        /**
         * 初始化数据
         */
        private void initData(){
            //状态保存文件
            pref = getContext().getSharedPreferences(REFRESH_PREF, Context.MODE_PRIVATE);
        }

        public void updateViews(HeadRefreshState state){
            if(state == HeadRefreshState.INIT){  //初始化状态

            }
        }

        /**
         * 当刷新状态为初始化状态时，更新界面数据
         */
        private void updateWithInitState(){
            mArrowIV.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mTitleTV.setText(R.string.refreshable_head_title_pulling);
            mUpdateTimeTV.setText(R.string.refreshable_head_update_init);
            mArrowIV.clearAnimation();
        }

        private void updateWithPullingForLoad(){
            mArrowIV.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mTitleTV.setText(R.string.refreshable_head_title_release);
            mUpdateTimeTV.setText(R.string.refreshable_head_update_init);
            mArrowIV.clearAnimation();
        }

        private String getUpdateTime(){
            String updateTime = pref.getString("update_time","");
            if(TextUtils.isEmpty(updateTime))
                return getResources().getString(R.string.refreshable_head_update_init);
            else {
                return "";
            }
        }


        private void reset(){
            mArrowFromDegree = 0;
            mArrowToDegree = 0;
        }

        /**
         * 旋转箭头
         */
        public void rotateArrow(){
            mArrowFromDegree = mArrowToDegree;
            mArrowToDegree = mArrowToDegree+180;
            if(mArrowFromDegree>=360)
                mArrowFromDegree = 0;
            if(mArrowToDegree>360)
                mArrowToDegree -= 360;

            float pivotX = mArrowIV.getWidth()/2.0f;
            float pivotY = mArrowIV.getHeight()/2.0f;
            Animation animation = new RotateAnimation(mArrowFromDegree, mArrowToDegree,
                    pivotX, pivotY);
            //设置动画结束后保留旋转状态，否则动画结束后系统回自动还原
            animation.setFillAfter(true);
            //设置动画持续时间
            animation.setDuration(600);
            mArrowIV.setAnimation(animation);
        }
    }
}
