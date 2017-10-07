package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by sanal on 9/28/17.
 */


@Table(database = MyDatabase.class)
@Parcel
public class User extends BaseModel implements Serializable{
    @Column
    public String name;

    @Column
    @PrimaryKey
    public long uid;

    @Column
    public String screenName;

    @Column
    public String tagLine;

    @Column
    public int followingCount;

    @Column
    public int followersCount;

    @Column
    public String profileImageUrl;

    public User() {
    }

    //deserialize
    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");
        user.tagLine = jsonObject.getString("description");
        user.followersCount = jsonObject.getInt("followers_count");
        user.followingCount = jsonObject.getInt("friends_count");
        return user;
    }

    public long getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

//    @Override
//    public void writeToParcel(android.os.Parcel dest, int flags) {
//        dest.writeString(name);
//        dest.writeLong(uid);
//        dest.writeString(screenName);
//        dest.writeString(profileImageUrl);
//    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", uid=" + uid +
                ", screenName='" + screenName + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}
