package org.andglkmod.hunkypunk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.andglkmod.hunkypunk.events.AppPermissionChangeEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Welcome to the App first time, permission issue
 * NOTE: If the user presses "dney", the app will no longer prompt and user must manully go into Settings to grant.
 *     ToDo: detect this situation and display detailed instructions?
 *     User is likely to conclude that uninstall/re-install fixed problem...
 */
public class GameListActivityWelcomeFragment extends Fragment {

    public GameListActivityWelcomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("WelcomeFragment", "GameListActivityWelcomeFragment onCreateView");

        return inflater.inflate(R.layout.fragment_gamelist_welcome, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i("WelcomeFragment", "GameListActivityWelcomeFragment onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        EventBus.getDefault().register(this);
        checkIfPermissionsReady();
    }

    public void checkIfPermissionsReady()
    {
        if (GameListActivity.fileSystemStoragePermissionsReady)
        {
            if (getView() != null) {
                getView().findViewById(R.id.button_permissions_ready0).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.button_permissions_ready0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("WelcomeFragment", "click on GO! button");
                        getActivity().recreate();
                    }
                });
                getView().findViewById(R.id.message_file_permissions_need0).setVisibility(View.GONE);
                getView().findViewById(R.id.message_file_permissions_ready00).setVisibility(View.VISIBLE);
            }
        }
    }

    /*
    Use the Main thread so we can interact with User Interface.
    The Activity below this Fragment will EventBus this method when permissions have been altered by user interaction.
    */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppPermissionChangeEvent event) {
        Log.i("WelcomeFragment", "got AppPermissionChangeEvent");
        checkIfPermissionsReady();
    };
}
