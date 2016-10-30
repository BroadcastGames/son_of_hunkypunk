/*
	Copyright © 2009-2010 Rafał Rzepecki <divided.mind@gmail.com>

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

import android.os.Environment;
import android.util.Log;

import java.io.File;

/*
ToDO: this needs rework. Static doesn't have context, so important that app startup stuff values here.
 */
public abstract class Paths {
    private static File ifDirectory;
    // previous value: "Android/data/org.andglkmod.hunkypunk"
    public static String appDataDirectory = null;
    public static File appDataDirectoryFile = null;
    public static File appCardDirectory = null;

    // Name: "card" means "SD card".
    // Rename this method to "appRootGameDirectory()" because may not always be "SD Card".
    public static File cardDirectory() {
        return appCardDirectory;
    }

    public static File dataDirectory() {
        // By nature, this path should always exist or the app wouldn't be running or installed by convention?
        // By nature, this path can't be 'picked' by the user, it is right along side the APK
        if (!appDataDirectoryFile.canWrite())
        {
            Log.e("Paths", "unable to write to essential ifDirectory? dataDirectory? " + appDataDirectoryFile);
        }
        return appDataDirectoryFile;
    }

    public static File coverDirectory() {
        File f = new File(dataDirectory(), "StoryCovers");
        if (!f.exists())
        {
            boolean goodCreate = f.mkdirs();
            if (!goodCreate)
            {
                Log.e("Paths", "unable to create essential ifDirectory? coverDirectory? " + f);
            }
        }
        return f;
    }

    public static File tempDirectory() {
        File f = new File(dataDirectory(), "StoryTemp");
        boolean goodCreate = f.mkdirs();
        if (!goodCreate)
        {
            Log.e("Paths", "unable to create essential ifDirectory? tempDirectory? " + f);
        }
        return f;
    }

    // Currently unused? Was intention that this be user-installed fonts outside of assets?
    public static File fontDirectory() {
        File f = new File(cardDirectory(), "Fonts");
        if (!f.exists()) f.mkdir();
        return f;
    }

    public static File ifDirectory() {
        if (ifDirectory != null)
            return ifDirectory;

// ToDo: app startup, when is preferences populating this?
        Log.e("Paths", "ifDirectory is setting it's own value, probably DO NOT WANT THIS");
        if (1==1) {
            throw new RuntimeException("who set this ifDirectory?");
        }
        // this code only executes once until cached, so we can be slow here
        File f = new File(cardDirectory(), "Interactive Fiction");
        if (!f.exists())
        {
            boolean goodCreate = f.mkdir();
            if (!goodCreate)
            {
                Log.e("Paths", "unable to create essential ifDirectory? " + f);
            }
        }
        return f;
    }

    public static File transcriptDirectory() {
        File f = new File(ifDirectory(), "transcripts");
        if (!f.exists()) f.mkdir();
        return f;
    }


    public static void setIfDirectory(File file) {
        Log.i("Paths", "setIfDirectory to " + file.getPath());

        if (! file.exists())
        {
            boolean goodCreate = file.mkdirs();
            Log.w("Paths", "setIfDirectory to " + file.getPath() + " mkdirs() good? " + goodCreate);
        }

        ifDirectory = file;
    }

    public static void setIfDirectoryAppDefault() {
        setIfDirectory(new File(getIfDirectoryAppDefaultString()));
    }

    public static void setIfDirectoryAppDefault2() {
        setIfDirectory(new File(getIfDirectoryAppDefault2String()));
    }

    public static String getIfDirectoryAppDefaultString() {
        return Paths.cardDirectory().getPath() + "/Interactive Fiction";
    }

    public static String getIfDirectoryAppDefault2String() {
        return Paths.appDataDirectory + "/StoryFiles";
    }
}