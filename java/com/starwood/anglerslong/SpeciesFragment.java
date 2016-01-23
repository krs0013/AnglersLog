package com.starwood.anglerslong;

/**
 * Created by kennystreit on 1/17/16.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SpeciesFragment extends Fragment {

    /**
     * Used to store the last screen title.
     */
    private static String jsonItem;
    private static List<String> photosList = new ArrayList<String>();
    private static ArrayList<SpeciesItem> jsonArray = new ArrayList<SpeciesItem>();
    private URLConnection connection;

    public SpeciesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the Species Type: "Shark," "Billfish," etc.
        jsonItem = this.getArguments().getString("type");
        Log.d("KENNY", "String argument = " + jsonItem);

        try {
            JsonStorage jStore = new JsonStorage(getActivity());
            String temp = jStore.loadSpeciesJSON();
            populateItem(temp);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        final View myInflatedView = inflater.inflate(R.layout.fragment_species, container,false);

        /****************************************************************************
         *************** Favorite a fish ********************************************
         ****************************************************************************/
        Button favorite = (Button) myInflatedView.findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeFavorite(myInflatedView);
            }
        });


        /****************************************************************************
         **************** Log a fish ************************************************
         ****************************************************************************/
        Button log = (Button) myInflatedView.findViewById(R.id.log);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) myInflatedView.findViewById(R.id.species_name);
                Calendar c = Calendar.getInstance();
                String formattedDate = new SimpleDateFormat("MMM-dd-yyyy").format(c.getTime());

                Intent intent = new Intent(getActivity(), LogActivity.class);
                intent.putExtra("speciesName", text.getText());
                intent.putExtra("currDate", formattedDate);
                intent.putExtra("title", "Log a Fish");
                startActivity(intent);
            }
        });

        // Set the top text (above all the pics) to typeface and to this text
        TextView typeTextView = (TextView) myInflatedView.findViewById(R.id.type);
        StringBuilder typeString = new StringBuilder();             // Create string to capitalize first letters
        String[] typeArray = jsonItem.split("_");                   // Split string by underscores
        for (int i = 0; i < typeArray.length; i++) {                // Loop through only if multiple words
            String temp = typeArray[i];                             // Get the first word
            typeString.append(temp.substring(0, 1).toUpperCase());  // Capitalize the first letter and append
            typeString.append(temp.substring(1, temp.length()));    // Append the rest of the word to capitalized first letter
            if (typeArray.length - i > 1) typeString.append(" ");   // Put a space if there are multiple words.
        }
        typeTextView.setText(typeString.toString());
        Typeface typeTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Walkway_Bold.ttf");
        typeTextView.setTypeface(typeTypeFace);

        // Loop through each species of the specified type and populate below the top text
        for (int i = 0; i < jsonArray.size(); i++) {

            // Get the linear layout and set the parameters
            LinearLayout ll = (LinearLayout) myInflatedView.findViewById(R.id.species_ll);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;

            // Use this for each image in the list
            final ImageView image = new ImageView(getActivity());
            image.setAdjustViewBounds(true);            // Scales it to the screen
            image.setId(i);                             // Sets each with unique id
            ll.addView(image, params);                  // Adds the ImageView to screen BEFORE adding image (important)
            Picasso.with(getActivity())                 // THEN you add the image from photosList
                    .load(photosList.get(i))
                    .into(image);



            // Next, set click listener.  It makes a different view appear ABOVE the species images
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    populateList(myInflatedView, image);
                }
            });

        }
        return myInflatedView;
    }


    /******************************************************************************************************************************************
     * Populates all of the tabs that have been added to this feature.
     * Sets an onClickListener to each one of the items
     * Keeps track of all the button ID's
     * Keeps track of the TOTAL number of items in the list.
     * *****************************************************************************************************************************************
     * @param json
     * @throws org.json.JSONException
     ******************************************************************************************************************************************/

    private void populateItem(String json) throws JSONException {
        JSONObject j = new JSONObject(json);				// Whole JSON String
        JSONObject outer = j.getJSONObject("species");	    // Outermost JSON object
        JSONArray jArray = outer.getJSONArray(jsonItem);	// Array of that item

        photosList.clear();
        jsonArray.clear();

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject temp = jArray.getJSONObject(i);
            SpeciesItem speciesItem = new SpeciesItem(getActivity());

            photosList.add(temp.getString("image"));

            speciesItem.setName(temp.getString("type"));
            speciesItem.setScientificName(temp.getString("scientific"));
            speciesItem.setCommonName(temp.getString("common"));
            speciesItem.setIndividualLimit(temp.getString("individual"));
            speciesItem.setAggregateLimit(temp.getString("aggregate"));
            speciesItem.setSizeLimit(temp.getString("minimum"));
            speciesItem.setSeason(temp.getString("season"));
            speciesItem.setRecords(temp.getString("record"));

            jsonArray.add(speciesItem);
        }
    }

    /******************************************************************************************************************************************
     *              Populate List
     *
     * Populates the individual species the user clicked on
     ******************************************************************************************************************************************
     * @param mView The view the user clicked
     * @param mImage The image the user clicked
     ******************************************************************************************************************************************/
    private void populateList(View mView, ImageView mImage) {

        // Next 5 lines give a short fade-in animation for the single photo
        Welcome welcome = Welcome.getInstance();
        int mShortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
        mShortAnimationDuration *= 2;
        welcome.fadeIn(mView, mShortAnimationDuration);
        mView.findViewById(R.id.single_photo).setVisibility(View.VISIBLE);

        ImageView singleImage = (ImageView) mView.findViewById(R.id.single_img);
        Picasso.with(getActivity())
                .load(photosList.get(mImage.getId()))
                .into(singleImage);

        /************************************
         * 1.) Species Name
         ************************************/
        TextView text = (TextView) mView.findViewById(R.id.species_name);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Walkway_Bold.ttf");
        String jsonText = (jsonArray.get(mImage.getId()).getName());
        //text.setText(Html.fromHtml(singleText));
        text.setText(jsonText);
        text.setTypeface(font);
        /************************************
         * 2.) Scientific Name
         ************************************/
        text = (TextView) mView.findViewById(R.id.scientific);
        jsonText = ("Scientific Name: " + jsonArray.get(mImage.getId()).getScientificName());
        text.setText(jsonText);
        /************************************
         * 3.) Common Name
         ************************************/
        text = (TextView) mView.findViewById(R.id.common);
        jsonText = ("Common Name: " + jsonArray.get(mImage.getId()).getCommonName());
        text.setText(jsonText);
        /************************************
         * 4.) Individual Limits
         ************************************/
        text = (TextView) mView.findViewById(R.id.individual);
        jsonText = ("Individual Limit: \n" + jsonArray.get(mImage.getId()).getIndividualLimit());
        text.setText(jsonText);
        /************************************
         * 5.) Aggregate Limits
         ************************************/
        text = (TextView) mView.findViewById(R.id.aggregate);
        jsonText = ("Aggregate Limit: \n" + jsonArray.get(mImage.getId()).getAggregateLimit());
        text.setText(jsonText);
        /************************************
         * 6.) Minimum
         ************************************/
        text = (TextView) mView.findViewById(R.id.minimum);
        jsonText = ("Size Limits: \n" + jsonArray.get(mImage.getId()).getSizeLimit());
        text.setText(jsonText);
        /************************************
         * 7.) Season
         ************************************/
        text = (TextView) mView.findViewById(R.id.season);
        jsonText = ("Season: \n" + jsonArray.get(mImage.getId()).getSeason());
        text.setText(jsonText);
        /************************************
         * 8.) Records
         ************************************/
        text = (TextView) mView.findViewById(R.id.record);
        jsonText = ("Records: \n" + jsonArray.get(mImage.getId()).getRecords());
        text.setText(jsonText);
    }

    /****************************************************************************************************************************************
     *          STORE FAVORITES
     ****************************************************************************************************************************************/
    private void storeFavorite(View view) {

        int imgPosition = -1;
        for (int i = 0; i < jsonArray.size(); i++) {
            TextView tv = (TextView) view.findViewById(R.id.species_name);
            String name = tv.getText().toString();
            if (jsonArray.get(i).getName().equals(name))
                imgPosition = i;
        }

        //===================================================================
        // Step 1: Get all text views and the image
        //===================================================================
        TextView speciesName = (TextView) view.findViewById(R.id.species_name);
        TextView scientific = (TextView) view.findViewById(R.id.scientific);
        TextView common = (TextView) view.findViewById(R.id.common);
        Log.d("KENNY", "Image Position: " + String.valueOf(imgPosition));
        Log.d("KENNY", "Photos List size: " + String.valueOf(photosList.size()));
        String favoriteImage = photosList.get(imgPosition);
        TextView individual = (TextView) view.findViewById(R.id.individual);
        TextView aggregate = (TextView) view.findViewById(R.id.aggregate);
        TextView minimum = (TextView) view.findViewById(R.id.minimum);
        TextView season = (TextView) view.findViewById(R.id.season);
        TextView record = (TextView) view.findViewById(R.id.record);

        //===================================================================
        // Step 2: Store all information in a single json object
        //===================================================================
        JSONObject favorite = new JSONObject();
        try {
            favorite.put("scientific"   , scientific.getText().toString());
            favorite.put("common"       , common.getText().toString());
            favorite.put("image"        , favoriteImage);
            favorite.put("individual"   , individual.getText().toString());
            favorite.put("aggregate"    , aggregate.getText().toString());
            favorite.put("minimum"      , minimum.getText().toString());
            favorite.put("season"       , season.getText().toString());
            favorite.put("record"       , record.getText().toString());
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Could not save your favorite fish at this time...",
                    Toast.LENGTH_SHORT).show();
        }

        //===================================================================
        // Step 3: Get the json array and add it, or make a new one if necessary.
        //         Then write it to the new json text.
        //===================================================================
        JsonStorage json = new JsonStorage(getActivity());
        boolean isOutermostThere = false;
        try {
            String favoriteJson = json.readProfileJSON();
            JSONObject object = new JSONObject(favoriteJson);
            JSONObject outer = object.getJSONObject("outermost");       // Get outermost json object (everything)
            isOutermostThere = true;                                    // There is a JSON object already made
            JSONObject favorites = outer.getJSONObject("favorites");    // Get the "favorites" JSONObject
            favorites.put(speciesName.getText().toString(), favorite);  // Add it to the favorites object list
            outer.put("favorites", favorites);                          // Overwrite the favorites list with new one
            object.put("outermost", outer);                             // Add outermost to the JSON object
            json.writeJSON(object);                                     // Populate this in the memory
            Toast.makeText(getActivity(), "You have added a favorite!",
                    Toast.LENGTH_SHORT).show();
        } catch (IOException | JSONException e) {
            //********* Go here if the array did not already exist ***********//
            JSONObject newFavList = new JSONObject();                  // Create new JSONObject
            try {
                newFavList.put(speciesName.getText().toString(), favorite); // Add the first entry
                if (isOutermostThere) {                                     // If there is a JSONObject already made
                    String favoriteJson = json.readProfileJSON();           // Load the populated data
                    JSONObject object = new JSONObject(favoriteJson);       // Get the whole json object
                    JSONObject outer = object.getJSONObject("outermost");   // Get outermost object
                    outer.put("favorites", newFavList);                     // Add new favorites array to the profile object
                    object.put("outermost", outer);
                    json.writeJSON(object);                                 // Write this to the memory
                } else {
                    JSONObject favorites = new JSONObject();        // Create new favorites object
                    favorites.put("favorites", newFavList);         // Add favorites list to favorites JSON object
                    JSONObject outer = new JSONObject();            // Create new outermost JSON since it doesnt exist
                    outer.put("outermost", favorites);              // Store new favorites list into it.
                    json.writeJSON(outer);
                }
                Toast.makeText(getActivity(), "You have added your first favorite fish!",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException | JSONException e1) {
                Toast.makeText(getActivity(), "Something went wrong saving.  Please try again later.",
                        Toast.LENGTH_SHORT).show();
                e1.printStackTrace();
            }
        }
    }

    /**
     * Store the image as cache so it can be loaded later
     * @param strUrl Holds the url to be cached
     * @param context Passes the context
     */
    private void storeCache(String strUrl, Context context) {
        try {
            URL url = new URL(strUrl);
            connection = url.openConnection();
            connection.setUseCaches(true);
            Object response = connection.getContent();
            if (response instanceof Bitmap) {
                Bitmap bitmap = (Bitmap) response;
            }
        } catch (MalformedURLException eURL) {
            Toast.makeText(context, "URL was malformed\nCould not cache",
                    Toast.LENGTH_SHORT).show();
            eURL.printStackTrace();
        } catch (IOException eIO) {
            Toast.makeText(context, "IOException\nCould not cache",
                    Toast.LENGTH_SHORT).show();
            eIO.printStackTrace();
        }
    }

}
