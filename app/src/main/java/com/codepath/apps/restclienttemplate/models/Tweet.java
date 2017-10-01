package com.codepath.apps.restclienttemplate.models;


import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by sanal on 9/28/17.
 */

@Table(database = MyDatabase.class)
@Parcel
public class Tweet extends BaseModel implements Serializable {
    @Column
    public String body;

    @Column
    @PrimaryKey
    public long uid;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    public User user;

    @Column
    public String createdAt;

    @Column
    @Nullable
    @ForeignKey(saveForeignKeyModel = false)
    public Media media;

    public Tweet() {
    }

    //deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject)  {
        Tweet tweet = new Tweet();
        //extract the values from JSON

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            JSONObject objExtMedias = jsonObject.getJSONObject("extended_entities");
            if(objExtMedias!=null) {
                JSONArray jsonMedias = objExtMedias.getJSONArray("media");
                if(jsonMedias != null ) {
                    tweet.media = Media.fromJSON(jsonMedias.getJSONObject(0));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Debug","Tweets " + tweet.toString());

        return tweet;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getUid() {
        return uid;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "body='" + body + '\'' +
                ", uid=" + uid +
                ", user=" + user +
                ", createdAt='" + createdAt + '\'' +
                ", media=" + media +
                '}';
    }
}
