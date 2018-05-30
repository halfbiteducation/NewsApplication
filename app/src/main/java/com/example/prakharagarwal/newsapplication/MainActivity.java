package com.example.prakharagarwal.newsapplication;

import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.prakharagarwal.newsapplication.Data.NewsDBHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    String TAG = MainActivity.class.getSimpleName();
    NewsRecyclerAdapter newsRecyclerAdapter;
    ArrayList<NewsArticle> newsArticles;
    RecyclerView recyclerView;
    NewsDBHelper newsDBHelper;
    List<ContentValues> contentValuesList = new ArrayList<>();
    Boolean mTwoPane;


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.menu_item_refresh) {
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//
//            String source = null;
//            if (preferences.getString("sources", null) != null) {
//                source = preferences.getString("sources", "the-verge");
//            }
////            new SyncTask_GET().execute(source);
////            Intent intent=new Intent(MainActivity.this, NewsIntentService.class);
////            //startService(intent);
//        } else if (id == R.id.menu_item_settings) {
//            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//            startActivity(intent);
//        }
//        return true;
//    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("lifecyle", "in onstop of activity");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("lifecyle", "in onresume of activity");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("lifecyle", "in onpause of activity");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detailContainer) != null) {
            mTwoPane = true;

        } else {
            mTwoPane = false;
        }

        Log.e("lifecyle", "in oncreate of activity");
//        SharedPreferences preferences1 = getSharedPreferences("Alarm",MODE_PRIVATE);
//
//        if (preferences1.getString("alarmSet", null) == null) {
//            Intent intent=new Intent(MainActivity.this,NewsIntentService.class);
//            PendingIntent pendingIntent=PendingIntent.getService(this,1,intent,0);
//            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),6*60*60*1000, pendingIntent);
//
//            SharedPreferences.Editor editor=preferences1.edit();
//            editor.putString("alarmSet","set");
//            editor.apply();
//
//            //if you want to cancel the alarm, pass same intent with same request code;
////               alarmManager.cancel(pendingIntent);
//        }
//
//
//        newsDBHelper = new NewsDBHelper(this);
//
//        newsArticles = new ArrayList<>();
//
//        newsRecyclerAdapter = new NewsRecyclerAdapter(this, newsArticles);
//        recyclerView = findViewById(R.id.news_recycler_view);
//        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
//            recyclerView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
//
//        } else {
//            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        }
//        recyclerView.setAdapter(newsRecyclerAdapter);
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//
//        if (savedInstanceState == null) {
//            Cursor cursor = getContentResolver().query(NewsContract.ArticleEntry.CONTENT_URI,null,null,null,null);
//            if (cursor.getCount()>0) {
//                newsArticles.clear();
//                while (cursor.moveToNext()) {
//                    NewsArticle article = new NewsArticle();
//                    article.setTitle(cursor.getString(0));
//                    article.setDescription(cursor.getString(1));
//                    article.setUrl(cursor.getString(2));
//                    article.setUrlToImage(cursor.getString(3));
//                    newsArticles.add(article);
//                }
//                newsRecyclerAdapter.addAll(newsArticles);
//                newsRecyclerAdapter.notifyDataSetChanged();
//            } else {
//                String source = null;
//                if (preferences.getString("sources", null) != null) {
//                    source = preferences.getString("sources", "the-verge");
//                }
//                new SyncTask_GET().execute(source);
//            }
//        }
//        preferences.registerOnSharedPreferenceChangeListener(this);


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
