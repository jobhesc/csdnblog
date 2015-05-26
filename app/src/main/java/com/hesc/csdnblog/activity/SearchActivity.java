package com.hesc.csdnblog.activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;

import com.hesc.csdnblog.R;
import com.hesc.csdnblog.actionbar.ActionBarShowMode;
import com.hesc.csdnblog.actionbar.IActionBar;
import com.hesc.csdnblog.base.BaseActivity;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search, ActionBarShowMode.ACTIONBAR_EMBEDED);
        getActionBarFacade().setBackActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.actionbar_search));
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);
        return super.onCreateOptionsMenu(menu);
    }
}
