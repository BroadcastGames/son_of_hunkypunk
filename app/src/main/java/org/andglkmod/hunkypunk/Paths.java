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
    public static File appCardDirectory = null;

    // Name: "card" means "SD card".
    // Rename this method to "appRootGameDirectory()" because may not always be "SD Card".
    public static File cardDirectory() {
        return appCardDirectory;
    }

    public static File dataDirectory() {
        File f = new File(cardDirectory(), appDataDirectory);
        if (!f.exists()) f.mkdir();
        return f;
    }

    public static File coverDirectory() {
        File f = new File(dataDirectory(), "covers");
        if (!f.exists()) f.mkdir();
        return f;
    }

    public static File tempDirectory() {
        File f = new File(dataDirectory(), "temp");
        if (!f.exists()) f.mkdir();
        return f;
    }

    public static File fontDirectory() {
        File f = new File(cardDirectory(), "Fonts");
        if (!f.exists()) f.mkdir();
        return f;
    }

    public static File ifDirectory() {
        if (ifDirectory != null)
            return ifDirectory;

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

    public static void setIfDirectory(File file) {
        ifDirectory = file;
    }
    public static File transcriptDirectory() {
        File f = new File(ifDirectory(), "transcripts");
        if (!f.exists()) f.mkdir();
        return f;
    }

    public static void setIfDirectoryAppDefault() {
        setIfDirectory(new File(getIfDirectoryAppDefaultString()));
    }

    public static String getIfDirectoryAppDefaultString() {
        return Paths.cardDirectory().getPath() + "/Interactive Fiction";
    }
}