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
import android.widget.Toast;

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
    TweetAdapter adapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    SwipeRefreshLayout swipeRefreshLayout;
    EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<Tweet> renderTweets;
    LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_tweet_list, parent, false);
        rvTweets = (RecyclerView)v.findViewById(R.id.rvTweet);
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets);
        renderTweets = new ArrayList<Tweet>();

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        return v;
    }

    public Long addItems(JSONArray response) {
        adapter.clear();
        Long lastTweetId = 0L;
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
        adapter.notifyItemInserted(tweets.size()-1);
        return lastTweetId;
    }
}
