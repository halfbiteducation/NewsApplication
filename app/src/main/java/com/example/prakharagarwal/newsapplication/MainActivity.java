package com.example.prakharagarwal.newsapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    String TAG = MainActivity.class.getSimpleName();
    NewsRecyclerAdapter newsRecyclerAdapter;
    ArrayList<NewsArticle> newsArticles;
    RecyclerView recyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_refresh) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

                String source=null;
                if (preferences.getString("sources", null) != null) {
                    source=preferences.getString("sources", "the-verge");
                }
                new SyncTask_GET().execute(source);
        }else if (id == R.id.menu_item_settings) {
            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "on oncreate");

        newsArticles = new ArrayList<>();

        newsRecyclerAdapter = new NewsRecyclerAdapter(this, newsArticles);
        recyclerView = findViewById(R.id.news_recycler_view);
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2,LinearLayoutManager.VERTICAL,false));

        } else
//
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
        recyclerView.setAdapter(newsRecyclerAdapter);

//        SharedPreferences preferences = getSharedPreferences("NewsSource", MODE_PRIVATE);
//        if (preferences.getString("source", null) == null) {
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString("source", "the-verge");
//            editor.apply();
//        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            String source=null;
            if (preferences.getString("sources", null) != null) {
                 source=preferences.getString("sources", "the-verge");
            }
            new SyncTask_GET().execute(source);
        }
        preferences.registerOnSharedPreferenceChangeListener(this);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e(TAG, "on restore instance state");
        String value = (String) savedInstanceState.get("value");

        newsArticles = (ArrayList<NewsArticle>) savedInstanceState.getSerializable("newsList");
        newsRecyclerAdapter.addAll(newsArticles);
        newsRecyclerAdapter.notifyDataSetChanged();

        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "on save instance state");
        outState.putString("value", "1");
        outState.putSerializable("newsList", newsArticles);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        new SyncTask_GET().execute(sharedPreferences.getString(key,"the-verge"));
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
            String source=strings[0];

            try {
                URL url = new URL("https://newsapi.org/v1/articles?source="+source+"&apiKey=52810607c13742f187156c46355d01b7");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                // if there is ssl handshake exception
                urlConnection.setSSLSocketFactory(buildSslSocketFactory(MainActivity.this));
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
                newsArticles.clear();
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

//                        newsArticles.add(title);
                        newsArticles.add(newsArticle);
                    }
                    newsRecyclerAdapter.addAll(newsArticles);
                    newsRecyclerAdapter.notifyDataSetChanged();

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


}
