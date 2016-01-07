package com.hesc.csdnblog.data;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
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

    /**
     * 获取博客地址
     * @param blogID
     * @return
     */
    public static String getBlogUrl(String blogID){
        return BLOG_BASEURL + blogID + "/";
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

        Document document = connect(getBlogUrl(blogID));
        //校验文档的合法性
        checkDocValidate(document);

        return document;
    }

    private static Document connect(String url) throws IOException {
        //不能使用默认的user-agent，这样就会使服务器认为是mobile的网络请求，从而返回m.blog.csdn.net的内容，从而导致错误
        String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11";
        return HttpConnection.connect(url).userAgent(userAgent).get();
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
            Document articleDoc = connect(pageUrl);
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
        article.url = BLOG_BASEURL + titleEle.select("a[href]").attr("href");
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
     * 对博客文章进行裁剪
     * @param articleUrl
     * @return
     * @throws Exception
     */
    public static String clipArticle(String articleUrl) throws Exception {
        Document document = Jsoup.connect(articleUrl).get();
        //去掉博客文章头
        document.getElementsByClass("blog_top_wrap").first().remove();
        document.getElementsByClass("blog_article_t").first().remove();
        //去掉栏目标题
        document.getElementsByClass("article_t").first().remove();
        //去掉上下页
        document.getElementsByClass("prev_next").first().remove();
        //去掉评论
        document.getElementsByClass("no_comment").first().remove();
        document.getElementsByClass("blog_comment").first().remove();
        document.getElementById("tags").remove();
        document.getElementsByClass("my_hot_article").first().remove();
        document.getElementsByClass("my_hot_article").first().remove();

        //去掉底部
        document.getElementsByClass("leftNav").first().remove();
        document.getElementsByClass("blog_handle").first().remove();
        document.getElementsByClass("backToTop").first().remove();
        document.getElementsByClass("popup_cover").first().remove();
        document.getElementsByClass("sharePopup_box").first().remove();
        document.getElementsByClass("ad_box").first().remove();
        document.getElementsByClass("backToTop").first().remove();
        document.getElementsByClass("blog_footer").first().remove();

        return document.outerHtml();
    }
}
