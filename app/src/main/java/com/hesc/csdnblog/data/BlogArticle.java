package com.hesc.csdnblog.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by hesc on 15/4/29.
 */
@DatabaseTable(tableName = "blog_article")
public class BlogArticle extends BaseDaoEnabled {

    BlogArticle(){}

    BlogArticle(Dao dao){
        super.setDao(dao);
    }

    /**
     * ID字段
     */
    @DatabaseField(columnName = "id", generatedId = true)
    public long id;
    /**
     * 博主信息（外键）
     */
    @DatabaseField(columnName = "blogger", foreign = true )
    public Blogger blogger;
    /**
     * 文章标题
     */
    @DatabaseField(columnName = "title")
    public String title;
    /**
     * 概要
     */
    @DatabaseField(columnName = "summary", width = 1000)
    public String summary;
    /**
     * 更新时间
     */
    @DatabaseField(columnName = "update_on", format = "yyyy-MM-dd hh:mm")
    public String updateOn;
    /**
     * 阅读次数
     */
    @DatabaseField(columnName = "read_count")
    public int readCount;
    /**
     * 评论数
     */
    @DatabaseField(columnName = "comment_count")
    public int commentCount;
    /**
     * 博客地址
     */
    @DatabaseField(columnName = "url")
    public String url;
}
