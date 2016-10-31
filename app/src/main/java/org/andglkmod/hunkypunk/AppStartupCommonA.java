package org.andglkmod.hunkypunk;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.andglkmod.SharedPrefKeys;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
            String defaultPath = Paths.appDataDirectory + "/StoryFiles";
            Log.w("AppStartupCommonA", "Paths.java setting setIfDirectory path to default value: " + defaultPath);
            Paths.setIfDirectory(new File(defaultPath));
        }

        // Construct list of directories to scan
        // Reset from template
        ArrayList<String> workingList = new ArrayList<String>(Arrays.asList(EasyGlobalsA.additionalStoryDirectoriesTemplate));
        // "The new android.content.Context.getExternalMediaDirs() returns paths to these directories on all shared storage devices."
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            File[] additionsList = runContext.getExternalMediaDirs();

            for (int i = 0; i < additionsList.length; i++) {
                workingList.add(additionsList[i].getPath());
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            // passing in null gives root?
            File[] additionsList = runContext.getExternalFilesDirs(null);

            for (int i = 0; i < additionsList.length; i++) {
                if (additionsList[i] != null) {
                    workingList.add(additionsList[i].getPath());
                }
            }
        }

        // ToDo: scanning large external SD Cards with many files may be slow, have Preference to disable or set folder.
        String secStore = System.getenv("SECONDARY_STORAGE");
        if (secStore != null && secStore.length() > 0) {
            Log.i("AppStartupCommonA", "Paths.java related, SECONDARY_STORAGE " + secStore);
            workingList.add(secStore);
        }


        String secStore2 = System.getenv("EXTERNAL_SDCARD_STORAGE");
        if (secStore2 != null && secStore2.length() > 0) {
            Log.i("AppStartupCommonA", "Paths.java related, EXTERNAL_SDCARD_STORAGE " + secStore2);
            workingList.add(secStore2);
        }

        EasyGlobalsA.additionalStoryDirectories = workingList.toArray(new String[0]);
    }
}
