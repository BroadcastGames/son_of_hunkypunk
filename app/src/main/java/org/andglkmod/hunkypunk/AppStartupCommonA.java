package org.andglkmod.hunkypunk;

import android.content.Context;
import android.util.Log;

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
                Log.i("AppStartupCommonA", "path " + EasyGlobalsA.getPathNullPathA + " writable? " + EasyGlobalsA.getPathNullPathA.canWrite());
            }
        }
    }
}
