package com.codepath.apps.restclienttemplate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.listener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private Long lastTweetId = 0L;
    private TwitterClient client;
    private TweetAdapter adapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;

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
                populateTimeline();
            }
        };
        rvTweets.addOnScrollListener(scrollListener);
        populateTimeline();
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("debug", response.toString());
                //iterate through json array
                for(int i=0;i<response.length();i++) {
                    //for each entry deserialise the item
                    try {
                        //conver each object to tweet model
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        if(i==response.length()-1) {
                            lastTweetId = tweet.uid;
                        }
                        //add the tweetmodel  to our datasource
                        tweets.add(tweet);
                        //notify adapter
                        adapter.notifyItemInserted(tweets.size() -1 );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("debug", errorResponse.toString());
                throwable.printStackTrace();
            }
        }, lastTweetId);
    }
}
