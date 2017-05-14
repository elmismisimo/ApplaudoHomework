package com.sandersoft.applaudohomework.views;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sandersoft.applaudohomework.R;
import com.sandersoft.applaudohomework.models.Team;
import com.sandersoft.applaudohomework.utils.Globals;

public class DetailActivity extends AppCompatActivity {

    DetailFragment mDetailFragment;
    Team mTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //verificamos si se esta abriendo con un entrenamiento que no se termino
        Bundle b = getIntent().getExtras();
        try{
            mTeam = b.getParcelable("team");
        }
        catch(Exception ex){
            //if there is no team information, then this activity must not be shown
            finish();
            return;
        }

        if (null == savedInstanceState) {
            //place the fragment in the container
            mDetailFragment = DetailFragment.getInstance(mTeam);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.detail_fragment, mDetailFragment, Globals.DETAIL_FRAGMENT);
            ft.commit();
        } else {
            //recover the values after a destroy
            mDetailFragment = (DetailFragment) getFragmentManager().findFragmentByTag(Globals.DETAIL_FRAGMENT);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
     * When the device is rotates to landscape, this function sets the result information for
     * the main activity to continue with the video playing and position
     */
    public void destroyActivityLandscape(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Globals.DETAIL_PLAYING, mDetailFragment.playing);
        returnIntent.putExtra(Globals.DETAIL_POSITION, mDetailFragment.videoPosition);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
