package com.hesc.csdnblog.data;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hesc on 15/5/4.
 */
class BlogHtmlParser {
    private static final String LOG_TAG=BlogHtmlParser.class.getName();
    //博客的基地址
    private static final String BLOG_BASEURL="http://blog.csdn.net/";
    //每一页的博客数
    private static final int BLOG_PAGESIZE = 25;
    //博客ID
    private String mBlogID = null;
    //博主信息
    private Blogger mBlogger = null;
    //文档
    private Document mDocument = null;
    //博客文章信息
    private SparseArray<BlogArticle> mArticles = new SparseArray<BlogArticle>();
    //博客文章总数量
    private int mArticleTotalCount = 0;

    public BlogHtmlParser(String blogID){
        mBlogID = blogID;
    }

    /**
     * 获取博客地址
     * @return
     */
    public String getBlogUrl(){
        return BLOG_BASEURL + mBlogID;
    }

    /**
     * 校验文档的合法性
     */
    private void checkDocValidate(){
        if(mDocument == null){
            throw new RuntimeException("mDocument为空异常！");
        }

        if(mDocument.getElementById("panel_Profile") == null){
            throw new RuntimeException("url对应的地址不是csdn的博客地址");
        }
    }

    /**
     * 执行解析html任务
     */
    public void parse(){
        if(mDocument != null) return;
        if(TextUtils.isEmpty(mBlogID)){
            throw new RuntimeException("博客ID不能为空！");
        }

        try {
            mDocument = Jsoup.connect(getBlogUrl()).get();
            //校验文档的合法性
            checkDocValidate();
            //解析博主信息
            parseBlogger();
            //解析博客文章概述信息
            parseArticleSummary();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    /**
     * 解析博客文章概述信息
     */
    private void parseArticleSummary(){
        Element pageEle = mDocument.getElementById("pagelist");
        String pageInfo = pageEle.getElementsByTag("span").first().text();
        mArticleTotalCount = parseArticleTotalCount(pageInfo);
    }

    /**
     * 解析博客文章总数
     * @param pageInfo
     * @return
     */
    private int parseArticleTotalCount(String pageInfo){
        int index = pageInfo.indexOf("条");
        return Integer.parseInt(pageInfo.substring(0, index));
    }

    /**
     * 解析博主信息
     */
    private void parseBlogger(){
        if(mBlogger != null) return;

        mBlogger = new Blogger();
        Element profileEle = mDocument.getElementById("panel_Profile");
        Element userfaceEle = profileEle.getElementById("blog_userface");
        //博客编号
        mBlogger.blogCode = userfaceEle.getElementsByClass("user_name").first().text();
        //博客头像地址
        mBlogger.iconUrl = userfaceEle.getElementsByTag("img").first().attr("src");

        //博客名称
        Element titleEle = mDocument.getElementById("blog_title");
        mBlogger.blogName = titleEle.getElementsByTag("h2").select("a[href]").first().text();
        mBlogger.customName = mBlogger.blogName;
        //博客描述
        mBlogger.blogDesc = titleEle.getElementsByTag("h3").first().text();

        Elements rankEles = mDocument.getElementById("blog_rank").select("li>span");
        //博客访问次数
        mBlogger.visitCount = rankEles.get(0).text();
        //积分
        mBlogger.points = rankEles.get(1).text();
        //等级
        mBlogger.rankUrl = rankEles.get(2).getElementsByTag("img").first().attr("src");
        //排名
        mBlogger.rank = rankEles.get(3).text();

        Elements statEles = mDocument.getElementById("blog_statistics").select("li>span");
        //原创文章数量
        mBlogger.originalCount = statEles.get(0).text();
        //转载文章数量
        mBlogger.reshipCount = statEles.get(1).text();
        //译文数量
        mBlogger.translationCount = statEles.get(2).text();
        //评论数量
        mBlogger.commentCount = statEles.get(3).text();
        //博客ID
        mBlogger.blogID = mBlogID;
        //博客地址
        mBlogger.url = getBlogUrl();
    }

    /**
     * 根据指定索引解析文章
     * @param index
     * @return
     */
    private BlogArticle parseArticle(int index) {
        //计算index对应的文章在第几页
        int pageIndex = index/BLOG_PAGESIZE;
        int beginIndex = pageIndex*BLOG_PAGESIZE;

        //按照博客的一整页进行解析
        String pageUrl = getArticleUrl(pageIndex);
        try {
            Document articleDoc = Jsoup.connect(pageUrl).get();
            Elements articleEles = articleDoc.getElementById("article_list").getElementsByClass("list_item article_item");
            int count = articleEles.size();
            BlogArticle article = null;
            for(int i=0; i<count; i++){
                //根据html元素解析博客文章,返回博客文章实体
                article = parseArticle(articleEles.get(i));
                mArticles.put(beginIndex+i, article);
            }
            return mArticles.get(index);
        } catch(Exception e){
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据html元素解析博客文章
     * @param element
     * @return
     */
    private BlogArticle parseArticle(Element element){
        BlogArticle article = new BlogArticle();
        article.blogger = mBlogger;

        Element titleEle = element.getElementsByClass("article_title").first();
        //文章标题
        article.title = titleEle.select("a[href]").first().text();
        //文章地址
        article.url = BLOG_BASEURL + titleEle.select("a[href]").attr("href");
        //概要
        article.summary = element.getElementsByClass("article_description").first().text();

        Element manageEle = element.getElementsByClass("article_manage").first();
        //更新时间
        article.updateOn = manageEle.getElementsByClass("link_postdate").first().text();
        //阅读次数
        article.readCount = parseArticleCount(manageEle.getElementsByClass("link_view").first().text());
        //评论次数
        article.commentCount = parseArticleCount(manageEle.getElementsByClass("link_comments").first().text());

        return article;
    }

    /**
     * 由于阅读次数和评论数在html里是带括号的，如(9900),需要进行解析出数字
     * @param articleCount
     * @return
     */
    private int parseArticleCount(String articleCount){
        return Integer.parseInt(articleCount.replace("(","").replace(")",""));
    }

    /**
     * 获取指定页码的博客文章地址
     * @param pageIndex
     * @return
     */
    private String getArticleUrl(int pageIndex){
        return getBlogUrl() + "/article/list/" + (pageIndex + 1);
    }

    /**
     * 获取博主信息
     * @return
     */
    public Blogger getBlogger(){
        return mBlogger;
    }

    /**
     * 获取博客文章总数
     * @return
     */
    public int getArticleTotalCount(){
        return mArticleTotalCount;
    }

    /**
     * 获取博客文章
     * @param beginIndex
     * @param endIndex
     * @return
     */
    public List<BlogArticle> getArticles(int beginIndex, int endIndex){
        if(beginIndex<0 || beginIndex>=mArticleTotalCount)
            throw new IndexOutOfBoundsException();
        if(endIndex<0 || endIndex>=mArticleTotalCount)
            throw new IndexOutOfBoundsException();

        if(beginIndex>endIndex)
            throw new RuntimeException("endIndex小于beginIndex异常");

        List<BlogArticle> resultList = new ArrayList<BlogArticle>();
        BlogArticle article = null;
        for(int index = beginIndex; index<=endIndex; index++){
            article = mArticles.get(index);
            if(article == null){
                article = parseArticle(index);
            }
            resultList.add(article);
        }
        return resultList;
    }
}
