package com.starwood.anglerslong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by kennystreit on 1/28/16.
 */
public class FishingRegulationsFragment extends Fragment {
    private String webURL;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fishing_regulations, container, false);

        webURL = this.getArguments().getString("url");
        Log.d("KENNY NEW", "Current URL: " + webURL);

        if (webURL != null) {
            WebView webview = (WebView) view.findViewById(R.id.webview);
            WebSettings settings = webview.getSettings();
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setDomStorageEnabled(true);
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(webURL);
                    return false;
                }
            });
            webview.setOnKeyListener(new View.OnKeyListener() {
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
            webview.loadUrl(webURL);
        }

        return view;
    }

    public void updateUrl(String url) {
        webURL = url;
        WebView webview = (WebView) getView().findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webview.setOnKeyListener(new View.OnKeyListener() {
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
        Log.d("KENNY", "Current URL: " + webURL);
        webview.loadUrl(url);
    }

}
