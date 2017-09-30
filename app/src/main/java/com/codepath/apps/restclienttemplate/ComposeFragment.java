package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;


public class ComposeFragment extends DialogFragment {
    private EditText etStatus;
    private Button btnTweet;
    private TwitterClient client;
    private ImageView ivProfileImage;
    private TextView tvUsername;
    private User user;
    public ComposeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose,container);
    }

    public static ComposeFragment newInstance() {
        
        Bundle args = new Bundle();
        ComposeFragment fragment = new ComposeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = TwitterApp.getRestClient();

        etStatus = (EditText) view.findViewById(R.id.etStatus);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        tvUsername = (TextView)view.findViewById(R.id.tvFragmentUserName);
        ivProfileImage = (ImageView) view.findViewById(R.id.ivFragmentProfileImage);


        client.verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("debug","Verify Credentials :: " + response.toString());
                try {
                    user = new User();
                    user.uid = response.getLong("id");
                    user.name = response.getString("name");
                    user.screenName = response.getString("screen_name");
                    user.profileImageUrl = response.getString("profile_image_url");
                    tvUsername.setText(user.screenName);
                    Glide.with(getContext())
                            .load(user.profileImageUrl)
                            .into(ivProfileImage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("debug","Verify Credentials Failed");
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String updateStatus = etStatus.getText().toString();
                Log.d("debug","Post stauts :: " + updateStatus);
                client.postUpdate(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Tweet tweet = new Tweet();
                        tweet.user = user;
                        tweet.body = updateStatus;
                        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
                        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
                        Date dt = new Date();
                        tweet.createdAt = sf.format(dt);
                        Toast.makeText(getContext(),"Tweet posted successfully!",Toast.LENGTH_LONG).show();
                        Log.d("debug","Posted successfully" + tweet.toString());
                        OnSuccessTweetUpdate listener = (OnSuccessTweetUpdate)getActivity();
                        listener.onFinishTweetCompose(tweet);
                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("debug","Tweet post failed");
                    }
                },updateStatus);
            }
        });

    }

    public interface OnSuccessTweetUpdate {
        void onFinishTweetCompose(Tweet tweet);
    }

}
