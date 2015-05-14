package com.hesc.csdnblog.data;

import com.squareup.okhttp.internal.DiskLruCache;

/**
 * Created by Administrator on 2015/5/15 0015.
 */
public class ArticleCache {
    private DiskLruCache cache;
    public ArticleCache(){
        cache = DiskLruCache.create();
    }


}
