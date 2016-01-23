package com.starwood.anglerslong;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class FavoriteActivity extends ActionBarActivity  {

    private java.util.List<String> photosList = new ArrayList<String>();
    private ArrayList<SpeciesItem> jsonArray = new ArrayList<SpeciesItem>();
    private boolean isSingleView = false;
    private int currentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_species);


        try {
            JsonStorage jsonStorage = new JsonStorage(this);
            String wholeString = jsonStorage.loadTabs();
            populateItem(wholeString);
            setUpFavorites();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /*********************************************************
     * Creates the action bar menu
     *********************************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    /******************************************************************************************************************************************
     * Populates all of the tabs that have been added to this feature.
     * Sets an onClickListener to each one of the items
     * Keeps track of all the button ID's
     * Keeps track of the TOTAL number of items in the list.
     * *****************************************************************************************************************************************
     * @param json
     * @throws JSONException
     ******************************************************************************************************************************************/

    private void populateItem(String json) throws JSONException {
        JSONObject j = new JSONObject(json);				// Whole JSON String
        JSONObject outer = j.getJSONObject("outermost");    // Outermost JSON object
        JSONObject favorites = outer.getJSONObject("favorites");

        photosList.clear();
        jsonArray.clear();

        Iterator<String> iter = favorites.keys();
        while (iter.hasNext()) {
            String key = iter.next();                               // Name of the fishing spot
            try {
                SpeciesItem speciesItem = new SpeciesItem(getApplicationContext());
                speciesItem.setName(key);

                JSONObject keyObject = favorites.getJSONObject(key);

                String image = keyObject.getString("image");
                photosList.add(image);
                String scientific = keyObject.getString("scientific");
                speciesItem.setScientificName(scientific.substring(scientific.indexOf(": ") + 2, scientific.length()));
                String common = keyObject.getString("common");
                speciesItem.setCommonName(common.substring(common.indexOf(": ") + 2, common.length()));
                String individual = keyObject.getString("individual");
                speciesItem.setIndividualLimit(individual.substring(individual.indexOf(": ") + 2, individual.length()));
                String aggregate = keyObject.getString("aggregate");
                speciesItem.setAggregateLimit(aggregate.substring(aggregate.indexOf(": ") + 2, aggregate.length()));
                String minimum = keyObject.getString("minimum");
                speciesItem.setSizeLimit(minimum.substring(minimum.indexOf(": ") + 2, minimum.length()));
                String season = keyObject.getString("season");
                speciesItem.setSeason(season.substring(season.indexOf(": ") + 2, season.length()));
                String record = keyObject.getString("record");
                speciesItem.setRecords(record.substring(record.indexOf(": ") + 2, record.length()));

                jsonArray.add(speciesItem);

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong ITERATING",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpFavorites() {

        /****************************************************************************
         **************** Log a fish ************************************************
         ****************************************************************************/
        Button log = (Button) findViewById(R.id.log);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) findViewById(R.id.species_name);
                Calendar c = Calendar.getInstance();
                String formattedDate = new SimpleDateFormat("MMM-dd-yyyy").format(c.getTime());

                Intent intent = new Intent(getApplicationContext(), LogActivity.class);
                intent.putExtra("speciesName", text.getText());
                intent.putExtra("currDate", formattedDate);
                intent.putExtra("title", "Log a Fish");
                startActivity(intent);
            }
        });

        // Set the top text (above all the pics) to typeface and to this text
        TextView typeTextView = (TextView) findViewById(R.id.type);
        typeTextView.setText("Favorites");
        Typeface typeTypeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Walkway_Bold.ttf");
        typeTextView.setTypeface(typeTypeFace);

        // Loop through each species of the specified type and populate below the top text
        for (int i = 0; i < jsonArray.size(); i++) {

            // Get the linear layout and set the parameters
            LinearLayout ll = (LinearLayout) findViewById(R.id.species_ll);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;

            // Use this for each image in the list
            final ImageView image = new ImageView(getApplicationContext());
            image.setAdjustViewBounds(true);            // Scales it to the screen
            image.setId(i);                             // Sets each with unique id
            ll.addView(image, params);                  // Adds the ImageView to screen BEFORE adding image (important)
            Picasso.with(this)                 // THEN you add the image from photosList
                    .load(photosList.get(i))
                    .into(image);



            // Next, set click listener.  It makes a different view appear ABOVE the species images
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    populateList(v, image);
                }
            });

        }
    }

    private void populateList(View mView, ImageView mImage) {

        isSingleView = true;
        currentID = mImage.getId();

        // Next 5 lines give a short fade-in animation for the single photo
        Welcome welcome = Welcome.getInstance();
        int mShortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
        mShortAnimationDuration *= 2;
        welcome.fadeIn(mView, mShortAnimationDuration);
        findViewById(R.id.single_photo).setVisibility(View.VISIBLE);

        ImageView singleImage = (ImageView) findViewById(R.id.single_img);
        Picasso.with(this)
                .load(photosList.get(currentID))
                .into(singleImage);

        /************************************
         * 1.) Species Name
         ************************************/
        TextView text = (TextView) findViewById(R.id.species_name);
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/Walkway_Bold.ttf");
        String jsonText = (jsonArray.get(currentID).getName());
        //text.setText(Html.fromHtml(singleText));
        text.setText(jsonText);
        text.setTypeface(font);
        /************************************
         * 2.) Scientific Name
         ************************************/
        text = (TextView) findViewById(R.id.scientific);
        jsonText = ("Scientific Name: " + jsonArray.get(currentID).getScientificName());
        text.setText(jsonText);
        /************************************
         * 3.) Common Name
         ************************************/
        text = (TextView) findViewById(R.id.common);
        jsonText = ("Common Name: " + jsonArray.get(currentID).getCommonName());
        text.setText(jsonText);
        /************************************
         * 4.) Individual Limits
         ************************************/
        text = (TextView) findViewById(R.id.individual);
        jsonText = ("Individual Limit: \n" + jsonArray.get(currentID).getIndividualLimit());
        text.setText(jsonText);
        /************************************
         * 5.) Aggregate Limits
         ************************************/
        text = (TextView) findViewById(R.id.aggregate);
        jsonText = ("Aggregate Limit: \n" + jsonArray.get(currentID).getAggregateLimit());
        text.setText(jsonText);
        /************************************
         * 6.) Minimum
         ************************************/
        text = (TextView) findViewById(R.id.minimum);
        jsonText = ("Size Limits: \n" + jsonArray.get(currentID).getSizeLimit());
        text.setText(jsonText);
        /************************************
         * 7.) Season
         ************************************/
        text = (TextView) findViewById(R.id.season);
        jsonText = ("Season: \n" + jsonArray.get(currentID).getSeason());
        text.setText(jsonText);
        /************************************
         * 8.) Records
         ************************************/
        text = (TextView) findViewById(R.id.record);
        jsonText = ("Records: \n" + jsonArray.get(currentID).getRecords());
        text.setText(jsonText);
    }

    /**
     * If pressed and the single image is appeared first, it sets it to GONE
     * If pressed and all species images are visible, it goes back to the main menu.
     */
    public void onBackPressed(){
        if (isSingleView) {
            findViewById(R.id.single_photo).setVisibility(View.GONE);
            isSingleView = false;
        } else {
            this.finish();
        }
    }

}
