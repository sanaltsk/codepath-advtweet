package com.codepath.apps.restclienttemplate.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.listener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by sanal on 10/6/17.
 */

public class FragmentTweetList extends Fragment {
    private TweetAdapter adapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_tweet_list, parent, false);
        rvTweets = (RecyclerView)v.findViewById(R.id.rvTweet);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);
        // Retain an instance so that you can call `resetState()` for fresh searches
        /* scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                fetchTimelineAsync();
            }
        };
        rvTweets.addOnScrollListener(scrollListener); */
        return v;
    }

    public Long addItems(JSONArray response, Long lastTweetId) {
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
        return lastTweetId;
    }
}
