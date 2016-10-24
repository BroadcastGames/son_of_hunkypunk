package org.andglkmod.hunkypunk;

import android.view.View;

/**
 * Created by adminsag on 10/23/16.
 */

public interface GameListClickListener {
    void onGameListItemClick(View view, int position, GameListDatabaseRecyclerViewAdapter.ViewHolder viewHolder);
}