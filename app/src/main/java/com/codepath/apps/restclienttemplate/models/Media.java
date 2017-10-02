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
 * Created by sanal on 9/30/17.
 */

@Table(database = MyDatabase.class)
@Parcel
public class Media extends BaseModel implements Serializable{
    @PrimaryKey
    @Column
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column
    String type;

    @Column
    String mediaUrl;

    public void setType(String type) {
        this.type = type;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getType() {
        return type;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public Media() {
    }

    public static Media fromJSON(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.type = jsonObject.getString("type");
        media.mediaUrl = jsonObject.getString("media_url");
        return media;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                '}';
    }
}
