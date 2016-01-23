package com.starwood.anglerslong;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class LogActivity extends ActionBarActivity {

    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 1888;
    private LruCache<String, Bitmap> mMemoryCache;

    private Bitmap yourSelectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);

        Bundle bundle = this.getIntent().getExtras();
        getSupportActionBar().setTitle(bundle.getString("title"));
        String speciesName = bundle.getString("speciesName");
        String currDate = bundle.getString("currDate");

        EditText species = (EditText) findViewById(R.id.species);
        species.setText(speciesName);
        EditText date = (EditText) findViewById(R.id.date);
        date.setText(currDate);

        /****************************/
        /****** Take A Picture ******/
        /****************************/
        ImageView takePic = (ImageView) findViewById(R.id.take_pic);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoTakerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoTakerIntent, CAMERA_REQUEST);
            }
        });

        /****************************/
        /***** Choose A Picture *****/
        /****************************/
        ImageView choosePic = (ImageView) findViewById(R.id.choose_pic);
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });


        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    /*********************************************************
     * Creates the action bar menu
     *********************************************************/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_log, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save) {
            try {
                saveLog();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*********************************************************************************
     *          SAVE THIS LOG
     *********************************************************************************/
    private void saveLog() {
        new SaveLogTask().execute();
    }






    /********************************************************************************************************************
     ********************************************************************************************************************
     ********************************************************************************************************************
     **************************** Functions used for getting the images *************************************************
     ********************************************************************************************************************
     ********************************************************************************************************************
     ********************************************************************************************************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        /**** User chose a picture ****/
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                        int width = yourSelectedImage.getWidth();
                        int height = yourSelectedImage.getHeight();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "The InputStream didn't work...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            /**** User took a picture ****/
            case CAMERA_REQUEST:
                if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                    yourSelectedImage = (Bitmap) imageReturnedIntent.getExtras().get("data");
                }
                break;
        }

        /**** Remember to set the ImageView to the chosen/taken photo ****/
        ImageView customImage = (ImageView) findViewById(R.id.custom_image);
        customImage.setImageBitmap(yourSelectedImage);
        customImage.setVisibility(View.VISIBLE);
        customImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First convert bitmap to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap tempBitmap = yourSelectedImage;
                tempBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // Then pass that byte array
                Intent intent = new Intent(getApplicationContext(), Popup.class);
                intent.putExtra("log_image", byteArray);
                startActivity(intent);
            }
        });
    }

    /*****************************************************************************************
     *          Add Bitmap to Memory
     *****************************************************************************************
     * @param key
     * @param bitmap
     *****************************************************************************************/
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /*****************************************************************************************
     *          Retrieve Bitmap from Memory
     *****************************************************************************************
     * @param key
     *****************************************************************************************/
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }








    private class SaveLogTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd;
        JSONObject logObject;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(LogActivity.this, "Retrieving Your Drinks", "Please Wait...", true, false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("LogSaveAsyncTask", "Begin AsyncTask");
            try {
                generateJSONToSave();
            } catch (JSONException | IOException e) {
                Log.d("LogSaveAsyncTask", "generateJSONToSave() didnt work...");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            saveLogJSON();
            Log.d("LogSaveAsyncTask", "generateJSONToSave() worked!!!!");

            Toast.makeText(getApplicationContext(), "The InputStream didn't work...",
                    Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

        private void generateJSONToSave() throws IOException, JSONException {

            //=======================================================
            // 1) Retrieve all saved information
            //=======================================================
            TextView text = (TextView) findViewById(R.id.species);
            String species = text.getText().toString();
            text = (TextView) findViewById(R.id.date);
            String date = text.getText().toString();
            text = (TextView) findViewById(R.id.location);
            String location = text.getText().toString();
            text = (TextView) findViewById(R.id.bait);
            String bait = text.getText().toString();
            text = (TextView) findViewById(R.id.length);
            String length = text.getText().toString();
            text = (TextView) findViewById(R.id.notes);
            String notes = text.getText().toString();

            String image;
            if (yourSelectedImage != null) {
                try {
                    PictureStorage pictureStorage = new PictureStorage(getApplicationContext());
                    image = pictureStorage.saveToInternalSorage(getApplicationContext(), yourSelectedImage);
                } catch (Exception e) {
                    image = "no";
                    e.printStackTrace();
                }
            } else {
                image = "no";
            }

            //=======================================================
            // 2) Create a JSON Object with the info
            //=======================================================
            logObject = new JSONObject();
            logObject.put("image", image);
            logObject.put("species", species);
            logObject.put("date", date);
            logObject.put("location", location);
            logObject.put("bait", bait);
            logObject.put("length", length);
            logObject.put("notes", notes);
        }

        private void saveLogJSON() {

            //=======================================================
            // 3) Find out if this is the first log or not and save accordingly
            //=======================================================
            JsonStorage json = new JsonStorage(getApplicationContext());
            boolean isOutermostThere = false;
            try {
                String logJSON = json.readProfileJSON();
                JSONObject object = new JSONObject(logJSON);
                JSONObject outer = object.getJSONObject("outermost");       // Get outermost json object (everything)
                isOutermostThere = true;                                    // There is a JSON object already made
                JSONArray logs = outer.getJSONArray("log");                 // Get all the previous logs
                logs.put(logObject);                                        // Add this new log to the array
                outer.put("log", logs);                                     // Overwrite the log list with new one
                object.put("outermost", outer);                             // Add outermost to the JSON object
                json.writeJSON(object);                                     // Populate this in the memory
                Toast.makeText(getApplicationContext(), "Nice Catch!  Your log has been saved.",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException | JSONException e) {
                //********* Go here if the array did not already exist ***********//
                JSONArray newLogList = new JSONArray();                     // Create new JSONArray
                try {
                    newLogList.put(logObject);                                  // Add the first entry using unique image string as index
                    if (isOutermostThere) {                                     // If there is a JSONObject already made
                        String favoriteJson = json.readProfileJSON();           // Load the populated data
                        JSONObject object = new JSONObject(favoriteJson);       // Get the whole json object
                        JSONObject outer = object.getJSONObject("outermost");   // Get outermost object
                        outer.put("log", newLogList);                           // Add new favorites array to the profile object
                        object.put("outermost", outer);
                        json.writeJSON(object);                                 // Write this to the memory
                    } else {
                        JSONArray logs = new JSONArray();               // Create new favorites object
                        logs.put(newLogList);                           // Add favorites list to favorites JSON object
                        JSONObject outer = new JSONObject();            // Create new outermost JSON since it doesnt exist
                        outer.put("outermost", logs);                   // Store new favorites list into it.
                        json.writeJSON(outer);
                    }
                    Toast.makeText(getApplicationContext(), "Nice Catch!  Your first log has been saved!",
                            Toast.LENGTH_SHORT).show();
                } catch (IOException | JSONException e1) {
                    Toast.makeText(getApplicationContext(), "Something went wrong saving your log.  Please try again later.",
                            Toast.LENGTH_SHORT).show();
                    e1.printStackTrace();
                }
            }
        }
    }


}
