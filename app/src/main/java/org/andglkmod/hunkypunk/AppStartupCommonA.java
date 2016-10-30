package org.andglkmod.hunkypunk;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.andglkmod.SharedPrefKeys;

import java.io.File;

/**
 * Common methods when app starts up. Allows multiple activities to be the 'staritng point' of the app
 * and sitll share common code for creating folders, etc.
 */

public class AppStartupCommonA {
    public void setupGamesFromAssets(Context runContext)
    {
        if (EasyGlobalsA.getPathNullPathA == null)
        {
            // ToDo: use Android's built in context-specific from context to get the standard default storage place
            EasyGlobalsA.getPathNullPathA = new File("/sdcard/story000/tempGameRunA/");
            if (! EasyGlobalsA.getPathNullPathA.exists())
            {
                EasyGlobalsA.getPathNullPathA.mkdirs();
                Log.i("AppStartupCommonA", "path " + EasyGlobalsA.getPathNullPathA + " freeSpace? " + EasyGlobalsA.getPathNullPathA.getFreeSpace());
            }
        }
    }

    synchronized public void setupAppStarting(Context runContext) {
        if (Paths.appCardDirectory == null)
        {
            Paths.appCardDirectory = new File(Environment.getExternalStorageDirectory().getPath());
        }

        if (Paths.appDataDirectory == null)
        {
            Paths.appDataDirectory = runContext.getFilesDir().getAbsolutePath();
            Log.i("AppStartupCommonA", "Paths.appDataDirectory set to " + Paths.appDataDirectory);
            Paths.appDataDirectoryFile = new File(Paths.appDataDirectory);
            if (! Paths.appDataDirectoryFile.canWrite())
            {
                Log.e("AppStartupCommonA", "WRITE PROBLEM, why? Paths.appDataDirectory set to " + Paths.appDataDirectory);
            }
            else
            {
                Log.i("AppStartupCommonA", "canWrite() Paths.appDataDirectory set to " + Paths.appDataDirectory);
            }
        }

        /** gets the If-Path from SharedPreferences, which could be changed at the last session
         *  Note: This code is confusing as it creates a unique SharedPref file with the sane name as the String Key.
         *  ToDo: just use Default Shared Preferences for the app?
         *  */
        String path = runContext.getSharedPreferences(SharedPrefKeys.KEY_RootPath0, Context.MODE_PRIVATE).getString(SharedPrefKeys.KEY_RootPath0, "");
        if (! path.equals("")) {
            // Shared Preferences has no value, so Application default.
            Log.w("AppStartupCommonA", "Paths.java setting setIfDirectory path paths to SharedPreferences value: " + path);
            Paths.setIfDirectory(new File(path));
        }
        else
        {
            // Shared Preferences has no value, so Application default.
            String defaultPath = Paths.appDataDirectory;
            Log.w("AppStartupCommonA", "Paths.java setting setIfDirectory path to default value: " + defaultPath);
            Paths.setIfDirectory(new File(defaultPath));
        }
    }
}
