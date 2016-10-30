/*
	Copyright © 2009 Rafał Rzepecki <divided.mind@gmail.com>

	This file is part of Hunky Punk.

    Hunky Punk is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Hunky Punk is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Hunky Punk.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.andglkmod.hunkypunk;

import org.andglkmod.hunkypunk.HunkyPunk.Games;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/*
Note:
Moving this app to SDK 24 has made the ActionBar disappear on this GameList Activity.
The newer replacement is the "ToolBar". There are solutions to restore it.
Likley the Night Mode style work will need to be redone too.
 */
public class GamesList extends ListActivity implements OnClickListener {

    protected static final String TAG = "HunkyPunk";

    private GameListHelper gameListHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();

        gameListHelper.cleanDatabase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        getPermissionToUseStorage();

        // Do early to make sure paths are in place.
        AppStartupCommonA appStartupHelper = new AppStartupCommonA();
        appStartupHelper.setupAppStarting(this);
        appStartupHelper.setupGamesFromAssets(this);

        // ToDo: Rotation will create and destroy this, causing problems if done rapidly? Test and solve.
        gameListHelper = BackgroundOperationsA.getGameListHelper(this.getApplicationContext());

        setupListAdapter();

        setContentView(R.layout.games_list);
        findViewById(R.id.go_to_ifdb).setOnClickListener(this);
        findViewById(R.id.download_preselected).setOnClickListener(this);

        gameListHelper.sharedPreferencesAndFirstRunSetup();

        // setProgressBarIndeterminateVisibility(true);
        gameListHelper.startScanForGameFiles();

        //closing cursors locks start screen + crash
    }


    protected void setupListAdapter()
    {
        /** This part creates the list of Ifs */
        Cursor cursor = managedQuery(Games.CONTENT_URI, GameListHelper.PROJECTION, Games.PATH + " IS NOT NULL", null, null);
        // ToDo: use {@link android.app.LoaderManager} with a {@link android.content.CursorLoader}.
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
                new String[]{Games.TITLE, Games.AUTHOR}, new int[]{android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = new MenuInflater(getApplication());
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
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
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        /** the id is not equal to the position, cause of the list is alphabetical sorted.
         * We create an array, where the positions match the ids */

        long ifIDs[] = new long[getListAdapter().getCount()];//array for the right order of the ifIDs (non-alphabetical order)

        /** matching id of each IF to the position in ListView*/
        for (int j = 0; j < getListAdapter().getCount(); j++) {
            ifIDs[j] = getListView().getItemIdAtPosition(j);
            //System.out.println(ifIDs[j]);
        }

        Intent i = new Intent(Intent.ACTION_VIEW, Games.uriOfId(id), this, GameDetails.class);
        i.putExtra("position", position); //commit the position of the clicked item
        i.putExtra("ifIDs", ifIDs); //commiting the array, where the positions matches the ids
        startActivity(i);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_to_ifdb:
                startActivity(new Intent(Intent.ACTION_DEFAULT, Uri.parse("http://ifdb.tads.org")));
                break;
            case R.id.download_preselected:
                gameListHelper.downloadPreselected(this);
                break;
        }
    }
}
