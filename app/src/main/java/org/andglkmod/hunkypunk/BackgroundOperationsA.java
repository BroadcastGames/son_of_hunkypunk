package org.andglkmod.hunkypunk;

import android.content.Context;

/**
 * Android app design philosophy.
 * There are aspects of an app that benefit best from being detached from the user interface (GUI). The user can open/close activities
 * and rotate the screen at the most unpredictable times.
 * One solution is to have a Service that runs and manages all background related activities. But this approach can be a bit persistent
 * and long-running. Threads can also be initiated that are independent of the user interface.
 *
 * This class is intended to be user interface independent to hold various actions that could be hosted by a service.
 * if the app features evolve such that a Service or ad-hoc IntentService makes sense, this code would be a central point of
 * 'background operations' to wrap.
 */

public class BackgroundOperationsA {
    private static GameListHelper gameListHelper = null;

    /*
    Probably best to pass in an Applicaiton context given that an Activity may close and it's context become invalid.
    synchronized to prevent rapid duplicate calls taht sometimes happens on Activity creation.
     */
    static synchronized public GameListHelper getGameListHelper(Context context)
    {
        if (gameListHelper == null) {
            gameListHelper = new GameListHelper(context);
        }
        return gameListHelper;
    }
}
