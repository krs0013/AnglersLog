package com.starwood.anglerslong;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
//        final Bitmap roundedLogImage = getRoundedCorners(logImage, 12);

        /* Set the fish type text here: */
        holder.mImage.setImageBitmap(logImage);
        holder.mSpecies.setText(logListArray.get(position).getSpecies());
        holder.mDate.setText("Date: " + logListArray.get(position).getDate());
        holder.mLocation.setText("Location: " + logListArray.get(position).getLocation());
        holder.mBait.setText("Bait: " + logListArray.get(position).getBait());
        holder.mLength.setText("Length: " + logListArray.get(position).getLength() + " in.");
        holder.mNotes.setText("Notes:\n" + logListArray.get(position).getNotes());

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // First convert bitmap to byte array
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap tempBitmap = logImage;
                    tempBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    // Then pass that byte array
                    Intent intent = new Intent(context, Popup.class);
                    intent.putExtra("log_image", byteArray);
                    context.startActivity(intent);
                } catch (NullPointerException e) {
                    Log.d("KENNY", "Tried to load log pic on click, but didnt work");
                    e.printStackTrace();
                }
            }
        });

        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(30);      // Vibrate for 50 milliseconds
                showDeleteDialog(position);
                return false;
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return logListArray.size();
    }

    private Bitmap getRoundedCorners(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final  float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public void showDeleteDialog(final int pos) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.checklist_delete_dialog);
        dialog.setTitle(logListArray.get(pos).getSpecies());
        dialog.findViewById(R.id.cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.delete).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        removeLogListItem(pos);
                        dialog.dismiss();
                        ((MainActivity) context).onBackPressed();
                    }
                });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    /*************************************************************************************
     * Used to remove a log after a long click
     * Remember, the logs.remove is only for API 19
     *************************************************************************************
     * @param position
     *************************************************************************************/
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void removeLogListItem(int position) {
        JsonStorage json = new JsonStorage(context);
        try {
            String logJSON = json.readProfileJSON();
            JSONObject object = new JSONObject(logJSON);
            JSONObject outer = object.getJSONObject("outermost");       // Get outermost json object (everything)
            JSONArray logs = outer.getJSONArray("logs");                // Get all the previous logs}
            logs.remove(position);
            outer.put("logs", logs);                                    // Overwrite the log list with new one
            object.put("outermost", outer);                             // Add outermost to the JSON object
            json.writeJSON(object);                                     // Populate this in the memory
            Toast.makeText(context, "Your log was removed.", Toast.LENGTH_SHORT).show();
        } catch (IOException | JSONException e) {
            Toast.makeText(context, "Item could not be deleted at this time...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
