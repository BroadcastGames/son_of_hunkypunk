package org.andglkmod.hunkypunk;

import java.io.File;

/**
 * During active development a quick way to control app behavior.
 * This distinctive class name also makes it easy to find multiple places that reference ongoing feature work.
 * As things become more final and refined, probably best to move to shared preferences.
 */

public class EasyGlobalsA {
    public static boolean glk_c_to_java_char_loggingA = false;
    public static boolean glk_c_to_java_string_loggingA = false;
    public static boolean glk_c_to_java_output_appendA = true;
    public static boolean glk_c_to_java_output_flushA = false;

    public static boolean storageManagerAssetStuffing0 = true;

    public static File getPathNullPathA = null;
    public static long fileDownloadMinimumFreeSpaceA = 1024L * 1024L * 3L;  // 3MB
}
