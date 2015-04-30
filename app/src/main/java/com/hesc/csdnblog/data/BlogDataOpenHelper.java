package com.hesc.csdnblog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by hesc on 15/4/30.
 */
public class BlogDataOpenHelper extends OrmLiteSqliteOpenHelper {
    private static final String BLOG_DB_NAME="blog";
    private static final int BLOG_DB_VERSION = 1;

    public BlogDataOpenHelper(Context context) {
        super(context, BLOG_DB_NAME, null, BLOG_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            //创建博主信息表
            TableUtils.createTable(connectionSource, Blogger.class);
            //创建博客文章表
            TableUtils.createTable(connectionSource, BlogArticle.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
