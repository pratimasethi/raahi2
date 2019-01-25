package com.example.admin.mywebapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String myurl = "file:///android_asset/index.html";
        WebView view = this.findViewById(R.id.webView);
        view.getSettings().setJavaScriptEnabled(true);
        //view.setWebChromeClient(new WebChromeClient());
        view.addJavascriptInterface(new WebAppInterface(this), "Android");
        view.getSettings().setDomStorageEnabled(true);
        view.getSettings().setDatabaseEnabled(true);
        view.getSettings().setGeolocationEnabled(true);
        view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        view.getSettings().setAppCacheEnabled(true);
        view.getSettings().setAllowFileAccess(true);
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
        view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        view.setScrollbarFadingEnabled(false);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 0);

        view.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
// get IMEI

        String NetworkName = tm.getNetworkOperatorName();
        String CountryISO = tm.getSimCountryIso();
        String androidID1 = android.provider.Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        String html = "<html><body>" +
                "Network Name : " + NetworkName + "</br>" +
                "Country ISO : " + CountryISO + "</br>" +
                "Android ID : " + androidID1 +" </body></html>";
        String mime = "text/html";
        String encoding = "utf-8";

        view.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Log.i("WebView", "Attempting to load URL: " + url);

                view.loadUrl(url);
                return true;
            }
        });

        view.loadUrl(myurl);
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** The android function calld from javascript */
        @JavascriptInterface
        public void takePhoto() {
            dispatchTakePictureIntent();
        }
        @JavascriptInterface
        public void takeVideo() {
            dispatchTakeVideoIntent();
        }

    }

    /* This gests a Thumbnail only*/

    static final int REQUEST_IMAGE_CAPTURE = 1;



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    static final int VIDEO_CAPTURE = 101;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);
        }
    }

   /*static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private void dispatchTakeVoiceIntent() {
        Intent takeVideoIntent = new Intent(MediaRecorder.AudioSource.MIC);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent,);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            /* Encode bitmap as base64 and transmit to server */

        }

        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            /* Encode bitmap as base64 and transmit to server */

        }
    }
/*
    private String getImeiNumber() {
        final TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //getDeviceId() is Deprecated so for android O we can use getImei() method
            return telephonyManager.getImei();
        }
        else {
            return telephonyManager.getDeviceId();
        }

    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("smsto:") || url.startsWith("mailto:") || url.startsWith("mms:") || url.startsWith("mmsto:") || url.startsWith("market:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
            else {
                view.loadUrl(url);
                return true;
            }
        }
    } */
}
