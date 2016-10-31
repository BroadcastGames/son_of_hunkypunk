package org.andglkmod.hunkypunk;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 */
public class GameListDatabaseRecyclerViewAdapter extends RecyclerView.Adapter<GameListDatabaseRecyclerViewAdapter.ViewHolder> {

    private Cursor dataCursor;
    private Context parentContext;
    private GameListClickListener parentClickListener;
    private int columnIndex0 = 0;
    private int columnIndex1 = 0;
    private int columnIndex2 = 0;
    private int columnIndex3 = 0;
    private int columnIndex4 = 0;
    private int columnIndex5 = 0;

    public GameListDatabaseRecyclerViewAdapter(Context context, Cursor cursor) {
        dataCursor = cursor;
        parentContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gamelist_item, parent, false);
        return new ViewHolder(view);
    }


    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            // cache these once so that every single Item doesn't have to look them up
            columnIndex0 = dataCursor.getColumnIndex(HunkyPunk.Games.TITLE);
            columnIndex1 = dataCursor.getColumnIndex(HunkyPunk.Games.AUTHOR);
            columnIndex2 = dataCursor.getColumnIndex(HunkyPunk.Games._ID);
            columnIndex3 = dataCursor.getColumnIndex(HunkyPunk.Games.PATH);
            columnIndex4 = dataCursor.getColumnIndex(HunkyPunk.Games.IFID);
            columnIndex5 = dataCursor.getColumnIndex(HunkyPunk.Games.LOOKED_UP);

            this.notifyDataSetChanged();
        }
        return oldCursor;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        dataCursor.moveToPosition(position);

        final String gameTitle  = dataCursor.getString(columnIndex0);
        final String gameAuthor = dataCursor.getString(columnIndex1);
        final int dataRecordId =  dataCursor.getInt(   columnIndex2);
        String gameFilePath     = dataCursor.getString(columnIndex3);
        final String gameId0    = dataCursor.getString(columnIndex4);

        // gameFilePath can come up null when the user deletes or moves the game file after previous discovery.
        if (gameFilePath == null) {
            gameFilePath = "missing";
        } else {
            // shorten the path
            gameFilePath = gameFilePath.replace("/storage/emulated/0/", "SE0:");
            gameFilePath = gameFilePath.replace("/data/data/" + BuildConfig.APPLICATION_ID + "/files/", "DATA:");
        }

        holder.refPosition = position;
        holder.dataRecordId = dataRecordId;
        holder.gameIdentity0 = gameId0;
        holder.mTitleLineView.setText(gameTitle + " [" + position + " / " + dataRecordId + "]");
        if (gameAuthor == null) {
            holder.mFirstDetailView.setVisibility(View.GONE);
        }
        else {
            holder.mFirstDetailView.setVisibility(View.VISIBLE);
            holder.mFirstDetailView.setText(gameAuthor);
        }

        holder.mSecondDetailView.setVisibility(View.VISIBLE);
        holder.mSecondDetailView.setText("path " + gameFilePath);

        holder.mImageView.setImageDrawable(null);
        if (gameId0 != null) {
            File coverFile = HunkyPunk.getCover(gameId0);
            if (coverFile.exists())
            {
                Uri uri = Uri.fromFile(coverFile);
                holder.mImageView.setImageURI(uri);
            }
        }
    }

    @Override
    public int getItemCount() {
        return  (dataCursor == null) ? 0 : dataCursor.getCount();
    }


    public void setParentClickListener(GameListClickListener itemClickListener) {
        this.parentClickListener = itemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mTitleLineView;
        public final TextView mFirstDetailView;
        public final TextView mSecondDetailView;
        public final ImageView mImageView;
        public int dataRecordId;
        public String gameIdentity0;
        public int refPosition;  // Won't be accurate, gets recycled.

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTitleLineView = (TextView) itemView.findViewById(R.id.gameListGameTitle);
            mFirstDetailView = (TextView) itemView.findViewById(R.id.gameListGameInfo0);
            mSecondDetailView = (TextView) itemView.findViewById(R.id.gameListGameInfo1);
            mImageView = (ImageView) itemView.findViewById(R.id.gameListCoverImage0);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFirstDetailView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            if (parentClickListener != null) parentClickListener.onGameListItemClick(v, getAdapterPosition(), this);
        }
    }
}
