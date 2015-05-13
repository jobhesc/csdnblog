package com.hesc.csdnblog.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by hesc on 15/4/29.
 * 博客博主信息
 */
@DatabaseTable(tableName = "blog_blogger")
public class Blogger extends BaseDaoEnabled implements Serializable {
    //必须有一个无参数的构造函数，系统在构造实体表的时候会用到，否则会报错
    Blogger(){}

    Blogger(Dao dao){
        super.setDao(dao);
    }

    /**
     * ID字段
     */
    @DatabaseField(columnName = "id", generatedId = true)
    public long id;
    /**
     * 博客ID
     */
    @DatabaseField(columnName = "blog_id", uniqueIndex = true)
    public String blogID;
    /**
     * 博客编码
     */
    @DatabaseField(columnName = "blog_code")
    public String blogCode;
    /**
     * 博客名称
     */
    @DatabaseField(columnName = "blog_name")
    public String blogName;
    /**
     * 博客描述
     */
    @DatabaseField(columnName = "blog_desc")
    public String blogDesc;
    /**
     * 自定义的博客名称
     */
    @DatabaseField(columnName = "custom_name")
    public String customName;
    /**
     * 博客访问次数
     */
    @DatabaseField(columnName = "visit_count")
    public String visitCount;
    /**
     * 积分
     */
    @DatabaseField(columnName = "points")
    public String points;
    /**
     * 等级
     */
    @DatabaseField(columnName = "rank_url")
    public String rankUrl;
    /**
     * 排名
     */
    @DatabaseField(columnName = "rank")
    public String rank;
    /**
     * 原创文章数量
     */
    @DatabaseField(columnName = "original_count")
    public String originalCount;
    /**
     * 转载文章数量
     */
    @DatabaseField(columnName = "reship_count")
    public String reshipCount;
    /**
     * 译文数量
     */
    @DatabaseField(columnName = "translation_count")
    public String translationCount;
    /**
     * 评论数量
     */
    @DatabaseField(columnName = "comment_count")
    public String commentCount;
    /**
     * 博客地址
     */
    @DatabaseField(columnName = "url")
    public String url;
    /**
     * 头像地址
     */
    @DatabaseField(columnName = "icon_url")
    public String iconUrl;
    /**
     * 博客文章
     */
    @ForeignCollectionField(columnName = "articles",orderColumnName = "id", orderAscending = false)
    public ForeignCollection<BlogArticle> articles;
}
