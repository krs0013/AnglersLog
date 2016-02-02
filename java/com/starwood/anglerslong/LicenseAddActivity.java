package com.starwood.anglerslong;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kennystreit on 1/23/16.
 */
public class LicenseAddActivity extends ActionBarActivity implements OnItemSelectedListener {

    private boolean isPopulated;
    private boolean isArrayEmpty;
    private boolean isEdit = false;
    private int currentItemID;
    private boolean hasEmptyField = false;
    private int currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();

        setContentView(R.layout.license_add);

        Spinner s = (Spinner) findViewById(R.id.spinner);
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.state_array));
        s.setAdapter(adapter);

        s.setOnItemSelectedListener(this);

        isPopulated = bundle.getBoolean("isPopulated");
        isArrayEmpty = bundle.getBoolean("isArrayEmpty");
        if (bundle.containsKey("isEdit"))
            isEdit = bundle.getBoolean("isEdit");
        if (bundle.containsKey("currentItemID"))
            currentItemID = bundle.getInt("currentItemID");

        getSupportActionBar().setTitle(bundle.getString("title"));
//        getSupportActionBar().setSubtitle(bundle.getString("subtitle"));


        if (isEdit) {
            try {
                JsonStorage jsonStorage = new JsonStorage(getApplicationContext());
                String json = jsonStorage.readProfileJSON(); 							// Store the JSON into string
                JSONObject j = new JSONObject(json);
                JSONObject outer = j.getJSONObject("outermost"); 	// Outermost JSON
                JSONArray innerArray = outer.getJSONArray("license");
                JSONObject inner = innerArray.getJSONObject(currentItemID);

                Spinner editSpinner = (Spinner) findViewById(R.id.spinner);
                editSpinner.setSelection(inner.getInt("state") + 1);        // Set spinner

                EditText edit = (EditText) findViewById(R.id.license_name);
                edit.setText(inner.getString("name"));
                edit = (EditText) findViewById(R.id.license_number);
                edit.setText(inner.getString("number"));
                edit = (EditText) findViewById(R.id.license_type);
                edit.setText(inner.getString("type"));
                edit = (EditText) findViewById(R.id.license_issue_date);
                edit.setText(inner.getString("issue_date"));
                edit = (EditText) findViewById(R.id.license_expiration_date);
                edit.setText(inner.getString("exp_date"));
                edit = (EditText) findViewById(R.id.license_birthday);
                edit.setText(inner.getString("birthday"));
                edit = (EditText) findViewById(R.id.license_huntered);
                edit.setText(inner.getString("huntered"));

            } catch (JSONException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /***************************************************************************************
     * Tells app what to do when an item is selected.
     ***************************************************************************************/
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        if (pos > 0) {
            currentState = pos - 1;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        setTitle("");
    }

    /*********************************************************
     * Creates the action bar menu
     *********************************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*********************************************************
     * Gives functionality to the action bar
     *********************************************************/
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                try {
                    readLicense(isPopulated, isArrayEmpty);

                    if (!hasEmptyField) onBackPressed();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "**Error** Your item was not saved.",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /******************************************************************************
     * Reads the text file and looks to see if it is empty or not.  If it is
     * empty, it will write all new items to it.  If it's not empty, it will
     * add any info to the object.
     ******************************************************************************
     * @param isPop Says if it is populated or not
     * @param isEmpty Says if the object exist, but the array is empty
     * @throws IOException
     * @throws JSONException
     ******************************************************************************/
    private void readLicense(boolean isPop, boolean isEmpty) throws IOException, JSONException {

        String[] states = {"South Carolina", "Georgia", "Florida", "Alabama", "Mississippi"};

        String name = ((EditText) findViewById(R.id.license_name)).getText().toString();
        String number = ((EditText) findViewById(R.id.license_number)).getText()
                .toString();
        String type = ((EditText) findViewById(R.id.license_type))
                .getText().toString();
        String issueDate = ((EditText) findViewById(R.id.license_issue_date))
                .getText().toString();
        String expDate = ((EditText) findViewById(R.id.license_expiration_date)).getText()
                .toString();
        String birthday = ((EditText) findViewById(R.id.license_birthday)).getText()
                .toString();
        String huntered = ((EditText) findViewById(R.id.license_huntered)).getText()
                .toString();

        // These are required fields, if left blank don't continue!
        if (name.equals("")) {
            Toast.makeText(getApplicationContext(), "**Error** One or more of the required fields was left blank.",
                    Toast.LENGTH_SHORT).show();
            hasEmptyField = true;
            return;
        }

        //=========================================================================
        // STEP 1: Create all of the smaller objects as part of this object
        //=========================================================================
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        //obj.put("image", image);
        obj.put("number", "License No: " + number);
        obj.put("type", "License Type: " + type);
        obj.put("issue_date", "Issued: " + issueDate);
        obj.put("exp_date", "Expiration: " + expDate);
        obj.put("birthday", "Date of Birth: " + birthday);
        obj.put("huntered", "Hunter Ed. No. " + huntered);
        obj.put("state", Integer.toString(currentState));

        //=========================================================================
        // STEP 2: Create the individual JSON object and add it to the outermost
        //=========================================================================
        JSONObject outerMost;
        JsonStorage jsonStorage = new JsonStorage(getApplicationContext());
        if (!isEmpty) {
            String json = jsonStorage.readProfileJSON();
            JSONObject wholeJSON = new JSONObject(json);				// Whole JSON
            JSONObject outerLicense = wholeJSON.getJSONObject("outermost");	// Outermost JSON

            JSONArray addArray;

            // If the JSON object has been created, grab that array.
            // If it has not, create a new one and start from there.
            if (isPop) addArray = outerLicense.getJSONArray("license");
            else addArray = new JSONArray();
            // If you are editing currently existing data, go in the if
            // If you're creating an original entry, go in the else.
            if (isEdit) addArray.put(currentItemID, obj);
            else addArray.put(obj);

            JSONObject outer = new JSONObject();
            outer.put("license", addArray);
            outerMost = new JSONObject();

            try {
                JSONObject profile = outerLicense.getJSONObject("profile");
                outer.put("profile", profile);
            } catch (Exception e) {
                hasEmptyField = true;
            }
            outerMost.put("outermost", outer);

        } else {
            //******* If it's empty, you don't care, just make a new one *******//
            JSONArray addArray = new JSONArray();
            addArray.put(obj);

            JSONObject outer = new JSONObject();
            outer.put("license", addArray);
            outerMost = new JSONObject();

            //******* Then add the profile if there is one ********//
            try {
                String json = jsonStorage.readProfileJSON();
                JSONObject wholeJSON = new JSONObject(json);
                JSONObject outerProfile = wholeJSON.getJSONObject("outermost");    // Outermost JSON
                JSONObject profile = outerProfile.getJSONObject("profile");

                outer.put("profile", profile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            outerMost.put("outermost", outer);
        }

        //=========================================================================
        // STEP 3: Write it to the text file
        //=========================================================================
        write(outerMost);

        // Show this toast only if it made it through everything.
        Toast.makeText(getApplicationContext(), "Your license has been saved!",
                Toast.LENGTH_SHORT).show();
        hasEmptyField = false;
    }

    /******************************************************************************
     * This will write the actual object to the text file.
     *
     * @param outer It's the outermost json object that encompasses all tabs
     ******************************************************************************/
    private void write(JSONObject outer) {

        FileOutputStream fos;
        try {
            fos = openFileOutput("profile.txt", Context.MODE_PRIVATE);

            fos.write(outer.toString().getBytes());

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
