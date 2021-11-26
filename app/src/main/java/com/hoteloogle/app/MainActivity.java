package com.hoteloogle.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView mWebView;
    private String MyUrl = "http://m.hoteloogle.com/?mobile=1";
    private ProgressBar progressBar;
    private ImageView back_img, home_img, refresh_img;
    private String LOG_TAG = "MainActivity";
    private LinearLayout logo_container;
    private boolean clearHistory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        if (savedInstanceState == null) {
//            String message = savedInstanceState.getString("orientation");

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);
//        progressBar.getProgressDrawable().setColorFilter(getApplicationContext().getResources().getColor(R.color.progressColor), PorterDuff.Mode.CLEAR);


        logo_container = findViewById(R.id.logo_container);


        back_img = findViewById(R.id.back_imgview);
        back_img.setOnClickListener(this);

        home_img = findViewById(R.id.home_imgview);
        home_img.setOnClickListener(this);

        refresh_img = findViewById(R.id.refresh_imgview);
        refresh_img.setOnClickListener(this);

        prepareWebview();


        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);

                }

                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);

                }
            }
        });


        if (savedInstanceState == null) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    if (isOnline(getApplicationContext()) == true) {
                        logo_container.setVisibility(View.GONE);
                        mWebView.loadUrl(MyUrl);
                    } else {
                        logo_container.setVisibility(View.VISIBLE);
                        ShowSnakbar();
                    }
                }
            });
        }


    }


    private void prepareWebview() {

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new HelloWebViewClient());
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setSaveFormData(true);

        if (isOnline(getApplicationContext()) == true) {
            logo_container.setVisibility(View.GONE);
            mWebView.loadUrl(MyUrl);
        } else {
            logo_container.setVisibility(View.VISIBLE);
            ShowSnakbar();
        }


    }

    private void ShowSnakbar() {

        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar
                .make(parentLayout, getApplicationContext().getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG);

        snackbar.show();
    }


    @Override
    public void onClick(View v) {


        if (isOnline(getApplicationContext()) == true) {

            switch (v.getId()) {

                case R.id.back_imgview:

                    boolean checkBack = mWebView.canGoBack();

                    if (checkBack == true) {
                        mWebView.goBack();
                    }

                    logo_container.setVisibility(View.GONE);
                    break;
                case R.id.home_imgview:
                    clearHistory = true;


                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.setWebViewClient(new HelloWebViewClient());
                    mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                    mWebView.getSettings().setSaveFormData(true);

                    if (isOnline(getApplicationContext()) == true) {
                        logo_container.setVisibility(View.GONE);
                        mWebView.loadUrl(MyUrl);
                    } else {
                        logo_container.setVisibility(View.VISIBLE);
                        ShowSnakbar();
                    }


                    break;
                case R.id.refresh_imgview:

                    mWebView.reload();
                    logo_container.setVisibility(View.GONE);

                    break;

            }
        } else {
            ShowSnakbar();
        }


    }


    private class HelloWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            if (isOnline(getApplicationContext()) == true) {


                if ((url.contains("market://") || url.contains("mailto:") || url.contains("tel:") || url.contains("vid:")) == true) {
                    // Load new URL Don't override URL Link
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                    return true;
                } else {

                    view.loadUrl(url);
                    Log.d("urlHere", url);
                }


            } else {
                ShowSnakbar();
            }


            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {


            super.onPageFinished(view, url);


            boolean checkBack1 = mWebView.canGoBack();
            progressBar.setVisibility(View.GONE);


        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {


            if (isOnline(getApplicationContext()) == true) {

                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    MainActivity.this.finish();
                }

            } else {

                MainActivity.this.finish();

            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public static boolean isOnline(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            Log.e("On_Config_Change", "LANDSCAPE");
        } else {

            Log.e("On_Config_Change", "PORTRAIT");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("orientation", "This is my message to be reloaded");
        super.onSaveInstanceState(outState, outPersistentState);
        mWebView.saveState(outState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        mWebView.restoreState(savedInstanceState);

    }
}
