package org.andglkmod.hunkypunk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by adminsag on 11/5/16.
 */
public class MediaReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("MediaReceiver", "paths.java related? intent " + intent.getAction());
    }
}
