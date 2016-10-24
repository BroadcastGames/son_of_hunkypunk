package org.andglkmod.hunkypunk.events;


public class BackgroundScanEvent {
    public final int eventCode;

    public static final int ECODE_UNKNOWN = 0;
    public static final int ECODE_GAME_FOLDER_SCAN_COMPLETED = 1;

    public BackgroundScanEvent(int eCode) {
        eventCode = eCode;
    }
}
