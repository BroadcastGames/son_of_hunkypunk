package org.andglkmod.hunkypunk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Welcome to the App first time, permission issue
 * ToDo: button appears when permission is granted so we can restart the Activity.git diff
 * 
 */
public class GameListActivityWelcomeFragment extends Fragment {

    public GameListActivityWelcomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainActFragment", "MainActivityFragment onCreateView");
        return inflater.inflate(R.layout.fragment_gamelist_welcome, container, false);
    }
}
