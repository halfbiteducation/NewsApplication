package com.example.prakharagarwal.newsapplication;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity {

    String TAG = MainActivity.class.getSimpleName();
    //    ArrayList<String> newsArticles;
    ArrayAdapter adapter;
    NewsListAdapter newsListAdapter;
    ArrayList<NewsArticle> newsArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Displaying Toasts
        Toast.makeText(this, "this is a Toast", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "in onCreate()");

// list view mock data
//        String[] mobileArray = {"Black Widow","Brown Recluse","Honey Bee","Army Ants",
//                "Ladybug","Dog Flea","Head Lice","Malaria Mosquito","Wolf Spider","Brown Scorpion","Centipede","American Cockroach"
//        ,"Fruit Fly","Yellow Jacket"};

        // simple adapter without custom view
//        newsArticles = new ArrayList<>();
//        adapter = new ArrayAdapter<String>(this,
//                R.layout.item_listview, newsArticles);

        newsArticles = new ArrayList<>();
        newsListAdapter = new NewsListAdapter(this, newsArticles);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(newsListAdapter);

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
            SSLContext context=null;

            // Load CAs from an InputStream
// (could be from a resource or ByteArrayInputStream or ...)
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
// From https://www.washington.edu/itconnect/security/ca/load-der.crt
                InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
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
                 context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "hi", Toast.LENGTH_SHORT).show();
            }

            try {
                URL url = new URL("https://newsapi.org/v1/articles?source=the-verge&apiKey=52810607c13742f187156c46355d01b7");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
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

//                        newsArticles.add(title);
                        newsArticles.add(newsArticle);
                    }
                    newsListAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    e.printStackTrace();

                }
            }
        }
    }


}
