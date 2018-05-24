package com.example.prakharagarwal.newsapplication.Data;

import android.provider.BaseColumns;

/**
 * Created by prakharagarwal on 22/05/18.
 */

public class NewsContract implements BaseColumns {

    static final String DATABASE_NAME = "news.db";
     static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "article";
    public static final String COLUMN_TITLE = "title";

    // In order to uniquely pinpoint the location on the map when we launch the
    // map intent, we store the latitude and longitude as returned by openweathermap.
    public static final String COLUMN_DESCRIPTION = "discription";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_URL_TO_IMAGE = "urlToImage";
    public static final String COLUMN_CATEGORY = "category";
}