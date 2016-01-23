package com.starwood.anglerslong;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by kennystreit on 1/27/15.
 */
public class JsonStorage {

    private String mJson;
    private Context mContext;

    public JsonStorage(Context context) {
        mJson = "";
        this.mContext = context;
    }

    public JsonStorage(String json, Context context) {
        this.mJson = json;
        this.mContext = context;
    }

    /***************************************************************************************
     * Returns a string of the whole JSON.
     ***************************************************************************************/
    public String loadSpeciesJSON() throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(mContext.getAssets().open(
                    "species_json.txt")));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); // stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();

    }

    /******************************************************************************************************************************************
     * Loads the json array into string form
     * TODO: GET RID OF THIS METHOD!!!!!!!!!!!!!!!!!!!!!!!!
     ******************************************************************************************************************************************
     * @return The full string of the json array
     * @throws IOException
     * @throws JSONException
     ******************************************************************************************************************************************/
    public String loadTabs() throws IOException, JSONException {
        StringBuffer sb = new StringBuffer();
        FileInputStream fis = mContext.openFileInput("profile.txt");

        byte[] inputBuffer = new byte[fis.available()];
        fis.read(inputBuffer);
        String contents = new String(inputBuffer);
        contents = contents.trim();

        String[] split = contents.split("##;");
        for (String s : split) {
            if (!s.equals(""))
                sb.append(s);
        }

        fis.close();
        return sb.toString();
    }

    /*******************************************************************************************************************************
     * Reads the json array into string form
     *******************************************************************************************************************************
     * @return The full string of the json array
     * @throws IOException
     *******************************************************************************************************************************/
    public String readProfileJSON() throws IOException {
        StringBuffer sb = new StringBuffer();
        FileInputStream fis = mContext.openFileInput("profile.txt");

        byte[] inputBuffer = new byte[fis.available()];
        fis.read(inputBuffer);
        String contents = new String(inputBuffer);
        contents = contents.trim();

        String[] split = contents.split("##;");
        for (String s : split) {
            if (!s.equals(""))
                sb.append(s);
        }

        fis.close();
        return sb.toString();
    }

    /*********************************************************************************************************************************
     * This will write the actual object to the text file.
     *********************************************************************************************************************************
     * @param outer It's the outermost json object that encompasses all tabs
     *********************************************************************************************************************************/
    public void writeJSON(JSONObject outer) {

        FileOutputStream fos;
        try {
            fos = mContext.openFileOutput("profile.txt", Context.MODE_PRIVATE);

            fos.write(outer.toString().getBytes());

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to read the coordinates saved in text file
     * Can be to populate spots on the map.
     * @return The full string of the json coordinates
     * @throws IOException
     */
    public String readCoordinates() throws IOException {

        StringBuffer sb = new StringBuffer();
        FileInputStream fis = mContext.openFileInput("coordinates.txt");

        byte[] inputBuffer = new byte[fis.available()];
        fis.read(inputBuffer);
        String contents = new String(inputBuffer);
        contents = contents.trim();

        String[] split = contents.split("##;");
        for (String s : split) {
            if (!s.equals(""))
                sb.append(s);
        }

        fis.close();
        return sb.toString();
    }

    /**
     * Used to store the coordinates from a fishing trip to retrieve later.
     * @param coord Holds the JSONObject that has all of the coordinates
     */
    public void writeCoordinates(JSONObject coord) {

        FileOutputStream fos;
        try {
            fos = mContext.openFileOutput("coordinates.txt", Context.MODE_PRIVATE);

            fos.write(coord.toString().getBytes());

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void populateItem(String json, String jsonItem,
                              ArrayList<ArrayList<String>> jsonArray,
                              ArrayList<String> photosList) throws JSONException {
        JSONObject j = new JSONObject(json);				// Whole JSON String
        JSONObject outer = j.getJSONObject("species");	    // Outermost JSON object
        JSONArray jArray = outer.getJSONArray(jsonItem);	// Array of that item

        photosList.clear();
        jsonArray.clear();

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject temp = jArray.getJSONObject(i);
            ArrayList<String> tempArray = new ArrayList<String>();

            String image = temp.getString("image");
            photosList.add(image);

            String type = temp.getString("type");
            tempArray.add(type);
            String scientific = temp.getString("scientific");
            tempArray.add(scientific);
            String common = temp.getString("common");
            tempArray.add(common);
            String individual = temp.getString("individual");
            tempArray.add(individual);
            String aggregate = temp.getString("aggregate");
            tempArray.add(aggregate);
            String minimum = temp.getString("minimum");
            tempArray.add(minimum);
            String season = temp.getString("season");
            tempArray.add(season);
            String record = temp.getString("record");
            tempArray.add(record);

            jsonArray.add(tempArray);
        }
    }

}
