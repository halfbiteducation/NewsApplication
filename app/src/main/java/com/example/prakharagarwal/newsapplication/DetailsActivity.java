package com.example.prakharagarwal.newsapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    TextView headlineView;
    TextView descView;
    ImageView imageView;
    TextView link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_activty);
        headlineView =  findViewById(R.id.detail_headline);
        descView = findViewById(R.id.detail_desc);
        imageView = findViewById(R.id.detail_image);
        link = findViewById(R.id.detail_url);
        if (getIntent().getStringExtra("headline") != null) {

            Intent intent = getIntent();
            Picasso.with(this).load(intent.getStringExtra("urlToImage")).into(imageView);
            headlineView.setText(intent.getStringExtra("headline"));
            descView.setText(intent.getStringExtra("desc"));
            link.setText(intent.getStringExtra("url"));
        }
    }

}
