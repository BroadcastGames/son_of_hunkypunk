package org.andglkmod.hunkypunk;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.andglkmod.hunkypunk.dummy.DummyContent;
import org.andglkmod.hunkypunk.events.AppPermissionChangeEvent;
import org.andglkmod.hunkypunk.events.BackgroundScanEvent;
import org.andglkmod.hunkypunk.events.GameListEmptyEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

public class GameListActivity extends AppCompatActivity implements GameListFragment.OnListFragmentInteractionListener {

    protected static final String TAG = "GameListActivity";
    public static boolean fileSystemStoragePermissionsReady = false;

    private GameListHelper gameListHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        fileSystemStoragePermissionsReady = getPermissionToUseStorage();

        // ToDo: Rotation will create and destroy this, causing problems if done rapidly? Test and solve.
        gameListHelper = new GameListHelper(this);

        setContentView(R.layout.activity_gamelist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // changeMainFragment(2);
            }
        });

        if (fileSystemStoragePermissionsReady) {
            gameListHelper.sharedPreferencesAndFirstRunSetup();

            // setProgressBarIndeterminateVisibility(true);
            gameListHelper.startScanForGameFiles();

            changeMainFragment(3);
        }
        else
        {
            // permission not granted, so don't do scan for game files yet.
            Log.i(TAG, "GameListActivity didn't detect permissions to scan and use storage, sending to Welcome fragment");
            changeMainFragment(1);
        }
    }


    public void changeMainFragment(int fragmentSelector)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentSelector) {
            case 1:
                fragmentTransaction.replace(R.id.mainActivityContainer,     new GameListActivityWelcomeFragment());
                break;
            case 2:
                // fragmentTransaction.replace(R.id.mainActivityContainer,     BlankFragment.newInstance("hi", "there"));
                break;
            case 3:
                fragmentTransaction.replace(R.id.mainActivityContainer,     GameListFragment.newInstance(1 /* Column */));
                break;
        }
        fragmentTransaction.commit();
    }


    // Identifier for the permission request
    private static final int WRITE_STORAGE_PERMISSIONS_REQUEST = 1;


    // ToDo: testing reveals this only works the very first time App is started, if user denies, they must manually go into Android Settings to grant?
    //     can we detect that and alter the message/directions to user? One answer: keep track of the Denial response in preferences so we we know that was situation.
    public boolean getPermissionToUseStorage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return false;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        WRITE_STORAGE_PERMISSIONS_REQUEST);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult " + requestCode + " " + Arrays.toString(permissions));
        switch (requestCode) {
            case WRITE_STORAGE_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fileSystemStoragePermissionsReady = true;
                    Toast.makeText(this, "Write Storage permission granted",
                            Toast.LENGTH_LONG).show();
                    // ToDo: kick off the scan, change fragment?
                } else {
                    Toast.makeText(this, "Write Storage permission denied",
                            Toast.LENGTH_SHORT).show();
                    fileSystemStoragePermissionsReady = false;
                    // ToDo: use non /sdcard/ path, use application local storage path
                }
                break;
            }
        }
        EventBus.getDefault().post(new AppPermissionChangeEvent());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;
        switch (item.getNumericShortcut()) {
            case '1':
                intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                break;
            case '2':
                AlertDialog builder;
                try {
                    builder = DialogBuilder.showAboutDialog(this);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        switch (id)
        {
            case R.id.oldGamesList:
                intent = new Intent(this, GamesList.class);
                startActivity(intent);
                break;
            case R.id.runScanForGames:
                // setProgressBarIndeterminateVisibility(true);
                gameListHelper.startScanForGameFiles();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.i("GameListActivity", "onListFragmentInteraction");
    }


    // ToDo: GamesList.java has onClick at bottom, what to do to replace this?
    //   how about have another fragment for virgin opening page, intro page iwth those two choices?


    /*
    Use the Main thread so we can interact with User Interface.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThreadEvent(BackgroundScanEvent event) {
        switch (event.eventCode)
        {
            case BackgroundScanEvent.ECODE_GAME_FOLDER_SCAN_COMPLETED:
                Toast outToast = Toast.makeText(this, "Scan of folders for games completed.",
                        Toast.LENGTH_SHORT);
                outToast.setGravity(Gravity.CENTER, 0, 0);
                outToast.show();

                // setProgressBarIndeterminateVisibility(false);
                break;
        }
    };

    /*
    Use the Main thread so we can interact with User Interface.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThreadEvent(GameListEmptyEvent event) {
        switch (event.eventCode) {
            case GameListEmptyEvent.DOWNLOAD_PRESELECT_GAMES_SET0:
                gameListHelper.downloadPreselected();
                break;
            case GameListEmptyEvent.DOWNLOAD_COMPLETED_RECHECK0:
                // Recreate the entire Activity
                recreate();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
