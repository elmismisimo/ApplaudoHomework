package com.sandersoft.applaudohomework.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander on 11/05/2017.
 */

public class Team implements Parcelable{

    int mId;
    String mTeam_name;
    String mSince;
    String mCoach;
    String mTeam_nickname;
    String mStadium;
    String mImg_logo;
    String mImg_stadium;
    String mLatitude;
    String mLongitude;
    String mWebsite;
    String mTickets_url;
    String mAddress;
    String mPhone_number;
    String mDescription;
    String mVideo_url;
    List<Schedule_Games> mSchedule_games = new ArrayList<>();
    Bitmap mImage;

    /**
     * Empty contructor for the json serialization
     */
    public Team(){}

    public int getId() {
        return mId;
    }
    public void setId(int id) {
        this.mId = id;
    }
    public String getTeam_name() {
        return mTeam_name;
    }
    public void setTeam_name(String team_name) {
        this.mTeam_name = team_name;
    }
    public String getSince() {
        return mSince;
    }
    public void setSince(String since) {
        this.mSince = since;
    }
    public String getCoach() {
        return mCoach;
    }
    public void setCoach(String coach) {
        this.mCoach = coach;
    }
    public String getTeam_nickname() {
        return mTeam_nickname;
    }
    public void setTeam_nickname(String team_nickname) {
        this.mTeam_nickname = team_nickname;
    }
    public String getStadium() {
        return mStadium;
    }
    public void setStadium(String stadium) {
        this.mStadium = stadium;
    }
    public String getImg_logo() {
        return mImg_logo;
    }
    public void setImg_logo(String img_logo) {
        this.mImg_logo = img_logo;
    }
    public String getImg_stadium() {
        return mImg_stadium;
    }
    public void setImg_stadium(String img_stadium) {
        this.mImg_stadium = img_stadium;
    }
    public String getLatitude() {
        return mLatitude;
    }
    public void setLatitude(String latitude) {
        this.mLatitude = latitude;
    }
    public String getLongitude() {
        return mLongitude;
    }
    public void setLongitude(String longitude) {
        this.mLongitude = longitude;
    }
    public String getWebsite() {
        return mWebsite;
    }
    public void setWebsite(String website) {
        this.mWebsite = website;
    }
    public String getTickets_url() {
        return mTickets_url;
    }
    public void setTickets_url(String tickets_url) {
        this.mTickets_url = tickets_url;
    }
    public String getAddress() {
        return mAddress;
    }
    public void setAddress(String address) {
        this.mAddress = address;
    }
    public String getPhone_number() {
        return mPhone_number;
    }
    public void setPhone_number(String phone_number) {
        this.mPhone_number = phone_number;
    }
    public String getDescription() {
        return mDescription;
    }
    public void setDescription(String description) {
        this.mDescription = description;
    }
    public String getVideo_url() {
        return mVideo_url;
    }
    public void setVideo_url(String video_url) {
        this.mVideo_url = video_url;
    }
    public List<Schedule_Games> getSchedule_games() {
        return mSchedule_games;
    }
    public void setSchedule_games(List<Schedule_Games> schedule_games) {
        this.mSchedule_games = schedule_games;
    }
    public Bitmap getImage() {
        return mImage;
    }
    public void setImage(Bitmap image) {
        this.mImage = image;
    }

    // Parcelling part
    public Team(Parcel in){
        mId = in.readInt();
        mTeam_name = in.readString();
        mSince = in.readString();
        mCoach = in.readString();
        mTeam_nickname = in.readString();
        mStadium = in.readString();
        mImg_logo = in.readString();
        mImg_stadium = in.readString();
        mLatitude = in.readString();
        mLongitude = in.readString();
        mWebsite = in.readString();
        mTickets_url = in.readString();
        mAddress = in.readString();
        mPhone_number = in.readString();
        mDescription = in.readString();
        mVideo_url = in.readString();
        mSchedule_games = in.readArrayList(Schedule_Games.class.getClassLoader());
        //mImage = in.readParcelable(Bitmap.class.getClassLoader());
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTeam_name);
        dest.writeString(mSince);
        dest.writeString(mCoach);
        dest.writeString(mTeam_nickname);
        dest.writeString(mStadium);
        dest.writeString(mImg_logo);
        dest.writeString(mImg_stadium);
        dest.writeString(mLatitude);
        dest.writeString(mLongitude);
        dest.writeString(mWebsite);
        dest.writeString(mTickets_url);
        dest.writeString(mAddress);
        dest.writeString(mPhone_number);
        dest.writeString(mDescription);
        dest.writeString(mVideo_url);
        dest.writeList(mSchedule_games);
        //dest.writeParcelable(mImage, flags);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

}
