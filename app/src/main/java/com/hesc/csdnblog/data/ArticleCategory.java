package com.hesc.csdnblog.data;

/**
 * Created by hesc on 15/5/13.
 * 博客文章类型
 */
public interface ArticleCategory {
    /**
     * 原创文章
     */
    int ORIGINAL = 1;
    /**
     * 转载文章
     */
    int REPOST = 2;
    /**
     * 译文
     */
    int TRANSLATED =3;
}
