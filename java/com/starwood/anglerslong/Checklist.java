package com.starwood.anglerslong;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Checklist extends List {
    private ArrayList<ChecklistItem> list;
    ListView lv;
    ChecklistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);

        Bundle bundle = this.getIntent().getExtras();
        getSupportActionBar().setTitle(bundle.getString("abtitle"));

        lv = getListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_checklist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.checklist_dialog);
                dialog.setTitle("Add Item");

                final EditText edit = (EditText) dialog.findViewById(R.id.name);
                dialog.findViewById(R.id.add).setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if (edit.getText() != null) {
                                    // create new item from dialog text
                                    if (edit.getText().toString() != "") {
                                        list.add(new ChecklistItem(edit.getText()
                                                .toString(), false));
                                        try {
                                            writeObject(getApplicationContext(),
                                                    "checklist", list);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        adapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    } else
                                        Toast.makeText(getApplicationContext(),
                                                "Enter an item name",
                                                Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // write to cache
        try {
            writeObject(getApplicationContext(), "checklist", list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // read from cache and create list
        try {
            list = readObject(getApplicationContext(), "checklist");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();

            list = new ArrayList<ChecklistItem>();
            list.add(new ChecklistItem("Primary Rods and Reels", false));
            list.add(new ChecklistItem("Secondary Rods and Reels", false));
            list.add(new ChecklistItem("Hooks, Weights, Swivels, Floats", false));
            list.add(new ChecklistItem("Fishing License", false));
            list.add(new ChecklistItem("Food and Water", false));
            list.add(new ChecklistItem("Knife", false));
            list.add(new ChecklistItem("Fuel", false));
            list.add(new ChecklistItem("Bait", false));
            list.add(new ChecklistItem("Ice", false));
            list.add(new ChecklistItem("Scale and Tape Measure", false));
            list.add(new ChecklistItem("Tackle Box", false));
            list.add(new ChecklistItem("Pliers", false));
            list.add(new ChecklistItem("Net", false));
            list.add(new ChecklistItem("Gut Belt", false));
            list.add(new ChecklistItem("Gloves", false));
            list.add(new ChecklistItem("Towels", false));
            list.add(new ChecklistItem("Dry Bag and Ziploc Bags", false));
            list.add(new ChecklistItem("Tool Kit", false));
            list.add(new ChecklistItem("First Aid Kit", false));
            list.add(new ChecklistItem("Medication", false));
            list.add(new ChecklistItem("Duct Tape", false));
            list.add(new ChecklistItem("Lighter", false));
            list.add(new ChecklistItem("Spare Portable GPS", false));
            list.add(new ChecklistItem("Paper Map", false));
            list.add(new ChecklistItem("Rope", false));
            list.add(new ChecklistItem("Radio", false));
            list.add(new ChecklistItem("Sunscreen", false));
            list.add(new ChecklistItem("Sunglasses and/or Hat", false));
            list.add(new ChecklistItem("Flashlight and Spare Batteries", false));
            list.add(new ChecklistItem("Swimsuit", false));
            list.add(new ChecklistItem("Camera", false));
        }

        // set adapter to listview
        adapter = new ChecklistAdapter(getApplicationContext(), list);
        setListAdapter(adapter);
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int arg2, long arg3) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(30);      // Vibrate for 50 milliseconds
                showDeleteDialog(arg2);
                return false;
            }
        });
    }

    public void writeObject(Context context, String key,
                            ArrayList<ChecklistItem> object) throws IOException {
        FileOutputStream fos = context
                .openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public ArrayList<ChecklistItem> readObject(Context context, String key)
            throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        @SuppressWarnings("unchecked")
        ArrayList<ChecklistItem> object = (ArrayList<ChecklistItem>) ois
                .readObject();
        return object;
    }

    public void showDeleteDialog(final int pos) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.checklist_delete_dialog);
        dialog.setTitle(list.get(pos).getName());
        dialog.findViewById(R.id.cancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.delete).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        list.remove(pos);
                        adapter.notifyDataSetChanged();
                        try {
                            writeObject(getApplicationContext(), "checklist",
                                    list);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }
}
