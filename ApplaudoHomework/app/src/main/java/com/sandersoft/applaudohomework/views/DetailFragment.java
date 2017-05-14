package com.sandersoft.applaudohomework.views;

import android.app.Fragment;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sandersoft.applaudohomework.R;
import com.sandersoft.applaudohomework.controllers.ControllerDetail;
import com.sandersoft.applaudohomework.models.Team;
import com.sandersoft.applaudohomework.utils.Globals;

/**
 * Created by Sander on 12/05/2017.
 */

public class DetailFragment extends Fragment implements OnMapReadyCallback {

    ControllerDetail mController;

    RelativeLayout mLay_video;
    VideoView mVid_video;
    ImageView mImg_play;
    LinearLayout mLay_info;
    ImageView mImg_team;
    TextView mLbl_name;
    TextView mLbl_info;
    RelativeLayout mLay_map;
    MapFragment mMap;

    //variables for mPlaying video
    public boolean playing = false;
    public int videoPosition = 0;

    GoogleMap mGoogleMap;
    //variable par aalmacenar el zoom del mapa
    int mStartZoom = 15;

    public static DetailFragment getInstance(Team team){
        DetailFragment frag = new DetailFragment();
        frag.mController = new ControllerDetail(frag);
        frag.mController.setTeam(team);
        return frag;
    }
    public static DetailFragment getInstance(Team team, boolean playing, int videoPosition){
        DetailFragment frag = new DetailFragment();
        frag.mController = new ControllerDetail(frag);
        frag.mController.setTeam(team);
        frag.playing = playing;
        frag.videoPosition = videoPosition;
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);

        if (null != savedInstanceState){
            mController = savedInstanceState.getParcelable(Globals.DETAIL_CONTROLLER);
            mController.setView(this);
            //verify if the parent activity is a smallDecive, if not, continue with video
            if (!(getActivity() instanceof MainActivity) || !((MainActivity) getActivity()).isSmallDevice()) {
                //retrieve video state
                playing = savedInstanceState.getBoolean(Globals.DETAIL_PLAYING);
                videoPosition = savedInstanceState.getInt(Globals.DETAIL_POSITION);
            }
        }

        //if the fragment is positioned in a DetailActivity, it showld be closed to let the MainActivity show both fragments
        //when the orientation is landscape
        if (getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE
                && getActivity() instanceof DetailActivity) {
            ((DetailActivity)getActivity()).destroyActivityLandscape();
            return null;
        }

        //instnatiate all the elements in the layout
        defineElements(rootview);

        //chaneg the activity title
        if (!(getActivity() instanceof MainActivity) || !((MainActivity)getActivity()).isSmallDevice())
            getActivity().setTitle(mController.getTeam().getTeam_name());

        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //get objects backedup so we can reload the activity with the same infromation
        outState.putParcelable(Globals.DETAIL_CONTROLLER, mController);
        //save video state
        outState.putBoolean(Globals.DETAIL_PLAYING, playing);
        if (playing) {
            //pause the video (so the parameters get the latest info)
            pause();
            //return the mPlaying variable to true (so onResume can continue playing)
            playing = true;
        }
        outState.putInt(Globals.DETAIL_POSITION, videoPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        //if the video was playing before exiting, then start playing again
        if (playing)
            play();
    }

    @Override
    public void onPause() {
        super.onPause();
        //get the current video position
        videoPosition = mVid_video.getCurrentPosition();
    }

    public void defineElements(View rootview) {
        mLay_video = (RelativeLayout) rootview.findViewById(R.id.lay_video);
        mVid_video = (VideoView) rootview.findViewById(R.id.vid_video);
        mImg_play = (ImageView) rootview.findViewById(R.id.img_play);

        mLay_info = (LinearLayout) rootview.findViewById(R.id.lay_info);
        mLbl_name = (TextView) rootview.findViewById(R.id.lbl_name);
        mLbl_info = (TextView) rootview.findViewById(R.id.lbl_info);
        mImg_team = (ImageView) rootview.findViewById(R.id.img_team);
        mImg_play.setVisibility(View.GONE);

        mLay_map = (RelativeLayout) rootview.findViewById(R.id.lay_map);

        setUpMapIfNeeded();

        //set the video
        mVid_video.setVideoURI(Uri.parse(mController.getTeam().getVideo_url()));
        mVid_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (!playing)
                    mImg_play.setVisibility(View.VISIBLE);
                mVid_video.seekTo(videoPosition);
                mVid_video.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        pause();
                        return false;
                    }
                });
                mVid_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        pause();
                        mVid_video.seekTo(0);
                        videoPosition = 0;
                    }
                });
                mImg_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        play();
                    }
                });
            }
        });
        mVid_video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                mImg_play.setVisibility(View.VISIBLE);
                mImg_play.setImageResource(R.mipmap.image_not_found);
                mImg_play.setBackgroundColor(Color.GRAY);
                return true;
            }
        });

        mLbl_name.setText(mController.getTeam().getTeam_name());
        mLbl_info.setText(mController.getTeam().getDescription());
        if (mController.getTeam().getImage() == null) {
            mController.getTeamImage(mImg_team);
        } else {
            mImg_team.setImageBitmap(mController.getTeam().getImage());
        }
    }

    /**
     * Plays the video
     */
    public void play(){
        if (!mVid_video.isPlaying()) {
            mVid_video.start();
        }
        mImg_play.setVisibility(View.GONE);
        playing = true;
        //mc.show(1000);
    }
    /**
     * Pauses the video
     */
    public void pause(){
        if (mVid_video.isPlaying())
            mVid_video.pause();
        playing = false;
        videoPosition = mVid_video.getCurrentPosition();
        mImg_play.setVisibility(View.VISIBLE);
        //mc.hide();
    }

    /**
     * Initialize the map if it need initializing
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mMap != null) {
                mMap.getMapAsync(this);
            }
        }
    }
    /**
     * Loads the map and places it in it start position
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        //setup map
        mGoogleMap = mMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        //ponemos un listener de terminacion de carga del mapa
        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                positionMap();
            }
        });
    }
    /**
     * Position the map in the teams coordinates, and add a marker in the same position
     */
    public void positionMap(){
        // Creating a LatLng object for the current location
        LatLng currentLocation = new LatLng(Double.valueOf(mController.getTeam().getLatitude()),
                Double.valueOf(mController.getTeam().getLongitude()));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, mStartZoom));
        mGoogleMap.addMarker(new MarkerOptions()
                .title(mController.getTeam().getTeam_name())
                .snippet(mController.getTeam().getAddress())
                .position(currentLocation));

    }
}
