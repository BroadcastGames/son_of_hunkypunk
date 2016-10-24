package org.andglkmod.hunkypunk;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 */
public class GameListDatabaseRecyclerViewAdapter extends RecyclerView.Adapter<GameListDatabaseRecyclerViewAdapter.ViewHolder> {

    private Cursor dataCursor;
    private Context parentContext;

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
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        dataCursor.moveToPosition(position);

        final String gameTitle  = dataCursor.getString(dataCursor.getColumnIndex(HunkyPunk.Games.TITLE));
        final String gameAuthor = dataCursor.getString(dataCursor.getColumnIndex(HunkyPunk.Games.AUTHOR));
        final int dataRecordId =  dataCursor.getInt(dataCursor.getColumnIndex(HunkyPunk.Games._ID));
        String gameFilePath = dataCursor.getString(dataCursor.getColumnIndex(HunkyPunk.Games.PATH));
        gameFilePath = gameFilePath.replace("/storage/emulated/0/", "/SE0:");

        holder.refPosition = position;
        holder.dataRecordId = dataRecordId;
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
    }

    @Override
    public int getItemCount() {
        return  (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleLineView;
        public final TextView mFirstDetailView;
        public final TextView mSecondDetailView;
        public int dataRecordId;
        public int refPosition;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleLineView = (TextView) view.findViewById(R.id.gameListGameTitle);
            mFirstDetailView = (TextView) view.findViewById(R.id.gameListGameInfo0);
            mSecondDetailView = (TextView) view.findViewById(R.id.gameListGameInfo1);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFirstDetailView.getText() + "'";
        }
    }
}
