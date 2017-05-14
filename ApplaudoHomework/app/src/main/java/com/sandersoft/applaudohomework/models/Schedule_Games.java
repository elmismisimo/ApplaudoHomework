package com.sandersoft.applaudohomework.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sander on 11/05/2017.
 */

public class Schedule_Games implements Parcelable {
    String mDate;
    String mStadium;

    /**
     * Empty contructor for the json serialization
     */
    public Schedule_Games(){}

    public String getDate() {
        return mDate;
    }
    public void setDate(String date) {
        this.mDate = date;
    }
    public String getStadium() {
        return mStadium;
    }
    public void setStadium(String stadium) {
        this.mStadium = stadium;
    }

    // Parcelling part
    public Schedule_Games(Parcel in){
        mDate = in.readString();
        mStadium = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDate);
        dest.writeString(mStadium);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Schedule_Games createFromParcel(Parcel in) {
            return new Schedule_Games(in);
        }

        public Schedule_Games[] newArray(int size) {
            return new Schedule_Games[size];
        }
    };

}
