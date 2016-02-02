package com.starwood.anglerslong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kennystreit on 1/22/16.
 */
public class LogListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<LogListItem> logItemArray = new ArrayList<>();

    public LogListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        setLogItemArray();

        // specify an adapter (see also next example)
        mAdapter = new LogListAdapter(getActivity(), logItemArray);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void setLogItemArray() {

        JsonStorage json = new JsonStorage(getActivity());
        boolean isOutermostThere = false;
        try {
            String logJSON = json.readProfileJSON();
            JSONObject object = new JSONObject(logJSON);
            JSONObject outer = object.getJSONObject("outermost");       // Get outermost json object (everything)
            isOutermostThere = true;                                    // There is a JSON object already made
            JSONArray logs = outer.getJSONArray("logs");                 // Get all the previous logs
            for (int i = 0; i < logs.length(); i++) {
                JSONObject eachLog = logs.getJSONObject(i);
                LogListItem logItem = new LogListItem();
                logItem.setImage(eachLog.getString("image"));
                logItem.setSpecies(eachLog.getString("species"));
                logItem.setDate(eachLog.getString("date"));
                logItem.setLocation(eachLog.getString("location"));
                logItem.setLength(eachLog.getString("length"));
                logItem.setBait(eachLog.getString("bait"));
                logItem.setNotes(eachLog.getString("notes"));
                logItemArray.add(logItem);
            }
        } catch (IOException | JSONException e) {

            Toast.makeText(getActivity(), "You have not added any Logs...",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
    }
}
