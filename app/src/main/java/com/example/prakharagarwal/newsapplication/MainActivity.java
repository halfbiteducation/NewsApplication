package com.example.prakharagarwal.newsapplication;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.prakharagarwal.newsapplication.Data.NewsDBHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    Boolean mTwoPane;
    ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detailContainer) != null) {
            mTwoPane = true;

        } else {
            mTwoPane = false;
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment( MainActivityFragment.newInstance("general"), "GENERAL");
        viewPagerAdapter.addFragment(MainActivityFragment.newInstance("sports"), "SPORTS");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Log.e("lifecyle", "in oncreate of activity");



    }

    public void onItemSelected( ArrayList<NewsArticle> articles, int position) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            NewsArticle article=articles.get(position);
            arguments.putString("urlToImage", article.getUrlToImage());
            arguments.putString("headline", article.getTitle());
            arguments.putString("desc", article.getDescription());
            arguments.putString("url", article.getUrl());
            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailContainer, fragment, "DetailActivityFragment.TAG")
                    .commit();
        } else {
            NewsArticle article=articles.get(position);
            Intent intent = new Intent(this,  DetailsActivity.class);
            intent.putExtra("urlToImage", article.getUrlToImage());
            intent.putExtra("headline", article.getTitle());
            intent.putExtra("desc", article.getDescription());
            intent.putExtra("url", article.getUrl());

                startActivity(intent);

        }

    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        Log.e(TAG, "on restore instance state");
//        String value = (String) savedInstanceState.get("value");
//
//        newsArticles = (ArrayList<NewsArticle>) savedInstanceState.getSerializable("newsList");
//        newsRecyclerAdapter.addAll(newsArticles);
//        newsRecyclerAdapter.notifyDataSetChanged();
//
//        super.onRestoreInstanceState(savedInstanceState);
//    }
//
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Log.e(TAG, "on save instance state");
//        outState.putString("value", "1");
//        outState.putSerializable("newsList", newsArticles);
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }



}
