package com.starwood.anglerslong;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private String[] mOptionMenu;
    private int[] mImagesMenu;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerRelativeLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitleSection;
    private CharSequence mTitleApp;
    private Fragment mFragment = null;

    private ArrayList<Fragment> backstack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Sets the Drawer List */
        mOptionMenu = new String[] {
                "Fishing Regulations",
                "Favorites",
                "Log",
                "Checklist",
                "My Profile",
                "Save a License",
                "My Map",
                "Share Our App"
        };

        /* Sets images next to Drawer List Text */
        mImagesMenu = new int[] {
                R.drawable.home_icon,
                R.drawable.favorites_heart,
                R.drawable.my_drinks_icon,
                R.drawable.manage_bar_check,
                R.drawable.share_app_icon,
                R.drawable.feedback_icon,
                R.drawable.home_icon,
                R.drawable.favorites_heart
        };

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerRelativeLayout = (RelativeLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.list_view_drawer);
        mDrawerList.setAdapter(new DrawerListViewAdapter(this, mOptionMenu, mImagesMenu));

        initContentWithFirstFragment();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
//                    case 0:
//                        mFragment = new MainFragment(drinkList);
//                        break;
                    case 1:
                        Intent favIntent = new Intent(getApplicationContext(), FavoriteActivity.class);
                        favIntent.putExtra("abtitle", "Log");
                        startActivity(favIntent);
                        break;
                    case 2:
                        mFragment = new LogListFragment();
                        createFragment(mFragment, "Log");
                        break;
//                    case 3:
//                        mFragment = new LogListFragment();
//                        createFragment(mFragment, "Log");
//                        break;
                    case 4:
                        shareOurApp();
                        break;
//                    case 5:
//                        mFragment = new SuggestionFragment();
//                        break;
                }

//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, mFragment).commit();
//                mDrawerList.setItemChecked(position, true);
//
//                mTitleSection = mOptionMenu[position];
//                getSupportActionBar().setTitle(mTitleSection);
//
//                mDrawerLayout.closeDrawer(mDrawerRelativeLayout);
            }
        });

        mDrawerList.setItemChecked(0, true);
        mTitleSection = "Home";
        mTitleApp = getTitle();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitleSection);
                ActivityCompat.invalidateOptionsMenu(MainActivity.this);
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.app_name);
                ActivityCompat.invalidateOptionsMenu(MainActivity.this);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /* If the user toggles the drawer, don't do anything else */
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Ask the user if they want to exit before exiting
     */
    @Override
    public void onBackPressed() {

        if (backstack != null && backstack.size() > 0) {
            Fragment tempFrag = backstack.get(backstack.size() - 1);

            if (tempFrag instanceof SpeciesFragment) {
                if (findViewById(R.id.single_photo).getVisibility() == View.VISIBLE)
                    findViewById(R.id.single_photo).setVisibility(View.GONE);
                else {
                    createFragment(tempFrag, "Anglers Log");
                    popStack();
                }
            } else {
                createFragment(tempFrag, "Anglers Log");
                popStack();
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

            alertDialog.setTitle(R.string.app_name);
            alertDialog.setMessage("Are you sure you want to exit?");

            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.setNegativeButton("No", null);

            alertDialog.show();
        }
    }

    /**
     * Share the app through some medium
     */
    private void shareOurApp() {
        String appURL = "https://play.google.com/store/apps/details?id=com.starwood.budgetbartender";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_SUBJECT, "Check out the Budget Bartender app!");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Check out Budget Bartender of the Google play store here: "
                        + appURL);

        startActivity(Intent.createChooser(intent, "Share our app using: "));
    }

    /*****************************************************************************************
     * Creates a new fragment to be loaded
     *****************************************************************************************
     * @param newFrag New Fragment to switch to
     * @param fragTitle Title of the fragment actionbar
     *****************************************************************************************/
    public void createFragment(Fragment newFrag, String fragTitle) {

        Bundle bundle = new Bundle();
        bundle.putString("type", fragTitle);
        newFrag.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, newFrag);
        transaction.addToBackStack(null);
        transaction.commit();

        mFragment = newFrag;
        addToStack(newFrag);
    }

    /*****************************************************************************************
     * Pop the stack of fragments
     *****************************************************************************************/
    public void popStack() {
        backstack.remove(backstack.size()-1);
    }

    /*****************************************************************************************
     * Pop the stack of fragments
     *****************************************************************************************/
    public void addToStack(Fragment addFrag) {
        backstack.add(addFrag);
    }










    /******************************************************************************************
     ******************************************************************************************
     *          Drawer Toggle Functions
     ******************************************************************************************
     *******************************************************************************************/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void initContentWithFirstFragment() {
        mTitleSection = "Home";
        getSupportActionBar().setTitle(mTitleSection);
        mFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, mFragment).commit();
    }
}
