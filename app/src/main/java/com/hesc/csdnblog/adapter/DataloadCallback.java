package com.hesc.csdnblog.adapter;

/**
 * Created by hesc on 2015/5/10 0010.
 * 数据装载回调接口
 */
public interface DataloadCallback {
    /**
     * 数据装载成功后回调
     */
    void dataLoaded();

    /**
     * 数据装载失败后回调
     */
    void dataLoadFail();
}
