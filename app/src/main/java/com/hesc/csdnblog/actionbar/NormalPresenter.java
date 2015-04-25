package com.hesc.csdnblog.actionbar;

import android.app.ActionBar;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.hesc.csdnblog.R;


/**
 * Created by hesc on 15/4/24.
 */
class NormalPresenter extends BasePresenter {

    public NormalPresenter(Activity activity, IActionBar actionBar) {
        super(activity, actionBar);
    }

    private ActionBar getSysActionBar(){
        return getActivity().getActionBar();
    }

    private void checkActivityIsNull() throws ActionBarException{
        if(getActivity() == null){
            throw new ActionBarException("Activity不是ActionBarActivity的派生类异常");
        }
    }

    private void checkActionBarIsNull() throws ActionBarException{
        if(getSysActionBar() == null){
            throw new ActionBarException("ActionBar不能为空异常！");
        }
    }

    @Override
    public void showAsEmbedded(View contentView, ViewGroup.LayoutParams layoutParams) throws ActionBarException {
        //校验
        checkActivityIsNull();
        //设置内容控件到Activity
        getActivity().getWindow().setContentView(contentView, layoutParams);

        checkActionBarIsNull();
        ActionBar sysActionBar = getSysActionBar();
        //隐藏系统的返回指示标
        sysActionBar.setDisplayHomeAsUpEnabled(false);
        //隐藏系统的标题栏
        sysActionBar.setDisplayShowTitleEnabled(false);
        //隐藏系统的图标栏
        sysActionBar.setDisplayShowHomeEnabled(false);
        //自定义ActionBar
        sysActionBar.setDisplayShowCustomEnabled(true);
        sysActionBar.setCustomView(getActionBar().getActionView());
    }

    @Override
    public void showAsIndependent(View contentView, ViewGroup.LayoutParams layoutParams) throws ActionBarException {
        //不显示系统标题栏
        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View decorView = getActivity().getLayoutInflater().inflate(R.layout.actionbar_decor, null);
        //自定义的Actionbar视图
        ViewGroup actionbarView = (ViewGroup)decorView.findViewById(R.id.actionbar_decor_bar);
        actionbarView.addView(getActionBar().getActionView());
        //内容容器
        ViewGroup containerView = (ViewGroup)decorView.findViewById(R.id.actionbar_decor_content);
        containerView.addView(contentView, layoutParams);
        //设置内容控件到Activity
        getActivity().getWindow().setContentView(decorView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
