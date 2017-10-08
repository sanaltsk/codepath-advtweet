package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragment.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragment.MentionsTimelineFragment;

/**
 * Created by sanal on 10/6/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] {"Home", "Mentions" };
    private Context context;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==1){
            return new MentionsTimelineFragment();
        } else if(position==0) {
            return new HomeTimelineFragment();
        } else  {
            return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
