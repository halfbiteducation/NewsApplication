package com.example.prakharagarwal.newsapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by prakharagarwal on 29/12/16.
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ArticleAdapterViewHolder> {

    final private Context mContext;
    ArticleAdapterViewHolder holder;
    ArrayList<NewsArticle> articles;


    public NewsRecyclerAdapter(Context context, ArrayList<NewsArticle> articles) {
        mContext = context;
        this.articles=articles;
    }



    @Override
    public ArticleAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_list, parent, false);
            holder = new ArticleAdapterViewHolder(view);
            return holder;
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ArticleAdapterViewHolder holder, int position) {

        NewsArticle article= articles.get(position);
        holder.title.setText(article.getTitle());
        Picasso.with(mContext).load(article.getUrlToImage()).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void addAll(ArrayList<NewsArticle> newsArticles) {
        this.articles=newsArticles;
    }


    public class ArticleAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView imageView;

        public ArticleAdapterViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.list_item_news_title);
            imageView = view.findViewById(R.id.news_image);
            view.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            NewsArticle article= articles.get(getAdapterPosition());

            Intent intent = new Intent(mContext,  DetailsActivity.class);
            intent.putExtra("urlToImage",article.getUrlToImage());
            intent.putExtra("headline", article.getTitle());
            intent.putExtra("desc", article.getDescription());
            intent.putExtra("url", article.getUrl());
            mContext.startActivity(intent);

        }
    }


}
