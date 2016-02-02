package com.starwood.anglerslong;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

    public static final String scUrl = "http://www.dnr.sc.gov/regs/fishing.html";
    public static final String gaUrl = "http://www.eregulations.com/georgia/fishing/";
    public static final String flUrl = "http://www.eregulations.com/florida/fishing/saltwater/";
    public static final String alUrl = "http://www.outdooralabama.com/regulations-and-enforcement";
    public static final String msUrl = "https://www.mdwfp.com/law-enforcement/fishing-rules-regs.aspx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Sets the Drawer List */
        mOptionMenu = new String[] {
                "Home",
                "Favorites",
                "My Map",
                "Fishing Regulations",
                "Log",
                "Fishing License",
                "Checklist",
                "Share Our App"
        };

        /* Sets images next to Drawer List Text */
        mImagesMenu = new int[] {
                R.drawable.home_icon,
                R.drawable.favorites_heart,
                R.drawable.map_icon,
                R.drawable.fishing_regulations_icon,
                R.drawable.log_icon,
                R.drawable.license_icon,
                R.drawable.manage_bar_check,
                R.drawable.share_app_icon
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
                    case 0:
                        mFragment = new MainFragment();
                        break;
                    case 1:
                        Intent favIntent = new Intent(getApplicationContext(), FavoriteActivity.class);
                        favIntent.putExtra("abtitle", "Log");
                        startActivity(favIntent);
                        break;
                    case 2:
                        break;
                    case 3:
                        displayDialog();
                        break;
                    case 4:
                        mFragment = new LogListFragment();
                        break;
                    case 5:
                        Intent licenseIntent = new Intent(getApplicationContext(), LicenseActivity.class);
                        licenseIntent.putExtra("abtitle", "Fishing License");
                        startActivity(licenseIntent);
                        break;
                    case 6:
                        Intent checklistIntent = new Intent(getApplicationContext(), Checklist.class);
                        checklistIntent.putExtra("abtitle", "Checklist");
                        startActivity(checklistIntent);
                        break;
                    case 7:
                        shareOurApp();
                        break;
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, mFragment);
                transaction.addToBackStack(null);
                mDrawerList.setItemChecked(position, true);

                mTitleSection = mOptionMenu[position];
                getSupportActionBar().setTitle(mTitleSection);

                mDrawerLayout.closeDrawer(mDrawerRelativeLayout);

                transaction.commit();
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
        MenuInflater inflater = getMenuInflater();

        /**** Inflate menu to add items to the action bar if it is present ****/
        if (mFragment.getClass().equals(FishingRegulationsFragment.class)) {
            inflater.inflate(R.menu.menu_fishing_reg, menu);
            Log.d("KENNY", "Selected Webview Filter");
        } else {
            inflater.inflate(R.menu.menu_main, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.south_carolina:
                FishingRegulationsFragment fragmentSC =
                        (FishingRegulationsFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                fragmentSC.updateUrl(scUrl);
                break;
            case R.id.georgia:
                FishingRegulationsFragment fragmentGA =
                        (FishingRegulationsFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                fragmentGA.updateUrl(gaUrl);
                break;
            case R.id.florida:
                FishingRegulationsFragment fragmentFL =
                        (FishingRegulationsFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                fragmentFL.updateUrl(flUrl);
                break;
            case R.id.alabama:
                FishingRegulationsFragment fragmentAL =
                        (FishingRegulationsFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                fragmentAL.updateUrl(alUrl);
                break;
            case R.id.mississippi:
                FishingRegulationsFragment fragmentMS =
                        (FishingRegulationsFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                fragmentMS.updateUrl(msUrl);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ask the user if they want to exit before exiting
     */
    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (mFragment instanceof SpeciesFragment) {
                if (findViewById(R.id.single_photo).getVisibility() == View.VISIBLE)
                    findViewById(R.id.single_photo).setVisibility(View.GONE);
                else
                    getSupportFragmentManager().popBackStackImmediate();
            } else {
                getSupportFragmentManager().popBackStackImmediate();
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
     * Dialog to show fishing regulations
     *****************************************************************************************/
    private void displayDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fishing_reg_dialog);
        dialog.setTitle("Select a State:");

        dialog.findViewById(R.id.sc_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scUrl));
                startActivity(browserIntent);
            }
        });

        dialog.findViewById(R.id.ga_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gaUrl));
                startActivity(browserIntent);
            }
        });

        dialog.findViewById(R.id.fl_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(flUrl));
                startActivity(browserIntent);
            }
        });

        dialog.findViewById(R.id.al_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(alUrl));
                startActivity(browserIntent);
            }
        });

        dialog.findViewById(R.id.ms_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(msUrl));
                startActivity(browserIntent);
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /*****************************************************************************************
     * Creates a new fragment to be loaded
     *****************************************************************************************
     * @param newFrag New Fragment to switch to
     * @param fragTitle Title of the fragment actionbar
     *****************************************************************************************/
    public void createFragment(Fragment newFrag, String fragTitle) {

        try {
            Bundle bundle = new Bundle();
            bundle.putString("type", fragTitle);
            newFrag.setArguments(bundle);
        } catch (IllegalStateException i) {
            Log.d("KENNY", "IllegalStateException: Fragment already active.");
            i.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, newFrag);
        transaction.addToBackStack(null);

        mFragment = newFrag;

        transaction.commit();
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
