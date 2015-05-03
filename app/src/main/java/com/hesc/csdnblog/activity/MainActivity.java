package com.hesc.csdnblog.activity;

import android.os.Bundle;
import android.widget.Button;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.base.BaseActivity;
import com.hesc.csdnblog.data.BlogArticle;
import com.hesc.csdnblog.data.BlogProvider;
import com.hesc.csdnblog.data.Blogger;
import com.hesc.csdnblog.view.RefreshableView;

import java.util.List;


public class MainActivity extends BaseActivity{
    private RefreshableView mListView;
    private BlogProvider mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActionBar只有标题栏
        getActionBarFacade().setOnlyTitleActionBar();

        mProvider = new BlogProvider(this);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(v->{
            Blogger blogger = new Blogger();
            blogger.blogCode = "003";
            blogger.blogName = "我的博客3";
            blogger.commentCount = 2;
            blogger.reshipCount = 3;

            BlogArticle article = new BlogArticle();
            article.title = "博客标题2";
            article.summary = "博客2简介";

            blogger.articles.add(article);

            mProvider.insertBlogger(blogger);
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(v->{
            List<Blogger> bloggers = mProvider.queryBlogger();
            List<BlogArticle> articles = mProvider.queryArticles();
            System.out.print(bloggers.toString());
        });
    }
}
