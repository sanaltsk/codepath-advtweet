package com.codepath.apps.restclienttemplate.activity;


import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.adapter.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.fragment.ComposeFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragment.FragmentTweetList;
import com.codepath.apps.restclienttemplate.listener.EndlessRecyclerViewScrollListener;


public class TimelineActivity extends AppCompatActivity {
    private EndlessRecyclerViewScrollListener scrollListener;

    private FloatingActionButton fabCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        fabCompose = (FloatingActionButton) findViewById(R.id.fabCompose);
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeMessage();
            }
        });

        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager(), this));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.twittermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.miCompose) {
            composeMessage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void composeMessage() {
        Log.d("debug","compose");
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(null);
        composeFragment.show(fm, "fragment_compose");
    }

    //TODO
    /* @Override
    public void onFinishTweetCompose(Tweet tweet) {
        tweets.add(0,tweet);
        adapter.notifyDataSetChanged();
    } */


}
