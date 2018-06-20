package com.example.prakharagarwal.newsapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    TextView headlineView;
    TextView descView;
    ImageView imageView;
    TextView link;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater=getMenuInflater();
//        inflater.inflate(R.menu.menu_detail,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id=item.getItemId();
//        if(id==R.id.menu_item_share){
//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getIntent().getStringExtra("headline"));
//            sendIntent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra("url"));
//            sendIntent.setType("text/plain");
////            startActivity(sendIntent);
//            startActivity(Intent.createChooser(sendIntent, "Share"));
//        }
//        return true;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_activty);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
//        headlineView =  findViewById(R.id.detail_headline);
//        descView = findViewById(R.id.detail_desc);
//        imageView = findViewById(R.id.detail_image);
//        link = findViewById(R.id.detail_url);
//        if (!getIntent().getStringExtra("headline").equals("")) {
//
//            Intent intent = getIntent();
//            Picasso.with(this).load(intent.getStringExtra("urlToImage")).into(imageView);
//            headlineView.setText(intent.getStringExtra("headline"));
//            descView.setText(intent.getStringExtra("desc"));
//            link.setText(intent.getStringExtra("url"));
//        }
    }

}
