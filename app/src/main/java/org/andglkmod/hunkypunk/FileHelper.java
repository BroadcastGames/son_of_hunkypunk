package org.andglkmod.hunkypunk;

import android.util.Log;
import java.io.File;

/**
 * Created by adminsag on 11/1/16.
 */

public class FileHelper {

    public void walk(File root) {

        File[] list = root.listFiles();

        for (File f : list) {
            if (f.isDirectory()) {
                Log.d("FileHelper", "Dir: " + f.getAbsoluteFile());
                walk(f);
            }
            else {
                Log.d("FileHelper", "File: " + f.getAbsoluteFile());
            }
        }
    }

}
