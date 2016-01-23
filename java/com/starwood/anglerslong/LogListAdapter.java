package com.starwood.anglerslong;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by kennystreit on 1/22/16.
 */
public class LogListAdapter extends RecyclerView.Adapter<LogListAdapter.ViewHolder> {
    private ArrayList<LogListItem> logListArray;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImage;
        public TextView mSpecies;
        public TextView mDate;
        public TextView mLocation;
        public TextView mBait;
        public TextView mLength;
        public TextView mNotes;
        public CardView mCardView;

        public ViewHolder(View v) {
            super(v);
            mImage = (ImageView) v.findViewById(R.id.log_image);
            mSpecies = (TextView) v.findViewById(R.id.log_species);
            mDate = (TextView) v.findViewById(R.id.log_date);
            mLocation = (TextView) v.findViewById(R.id.log_location);
            mBait = (TextView) v.findViewById(R.id.log_bait);
            mLength = (TextView) v.findViewById(R.id.log_length);
            mNotes = (TextView) v.findViewById(R.id.log_notes);
            mCardView = (CardView) v.findViewById(R.id.card_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public LogListAdapter(Context context, ArrayList<LogListItem> logListArray) {
        this.context = context;
        this.logListArray = logListArray;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LogListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_log_list, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        PictureStorage ps = new PictureStorage(context);
        final Bitmap logImage = ps.stringToBitMap(logListArray.get(position).getImage());

        /* Set the fish type text here: */
        holder.mImage.setImageBitmap(logImage);
        holder.mSpecies.setText(logListArray.get(position).getSpecies());
        holder.mDate.setText(logListArray.get(position).getDate());
        holder.mLocation.setText(logListArray.get(position).getLocation());
        holder.mBait.setText(logListArray.get(position).getBait());
        holder.mLength.setText(logListArray.get(position).getLength());
        holder.mNotes.setText(logListArray.get(position).getNotes());

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First convert bitmap to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap tempBitmap = logImage;
                tempBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Then pass that byte array
                Intent intent = new Intent(context, Popup.class);
                intent.putExtra("log_image", byteArray);
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return logListArray.size();
    }
}
