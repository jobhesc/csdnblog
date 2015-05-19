package com.hesc.csdnblog.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.adapter.ArticleListAdapter;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.data.BlogArticle;
import com.hesc.csdnblog.data.BlogProvider;
import com.hesc.csdnblog.data.Blogger;
import com.hesc.csdnblog.data.DataloadCallback;
import com.hesc.csdnblog.view.RefreshableView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;

public class ArticleActivity extends BaseActivity {
    @InjectView(R.id.article_view)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        //ActionBar只有标题栏
        getActionBarFacade().setBackActionBar();
        ButterKnife.inject(this);
        //配置webview
        configWebView();
        BlogArticle article = (BlogArticle)getIntent().getSerializableExtra("article");
        setTitle(article.title);
        //装载数据
        loadData(article);
    }

    /**
     * 加载博客文章内容，并显示出来
     * @param article
     */
    private void loadData(BlogArticle article){
        showWaitingProgress();
        safeSubscription(BlogProvider.getInstance().loadArticleContent(article.url)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        r->{
                            hideWaitingProgress();
                            mWebView.loadDataWithBaseURL(article.url, r, "text/html", "utf-8", article.url);
                        },
                        e->{
                            hideWaitingProgress();
                            showToast(e.getMessage());
                        }
                ));
    }

    private void configWebView(){
        //启用js脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //去掉所有的链接的跳转
//                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
    }
}
