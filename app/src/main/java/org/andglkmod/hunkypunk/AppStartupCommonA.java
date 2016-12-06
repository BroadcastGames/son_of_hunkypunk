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
 * Common methods when app starts up. Allows multiple activities to be the 'starting point' of the app
 * and still share common code for creating folders, etc.
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

        if (EasyGlobalsA.storageManagerScanExternalWideA) {
            // "The new android.content.Context.getExternalMediaDirs() returns paths to these directories on all shared storage devices."
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                File[] additionsList = runContext.getExternalMediaDirs();

                for (int i = 0; i < additionsList.length; i++) {
                    if (additionsList[i] != null) {
                        Log.i("AppStartupCommonA", "Paths.java related, post-LOLLIPOP getExternalMediaDirs " + additionsList[i]);
                        workingList.add(additionsList[i].getPath());
                    }
                }
            }

            /*
            Testing notes: using the Android 7.0 emulator with an external SD, the path /storage/XXXX-XXXX comes up but no way have
              I found a way to get the 'root' of a free SD Card.  The Emulator internal screens gave option to encrypt or allow
              mounting on other devices. Even when saying to not encrypt, the root isn't really exposed on any API call I have found.
              None of the environment variables point to this parth.  So, one approach is to ask for the app-specific path and strip
              out the app-specific components.
             */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                // passing in null gives root?
                File[] additionsList = runContext.getExternalFilesDirs(null);

                for (int i = 0; i < additionsList.length; i++) {
                    //Huawei Y538 Android 5.1.1 crashed here without this null check
                    if (additionsList[i] != null) {
                        Log.i("AppStartupCommonA", "Paths.java related, post-KITKAT getExternalFilesDirs " + additionsList[i]);
                        workingList.add(additionsList[i].getPath());
                        // Now, can we get this as the root of an SD Card mounted (or USB mounted)?
                        String workPath = additionsList[i].getPath();
                        if (!workPath.startsWith("/storage/emulated")) {
                            if (workPath.length() >= "/storage/DEAD-BEEF/".length()) {
                                if (workPath.charAt(13) == '-') {
                                    if (workPath.charAt(18) == '/') {
                                        String workPathRoot = workPath.substring(0, 18);
                                        Log.i("AppStartupCommonA", "Paths.java related, post-KITKAT getExternalFilesDirs TARGET MATCH " + workPath + " to " + workPathRoot);
                                        workingList.add(workPathRoot);
                                    }
                                }
                            }
                        }
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

            // Blu Studio Energy 2 Android 5.0 had this value
            String secStore3 = System.getenv("USBOTG_STORAGE");
            if (secStore3 != null && secStore3.length() > 0) {
                Log.i("AppStartupCommonA", "Paths.java related, USBOTG_STORAGE " + secStore3);
                workingList.add(secStore3);
            }

            // Blu Studio Energy 2 Android 5.0 had this value
            String secStore4 = System.getenv("EXTERNAL_STORAGE");
            if (secStore4 != null && secStore4.length() > 0) {
                Log.i("AppStartupCommonA", "Paths.java related, EXTERNAL_STORAGE " + secStore4);
                workingList.add(secStore4);
            }

            // Must be a named folder type, Downloads is the most generic
            File secStore5 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (secStore5 != null) {
                Log.i("AppStartupCommonA", "Paths.java related, getExternalStoragePublicDirectory DIRECTORY_DOWNLOADS " + secStore5);
                workingList.add(secStore5.getPath());
            }
        }

        EasyGlobalsA.additionalStoryDirectories = workingList.toArray(new String[0]);
        Log.i("AppStartupCommonA", "Paths.java final count additionalStoryDirectories: " + EasyGlobalsA.additionalStoryDirectories.length);
    }
}
