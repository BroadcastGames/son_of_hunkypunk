package org.andglkmod.hunkypunk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.andglkmod.glk.Utils;
import org.andglkmod.ifdb.IFDb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by adminsag on 10/23/16.
 */

public class GameListHelper {
    public static final String TAG = "GameListHelper";

    public static final String[] PROJECTION = {
            HunkyPunk.Games._ID,
            HunkyPunk.Games.IFID,
            HunkyPunk.Games.TITLE,
            HunkyPunk.Games.AUTHOR,
            HunkyPunk.Games.PATH
    };

    public static final String[] BEGINNER_GAMES = {
            "http://www.ifarchive.org/if-archive/games/zcode/905.z5",
            "http://www.ifarchive.org/if-archive/games/zcode/Advent.z5",
            "http://www.ifarchive.org/if-archive/games/zcode/awaken.z5",
            "http://www.ifarchive.org/if-archive/games/zcode/dreamhold.z8",
            "http://www.ifarchive.org/if-archive/games/zcode/LostPig.z8",
            "http://www.ifarchive.org/if-archive/games/zcode/shade.z5",
            "http://www.ifarchive.org/if-archive/games/tads/indigo.t3",
            "http://www.ifarchive.org/if-archive/games/competition98/tads/plant/plant.gam",
            "http://www.ifarchive.org/if-archive/games/zcode/Bronze.zblorb",
            "http://www.ifarchive.org/if-archive/games/zcode/theatre.z5",
            "http://hunkypunk.googlecode.com/files/uu1.gam"
    };


    private StorageManager mScanner;
    private ProgressDialog progressDialog;
    private Thread downloadThread;
    protected boolean downloadCancelled;
    private Context parentContext;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case StorageManager.MESSAGE_CODE_DONE:
                    // setProgressBarIndeterminateVisibility(false);
                    startLookup();
                    break;
            }
        }
    };


    public GameListHelper(Context context) {
        Log.i(TAG, "GameListHelper constructor");
        parentContext = context;

        mScanner = StorageManager.getInstance(context);
        mScanner.setHandler(mHandler);
        mScanner.checkExisting();
    }

    public void cleanDatabase() {
        Log.i(TAG, "cleanDatabase");
        /** gets the If-Path from SharedPrefences, which could be changed at the last session */
        String path = parentContext.getSharedPreferences("ifPath", MODE_PRIVATE).getString("ifPath", "");
        if (!path.equals(""))
            Paths.setIfDirectory(new File(path));

// ToDo: this is some kind of upgrade path remover? I think this should be removed to allow mutliple directories.
        /** deletes all Ifs, which are not in the current Path, in other words, it deletes the
         * Ifs from the older Directory*/

        DatabaseHelper mOpenHelper = new DatabaseHelper(parentContext);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Disabled!
        /*
        for (int i = 0; i < adapter.getCount(); i++) {
            Cursor c = (Cursor) adapter.getItem(i);
            if (!Pattern.matches(".*" + Paths.ifDirectory() + ".*", c.getString(4))) {
                db.execSQL("delete from games where ifid = '" + c.getString(1) + "'");
            }
        }
        */

        /** helps to refresh the View, when come back from preferences */
        // ToDo: move to onPreferenceResults?
        // startScan(); //!!!crashes the app and doubles the first game!!!

        //closing cursors locks start screen + crash

        db.close();//not closing db locks it and results in an exception onResume
    }


    public void sharedPreferencesAndFirstRunSetup()
    {
        SharedPreferences sharedPreferences = parentContext.getSharedPreferences("shortcutPrefs", MODE_PRIVATE);
        SharedPreferences sharedShortcuts = parentContext.getSharedPreferences("shortcuts", MODE_PRIVATE);
        SharedPreferences sharedShortcutIDs = parentContext.getSharedPreferences("shortcutIDs", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("firstStart", true)) {
            SharedPreferences.Editor prefEditor = sharedPreferences.edit();
            SharedPreferences.Editor shortcutEditor = sharedShortcuts.edit();
            SharedPreferences.Editor shortcutIDEditor = sharedShortcutIDs.edit();

            String[] defaults = new String[]{"look", "examine", "take", "inventory", "ask", "drop", "tell", "again", "open", "close", "give", "show"};
            ArrayList<String> list = new ArrayList<String>();

            for (int i = 0; i < defaults.length; i++)
                list.add(defaults[i]);
            Collections.sort(list);

            for (int i = 0; i < list.size(); i++) {
                shortcutEditor.putString(list.get(i), list.get(i));
                shortcutIDEditor.putString(i + "", list.get(i));
            }
            prefEditor.putBoolean("firstStart", false);

            shortcutIDEditor.commit();
            shortcutEditor.commit();
            prefEditor.commit();
        }
    }


    public void startScanForGameFiles() {
        mScanner.startScanForGameFiles();
    }

    private void startLookup() {
        IFDb ifdb = IFDb.getInstance(parentContext.getContentResolver());
        ifdb.startLookup(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.w(TAG, "ifdb_connection error");
                Toast.makeText(parentContext, R.string.ifdb_connection_error, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void downloadPreselected() {
        downloadCancelled = false;

        progressDialog = new ProgressDialog(parentContext);
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setMessage(parentContext.getString(R.string.downloading_stories));
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        synchronized (downloadThread) {
                            downloadCancelled = true;
                        }
                        downloadThread.interrupt();
                    }
                }
        );
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(GameListHelper.BEGINNER_GAMES.length);
        progressDialog.show();

        downloadThread = new Thread() {
            @Override
            public void run() {
                int i = 0;
                for (String s : GameListHelper.BEGINNER_GAMES) {
                    synchronized (this) {
                        if (downloadCancelled)
                            return;
                    }
                    try {
                        final URL u = new URL(s);
                        final String fileName = Uri.parse(s).getLastPathSegment();
                        Utils.copyStream(u.openStream(), new FileOutputStream(new File(Paths.ifDirectory(), fileName)));
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "malformed URL when fetching " + s, e);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "file not found when fetching " + s, e);
                    } catch (IOException e) {
                        Log.e(TAG, "I/O error when fetching " + s, e);
                    }
                    progressDialog.setProgress(++i);
                }

                try {
                    mScanner.startScanForGameFiles();
                    // ToDo: should we do this as part of normal scan, or does it generate a lot of Internet traffic? Manual menu option?
                    IFDb.getInstance(parentContext.getContentResolver()).lookupGames();
                } catch (IOException e) {
                    Log.e(TAG, "I/O error when fetching metadata", e);
                }

                progressDialog.dismiss();
            }
        };
        downloadThread.setName("downloadThread");
        downloadThread.start();
    }
}
