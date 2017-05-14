package com.sandersoft.applaudohomework.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.sandersoft.applaudohomework.ApplicationMain;
import com.sandersoft.applaudohomework.R;
import com.sandersoft.applaudohomework.models.Team;
import com.sandersoft.applaudohomework.utils.TeamRequestListener;
import com.sandersoft.applaudohomework.views.DetailFragment;

/**
 * Created by Sander on 12/05/2017.
 */

public class ControllerDetail implements Parcelable, TeamRequestListener {
    DetailFragment mView;
    Team mTeam;

    public ControllerDetail(DetailFragment view){
        setView(view);
    }
    /**
     * Sets the view of this controller, normaly called after a screen rotation event
     * @param view the fragment view of this controller
     */
    public void setView(DetailFragment view){
        this.mView = view;
    }

    /**
     * Requests an image of the team, with an imageholder so the image is placed there when finished
     * @param imageHolder The ImageView where the returned image will be placed. It can be null
     */
    public void getTeamImage(ImageView imageHolder){
        ApplicationMain.instance.webManager.doImageRequest(this, imageHolder, mTeam, mTeam.getImg_logo());
    }

    public Team getTeam(){
        return mTeam;
    }
    public void setTeam(Team team){
        this.mTeam = team;
    }

    /**
     * Receives a callback from the webManager wit the respons eof the mTeams request (this function
     * is no tlikely to be called in this class)
     * @param mTeams The teams from the query
     */
    @Override
    public void receiveTeams(final Team[] mTeams){ }

    /**
     * Receives a callback from the webManager with the image response of the request
     * @param image the bitmap image returned from the server
     * @param team the team object of the image request
     * @param imageHolder the  image holder to place the image
     */
    @Override
    public void receiveImage(Bitmap image, Team team, ImageView imageHolder) {
        team.setImage(image);
        if (null != imageHolder)
            imageHolder.setImageBitmap(image);
    }
    /**
     * In case the teams or image request had an error, this segment is in charge of receiving it
     * @param error
     */
    @Override
    public void receiveError(String error){
        ApplicationMain.showToast(error);
    }

    /**
     * Generate an intent to call a team if there is an app that will handle the call
     */
    public void callTeam(){
        Intent sendIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mTeam.getPhone_number()));
        // Verify that the intent will resolve to an activity
        String title = mView.getResources().getString(R.string.call);
        Intent chooser = Intent.createChooser(sendIntent, title);
        if (sendIntent.resolveActivity(mView.getActivity().getPackageManager()) != null) {
            mView.startActivity(chooser);
        } else {
            ApplicationMain.showToast(R.string.no_call_app);
        }
    }
    /**
     * Generates an intent to share the team name and website  if there is an app that will handle the share
     */
    public void shareTeam(){
        // Create message for sharing
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String message = mTeam.getTeam_name() + " " + mTeam.getWebsite();
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        // Verify that the intent will resolve to an activity
        String title = mView.getResources().getString(R.string.sharing_title);
        Intent chooser = Intent.createChooser(sendIntent, title);
        if (sendIntent.resolveActivity(mView.getActivity().getPackageManager()) != null) {
            mView.startActivity(chooser);
        } else {
            ApplicationMain.showToast(R.string.no_share_app);
        }
    }

    // Parcelling part
    public ControllerDetail(Parcel in){
        mTeam = in.readParcelable(Team.class.getClassLoader());
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mTeam, flags);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ControllerDetail createFromParcel(Parcel in) {
            return new ControllerDetail(in);
        }

        public ControllerDetail[] newArray(int size) {
            return new ControllerDetail[size];
        }
    };

}
