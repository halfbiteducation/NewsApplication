package com.example.prakharagarwal.newsapplication.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by prakharagarwal on 22/05/18.
 */

public class NewsDBHelper extends SQLiteOpenHelper {

    public NewsDBHelper(Context context) {
        super(context, NewsContract.DATABASE_NAME, null, NewsContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ARTICLE_TABLE = "CREATE TABLE " + NewsContract.ArticleEntry.TABLE_NAME + " (" +
                NewsContract.ArticleEntry.COLUMN_TITLE + "  TEXT NOT NULL," +
                NewsContract.ArticleEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                NewsContract.ArticleEntry.COLUMN_URL + " TEXT NOT NULL, " +
                NewsContract.ArticleEntry.COLUMN_URL_TO_IMAGE + " TEXT NOT NULL, " +
                NewsContract.ArticleEntry.COLUMN_CATEGORY + " TEXT NOT NULL " +

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsContract.ArticleEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }


}
