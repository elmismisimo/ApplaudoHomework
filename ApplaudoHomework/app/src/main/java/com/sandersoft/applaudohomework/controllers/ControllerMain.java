package com.sandersoft.applaudohomework.controllers;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.sandersoft.applaudohomework.ApplicationMain;
import com.sandersoft.applaudohomework.models.Team;
import com.sandersoft.applaudohomework.utils.TeamRequestListener;
import com.sandersoft.applaudohomework.views.MainFragment;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Sander on 11/05/2017.
 */

public class ControllerMain implements Parcelable, TeamRequestListener {

    MainFragment mView;
    ArrayList<Team> mTeams = new ArrayList<>();
    boolean mMoreAvailable = false;
    boolean mFetching = false;
    int mIndex = 0;
    boolean mOffline = false;
    int mSelectedTeam = -1;

    public ControllerMain(MainFragment view){
        setView(view);
    }
    /**
     * Sets the view of this controller, normaly called after a screen rotation event
     * @param view the fragment view of this controller
     */
    public void setView(MainFragment view){
        this.mView = view;
    }

    public ArrayList<Team> getTeams(){
        return mTeams;
    }
    public int getSelectedTeam(){
        return mSelectedTeam;
    }
    public void setSelectedTeam(int mSelectedTeam) {
        this.mSelectedTeam = mSelectedTeam;
    }
    public boolean isOffline() {
        return mOffline;
    }
    public int geIndex() {
        return mIndex;
    }
    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
    }
    public boolean isMoreAvailable() {
        return mMoreAvailable;
    }
    public void setMoreAvailable(boolean moreAvailable) {
        this.mMoreAvailable = moreAvailable;
    }
    public boolean isFetching() {
        return mFetching;
    }

    /**
     * Creates a new web request to the applaudo server requetsing the mTeams
     */
    public void fetchTeams(){
        if (!ApplicationMain.instance.webManager.verifyConn()){
            mOffline = true;
            mView.placeOfflineLayout();
            return;
        }
        mOffline = false;
        mFetching = true;
        ApplicationMain.instance.webManager.doTeamsRequest(this);
    }
    /**
     * Receives a callback from the webManager wit the respons eof the mTeams request
     * @param mTeams
     */
    @Override
    public void receiveTeams(final Team[] mTeams){
        //we create the observable (this is in charge of retrieving the mTeam list)
        Observable teamsObservable = Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                //check if last element is null, then remove it
                if (null == ControllerMain.this.mTeams.get(ControllerMain.this.mTeams.size()-1))
                    ControllerMain.this.mTeams.remove(ControllerMain.this.mTeams.size()-1);
                //filter the mTeams
                int i = 0;
                for (; i < 10 && i + mIndex < mTeams.length; i++){
                    ControllerMain.this.mTeams.add(mTeams[i + mIndex]);
                    //fetch the images
                    ApplicationMain.instance.webManager.doImageRequest(ControllerMain.this, null,
                            ControllerMain.this.mTeams.get(ControllerMain.this.mTeams.size() - 1),
                            ControllerMain.this.mTeams.get(ControllerMain.this.mTeams.size() - 1).getImg_logo());
                }
                mIndex += i;
                mMoreAvailable = mTeams.length > mIndex;
                if (mMoreAvailable)
                    //add element thet will be interpreted as loading element
                    ControllerMain.this.mTeams.add(null);

                mFetching = false;
                return mMoreAvailable;
            }
        });
        //we create the observer
        Observer<Boolean> teamsObserver = new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {}
            @Override
            public void onNext(Boolean response) {
                    //mView.restoreScrollLinstener();
                    //mView.teamsAdapter.notifyData();
            }
            @Override
            public void onError(Throwable e) {}
            @Override
            public void onComplete() {
                mView.restoreScrollLinstener();
                mView.teamsAdapter.notifyData();
            }
        };
        //we create the subscrition (this is in charge of creating the task in background and starts it
        //at .subscribe(observer))
        teamsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(teamsObserver);
    }
    /**
     * Receives a callback from the webManager with the image response of the request
     * @param image the bitmap image returned from the server
     * @param team the team object of the image request
     * @param imageHolder the  image holder to place the image
     */
    @Override
    public void receiveImage(Bitmap image, Team team, ImageView imageHolder) {
        team.setImage(image);
        mView.teamsAdapter.notifyItemChanged(mTeams.indexOf(team));
        if (null != imageHolder)
            imageHolder.setImageBitmap(image);
    }
    /**
     * In case the mTeams request had an error this segment is in charge of receiving it
     * @param error
     */
    @Override
    public void receiveError(String error){
        mFetching = false;
        ApplicationMain.showToast(error);
    }

    // Parcelling part
    public ControllerMain(Parcel in){
        mTeams = in.readArrayList(Team.class.getClassLoader());
        mFetching = in.readInt() == 1;
        mMoreAvailable = in.readInt() == 1;
        mIndex = in.readInt();
        mOffline = in.readInt() == 1;
        mSelectedTeam = in.readInt();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mTeams);
        dest.writeInt(mFetching ? 1 : 0);
        dest.writeInt(mMoreAvailable ? 1 : 0);
        dest.writeInt(mIndex);
        dest.writeInt(mOffline ? 1 : 0);
        dest.writeInt(mSelectedTeam);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ControllerMain createFromParcel(Parcel in) {
            return new ControllerMain(in);
        }

        public ControllerMain[] newArray(int size) {
            return new ControllerMain[size];
        }
    };

}
