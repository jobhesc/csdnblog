package com.hesc.csdnblog.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.actionbar.ActionBarFacade;
import com.hesc.csdnblog.actionbar.ActionBarFactory;
import com.hesc.csdnblog.actionbar.ActionBarShowMode;
import com.hesc.csdnblog.actionbar.IActionBar;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;


public class BaseActivity extends ActionBarActivity {
    /**
     * ActionBar默认的显示样式
     */
    private static final ActionBarShowMode ACTIONBAR_DEFAULT_SHOWMODE=ActionBarShowMode.ACTIONBAR_EMBEDED;
    private ActionBarFacade mActionBarFacade;
    private List<Subscription> subscriptionList = new ArrayList<>();
    private Dialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBarFacade = new ActionBarFacade(this, createActionBar());
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, ACTIONBAR_DEFAULT_SHOWMODE);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(layoutResID, ACTIONBAR_DEFAULT_SHOWMODE);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        setContentView(view, params, ACTIONBAR_DEFAULT_SHOWMODE);
    }

    public void setContentView(int layoutResID, ActionBarShowMode showMode){
        mActionBarFacade.getShowPolicy().setActionBarShowMode(layoutResID, showMode);
    }

    public void setContentView(View view, ActionBarShowMode showMode){
        mActionBarFacade.getShowPolicy().setActionBarShowMode(view, showMode);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params, ActionBarShowMode showMode){
        mActionBarFacade.getShowPolicy().setActionBarShowMode(view, params, showMode);
    }

    public ActionBarFacade getActionBarFacade(){
        return mActionBarFacade;
    }

    public IActionBar createActionBar(){
        return ActionBarFactory.createCommonActionBar(this);
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示等待进度条
     */
    public void showWaitingProgress(){
        hideWaitingProgress();

        new Dialog(this, R.style.waiting_progress_dialog);
        mProgressDialog = new Dialog(this, R.style.waiting_progress_dialog);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setContentView(R.layout.base_progress);

        WindowManager.LayoutParams layoutParams = mProgressDialog.getWindow().getAttributes();
        //设定进入window时，隐藏软键盘
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        // 设定布局
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        // 当FLAG_DIM_BEHIND设置后生效。该变量指示后面的窗口变暗的程度。1.0表示完全不透明，0.0表示没有变暗;
        layoutParams.dimAmount=0.0F;

        mProgressDialog.show();
    }

    /**
     * 隐藏等待进度条
     */
    public void hideWaitingProgress(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 对订阅者进行安全回收，防止出现activity已经销毁而订阅者仍然在等待消息的情况
     * @param subscription
     */
    public void safeSubscription(Subscription subscription){
        subscriptionList.add(subscription);
    }

    /**
     * 取消所有订阅者的观察行为
     */
    public void unsubscribeAll(){
        if(subscriptionList.size()>0){
            for(Subscription subscription: subscriptionList)
                subscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        //取消所有订阅者的观察行为
        unsubscribeAll();
        super.onDestroy();
    }
}
