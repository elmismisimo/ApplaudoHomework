package com.sandersoft.applaudohomework.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sandersoft.applaudohomework.R;
import com.sandersoft.applaudohomework.controllers.ControllerMain;
import com.sandersoft.applaudohomework.models.Team;
import com.sandersoft.applaudohomework.utils.Globals;

/**
 * Created by Sander on 11/05/2017.
 */

public class MainFragment extends Fragment {

    ControllerMain mController;

    LinearLayout mLay_offline;
    Button mBtn_retry;
    RecyclerView mList;
    public TeamsAdapter teamsAdapter;
    ViewTreeObserver.OnScrollChangedListener mScrollListener;

    /**
     * Returns a new instance of the fragment (in case you would like to implement singleton, this is unnecesary)
     * @return new instance of the fragment
     */
    public static MainFragment getInstance(){
        MainFragment frag = new MainFragment();
        frag.mController = new ControllerMain(frag);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        if (null != savedInstanceState){
            mController = savedInstanceState.getParcelable(Globals.MAIN_CONTROLLER);
            mController.setView(this);
        }

        //instantiates the views of the layout
        defineElements(rootview);

        //if its the first load, get the movies
        if (null == savedInstanceState) {
            fillScreen();
        }

        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //get objects backedup so we can reload the activity with the same infromation
        outState.putParcelable(Globals.MAIN_CONTROLLER, mController);
    }

    public void defineElements(View rootview) {
        mLay_offline = (LinearLayout) rootview.findViewById(R.id.lay_offline);
        mBtn_retry = (Button) rootview.findViewById(R.id.btn_retry);
        mList = (RecyclerView) rootview.findViewById(R.id.lista);
        if (mController.isOffline())
            placeOfflineLayout();
        teamsAdapter = new TeamsAdapter();
        //set the vertical layout so the list is displayed top down
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mList.setLayoutManager(layoutManager);
        //set the adapter
        mList.setAdapter(teamsAdapter);
        //create listeners
        mBtn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillScreen();
            }
        });
        //create a scroll listener for the reciclerview, that will check if the list reached bottom
        //this will be added to the list when the first elements are received (check receiveMovies(int cant))
        mScrollListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                //verify if the list reached the end and load new elements automatically
                if (layoutManager.findLastCompletelyVisibleItemPosition() >= mController.getTeams().size()-2) {
                    //request the next set of the movies
                    loadAndFill();
                }
            }
        };
        restoreScrollLinstener();
    }

    /**
     * Start getting elements from the begining
     */
    public void fillScreen(){
        //hide offline layout
        mLay_offline.setVisibility(View.GONE);
        mList.setVisibility(View.VISIBLE);
        //Clear all the teams to load them again
        mController.setIndex(0);
        mController.getTeams().clear();
        //add element thet will be interpreted as loading element
        mController.getTeams().add(null);
        teamsAdapter.notifyData();
        //load the elements
        loadAndFill();
    }

    /**
     * Start process to get the records, if there are more than 0, they are shown
     */
    public void loadAndFill(){
        //remove scroll listener
        if (null != mList)
            mList.getViewTreeObserver().removeOnScrollChangedListener(mScrollListener);
        mController.setMoreAvailable(false);

        //get the teams
        mController.fetchTeams();
    }

    public void placeOfflineLayout(){
        //show offline layout
        mLay_offline.setVisibility(View.VISIBLE);
        mList.setVisibility(View.GONE);
        mController.getTeams().clear();
        teamsAdapter.notifyData();
    }

    /**
     * Restore the onScrollListener of the recyclerview in case its needed
     */
    public void restoreScrollLinstener(){
        if (mController.isMoreAvailable() && !mController.isFetching())
            //add a listener so when the scroll reaches bottom auto loads another set of records
            mList.getViewTreeObserver().addOnScrollChangedListener(mScrollListener);
    }

    /**
     * Recyclerview adapter that handles the elements
     */
    public class TeamsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM_NULL = 0;
        private final int VIEW_TYPE_ITEM_ELM = 1;

        public TeamsAdapter(){
            setHasStableIds(true);
        }

        public class ItemNullHolder extends RecyclerView.ViewHolder {
            public LinearLayout lay_item;

            public ItemNullHolder(View view) {
                super(view);
                lay_item = (LinearLayout) view.findViewById(R.id.lay_item);
            }
        }
        public class ItemElmHolder extends RecyclerView.ViewHolder {
            public LinearLayout lay_item;
            public TextView lbl_name;
            public TextView lbl_address;
            public ImageView img_team;

            public ItemElmHolder(View view) {
                super(view);
                lay_item = (LinearLayout) view.findViewById(R.id.lay_item);
                lbl_name = (TextView) view.findViewById(R.id.lbl_name);
                lbl_address = (TextView) view.findViewById(R.id.lbl_address);
                img_team = (ImageView) view.findViewById(R.id.img_team);
            }
        }

        @Override
        public int getItemViewType(int position) {
            //verify the type of the element
            if (null == mController.getTeams().get(position))
                return VIEW_TYPE_ITEM_NULL; //null = loading
            else
                return VIEW_TYPE_ITEM_ELM; //mTeam element
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM_NULL) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_loading, parent, false);
                return new ItemNullHolder(itemView);
            } else if (viewType == VIEW_TYPE_ITEM_ELM){
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_team, parent, false);
                return new ItemElmHolder(itemView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            //verify the element type
            if (holder instanceof ItemNullHolder){
                //cast the item holder
                final ItemNullHolder ih = (ItemNullHolder) holder;
                ih.lay_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request the next set of the movies
                        loadAndFill();
                    }
                });
            } else if (holder instanceof ItemElmHolder){
                //reference the mTeam
                final Team a = mController.getTeams().get(position);
                //cast the item holder
                final ItemElmHolder ih = (ItemElmHolder) holder;
                //place values
                ih.lbl_name.setText(a.getTeam_name());
                ih.lbl_address.setText(a.getAddress());
                if (null != mController.getTeams().get(position).getImage())
                    ih.img_team.setImageBitmap(mController.getTeams().get(position).getImage());
                else {
                    //place app image as preview of the mTeam (just placing something)
                    ih.img_team.setImageResource(R.mipmap.thumbnail);
                }

                ih.lay_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //set the selection
                        mController.setSelectedTeam(position);
                        //open activity in portrait
                        if (((MainActivity)getActivity()).isSmallDevice() && getResources().getConfiguration().orientation != getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
                            //open the activity from the activity (not form the fragment) so the result will be received from the activity
                            ((MainActivity)getActivity()).openActivityDetail(a);
                        }
                        //change fragment in landscape
                        else {
                            ((MainActivity)getActivity()).openTeamFragment(a);
                        }
                        //redraw main activity menu
                        ((MainActivity)getActivity()).redrawMenu();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mController.getTeams().size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void notifyData() {
            notifyDataSetChanged();
        }
    }

}
