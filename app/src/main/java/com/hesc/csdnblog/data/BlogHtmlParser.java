package com.hesc.csdnblog.data;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hesc on 15/5/4.
 */
class BlogHtmlParser {
    //博客的基地址
    private static final String BLOG_BASEURL="http://blog.csdn.net/";
    private static final String BLOG_BASEURL_MOBILE="http://m.blog.csdn.net/blog/";

    /**
     * 获取博客地址
     * @param blogID
     * @return
     */
    public static String getBlogUrl(String blogID){
        return BLOG_BASEURL + blogID + "/";
    }

    /**
     * 获取移动版博客地址
     * @param blogID
     * @return
     */
    public static String getMobileBlogUrl(String blogID){
        return BLOG_BASEURL_MOBILE + blogID + "/";
    }
    /**
     * 校验文档的合法性
     */
    private static void checkDocValidate(Document document)throws Exception{
        if(document == null){
            throw new Exception("mDocument为空异常！");
        }

        if(document.getElementById("panel_Profile") == null){
            throw new Exception("url对应的地址不是csdn的博客地址");
        }
    }

    /**
     * 解析博客文章页数
     * @param pageInfo
     * @return
     */
    private static int parseArticlePageCount(String pageInfo){
        int startIndex = pageInfo.indexOf("共");
        int endIndex = pageInfo.indexOf("页");
        return Integer.parseInt(pageInfo.substring(startIndex+1, endIndex));
    }

    /**
     * 获取博客的DOM
     * @param blogID
     * @throws Exception
     */
    private static Document obtainDocument(String blogID) throws Exception{
        if(TextUtils.isEmpty(blogID)){
            throw new Exception("博客ID不能为空！");
        }

        Document document = Jsoup.connect(getBlogUrl(blogID)).get();
        //校验文档的合法性
        checkDocValidate(document);

        return document;
    }

    /**
     * 解析博客文章
     * @param blogID
     * @return
     * @throws Exception
     */
    public static List<BlogArticle> parseArticles(String blogID) throws Exception {
        //获取博客的DOM
        Document document = obtainDocument(blogID);

        List<BlogArticle> articles = new ArrayList<>();
        //获取博客文章页码数
        int pageCount = 1;
        Element pageEle = document.getElementById("papelist");
        if (pageEle != null){
            String pageInfo = pageEle.getElementsByTag("span").first().text();
            pageCount = parseArticlePageCount(pageInfo);
        }

        for(int pageIndex = 0; pageIndex<pageCount; pageIndex++) {
            String pageUrl = getArticlePageUrl(blogID, pageIndex);
            Document articleDoc = Jsoup.connect(pageUrl).get();
            Elements articleEles = articleDoc.getElementById("article_list").children();

            int count = articleEles.size();
            BlogArticle article = null;
            for (int i = 0; i < count; i++) {
                //根据html元素解析博客文章,返回博客文章实体
                article = parseArticle(blogID, articleEles.get(i));
                articles.add(article);
            }
        }
        return articles;
    }

    /**
     * 解析博主信息
     * @param blogID
     */
    public static Blogger parseBlogger(String blogID) throws Exception{
        //获取博客的DOM
        Document document = obtainDocument(blogID);

        Blogger blogger = new Blogger();
        Element profileEle = document.getElementById("panel_Profile");
        Element userfaceEle = profileEle.getElementById("blog_userface");
        //博客编号
        blogger.blogCode = userfaceEle.getElementsByClass("user_name").first().text();
        //博客头像地址
        blogger.iconUrl = userfaceEle.getElementsByTag("img").first().attr("src");

        //博客名称
        Element titleEle = document.getElementById("blog_title");
        blogger.blogName = titleEle.getElementsByTag("h2").select("a[href]").first().text();
        blogger.customName = blogger.blogName;
        //博客描述
        blogger.blogDesc = titleEle.getElementsByTag("h3").first().text();

        Elements rankEles = document.getElementById("blog_rank").select("li>span");
        //博客访问次数
        blogger.visitCount = rankEles.get(0).text();
        //积分
        blogger.points = rankEles.get(1).text();
        //等级
        blogger.rankUrl = rankEles.get(2).getElementsByTag("img").first().attr("src");
        //排名
        blogger.rank = rankEles.get(3).text();

        Elements statEles = document.getElementById("blog_statistics").select("li>span");
        //原创文章数量
        blogger.originalCount = statEles.get(0).text();
        //转载文章数量
        blogger.reshipCount = statEles.get(1).text();
        //译文数量
        blogger.translationCount = statEles.get(2).text();
        //评论数量
        blogger.commentCount = statEles.get(3).text();
        //博客ID
        blogger.blogID = blogID;
        //博客地址
        blogger.url = getBlogUrl(blogID);

        return blogger;
    }

