package com.hesc.csdnblog.data;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

/**
 * Created by hesc on 15/4/29.
 */
public class BlogProvider {

    private static final String LOG_TAG=BlogProvider.class.getName();
    //博主信息数据访问对象
    private RuntimeExceptionDao<Blogger, ?> mBloggerDAO;
    //博客文章数据访问对象
    private RuntimeExceptionDao<BlogArticle, ?> mBlogArticleDAO;

    public BlogProvider(Context context){
        OrmLiteSqliteOpenHelper dbHelper = OpenHelperManager.getHelper(context, BlogOpenHelper.class);
        mBloggerDAO = dbHelper.getRuntimeExceptionDao(Blogger.class);
        mBlogArticleDAO = dbHelper.getRuntimeExceptionDao(BlogArticle.class);
    }

    public Blogger newBlogger(){
        Blogger blogger = new Blogger();
        assignArticleKeys(blogger);
        return blogger;
    }

    /**
     * 插入博主信息
     * @param blogger
     */
    public void insertBlogger(Blogger blogger){
        mBloggerDAO.create(blogger);
    }

    /**
     * 给blogger分配博客外键
     * @param blogger
     */
    public void assignArticleKeys(Blogger blogger){
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
