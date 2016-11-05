package org.andglkmod.hunkypunk;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

import org.andglkmod.hunkypunk.dummy.DummyContent;
import org.andglkmod.hunkypunk.events.GameListEmptyEvent;
import org.greenrobot.eventbus.EventBus;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 *
 * ToDo: the ListActivity that this RecylcerView replaced had a swipe left/right on the Game Details page. This now crashes on this page.
 */
public class GameListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, GameListClickListener {

    public static final String TAG = "GameListFragment";
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private GameListDatabaseRecyclerViewAdapter recyclerViewAdapter;
    private static final int LOADMANAGER_GAMELIST_ID = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GameListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GameListFragment newInstance(int columnCount) {
        GameListFragment fragment = new GameListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gamelist_list, container, false);

        // itemClickAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.item_press_animation0);
        itemClickAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.item_press_animation_shake0);

        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewGameList);
        //if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            switch (2) {
                case 1:
                    recyclerView.setAdapter(new GameListRecyclerViewAdapter(DummyContent.ITEMS, mListener));
                    break;
                case 2:
                    recyclerViewAdapter = new GameListDatabaseRecyclerViewAdapter(getActivity(), null /* Cursor will be stuffed in later */);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.setParentClickListener(this);
                    break;
            }
        //}

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated LoadManager kickoff");
        getLoaderManager().initLoader(LOADMANAGER_GAMELIST_ID, null, this);


        View.OnClickListener emptyListOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.go_to_ifdb:
                        startActivity(new Intent(Intent.ACTION_DEFAULT, Uri.parse("http://ifdb.tads.org")));
                        break;
                    case R.id.download_preselected:
                        EventBus.getDefault().post(new GameListEmptyEvent(GameListEmptyEvent.DOWNLOAD_PRESELECT_GAMES_SET0));
                        break;
                }
            }
        };
        getView().findViewById(R.id.go_to_ifdb).setOnClickListener(emptyListOnClickListener);
        getView().findViewById(R.id.download_preselected).setOnClickListener(emptyListOnClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), HunkyPunk.Games.CONTENT_URI, null, null, null, null);
    }

    private int onLoadFinishCallCount = 0;
    private boolean noItemsMessageOnDisplay = false;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        onLoadFinishCallCount++;
        Log.v(TAG, "onLoadFinished swapCursor " + onLoadFinishCallCount);
        recyclerViewAdapter.swapCursor(data);
        if (recyclerViewAdapter.getItemCount() == 0)
        {
            Log.w(TAG, "RecyclerView shows zero items");
            if (!noItemsMessageOnDisplay)
            {
                noItemsMessageOnDisplay = true;
                ViewSwitcher viewSwitcher = (ViewSwitcher) getView().findViewById(R.id.switcherGameList);
                viewSwitcher.showNext();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recyclerViewAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    Animation itemClickAnimation;

    @Override
    public void onGameListItemClick(View view, int position, GameListDatabaseRecyclerViewAdapter.ViewHolder viewHolder) {
        Log.i(TAG, "GameList RecyclerView onClick position " + position + " identity " + viewHolder.gameIdentity0);
        view.startAnimation(itemClickAnimation);

        Intent i = new Intent(Intent.ACTION_VIEW, HunkyPunk.Games.uriOfDataRecordId(viewHolder.dataRecordId), getActivity(), GameDetails.class);
        i.putExtra("position", position); // commit the position of the clicked item
        i.putExtra("ifIDs", viewHolder.gameIdentity0); //commiting the array, where the positions matches the ids
        startActivity(i);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        // ToDo: DummyContent.DummyItem is only temporary until we get GameList Database implemented
        void onListFragmentInteraction(DummyContent.DummyItem item);
    }
}
