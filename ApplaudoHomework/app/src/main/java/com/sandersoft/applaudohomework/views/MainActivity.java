package com.sandersoft.applaudohomework.views;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sandersoft.applaudohomework.R;
import com.sandersoft.applaudohomework.models.Team;
import com.sandersoft.applaudohomework.utils.Globals;

public class MainActivity extends AppCompatActivity {

    MainFragment mMainFragment;
    DetailFragment mDetailFragment;
    boolean mSmallDevice = false;
    boolean mLeavingActivity = false;

    //variables for video continuity even after rotations
    boolean mPlaying = false;
    int mVideoPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //define if is a smal device just by trying to get a view that only is in big or landscape layouts
        mSmallDevice = findViewById(R.id.detail_fragment) == null;

        setTitle(R.string.app_name);

        if (null == savedInstanceState) {
            //place the fragment in the container
            mMainFragment = MainFragment.getInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment, mMainFragment, Globals.MAIN_FRAGMENT);
            ft.commit();
        } else {
            //recover the values after a destroy
            mMainFragment = (MainFragment) getFragmentManager().findFragmentByTag(Globals.MAIN_FRAGMENT);
            mDetailFragment = (DetailFragment) getFragmentManager().findFragmentByTag(Globals.DETAIL_FRAGMENT);
            mLeavingActivity = savedInstanceState.getBoolean(Globals.MAIN_LEAVING_ACTIVITY);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //get objects backedup so we can reload the activity with the same infromation
        outState.putBoolean(Globals.MAIN_LEAVING_ACTIVITY, mLeavingActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if we are returning from DetailActivity, and the orientation is landscape and a team was previously selected
        //then we show the team in the detail fragment
        if (mLeavingActivity
                && getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE
                && mMainFragment.mController.getSelectedTeam() >= 0) {
            openTeamFragment(mMainFragment.mController.getTeams().get(mMainFragment.mController.getSelectedTeam()));
        }
        //restart the leavingActivity variable
        mLeavingActivity = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //verify if the meu should be shown depending on the device size and if a team is selected
        menu.getItem(0).setVisible(!mSmallDevice && mMainFragment.mController.getSelectedTeam() >= 0); //call
        menu.getItem(1).setVisible(!mSmallDevice && mMainFragment.mController.getSelectedTeam() >= 0); //share
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_call) {
            mDetailFragment.mController.callTeam();
            return true;
        }
        else if (id == R.id.menu_share) {
            mDetailFragment.mController.shareTeam();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Redraw the menu
     */
    public void redrawMenu(){
        //invalidateOptionsMenu();
        supportInvalidateOptionsMenu();
    }

    /**
     * Open a detail activity of a team
     * @param team
     */
    public void openActivityDetail(Team team){
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("team", team);
        intent.putExtras(b);
        startActivityForResult(intent, Globals.MAIN_OPEN_ACTIVITY_RESULT);
        //mark in the main_activity that we left the activity so when come back,
        //it places the mTeam detail in the detail fragment (if needed)
        mLeavingActivity = true;
    }

    /**
     * Receives a result from DetailActivity, this result contains video info like playing and position
     * so the reproduction can be resumed when changing to landscape mode in smal devices
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Globals.MAIN_OPEN_ACTIVITY_RESULT) {
            if(resultCode == Activity.RESULT_OK){
                mPlaying = (boolean) data.getExtras().get(Globals.DETAIL_PLAYING);
                mVideoPosition = (int) data.getExtras().get(Globals.DETAIL_POSITION);
            }
        }
    }

    /**
     * Returns <b>true</b> if this current device is a samll device
     * @return <b>true</b> if this current device is a samll device
     */
    public boolean isSmallDevice(){
        return mSmallDevice;
    }

    /**
     * Displays a team infromation layout in the detail fragment
     * @param team the team to be shown
     */
    public void openTeamFragment(Team team){
        mDetailFragment = DetailFragment.getInstance(team, mPlaying, mVideoPosition);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.detail_fragment, mDetailFragment, Globals.DETAIL_FRAGMENT);
        ft.commit();
        //restart video variables (avoid repeating parameters when changing fragments)
        mPlaying = false;
        mVideoPosition = 0;
    }
}
