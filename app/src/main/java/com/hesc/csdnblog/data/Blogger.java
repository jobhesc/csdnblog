package com.hesc.csdnblog.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by hesc on 15/4/29.
 * 博客博主信息
 */
@DatabaseTable(tableName = "blog_blogger")
public class Blogger extends BaseDaoEnabled {
    /**
     * ID字段
     */
    @DatabaseField(columnName = "id", generatedId = true)
    public long id;
    /**
     * 博客编码
     */
    @DatabaseField(columnName = "blog_code", uniqueIndex = true)
    public String blogCode;
    /**
     * 博客名称
     */
    @DatabaseField(columnName = "blog_name")
    public String blogName;
    /**
     * 自定义的博客名称
     */
    @DatabaseField(columnName = "custom_name")
    public String customName;
    /**
     * 博客访问次数
     */
    @DatabaseField(columnName = "visit_count")
    public int visitCount;
    /**
     * 积分
     */
    @DatabaseField(columnName = "points")
    public int points;
    /**
     * 排名
     */
    @DatabaseField(columnName = "rank")
    public int rank;
    /**
     * 原创文章数量
     */
    @DatabaseField(columnName = "original_count")
    public int originalCount;
    /**
     * 转载文章数量
     */
    @DatabaseField(columnName = "reship_count")
    public int reshipCount;
    /**
     * 译文数量
     */
    @DatabaseField(columnName = "translation_count")
    public int translationCount;
    /**
     * 评论数量
     */
    @DatabaseField(columnName = "comment_count")
    public int commentCount;
    /**
     * 博客地址
     */
    @DatabaseField(columnName = "url")
    public String url;
    /**
     * 博客文章
     */
    @ForeignCollectionField(columnName = "articles",orderColumnName = "id", orderAscending = false)
    public ForeignCollection<BlogArticle> articles;
}
