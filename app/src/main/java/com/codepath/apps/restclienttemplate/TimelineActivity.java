package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.listener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.R.id.swipeContainer;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.OnSuccessTweetUpdate{
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private Long lastTweetId = 0L;
    private TwitterClient client;
    private TweetAdapter adapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    private FloatingActionButton fabCompose;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient();
        //find the recycler view
        rvTweets = (RecyclerView)findViewById(R.id.rvTweet);
        //init the arraylist
        tweets = new ArrayList<>();
        //construct the adapter from datasource
        adapter = new TweetAdapter(tweets);

        //recyclerview setup (layoutmanger,use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                fetchTimelineAsync();
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        fabCompose = (FloatingActionButton) findViewById(R.id.fabCompose);
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeMessage();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(swipeContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastTweetId = 0L;
                fetchTimelineAsync();
            }
        });
        fetchTimelineAsync();
    }

    public void fetchTimelineAsync() {
        if (isNetworkAvailable()) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("debug", response.toString());
                    adapter.clear();
                    //iterate through json array
                    ArrayList<Tweet> renderTweets = new ArrayList<Tweet>();
                    for (int i = 0; i < response.length(); i++) {
                        //for each entry deserialise the item
                        try {
                            //conver each object to tweet model
                            Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                            if (i == response.length() - 1) {
                                lastTweetId = tweet.uid;
                            }
                            //add the tweetmodel  to our datasource
                            if(tweet.media!=null) {
                                tweet.media.save();
                            }
                            tweet.user.save();
                            tweet.save();
                            renderTweets.add(tweet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    tweets.addAll(renderTweets);
                    //notify adapter
                    adapter.notifyItemInserted(tweets.size() - 1);
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            },lastTweetId);
        } else {
            List<Tweet> tweetList = SQLite.select().from(Tweet.class).queryList();
            Collections.reverse(tweetList);
            tweets.addAll(tweetList);
        }
    }




    private void populateTimeline() {
        if (isNetworkAvailable()) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("debug", errorResponse.toString());
                    throwable.printStackTrace();
                }
            }, lastTweetId);
        }
        else {
            List<Tweet> tweetList = SQLite.select().from(Tweet.class).queryList();
            Collections.reverse(tweetList);
            tweets.addAll(tweetList);
        }
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

    @Override
    public void onFinishTweetCompose(Tweet tweet) {
        tweets.add(0,tweet);
        adapter.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
