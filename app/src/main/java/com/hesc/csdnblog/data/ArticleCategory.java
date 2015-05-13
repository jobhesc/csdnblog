package com.hesc.csdnblog.data;

/**
 * Created by hesc on 15/5/13.
 * 博客文章类型
 */
public interface ArticleCategory {
    /**
     * 原创文章
     */
    public static final int ORIGINAL = 1;
    /**
     * 转载文章
     */
    public static final int REPOST = 2;
    /**
     * 译文
     */
    public static final int TRANSLATED =3;
}
