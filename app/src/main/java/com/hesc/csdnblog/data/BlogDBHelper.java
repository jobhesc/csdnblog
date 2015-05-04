package com.hesc.csdnblog.data;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by hesc on 15/4/29.
 */
class BlogDBHelper {

    private static final String LOG_TAG=BlogDBHelper.class.getName();
    //博主信息数据访问对象
    private Dao<Blogger, ?> mBloggerDAO;
    private RuntimeExceptionDao<Blogger, ?> mRuntimeBloggerDAO;
    //博客文章数据访问对象
    private Dao<BlogArticle, ?> mBlogArticleDAO;
    private RuntimeExceptionDao<BlogArticle, ?> mRuntimeBlogArticleDAO;

    public BlogDBHelper(Context context){
        OrmLiteSqliteOpenHelper dbHelper = OpenHelperManager.getHelper(context, BlogOpenHelper.class);
        try {
            mBloggerDAO = dbHelper.getDao(Blogger.class);
            //直接使用mBloggerDAO不是很方便，会导致每个方法都要处理SQLException异常，
            //直接使用mRuntimeBloggerDAO就不会有这个问题
            mRuntimeBloggerDAO = new RuntimeExceptionDao<>(mBloggerDAO);
        } catch (SQLException e) {
            Log.e(LOG_TAG, e.getMessage(),e);
        }

        try {
            mBlogArticleDAO = dbHelper.getDao(BlogArticle.class);
            mRuntimeBlogArticleDAO = new RuntimeExceptionDao<>(mBlogArticleDAO);
        } catch (SQLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    public RuntimeExceptionDao<Blogger, ?> getBloggerDAO(){
        return mRuntimeBloggerDAO;
    }

    public RuntimeExceptionDao<BlogArticle, ?> getBlogArticleDAO(){
        return mRuntimeBlogArticleDAO;
    }

    /**
     * 新建博主实体对象
     * @return
     */
    public Blogger newBlogger(){
        Blogger blogger = new Blogger(mBloggerDAO);
        assignArticleKeys(blogger);
        return blogger;
    }

    /**
     * 新建博客文章实体对象
     * @param blogger
     * @return
     */
    public BlogArticle newBlogArticle(Blogger blogger){
        BlogArticle article = new BlogArticle(mBlogArticleDAO);
        article.blogger = blogger;
        return article;
    }

    /**
     * 插入博主信息
     * @param blogger
     */
    public void insertBlogger(Blogger blogger){
        mRuntimeBloggerDAO.create(blogger);
    }

    /**
     * 批量插入博主信息
     * @param bloggers
     */
    public void insertBloggers(List<Blogger> bloggers){
        if(bloggers == null || bloggers.size() == 0) return;

        mRuntimeBloggerDAO.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Blogger blogger : bloggers) {
                    mRuntimeBloggerDAO.create(blogger);
                }
                return null;
            }
        });
    }

    /**
     * 插入博主信息
     * @param blogger
     */
    public void deleteBlogger(Blogger blogger){
        mRuntimeBloggerDAO.delete(blogger);
    }

    /**
     * 批量删除博主信息
     * @param bloggers
     */
    public void deleteBloggers(List<Blogger> bloggers){
        if(bloggers == null || bloggers.size() == 0) return;

        mRuntimeBloggerDAO.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Blogger blogger : bloggers) {
                    mRuntimeBloggerDAO.delete(blogger);
                }
                return null;
            }
        });
    }

    /**
     * 更新博主信息
     * @param blogger
     */
    public void updateBlogger(Blogger blogger){
        mRuntimeBloggerDAO.update(blogger);
    }

    /**
     * 批量更新博主信息
     * @param bloggers
     */
    public void updateBloggers(List<Blogger> bloggers){
        if(bloggers == null || bloggers.size() == 0) return;

        mRuntimeBloggerDAO.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Blogger blogger : bloggers) {
                    mRuntimeBloggerDAO.update(blogger);
                }
                return null;
            }
        });
    }

    /**
     * 插入博客文章
     * @param article
     */
    public void insertArticle(BlogArticle article){
        mRuntimeBlogArticleDAO.create(article);
    }

    /**
     * 批量插入博客文章
     * @param articles
     */
    public void insertArticles(List<BlogArticle> articles){
        if(articles == null || articles.size() == 0) return;

        mRuntimeBlogArticleDAO.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for(BlogArticle article: articles){
                    mRuntimeBlogArticleDAO.create(article);
                }
                return null;
            }
        });
    }

    /**
     * 更新博客文章
     * @param article
     */
    public void updateArticle(BlogArticle article){
        mRuntimeBlogArticleDAO.update(article);
    }

    /**
     * 批量更新博客文章
     * @param articles
     */
    public void updateArticles(List<BlogArticle> articles){
        if(articles == null || articles.size() == 0) return;

        mRuntimeBlogArticleDAO.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for(BlogArticle article: articles){
                    mRuntimeBlogArticleDAO.update(article);
                }
                return null;
            }
        });
    }

    /**
     * 删除博客文章
     * @param article
     */
    public void deleteArticle(BlogArticle article){
        mRuntimeBlogArticleDAO.delete(article);
    }

    /**
     * 批量删除博客文章
     * @param articles
     */
    public void deleteArticles(List<BlogArticle> articles){
        if(articles == null || articles.size() == 0) return;

        mRuntimeBlogArticleDAO.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for(BlogArticle article: articles){
                    mRuntimeBlogArticleDAO.delete(article);
                }
                return null;
            }
        });
    }

    /**
     * 给blogger分配博客外键
     * @param blogger
     */
    public void assignArticleKeys(Blogger blogger){
        mRuntimeBloggerDAO.assignEmptyForeignCollection(blogger, "articles");
    }

    /**
     * 查找所有的博主信息
     * @return
     */
    public List<Blogger> findAllBloggers(){
        return mRuntimeBloggerDAO.queryForAll();
    }

    /**
     * 查找所有的博客文章
     * @return
     */
    public List<BlogArticle> findAllArticles(){
        return mRuntimeBlogArticleDAO.queryForAll();
    }

    /**
     * 查找博主下的所有博客文章
     * @param blogger
     * @return
     */
    public List<BlogArticle> findArticles(Blogger blogger){
        return mRuntimeBlogArticleDAO.queryForEq("blogger", blogger.id);
    }

    @Override
    protected void finalize() throws Throwable {
        OpenHelperManager.releaseHelper();
        super.finalize();
    }
}
