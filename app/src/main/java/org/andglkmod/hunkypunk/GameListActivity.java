package org.andglkmod.hunkypunk;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.andglkmod.hunkypunk.dummy.DummyContent;

public class GameListActivity extends AppCompatActivity implements GameListFragment.OnListFragmentInteractionListener {

    protected static final String TAG = "GameListActivity";

    private GameListHelper gameListHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        getPermissionToUseStorage();

        // ToDo: Rotation will create and destroy this, causing problems if done rapidly? Test and solve.
        gameListHelper = new GameListHelper(this);

        setContentView(R.layout.activity_main);
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

        gameListHelper.sharedPreferencesAndFirstRunSetup();

        gameListHelper.startScan();

        changeMainFragment(3);
    }


    public void changeMainFragment(int fragmentSelector)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentSelector) {
            case 1:
                // fragmentTransaction.replace(R.id.mainActivityContainer,     new MainActivityFragment());
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


    public void getPermissionToUseStorage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        WRITE_STORAGE_PERMISSIONS_REQUEST);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_STORAGE_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Write Storage permission granted",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Write Storage permission denied",
                            Toast.LENGTH_SHORT).show();
                    // ToDo: use non /sdcard/ path, use application local storage path
                }
                return;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        // ToDo: why isn't this in R.menu?
        MenuInflater inflater = new MenuInflater(getApplication());
        //noinspection ResourceType
        inflater.inflate(R.layout.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.i("GameListActivity", "onListFragmentInteraction");
    }

    // ToDo: GamesList.java has onClick at bottom, what to do to replace this?
    //   how about have another fragment for virgin opening page, intro page iwth those two choices?
}
