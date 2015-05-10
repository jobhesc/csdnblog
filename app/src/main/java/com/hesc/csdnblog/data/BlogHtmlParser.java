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
    //博客的基地址
    private static final String BLOG_BASEURL="http://blog.csdn.net/";
    //博客ID
    private String mBlogID = null;
    //文档
    private Document mDocument = null;

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
    private void checkDocValidate()throws Exception{
        if(mDocument == null){
            throw new Exception("mDocument为空异常！");
        }

        if(mDocument.getElementById("panel_Profile") == null){
            throw new Exception("url对应的地址不是csdn的博客地址");
        }
    }

    /**
     * 解析博客文章页数
     * @param pageInfo
     * @return
     */
    private int parseArticlePageCount(String pageInfo){
        int startIndex = pageInfo.indexOf("共");
        int endIndex = pageInfo.indexOf("页");
        return Integer.parseInt(pageInfo.substring(startIndex+1, endIndex));
    }

    /**
     * 获取博客的DOM
     * @throws Exception
     */
    private void obtainDocument() throws Exception{
        if(mDocument == null){
            if(TextUtils.isEmpty(mBlogID)){
                throw new Exception("博客ID不能为空！");
            }

            mDocument = Jsoup.connect(getBlogUrl()).get();
            //校验文档的合法性
            checkDocValidate();
        }
    }

    /**
     * 解析博客文章
     * @return
     * @throws Exception
     */
    public List<BlogArticle> parseArticles() throws Exception {
        //获取博客的DOM
        obtainDocument();

        List<BlogArticle> articles = new ArrayList<>();
        //获取博客文章页码数
        Element pageEle = mDocument.getElementById("papelist");
        String pageInfo = pageEle.getElementsByTag("span").first().text();
        int pageCount = parseArticlePageCount(pageInfo);
        if(pageCount == 0) return articles;

        for(int pageIndex = 0; pageIndex<pageCount; pageIndex++) {
            String pageUrl = getArticleUrl(pageIndex);
            Document articleDoc = Jsoup.connect(pageUrl).get();
            Elements articleEles = articleDoc.getElementById("article_list").getElementsByClass("list_item article_item");

            int count = articleEles.size();
            BlogArticle article = null;
            for (int i = 0; i < count; i++) {
                //根据html元素解析博客文章,返回博客文章实体
                article = parseArticle(articleEles.get(i));
                articles.add(article);
            }
        }
        return articles;
    }

    /**
     * 解析博主信息
     */
    public Blogger parseBlogger() throws Exception{
        //获取博客的DOM
        obtainDocument();

        Blogger blogger = new Blogger();
        Element profileEle = mDocument.getElementById("panel_Profile");
        Element userfaceEle = profileEle.getElementById("blog_userface");
        //博客编号
        blogger.blogCode = userfaceEle.getElementsByClass("user_name").first().text();
        //博客头像地址
        blogger.iconUrl = userfaceEle.getElementsByTag("img").first().attr("src");

        //博客名称
        Element titleEle = mDocument.getElementById("blog_title");
        blogger.blogName = titleEle.getElementsByTag("h2").select("a[href]").first().text();
        blogger.customName = blogger.blogName;
        //博客描述
        blogger.blogDesc = titleEle.getElementsByTag("h3").first().text();

        Elements rankEles = mDocument.getElementById("blog_rank").select("li>span");
        //博客访问次数
        blogger.visitCount = rankEles.get(0).text();
        //积分
        blogger.points = rankEles.get(1).text();
        //等级
        blogger.rankUrl = rankEles.get(2).getElementsByTag("img").first().attr("src");
        //排名
        blogger.rank = rankEles.get(3).text();

        Elements statEles = mDocument.getElementById("blog_statistics").select("li>span");
        //原创文章数量
        blogger.originalCount = statEles.get(0).text();
        //转载文章数量
        blogger.reshipCount = statEles.get(1).text();
        //译文数量
        blogger.translationCount = statEles.get(2).text();
        //评论数量
        blogger.commentCount = statEles.get(3).text();
        //博客ID
        blogger.blogID = mBlogID;
        //博客地址
        blogger.url = getBlogUrl();

        return blogger;
    }

    /**
     * 根据html元素解析博客文章
     * @param element
     * @return
     */
    private BlogArticle parseArticle(Element element){
        BlogArticle article = new BlogArticle();

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
}
