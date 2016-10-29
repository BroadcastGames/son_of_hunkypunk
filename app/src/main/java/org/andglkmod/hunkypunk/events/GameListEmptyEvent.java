package org.andglkmod.hunkypunk.events;

/**
 * Created by adminsag on 10/29/16.
 */

public class GameListEmptyEvent {
    public static final int EVENT_CUDE_UNKNOWN = 0;
    public static final int DOWNLOAD_PRESELECT_GAMES_SET0 = 1;
    public static final int DOWNLOAD_COMPLETED_RECHECK0 = 2;

    public int eventCode = EVENT_CUDE_UNKNOWN;

    public GameListEmptyEvent(int inEventCode) {
        eventCode = inEventCode;
    }
}
