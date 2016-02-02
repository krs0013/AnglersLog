package com.starwood.anglerslong;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by kennystreit on 1/29/16.
 */
public class FishingRegulationsActivity extends ActionBarActivity {

    private WebView webView;
    private String webUrl;

    public static final String scUrl = "www.dnr.sc.gov/regs/saltwaterregs.html";
    public static final String gaUrl = "www.eregulations.com/georgia/fishing/georgia-saltwater-fish/";
    public static final String flUrl = "m.myfwc.com/fishing/saltwater/recreational/";
    public static final String alUrl = "www.outdooralabama.com/regulations-and-enforcement";
    public static final String msUrl = "https://www.mdwfp.com/law-enforcement/fishing-rules-regs.aspx";

    /*

            TO CALL THIS ACTIVITY:

    //                        Intent intent = new Intent(getApplicationContext(), FishingRegulatoryPagerActivity.class);
    //                        intent.putExtra("url", alUrl);
    //                        startActivity(intent);


     */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fishing_regulations);

        webUrl = this.getIntent().getStringExtra("url");

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webview = (WebView) v;
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webview.canGoBack()) {
                                webview.goBack();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });
        webView.loadUrl(webUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_fishing_reg, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

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

    public void updateUrl(String url) {
        webUrl = url;

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(webUrl);
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webview = (WebView) v;
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webview.canGoBack()) {
                                webview.goBack();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });
        Log.d("KENNY", "Current URL: " + webUrl);
        webView.loadUrl(url);
    }
}
