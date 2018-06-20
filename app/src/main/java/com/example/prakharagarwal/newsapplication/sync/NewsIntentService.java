package com.example.prakharagarwal.newsapplication.sync;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.prakharagarwal.newsapplication.Data.NewsContract;
import com.example.prakharagarwal.newsapplication.DetailsActivity;
import com.example.prakharagarwal.newsapplication.MainActivity;
import com.example.prakharagarwal.newsapplication.NewsArticle;
import com.example.prakharagarwal.newsapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by prakharagarwal on 28/05/18.
 */

public class NewsIntentService extends IntentService {
    String TAG = NewsIntentService.class.getSimpleName();
    List<ContentValues> contentValuesList = new ArrayList<>();
    NewsArticle notificationArticle=null;

    public NewsIntentService() {
        super("NewsIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("hi","on handle in intent service");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String source = null;
        if (preferences.getString("sources", null) != null) {
            source = preferences.getString("sources", "the-verge");
        }
        new SyncTask_GET().execute(source);

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
            String source = strings[0];

            try {
                URL url = new URL("https://newsapi.org/v1/articles?source=" + source + "&apiKey=52810607c13742f187156c46355d01b7");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                // if there is ssl handshake exception
                urlConnection.setSSLSocketFactory(buildSslSocketFactory(getApplicationContext()));
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

                        NewsArticle newsArticle = new NewsArticle();
                        newsArticle.setTitle(title);
                        newsArticle.setDescription(description);
                        newsArticle.setUrl(url);
                        newsArticle.setUrlToImage(urlToImage);
                        if(notificationArticle==null){
                            notificationArticle=newsArticle;
                        }

                        ContentValues NewsValues = new ContentValues();

                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_TITLE, title);
                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_DESCRIPTION, description);
                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_URL, url);
                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_URL_TO_IMAGE, urlToImage);
                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_CATEGORY, "general");
                        contentValuesList.add(NewsValues);
                    }


                    getContentResolver().delete(NewsContract.ArticleEntry.CONTENT_URI,null,null);
                    ContentValues[] newsValues = new ContentValues[contentValuesList.size()];
                    contentValuesList.toArray(newsValues);
                    getContentResolver().bulkInsert(NewsContract.ArticleEntry.CONTENT_URI, newsValues);
                    showNotification();

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    e.printStackTrace();

                }
            }
        }

        private SSLSocketFactory buildSslSocketFactory(Context context) {
            // Add support for self-signed (local) SSL certificates
            // Based on http://developer.android.com/training/articles/security-ssl.html#UnknownCa
            try {

                // Load CAs from an InputStream
                // (could be from a resource or ByteArrayInputStream or ...)
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                // From https://www.networking4all.com/en/ssl+certificates/quickscan/?lang=en&archive=latest&host=newsapi.org
                InputStream is = context.getResources().getAssets().open("USERTrustRSACertificationAuthority.crt");
                InputStream caInput = new BufferedInputStream(is);
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    // System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                } finally {
                    caInput.close();
                }

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext context1 = SSLContext.getInstance("TLS");
                context1.init(null, tmf.getTrustManagers(), null);
                return context1.getSocketFactory();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    private void showNotification() {
        Intent resultIntent = new Intent(this, DetailsActivity.class);
        resultIntent.putExtra("urlToImage", notificationArticle.getUrlToImage());
        resultIntent.putExtra("headline", notificationArticle.getTitle());
        resultIntent.putExtra("desc", notificationArticle.getDescription());
        resultIntent.putExtra("url", notificationArticle.getUrl());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(), "1")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Test Notification")
                .setContentText("content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_android_toy))
                        .bigLargeIcon(null))

                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());

        notificationManager.notify(1, mBuilder.build());
        notificationManager.notify(1, mBuilder.build());
    }
}
