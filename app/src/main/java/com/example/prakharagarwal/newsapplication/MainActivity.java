package com.example.prakharagarwal.newsapplication;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Displaying Toasts
        Toast.makeText(this, "this is a Toast", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "in onCreate()");

        //Connecting to NewsApi without Async Task
//        HttpURLConnection urlConnection = null;
//        BufferedReader reader=null;
//
//        try {
//            URL url = new URL("https://newsapi.org/v1/articles?source=the-verge&apiKey="+R.string.news_api_key);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
////            urlConnection.connect();
//
//
//            // Read the input stream into a String
//            InputStream inputStream = urlConnection.getInputStream();
//            StringBuffer buffer = new StringBuffer();
//
//             reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                // But it does make debugging a *lot* easier if you print out the completed
//                // buffer for debugging.
//                buffer.append(line + "\n");
//            }
//
//            String JsonStr = buffer.toString();
//            Log.d("News Response",JsonStr);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (final IOException e) {
//                    Log.e(TAG, "Error closing stream", e);
//                }
//            }
//        }

        new SyncTask_GET().execute();
    }

    public class SyncTask_GET extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;

            try {
                URL url = new URL("https://newsapi.org/v1/articles?source=the-verge&apiKey=52810607c13742f187156c46355d01b7");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                JsonStr = buffer.toString();
                Log.d("News Response", JsonStr);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return JsonStr;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                final String OWM_ARTICLES = "articles";
                final String OWM_SOURCE = "source";
                final String OWM_TITLE = "title";
                final String OWM_DESCRIPTION = "description";
                final String OWM_URL = "url";
                final String OWM_URL_TO_IMAGE = "urlToImage";

                try {
                    JSONObject forecastJson = new JSONObject(s);

                    String mSource = forecastJson.getString(OWM_SOURCE);
                    JSONArray newsArray = forecastJson.getJSONArray(OWM_ARTICLES);

                    for (int i = 0; i < newsArray.length(); i++) {
                        // These are the values that will be collected.
                        String title;
                        String description;
                        String url;
                        String urlToImage;

                        // Get the JSON object representing the day
                        JSONObject article = newsArray.getJSONObject(i);

                        title = article.getString(OWM_TITLE);
                        description = article.getString(OWM_DESCRIPTION);
                        url = article.getString(OWM_URL);
                        urlToImage = article.getString(OWM_URL_TO_IMAGE);

                    }

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    e.printStackTrace();

                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "in onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "in onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w(TAG, "in onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "in onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.w(TAG, "in onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "in onDestroy()");

    }
}
