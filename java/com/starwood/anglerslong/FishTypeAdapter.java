package com.starwood.anglerslong;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by kennystreit on 1/20/16.
 */
public class FishTypeAdapter extends RecyclerView.Adapter<FishTypeAdapter.ViewHolder> {
    private String[] fishTypeList;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
//        public TextView mIngredients;
//        public ImageView mImage;
        public CardView mCardView;
        //        public TextView mOunces;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.fish_type);
//            mIngredients = (TextView) v.findViewById(R.id.cocktail_description);
//            mImage = (ImageView) v.findViewById(R.id.cocktail_img);
            mCardView = (CardView) v.findViewById(R.id.card_view);
//            mOunces = (TextView) v.findViewById(R.id.cocktail_ounces);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FishTypeAdapter(Context context) {
        this.context = context;

        /* Set Main Fragment with Fish Types */
        fishTypeList = new String[] {
                "Billfish",
                "Catfish",
                "Drum",
                "Flounder",
                "Grouper",
                "Grunt",
                "Jack",
                "Mackerel",
                "Mullet",
                "Porgy",
                "Ray",
                "Reef Fish",
                "Shark",
                "Snapper",
                "Snook",
                "Tarpon",
                "Tuna"
        };
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FishTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fish_type_card_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        /* Set the fish type text here: */
        holder.mTextView.setText(fishTypeList[position].toUpperCase());

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).createFragment(new SpeciesFragment(), fishTypeList[position].toLowerCase());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return fishTypeList.length;
    }
}