    /**
     * 根据html元素解析博客文章
     * @param blogID
     * @param element
     * @return
     */
    private static BlogArticle parseArticle(String blogID, Element element){
        BlogArticle article = new BlogArticle();

        Element titleEle = element.getElementsByClass("article_title").first();
        //文章标题
        article.title = titleEle.select("a[href]").first().text();
        //文章地址
        article.url = getArticleUrl(blogID, getArticleID(titleEle.select("a[href]").attr("href")));
        //概要
        article.summary = element.getElementsByClass("article_description").first().text();

        Element manageEle = element.getElementsByClass("article_manage").first();
        //更新时间
        article.updateOn = manageEle.getElementsByClass("link_postdate").first().text();
        //阅读次数
        article.readCount = manageEle.getElementsByClass("link_view").first().text();
        //评论次数
        article.commentCount = manageEle.getElementsByClass("link_comments").first().text();
        //文章类型
        article.category = getArticleCategory(titleEle);

        return article;
    }

    /**
     * 获取博客文章ID
     * @param href
     * @return
     */
    private static String getArticleID(String href){
        //地址一般为"/XXXX/article/details/26365913"，需要把26365913截取出来
        int index = href.lastIndexOf("/");
        return href.substring(index+1, href.length());

    }

    private static int getArticleCategory(Element titleEle) {
        String c = titleEle.select("div > span:first-child").first().attr("class");
        if("ico ico_type_Translated".equals(c))
            return BlogArticle.TRANSLATED;  //译文
        else if("ico ico_type_Original".equals(c))
            return BlogArticle.ORIGINAL;  //原创文章
        else if("ico ico_type_Repost".equals(c))
            return BlogArticle.REPOST;  //转载文章
        else
            throw new IllegalArgumentException("不支持的博客文章类型");
    }

    /**
     * 获取指定页码的博客文章地址
     * @param blogID
     * @param pageIndex
     * @return
     */
    private static String getArticlePageUrl(String blogID, int pageIndex){
        return getBlogUrl(blogID) + "/article/list/" + (pageIndex + 1);
    }

    /**
     * 获取博客文章ID对应的博客文章地址
     * @param blogID
     * @param articleID
     * @return
     */
    private static String getArticleUrl(String blogID, String articleID){
        return getMobileBlogUrl(blogID) + articleID;
    }

    /**
     * 对博客文章进行裁剪
     * @param articleUrl
     * @return
     * @throws Exception
     */
    public static String clipArticle(String articleUrl) throws Exception {
        Document document = Jsoup.connect(articleUrl).get();
        //去掉博客文章头
        document.getElementById("header").remove();
        //去掉导航
        document.getElementById("nav").remove();

        Element topElement = document.getElementById("top");
        //去掉栏目标题
        topElement.getElementsByClass("avatar").first().remove();
        //去掉上下页
        topElement.getElementsByClass("next_page").first().remove();
        //去掉[请先登录后，再发表评论！]
        topElement.select("div.view").first().remove();
        //去掉评论
        topElement.select("div.comment_sub").first().remove();
        //去掉底部
        document.getElementById("ding1").remove();
        document.getElementById("footer").remove();
        return document.outerHtml();
    }
}
