package org.andglkmod.hunkypunk;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.andglkmod.hunkypunk.dummy.DummyContent;

import java.util.List;

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
                .inflate(R.layout.fragment_item, parent, false);
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

        String gameTitle  = dataCursor.getString(dataCursor.getColumnIndex(HunkyPunk.Games.TITLE));
        String gameAuthor = dataCursor.getString(dataCursor.getColumnIndex(HunkyPunk.Games.AUTHOR));

        //holder.mItem = mValues.get(position);
        holder.mIdView.setText(gameTitle);
        holder.mContentView.setText(gameAuthor);
    }

    @Override
    public int getItemCount() {
        return  (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        //public DummyContent.DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
