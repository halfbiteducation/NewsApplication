package com.example.prakharagarwal.newsapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailActivityFragment extends Fragment {

    TextView headlineView;
    TextView descView;
    ImageView imageView;
    TextView link;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail,menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.menu_item_share){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getIntent().getStringExtra("headline"));
            sendIntent.putExtra(Intent.EXTRA_TEXT, getActivity().getIntent().getStringExtra("url"));
            sendIntent.setType("text/plain");
//            startActivity(sendIntent);
            startActivity(Intent.createChooser(sendIntent, "Share"));
        }else if (id == android.R.id.home) {
            //  onBackPressed();

            NavUtils.navigateUpFromSameTask(getActivity());
            // or pass an intent to main activity
        }
        return true;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_detail, container, false);

        headlineView =  rootView.findViewById(R.id.detail_headline);
        descView = rootView.findViewById(R.id.detail_desc);
        imageView = rootView.findViewById(R.id.detail_image);
        link = rootView.findViewById(R.id.detail_url);
        Bundle arguments = getArguments();

        if (arguments != null) {
            headlineView.setText(arguments.getString("headline"));
            descView.setText(arguments.getString("desc"));
            link.setText(arguments.getString("url"));
            Picasso.with(getContext()).load(arguments.getString("urlToImage")).into(imageView);
        } else
        if (!getActivity().getIntent().getStringExtra("headline").equals("")) {

            Intent intent = getActivity().getIntent();
            Picasso.with(getActivity()).load(intent.getStringExtra("urlToImage")).into(imageView);
            headlineView.setText(intent.getStringExtra("headline"));
            descView.setText(intent.getStringExtra("desc"));
            link.setText(intent.getStringExtra("url"));
        }
        return rootView;
    }

}
