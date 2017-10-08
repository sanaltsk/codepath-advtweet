package com.codepath.apps.restclienttemplate.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

public class ComposeFragment extends DialogFragment {
    private SharedPreferences pref;
    private EditText etStatus;
    private Button btnTweet;
    private TwitterClient client;
    private ImageView ivProfileImage;
    private TextView tvName;
    private TextView tvUsername;
    private User user;
    private TextView tvCharCount;
    private TextView tvComposeTitle;
    private OnSuccessTweetUpdateListener mTweetUpdate;
    public ComposeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose,container);
    }

    public static ComposeFragment newInstance(String replyTo) {
        
        Bundle args = new Bundle();
        ComposeFragment fragment = new ComposeFragment();
        args.putString("replyTo", replyTo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("tweet", etStatus.getText().toString());
        edit.commit();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String replyTo = getArguments().getString("replyTo");

        super.onViewCreated(view, savedInstanceState);
        client = TwitterApp.getRestClient();

        etStatus = (EditText) view.findViewById(R.id.etStatus);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvUsername = (TextView)view.findViewById(R.id.tvFragmentUserName);
        ivProfileImage = (ImageView) view.findViewById(R.id.ivFragmentProfileImage);
        tvCharCount = (TextView)view.findViewById(R.id.tvCharCount);
        tvComposeTitle = (TextView)view.findViewById(R.id.tvComposeTitle);

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String savedTweet = pref.getString("tweet","none");
        if(!savedTweet.equals("none")) {
            etStatus.setText(savedTweet);
            etStatus.setSelection(savedTweet.length());
        }

        if(!TextUtils.isEmpty(replyTo)){
            String reply = replyTo;
            etStatus.setText("@"+reply+" ");
            etStatus.setSelection(etStatus.getText().length());
            tvComposeTitle.setText("Reply " + reply);
            int remainingChar = 140-reply.length()-1;
            tvCharCount.setText(remainingChar+"");
        } else {
            tvComposeTitle.setText("Compose");
        }
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
                    tvName.setText(user.name);
                    tvUsername.setText("@"+user.screenName);
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
//                        OnSuccessTweetUpdateListener listener = (OnSuccessTweetUpdateListener) getActivity().getSupportFragmentManager().findFragmentById(R.id.viewpager);
                        OnSuccessTweetUpdateListener listener = (OnSuccessTweetUpdateListener) getActivity();
                        SharedPreferences.Editor edite = pref.edit();
                        edite.remove("tweet");
                        edite.commit();
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

        etStatus.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int length = etStatus.getText().length();
                int maxLength = 140;
                int remaining = maxLength - length;
                tvCharCount.setText(remaining+"");
                return false;
            }
        });

    }

    public interface OnSuccessTweetUpdateListener {
        void onFinishTweetCompose(Tweet tweet);
    }

}
