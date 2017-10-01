package com.codepath.apps.restclienttemplate;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailTweetActivity extends AppCompatActivity {
    TextView tvTweetDetailUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tweet);

//        Tweet tweet = (Tweet) getIntent().getParcelableExtra("tweet");
        Tweet tweet = (Tweet) getIntent().getSerializableExtra("tweet");
        if(tweet!=null) {
            Log.d("debug",tweet.toString());

            TextView tvTweetDetailName = (TextView)findViewById(R.id.tvTweetDetailName);
            tvTweetDetailUsername = (TextView) findViewById(R.id.tvTweetDetailUsername);
            TextView tvTweetDetailTime = (TextView) findViewById(R.id.tvTweetDetailTime);
            TextView tvTweetDetailBody = (TextView) findViewById(R.id.tvTweetDetailBody);
            ImageView ivTweetDetailProfileImage = (ImageView)findViewById(R.id.ivTweetDetailProfileImage);
            ImageButton btnReply = (ImageButton) findViewById(R.id.btnReply);

            tvTweetDetailName.setText(tweet.user.name);
            tvTweetDetailUsername.setText(tweet.user.screenName);
            tvTweetDetailTime.setText(getRelativeTimeAgo(tweet.createdAt));
            tvTweetDetailBody.setText(tweet.body);
            Glide.with(getApplicationContext())
                    .load(tweet.user.profileImageUrl)
                    .into(ivTweetDetailProfileImage);

            btnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    ComposeFragment composeFragment = ComposeFragment.newInstance(tvTweetDetailUsername.getText().toString());
                    composeFragment.show(fm, "fragment_compose");
                }
            });
        }

    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
