package com.starwood.anglerslong;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by kennystreit on 1/23/16.
 */
public class LicenseActivity  extends ActionBarActivity {

    private boolean isLicensePopulated = false;				// Are there items in the license list
    public boolean isArrayEmpty = true;						// Used if the json object is there, but the array is empty.
    private int numItems = 0;								// Total number of items that are deletable.
    private int currentItemID;								// Used for DELETING items.
    private boolean isDeletable = false;					// Semaphore only if the user clicked the delete button in actionbar.
    private boolean isCreateTextViewSet = false;
    private boolean isEdit = false;
    //*** Selecting a photo ***//
    private static final int SELECT_PHOTO = 100;
    private LruCache<String, Bitmap> mMemoryCache;

    private int[] imageViewIdArray = {
            R.id.licenseimage1,
            R.id.licenseimage2,
            R.id.licenseimage3,
            R.id.licenseimage4,
            R.id.licenseimage5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.license);

        Bundle bundle = this.getIntent().getExtras();
        getSupportActionBar().setTitle(bundle.getString("abtitle"));
//        getSupportActionBar().setSubtitle(bundle.getString("subtitle"));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    // <- This puts the back arrow in actionbar

        if (bundle.containsKey("isArrayEmpty")) isArrayEmpty = bundle.getBoolean("isArrayEmpty");

