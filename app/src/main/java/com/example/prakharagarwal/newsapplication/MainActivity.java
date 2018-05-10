package com.example.prakharagarwal.newsapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Displaying Toasts
        Toast.makeText(this, "this is a Toast", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "in onCreate()");
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
