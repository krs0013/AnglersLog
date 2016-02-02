package com.starwood.anglerslong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by kennystreit on 9/7/15.
 */
public class SouthCarolinaFishRegFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "bitcoin_";

    private WebView fishregWebview;
    private boolean isWebViewAvailable;
    private static String currUrl;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SouthCarolinaFishRegFragment newInstance(int sectionNumber, String title, String currUrl) {
        SouthCarolinaFishRegFragment fragment = new SouthCarolinaFishRegFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("sectionTitle", title);
        args.putString("url", currUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public SouthCarolinaFishRegFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fishing_reg_webview, container, false);
        if (fishregWebview != null)
            fishregWebview.destroy();

        currUrl = this.getArguments().getString("url");

        fishregWebview = (WebView) rootView.findViewById(R.id.fishing_reg_webview);
        fishregWebview.setVerticalScrollBarEnabled(false);
        fishregWebview.setHorizontalScrollBarEnabled(false);
        fishregWebview.getSettings().setJavaScriptEnabled(true);
        fishregWebview.getSettings().setDomStorageEnabled(true);
        fishregWebview.getSettings().setSupportZoom(true);
        fishregWebview.getSettings().setBuiltInZoomControls(true);
        fishregWebview.getSettings().setDisplayZoomControls(false);
//        fishregWebview.getSettings().setLoadWithOverviewMode(wideView);
//        fishregWebview.getSettings().setUseWideViewPort(wideView);
        fishregWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url2) {
                view.loadUrl(url2);
                return true;
            }
        });
        fishregWebview.loadUrl(currUrl);

        return rootView;
    }

    @Override
    public void onResume() {
        fishregWebview.onResume();
        super.onResume();
    }

    public WebView getWebView() {
        return fishregWebview;
    }

}