        populateTabs(false);		// Populate the tabs within the clicked tab.

    }

    /***********************************************************************************
     * Used to overwrite onResume function when user adds an item.
     * This will populate the new item right when they click save in LicenseAddActivity
     ***********************************************************************************/
    public void onRestart() {
        super.onRestart();
        populateTabs(true);
    }

    /*********************************************************
     * Creates the action bar menu
     *********************************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_license, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*********************************************************
     * Gives functionality to the action bar
     *********************************************************/
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();

        switch (item.getItemId()) {
            case R.id.add:
                if (numItems <= 5) {
                    intent.setClass(getApplicationContext(), LicenseAddActivity.class);
                    intent.putExtra("title", "License");
                    intent.putExtra("isPopulated", isLicensePopulated);
                    intent.putExtra("isArrayEmpty", isArrayEmpty);
                    intent.putExtra("isLicense", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot have more than 5 licenses",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.delete:
                if (isLicensePopulated && !isArrayEmpty) {
                    isDeletable = true;
                    Toast.makeText(getApplicationContext(), "Please click the item you would like to delete",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "There's nothing to delete.  Please Add an item first.", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /******************************************************************************
     * Reads in all of the items in that should be populated.
     ******************************************************************************/
    private void populateTabs(boolean isRestart) {
        try {
            JsonStorage jsonStorage = new JsonStorage(getApplicationContext());
            String wholeString = jsonStorage.loadTabs();
            if (!wholeString.equals("")) isLicensePopulated = true;
            populateLicense(wholeString, isRestart);
            if (numItems == 0) {
                isCreateTextViewSet = false;
                createTextView();
            } else {
                try {
                    TextView tv = (TextView) findViewById(0);
                    if (tv.getVisibility() == View.VISIBLE || numItems > 0) {
                        tv.setVisibility(View.GONE);
                        isCreateTextViewSet = false;
                    }
                } catch (NullPointerException e) {
                    Log.d("KENNY", "The text view was null.");
                }
            }

        } catch (IOException | JSONException e) {
            if (!isLicensePopulated) isArrayEmpty = true;
            createTextView();
            e.printStackTrace();
        }
    }

    private void createTextView() {

        if (isCreateTextViewSet) return;

        // Get the linear layout and set the parameters
        LinearLayout ll = (LinearLayout) findViewById(R.id.profile_ll);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        TextView createLicense = new TextView(this);
        createLicense.setId(0);
        createLicense.setText(Html.fromHtml(getResources().getString(R.string.create_license_string)));
        createLicense.setPadding(70, 100, 70, 100);
        createLicense.setTextSize(18);
        ll.addView(createLicense, params);
        isCreateTextViewSet = true;
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
    private void populateLicense(String json, boolean isRestart) throws JSONException {

        //**** Used to get the correct relative layout ****//
        int[] rlIdArray = {
                R.id.rl1,
                R.id.rl2,
                R.id.rl3,
                R.id.rl4,
                R.id.rl5
        };
        //**** Get the correct textview and populate it ****//
        int[][] textViewIdArray = {
                {R.id.name_rl, R.id.name_rl2, R.id.name_rl3, R.id.name_rl4, R.id.name_rl5},
                {R.id.number_rl, R.id.number_rl2, R.id.number_rl3, R.id.number_rl4, R.id.number_rl5},
                {R.id.type_rl, R.id.type_rl2, R.id.type_rl3, R.id.type_rl4, R.id.type_rl5},
                {R.id.issuedate_rl, R.id.issuedate_rl2, R.id.issuedate_rl3, R.id.issuedate_rl4, R.id.issuedate_rl5},
                {R.id.expdate_rl, R.id.expdate_rl2, R.id.expdate_rl3, R.id.expdate_rl4, R.id.expdate_rl5},
                {R.id.birthday_rl, R.id.birthday_rl2, R.id.birthday_rl3, R.id.birthday_rl4, R.id.birthday_rl5},
                {R.id.huntered_rl, R.id.huntered_rl2, R.id.huntered_rl3, R.id.huntered_rl4, R.id.huntered_rl5},
                {R.id.flag_rl, R.id.flag_rl2, R.id.flag_rl3, R.id.flag_rl4, R.id.flag_rl5}
        };
        //**** Populate the state flag the user chose ****//
        int[] flagArray = {
                R.drawable.sc_flag,
                R.drawable.ga_flag,
                R.drawable.fl_flag,
                R.drawable.al_flag,
                R.drawable.ms_flag
        };
        //**** These are oll the options the user can enter ****//
        String[] jsonStrings = {
                "name", "number", "type", "issue_date", "exp_date", "birthday", "huntered", "state"
        };
        numItems = 0;
        JSONObject j = new JSONObject(json);				// Whole JSON String
        JSONObject outer = j.getJSONObject("outermost");	// Outermost JSON
        JSONArray licenseArray = outer.getJSONArray("license");
        if (licenseArray.length() == 0 || licenseArray == null) {
            isLicensePopulated = true;
            isArrayEmpty = true;
        }

        for (int i = 0; i < licenseArray.length(); i++) {
            if (isRestart && !isEdit) i = licenseArray.length()-1;			// Execute this only if you just added a button (so you dont reload all buttons).
            else if (isEdit) {
                isEdit = false;
                break;
            }

            findViewById(rlIdArray[i]).setVisibility(View.VISIBLE);

            JSONObject temp = licenseArray.getJSONObject(i);

            TextView tv;
            for (int n = 0; n < jsonStrings.length; n++) {
                if (n == 7) {   // Only go here if you're populating the FLAG (b/c it's not a textview)
                    ImageView flag = (ImageView) findViewById(textViewIdArray[n][i]);
                    flag.setImageDrawable(getResources().getDrawable(flagArray[Integer.parseInt(temp.getString(jsonStrings[n]))]));
                    continue;
                }
                tv = (TextView) findViewById(textViewIdArray[n][i]);
                tv.setText(temp.getString(jsonStrings[n]));
            }

            ImageView licenseImage = (ImageView) findViewById(imageViewIdArray[i]);
            licenseImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clickedItem(v);
                }
            });

            isArrayEmpty = false;
            isLicensePopulated = true;
            numItems++;			// <<<<------- Lets you know how many items are populated on screen
        }
    }

    /*****************************************************************************************************
     * Handles the onClick of the button/image
     *****************************************************************************************************
     * @param v Holds the clicked view
     *****************************************************************************************************/
    private void clickedItem(View v) {
        for (int i = 0; i < imageViewIdArray.length; i++) {
            if (v.getId() == imageViewIdArray[i]) {
                currentItemID = i;
                break;
            }
        }

        //*******************************************************************************************************
        // Only goes here if they clicked the DELETE button in the actionbar menu.
        //*******************************************************************************************************
        if (isDeletable) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    LicenseActivity.this);
            builder.setMessage(
                    "Are you sure you want to delete this item from the list?")
                    .setTitle("Delete")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    try {
                                        delete(currentItemID);
                                        onBackPressed();
                                        Toast.makeText(getApplicationContext(), "Your item has been deleted!",
                                                Toast.LENGTH_SHORT).show();
                                    } catch (IOException | JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();
            isDeletable = false;
        }
        //*******************************************************************************************************
        // Go here if you just want to click the item, see info that's stored, and possibly add to it.
        //*******************************************************************************************************
        else {
            isEdit = true;
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LicenseAddActivity.class);
            intent.putExtra("title", "License");
            intent.putExtra("isPopulated", isLicensePopulated);
            intent.putExtra("isArrayEmpty", isArrayEmpty);
            intent.putExtra("isEdit", isEdit);
            intent.putExtra("currentItemID", currentItemID);
            intent.putExtra("isLicense", true);
            startActivity(intent);
        }
    }

    /*****************************************************************************************************************************************
     * DELETES the current item that you clicked on.
     *****************************************************************************************************************************************
     * @param current Holds the current id of item to delete.
     * @throws IOException
     * @throws JSONException
     *****************************************************************************************************************************************/
    @SuppressLint("NewApi")
    private void delete(int current) throws IOException, JSONException {
        JsonStorage jsonStorage = new JsonStorage(getApplicationContext()); // Call the function in other class
        String json = jsonStorage.readProfileJSON(); 				// Store the JSON into string
        JSONObject j = new JSONObject(json);				// Whole JSON String
        JSONObject outer = j.getJSONObject("outermost");	// Outermost JSON
        JSONArray licenseArray = outer.getJSONArray("license");

        licenseArray.remove(current); 					// Removes selected item.
        outer.put("license", licenseArray); 			// Add this array to the object

        JSONObject outerMost = new JSONObject(); 		// Create the outermost object
        try {
            outerMost.put("outermost", outer); 			// Add the newly created object to this outermost object
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonStorage.writeJSON(outerMost); 				// Now write this new JSON to the text file to save it.
    }
}
