package com.hesc.csdnblog.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hesc.csdnblog.actionbar.ActionBarFacade;
import com.hesc.csdnblog.actionbar.ActionBarFactory;
import com.hesc.csdnblog.actionbar.ActionBarShowMode;
import com.hesc.csdnblog.actionbar.IActionBar;


public class BaseActivity extends ActionBarActivity {
    /**
     * ActionBar默认的显示样式
     */
    private static final ActionBarShowMode ACTIONBAR_DEFAULT_SHOWMODE=ActionBarShowMode.ACTIONBAR_EMBEDED;
    private ActionBarFacade mActionBarFacade;

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
}
