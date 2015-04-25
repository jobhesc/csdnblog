package com.hesc.csdnblog.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by hesc on 15/4/25.
 */
public class RefreshableView extends ListView{
    public RefreshableView(Context context){
        super(context);
    }

    public RefreshableView(Context context, AttributeSet attrs){
        super(context, attrs, 0);
    }

    public RefreshableView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }


}
