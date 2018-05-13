package com.example.prakharagarwal.newsapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by prakharagarwal on 13/05/18.
 */

public class NewsListAdapter extends ArrayAdapter<NewsArticle> {

    Context context;

    public NewsListAdapter(@NonNull Context context, ArrayList<NewsArticle> newsList) {
        super(context, 0,newsList);
        this.context=context;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
//        String news = getItem(position);
        NewsArticle article= getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_news_list, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.list_item_news_title);
        TextView description = (TextView) convertView.findViewById(R.id.list_item_news_description);
        ImageView imageView=convertView.findViewById(R.id.news_image);
        // Populate the data into the template view using the data object
        title.setText(article.getTitle());
        description.setText(article.getDescription());
        Picasso.with(context).load(article.getUrlToImage()).into(imageView);


        // Return the completed view to render on screen
        return convertView;
    }

}