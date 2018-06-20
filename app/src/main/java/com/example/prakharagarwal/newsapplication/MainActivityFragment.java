package com.example.prakharagarwal.newsapplication;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.prakharagarwal.newsapplication.Data.NewsContract;
import com.example.prakharagarwal.newsapplication.Data.NewsDBHelper;
import com.example.prakharagarwal.newsapplication.sync.NewsIntentService;

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
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by prakharagarwal on 30/05/18.
 */

public class MainActivityFragment extends Fragment {
    String TAG = MainActivity.class.getSimpleName();
    NewsRecyclerAdapter newsRecyclerAdapter;
    ArrayList<NewsArticle> newsArticles;
    RecyclerView recyclerView;
    List<ContentValues> contentValuesList = new ArrayList<>();

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            new SyncTask_GET().execute(sharedPreferences.getString(key, "the-verge"));

        }
    };

    public static MainActivityFragment newInstance(String id) {

        Bundle args = new Bundle();
        args.putString("ID",id);
        MainActivityFragment fragment = new MainActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_refresh) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String source = null;
            if (preferences.getString("sources", null) != null) {
                source = preferences.getString("sources", "the-verge");
            }
            if(getArguments().get("ID").equals("general"))
            new SyncTask_GET().execute(source);
            else
                new SyncTask_GET().execute("bbc-sport");
        } else if (id == R.id.menu_item_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.e("lifecyle", "in oncreate of fragment");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            newsArticles = (ArrayList<NewsArticle>) savedInstanceState.getSerializable("newsList");
            newsRecyclerAdapter.addAll(newsArticles);
            newsRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.e("lifecyle", "in oncreateview of fragment");
        SharedPreferences preferences1 = getActivity().getSharedPreferences("Alarm", MODE_PRIVATE);

        if (preferences1.getString("alarmSet", null) == null) {
            Intent intent = new Intent(getActivity(), NewsIntentService.class);
            PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 1, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),  6*60*60 * 1000, pendingIntent);

            SharedPreferences.Editor editor = preferences1.edit();
            editor.putString("alarmSet", "set");
            editor.apply();

            //if you want to cancel the alarm, pass same intent with same request code;
//               alarmManager.cancel(pendingIntent);
        }


        newsArticles = new ArrayList<>();

        newsRecyclerAdapter = new NewsRecyclerAdapter(getActivity(), newsArticles);
        recyclerView = rootView.findViewById(R.id.news_recycler_view);
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));

        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        }
        recyclerView.setAdapter(newsRecyclerAdapter);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (savedInstanceState == null) {
            if(getArguments().get("ID").equals("general"))
            {
            Cursor cursor = getActivity().getContentResolver().query(NewsContract.ArticleEntry.CONTENT_URI, null, null, null, null);
            if (cursor.getCount() > 0) {
                newsArticles.clear();
                while (cursor.moveToNext()) {
                    NewsArticle article = new NewsArticle();
                    article.setTitle(cursor.getString(0));
                    article.setDescription(cursor.getString(1));
                    article.setUrl(cursor.getString(2));
                    article.setUrlToImage(cursor.getString(3));
                    newsArticles.add(article);
                }
                newsRecyclerAdapter.addAll(newsArticles);
                newsRecyclerAdapter.notifyDataSetChanged();
            } else {
                String source = "the-verge";
                if (preferences.getString("sources", null) != null) {
                    source = preferences.getString("sources", "the-verge");
                }
                new SyncTask_GET().execute(source);
            }
                preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

            }else {
                new SyncTask_GET().execute("bbc-sport");
            }
        }

createNotificationChannel();
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), "1")
//                .setSmallIcon(R.drawable.ic_notification)
//                .setContentTitle("Test Notification")
//                .setContentText("content")
//                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line   Much longer text that cannot fit one line"))
//                .setVisibility(NotificationCompat.VISIBILITY_SECRET);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), "1")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Test Notification")
                .setContentText("content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_android_toy))
                .bigLargeIcon(null))

                .setVisibility(NotificationCompat.VISIBILITY_SECRET);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());

        notificationManager.notify(1, mBuilder.build());
        notificationManager.notify(1, mBuilder.build());

        return rootView;
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "news";
            String description = "news sources";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "on save instance state");
        outState.putString("value", "1");
        outState.putSerializable("newsList", newsArticles);
        super.onSaveInstanceState(outState);
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
                urlConnection.setSSLSocketFactory(buildSslSocketFactory(getActivity()));
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

                        newsArticles.add(newsArticle);

                        ContentValues NewsValues = new ContentValues();

                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_TITLE, title);
                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_DESCRIPTION, description);
                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_URL, url);
                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_URL_TO_IMAGE, urlToImage);
                        NewsValues.put(NewsContract.ArticleEntry.COLUMN_CATEGORY, "general");
                        contentValuesList.add(NewsValues);
                    }

                    newsRecyclerAdapter.addAll(newsArticles);
                    newsRecyclerAdapter.notifyDataSetChanged();
                    if(getArguments().get("ID").equals("general")) {
                        getActivity().getContentResolver().delete(NewsContract.ArticleEntry.CONTENT_URI, null, null);
                        ContentValues[] newsValues = new ContentValues[contentValuesList.size()];
                        contentValuesList.toArray(newsValues);
                        getActivity().getContentResolver().bulkInsert(NewsContract.ArticleEntry.CONTENT_URI, newsValues);
                    }
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
