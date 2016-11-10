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

    public static boolean IFDB_lookup_fetchA_logging = true;

    public static boolean storageManagerAssetStuffing0 = false;

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

            "/sdcard/storyGames_TechTest0",
        };

    public static boolean storageManagerScanExternalWideA = true;
    public static String[] additionalStoryDirectories = additionalStoryDirectoriesTemplate;
    public static boolean storageManagerScanDirectoryTreeLogA = false;

    public static boolean glk_c_to_java_input_events_LogA = true;
    public static boolean commandInput_colorSetA = true;
    public static boolean commandInput_extraViewAtBottomA = false;
    // App default behavior was to have these true:
    public static boolean commandInput_paddingAndNoExtractUI_SetA = false;

    public static boolean commandInput_paddingForceVisibleA = false;
    public static boolean commandInput_Prompt_muckUpA = false;

    // Note: The emulator, and possibly real devices, shows an odd View echo/duplication and bottom void
    //   on the ScrollView. Turning on the Soft Keyboard and turning it back off seems to resolve it.
    //   Seems to be related to the Android soft keyboard suggestions above top row of keys?

    public static boolean storyLayout_scrollView_PaddingAddSidesA = true;
    // Useful for debugging layout problems
    public static boolean storyLayout_inputSections_ColorLayoutsA = false;

    // Page copy/paste selection
    public static boolean selectionLoggingA = true;

    // Issue #42 workaround - but consumes more vertical space.
    public static boolean storyLayout_inputSections_PromptCommandsVerticalA = true;

    public static boolean textBufferWindow_selectionChanged_SkipA = false;
    // Testing reveals this does not solve any problems:
    public static boolean textBufferWindow_textWatcher_skipOnTextChangedA = false;

    public static boolean commandInput_flapKeyboardShowA = true;

    public static InterpreterActivity interpreterActivityCurrent = null;

    // Set this to true to try and work around Issue #43
    public static boolean commandInput_HardKeyboard_WorkaroundA = true;
}
