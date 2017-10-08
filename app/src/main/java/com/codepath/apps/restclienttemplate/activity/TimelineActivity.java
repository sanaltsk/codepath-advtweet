package com.codepath.apps.restclienttemplate.activity;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.adapter.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.fragment.ComposeFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragment.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.listener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;


public class TimelineActivity extends AppCompatActivity implements ComposeFragment.OnSuccessTweetUpdate{
    private EndlessRecyclerViewScrollListener scrollListener;

    private FloatingActionButton fabCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    public void onProfileView(MenuItem item) {
        Log.d("debug","profileview");
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void onTweet(MenuItem item) {
        Log.d("debug","compose");
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(null);
        composeFragment.show(fm, "fragment_compose");
    }


    public void onFinishTweetCompose(Tweet tweet) {
        Log.d("debug",tweet.toString());
    }


}
