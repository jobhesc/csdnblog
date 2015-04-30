package com.hesc.csdnblog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by hesc on 15/4/29.
 */
public class BlogDataProvider {

    private static final String LOG_TAG="com.hesc.csdnblog.data.BlogDataProvider";
    //博主信息数据访问对象
    private RuntimeExceptionDao<Blogger, ?> mBloggerDAO;
    //博客文章数据访问对象
    private RuntimeExceptionDao<BlogArticle, ?> mBlogArticleDAO;

    public BlogDataProvider(Context context){
        OrmLiteSqliteOpenHelper dbHelper = OpenHelperManager.getHelper(context, BlogDataOpenHelper.class);
        mBloggerDAO = dbHelper.getRuntimeExceptionDao(Blogger.class);
        mBlogArticleDAO = dbHelper.getRuntimeExceptionDao(BlogArticle.class);
    }

    /**
     * 插入博主信息
     * @param blogger
     */
    public void insertBlogger(Blogger blogger){
        mBloggerDAO.create(blogger);
    }

    public void assign(Blogger blogger){
        mBloggerDAO.assignEmptyForeignCollection(blogger, "articles");
    }

    public List<Blogger> queryBlogger(){
        return mBloggerDAO.queryForAll();
    }

    public List<BlogArticle> queryArticles(){
        return mBlogArticleDAO.queryForAll();
    }

    /**
     * 插入博主信息
     * @param blogger
     */
    public void deleteBlogger(Blogger blogger){
        mBloggerDAO.delete(blogger);
    }

    /**
     * 插入博主信息
     * @param blogger
     */
    public void updateBlogger(Blogger blogger){
        mBloggerDAO.update(blogger);
    }

    @Override
    protected void finalize() throws Throwable {
        OpenHelperManager.releaseHelper();
        super.finalize();
    }
}
