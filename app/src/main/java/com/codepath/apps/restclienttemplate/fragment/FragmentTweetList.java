package com.codepath.apps.restclienttemplate.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.listener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sanal on 10/6/17.
 */

public class FragmentTweetList extends Fragment {
    private TweetAdapter adapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    private Long lastTweetId = 0L;
    SwipeRefreshLayout swipeRefreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<Tweet> renderTweets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_tweet_list, parent, false);
        rvTweets = (RecyclerView)v.findViewById(R.id.rvTweet);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets);
        renderTweets = new ArrayList<Tweet>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
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
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastTweetId = 0L;
                fetchTimelineAsync();
            }
        });
        return v;
    }

    public void addItems(JSONArray response) {
        adapter.clear();
        //iterate through json array
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
        Log.d("debug", "tweet size" + tweets.size());
        //notify adapter
        adapter.notifyItemInserted(tweets.size());
    }


    public void fetchTimelineAsync() {
        TwitterClient client = TwitterApp.getRestClient();
        if (isNetworkAvailable()) {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("debug", response.toString());
                    addItems(response);
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
