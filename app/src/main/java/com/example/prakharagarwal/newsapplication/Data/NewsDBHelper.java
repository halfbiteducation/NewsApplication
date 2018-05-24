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
    SQLiteDatabase mDatabase;

    public void openConnection() {
        if (mDatabase == null)
            mDatabase = this.getWritableDatabase();
    }


    public NewsDBHelper(Context context) {
        super(context, NewsContract.DATABASE_NAME, null, NewsContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ARTICLE_TABLE = "CREATE TABLE " + NewsContract.TABLE_NAME + " (" +
                NewsContract.COLUMN_TITLE + "  TEXT NOT NULL," +
                NewsContract.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                NewsContract.COLUMN_URL + " TEXT NOT NULL, " +
                NewsContract.COLUMN_URL_TO_IMAGE + " TEXT NOT NULL, " +
                NewsContract.COLUMN_CATEGORY + " TEXT NOT NULL " +

                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_ARTICLE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsContract.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public void insertNews(List<ContentValues> contentValuesList) {
        for (int i = 0; i < contentValuesList.size(); i++) {
            mDatabase.insert(NewsContract.TABLE_NAME, null, contentValuesList.get(i));
        }
    }

    public void clearNews() {
        mDatabase.delete(NewsContract.TABLE_NAME, null, null);
    }

    public int getNewsCount() {
        String query = "select count(*) from " + NewsContract.TABLE_NAME + "";
        Cursor cursor = mDatabase.rawQuery(query, null);
        cursor.moveToFirst();

        return cursor.getInt(0);

    }

    public Cursor getNews() {
//        String query="select * from " +NewsContract.TABLE_NAME+"";
//        Cursor cursor=mDatabase.rawQuery(query,null);
        Cursor cursor = mDatabase.query(NewsContract.TABLE_NAME, null, null, null, null, null, null);
//        String projection[]={"title","discription"};
//        Cursor cursor=mDatabase.query(NewsContract.TABLE_NAME,projection,null,null,null,null,null);
        return cursor;
    }
}
