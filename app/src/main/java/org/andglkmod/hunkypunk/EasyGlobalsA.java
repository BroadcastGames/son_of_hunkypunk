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

    public static boolean IFDB_lookup_fetchA = true;

    public static boolean storageManagerAssetStuffing0 = true;

    public static File getPathNullPathA = null;
    public static long fileDownloadMinimumFreeSpaceA = 1024L * 1024L * 3L;  // 3MB

    // Template is base, more are added dynamically to resulting set.
    public static final String[] additionalStoryDirectoriesTemplate = new String[] {
            "/sdcard/story000/Glulx_Tests0",
            "/sdcard/story000/setZ",
            "/sdcard/story000/setA",
            "/sdcard/storyGames0",
            "/sdcard/storyGames1",
            // Teclast Android 5.0 tablet uses this for physically removable SD Card
            // Code at the time of this comment checkin does not seem to generate that path.
            "/storage/sdcard1",
            // Blu Studio Energy 2 android 5.0 phone had this
            // Code at the time of this comment checkin does not seem to generate that path.
            // But it is in 'external storage', yes it seems backwards sdcard1 is built-in
            //   sdcard0 is removable SD Card!
            // "storage/sdcard0"
        };

    public static String[] additionalStoryDirectories = additionalStoryDirectoriesTemplate;
    public static boolean storageManagerScandIrectoryTreeLogA = false;
}
